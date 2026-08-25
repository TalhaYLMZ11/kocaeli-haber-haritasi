package tr.edu.kocaeli.proje_1.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import tr.edu.kocaeli.proje_1.model.Haber;
import tr.edu.kocaeli.proje_1.repository.HaberRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ScrapingService {

    private final HaberRepository haberRepository;
    private final ClassificationService classificationService;
    private final LocationService locationService;
    private final EmbeddingService embeddingService;
    private final GeocodingService geocodingService;

    private static final Map<String, Map<String, Double>> OZEL_ILCE_MERKEZLERI = new HashMap<>();

    static {
        OZEL_ILCE_MERKEZLERI.put("izmit", Map.of("enlem", 40.765400, "boylam", 29.940800));
        OZEL_ILCE_MERKEZLERI.put("kartepe", Map.of("enlem", 40.75316461365724, "boylam", 30.023194496102196));
        OZEL_ILCE_MERKEZLERI.put("gölcük", Map.of("enlem", 40.716730740949075, "boylam", 29.81962128119578));
        OZEL_ILCE_MERKEZLERI.put("başiskele", Map.of("enlem", 40.71423155122175, "boylam", 29.92120418253671));
        OZEL_ILCE_MERKEZLERI.put("karamürsel", Map.of("enlem", 40.69131301568233, "boylam", 29.616441684743776));
        OZEL_ILCE_MERKEZLERI.put("derince", Map.of("enlem", 40.75594077183613, "boylam", 29.831051794603376));
        OZEL_ILCE_MERKEZLERI.put("dilovası", Map.of("enlem", 40.787536227451504, "boylam", 29.544146601591855));
        OZEL_ILCE_MERKEZLERI.put("darıca", Map.of("enlem", 40.77379853594419, "boylam", 29.400276345471692));
    }

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";

    public ScrapingService(HaberRepository haberRepository, ClassificationService classificationService, LocationService locationService, EmbeddingService embeddingService, GeocodingService geocodingService) {
        this.haberRepository = haberRepository;
        this.classificationService = classificationService;
        this.locationService = locationService;
        this.embeddingService = embeddingService;
        this.geocodingService = geocodingService;
    }

    public void tumHaberleriCek(int gunSayisi) {
        System.out.println("🚀 SİSTEM BAŞLATILIYOR...");

        eskiHaberleriTemizle(gunSayisi);

        System.out.println("🚀 VERİ ÇEKME İŞLEMİ BAŞLATILIYOR...\n");

        ortakSablonVeriCek("https://www.ozgurkocaeli.com.tr", "Özgür Kocaeli", gunSayisi);
        ortakSablonVeriCek("https://www.cagdaskocaeli.com.tr", "Çağdaş Kocaeli", gunSayisi);
        ortakSablonVeriCek("https://www.seskocaeli.com", "Ses Kocaeli", gunSayisi);
        ortakSablonVeriCek("https://www.bizimyaka.com", "Bizim Yaka Kocaeli", gunSayisi);

        System.out.println("🏁 TÜM SİTELERİN TARAMASI TAMAMLANDI.");
    }

    private void eskiHaberleriTemizle(int gunSayisi) {
        System.out.println("--------------------------------------------------");
        System.out.println("🧹 VERİTABANI TEMİZLİĞİ YAPILIYOR...");

        LocalDateTime sinirTarihi = LocalDate.now().minusDays(gunSayisi - 1L).atStartOfDay();

        try {
            haberRepository.deleteByYayinTarihiBefore(sinirTarihi);
            System.out.println("✨ Son " + gunSayisi + " gün dışındaki tüm haberler veritabanından silindi.");
        } catch (Exception e) {
            System.err.println("❌ Eski haberleri silerken bir hata oluştu: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------\n");
    }

    private void ortakSablonVeriCek(String baseUri, String siteAdi, int gunSayisi) {
        for (int i = 0; i < gunSayisi; i++) {
            LocalDate hedefTarih = LocalDate.now().minusDays(i);
            String formatliTarih = hedefTarih.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String arsivUrl = baseUri + "/arsiv/" + formatliTarih;

            System.out.println("--------------------------------------------------");
            System.out.println("📅 [" + formatliTarih + "] " + siteAdi.toUpperCase() + " ARŞİVİ TARANIYOR...");
            System.out.println("--------------------------------------------------");

            try {
                Document arsivSayfasi = Jsoup.connect(arsivUrl).userAgent(USER_AGENT).timeout(20000).get();
                Elements haberLinkleri = arsivSayfasi.select("div.f-cat.f-item a");

                for (Element haberLink : haberLinkleri) {
                    String link = haberLink.attr("abs:href");

                    if (link.isEmpty() || !link.contains("/haber/") || haberRepository.existsByKaynaklarUrl(link)) {
                        continue;
                    }

                    try {
                        Document detaySayfasi = Jsoup.connect(link).userAgent(USER_AGENT).referrer(arsivUrl).get();

                        String baslik = detaySayfasi.select("h1").text();

                        Element spotElementi = detaySayfasi.selectFirst("p[itemprop=description], .spot, .summary, meta[name=description]");
                        String spotMetni = "";

                        if (spotElementi != null) {
                            if (spotElementi.tagName().equals("meta")) {
                                spotMetni = spotElementi.attr("content");
                            } else {
                                spotMetni = spotElementi.text();
                            }
                            spotMetni += " ";
                        }

                        Element haberGovdesi = detaySayfasi.selectFirst(".article-text, #main-text");
                        String temizIcerik = "";

                        if (haberGovdesi != null) {
                            haberGovdesi.select(".related-news, .related-posts, .tags, .social-share, .ad-box, script, style").remove();
                            temizIcerik = spotMetni + haberGovdesi.text();
                        } else {
                            temizIcerik = spotMetni.trim();
                        }

                        String belirlenenTur = classificationService.haberTuruBelirle(baslik, temizIcerik);

                        if (belirlenenTur != null) {
                            String hamKonum = locationService.konumBul(baslik, temizIcerik, belirlenenTur);
                            KonumCozumlemeSonucu konumSonucu = konumCozumle(hamKonum);

                            if (konumSonucu == null) {
                                System.out.println("❌ [PAS GEÇİLDİ]");
                                System.out.println("   ├─ Sebep : Kocaeli dışı veya geçersiz konum");
                                System.out.println("   └─ Başlık: " + (baslik.length() > 60 ? baslik.substring(0, 60) + "..." : baslik) + "\n");
                                continue;
                            }

                            String belirlenenKonum = konumSonucu.konumMetni;

                            String birlesikMetin = baslik + ". " + temizIcerik;
                            List<Double> haberVektoru = embeddingService.vektorOlustur(birlesikMetin);

                            Haber mevcutHaber = benzerHaberBul(haberVektoru, belirlenenTur, belirlenenKonum);

                            if (mevcutHaber != null) {
                                mevcutHaber.kaynakEkle(siteAdi, link);

                                String eskiKonum = mevcutHaber.getKonumMetni();
                                boolean konumGuncellendiMi = false;

                                if (dahaIyiKonumMu(eskiKonum, belirlenenKonum)) {
                                    mevcutHaber.setKonumMetni(belirlenenKonum);
                                    konumGuncellendiMi = true;

                                    Map<String, Double> koordinatlar = koordinatlariHazirla(belirlenenKonum, konumSonucu.koordinatlar);
                                    if (koordinatlar != null && !koordinatlar.isEmpty()) {
                                        Double enlem = koordinatlar.get("enlem");
                                        Double boylam = koordinatlar.get("boylam");
                                        if (enlem != null && boylam != null) {
                                            mevcutHaber.setEnlem(enlem);
                                            mevcutHaber.setBoylam(boylam);
                                        }
                                    }
                                }

                                haberRepository.save(mevcutHaber);

                                System.out.println("🛑 [KOPYA ENGELLENDİ - BİRLEŞTİRİLDİ]");
                                System.out.println("   ├─ Tür   : " + belirlenenTur);
                                if (konumGuncellendiMi) {
                                    System.out.println("   ├─ Konum : " + eskiKonum + " -> " + belirlenenKonum + " (YÜKSELTİLDİ)");
                                } else {
                                    System.out.println("   ├─ Konum : " + eskiKonum + " (KORUNDU)");
                                }
                                System.out.println("   ├─ Kaynak: " + siteAdi + " -> Link eklendi.");
                                System.out.println("   └─ Başlık: " + baslik + "\n");
                            } else {
                                // YENİ HABER KAYDI
                                Haber yeniHaber = new Haber();
                                yeniHaber.kaynakEkle(siteAdi, link);
                                yeniHaber.setBaslik(baslik);
                                yeniHaber.setIcerik(temizIcerik);
                                yeniHaber.setHaberTuru(belirlenenTur);
                                yeniHaber.setKonumMetni(belirlenenKonum);
                                yeniHaber.setYayinTarihi(hedefTarih.atStartOfDay());
                                yeniHaber.setEmbedding(haberVektoru);

                                Map<String, Double> koordinatlar = koordinatlariHazirla(belirlenenKonum, konumSonucu.koordinatlar);
                                if (koordinatlar != null && !koordinatlar.isEmpty()) {
                                    Double enlem = koordinatlar.get("enlem");
                                    Double boylam = koordinatlar.get("boylam");
                                    if (enlem != null && boylam != null) {
                                        yeniHaber.setEnlem(enlem);
                                        yeniHaber.setBoylam(boylam);
                                    }
                                }

                                haberRepository.save(yeniHaber);

                                System.out.println("✅ [YENİ KAYIT]");
                                System.out.println("   ├─ Tür   : " + belirlenenTur);
                                System.out.println("   ├─ Konum : " + belirlenenKonum);
                                if (yeniHaber.getEnlem() != null && yeniHaber.getBoylam() != null) {
                                    System.out.println("   ├─ GPS   : (" + yeniHaber.getEnlem() + ", " + yeniHaber.getBoylam() + ") ✅");
                                }
                                System.out.println("   ├─ Kaynak: " + siteAdi);
                                System.out.println("   └─ Başlık: " + baslik + "\n");
                            }
                        } else {
                            System.out.println("❌ [PAS GEÇİLDİ]");
                            System.out.println("   ├─ Sebep : Kategori Dışı (Spor/Siyaset/Ekonomi vb.)");
                            System.out.println("   └─ Başlık: " + (baslik.length() > 60 ? baslik.substring(0, 60) + "..." : baslik) + "\n");
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Detay hatası (" + siteAdi + "): " + link);
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Arşiv erişim hatası (" + siteAdi + "): " + arsivUrl);
            }
        }
    }

    private KonumCozumlemeSonucu konumCozumle(String hamKonum) {
        if (hamKonum == null || hamKonum.trim().isEmpty() || hamKonum.equalsIgnoreCase("null")) {
            return null;
        }

        List<String> adaylar = konumAdaylari(hamKonum);
        if (adaylar.isEmpty()) {
            return null;
        }

        for (String aday : adaylar) {
            if (aday.equalsIgnoreCase("Kocaeli")) {
                continue;
            }

            String kayitKonumu = konumKayitIcinNormalizeEt(aday);

            Map<String, Double> ozelKoordinat = ozelIlceMerkeziKoordinatiBul(aday);
            if (ozelKoordinat != null) {
                return new KonumCozumlemeSonucu(kayitKonumu, ozelKoordinat);
            }

            Map<String, Double> koordinatlar = geocodingService.konumuGeocode(aday);
            if (koordinatlar != null && !koordinatlar.isEmpty()) {
                return new KonumCozumlemeSonucu(kayitKonumu, koordinatlar);
            }
        }

        for (int i = adaylar.size() - 1; i >= 0; i--) {
            String aday = adaylar.get(i);
            if (aday.equalsIgnoreCase("Kocaeli")) {
                return new KonumCozumlemeSonucu("Kocaeli / İzmit", OZEL_ILCE_MERKEZLERI.get("izmit"));
            }
            if (!aday.equalsIgnoreCase("null") && !aday.trim().isEmpty()) {
                return new KonumCozumlemeSonucu(konumKayitIcinNormalizeEt(aday), null);
            }
        }

        return null;
    }

    private List<String> konumAdaylari(String hamKonum) {
        String[] parcalar = hamKonum.split("/");
        List<String> temizParcalar = new ArrayList<>();
        List<String> adaylar = new ArrayList<>();

        for (String parca : parcalar) {
            String temiz = parca == null ? "" : parca.trim();
            if (temiz.isEmpty() || temiz.equalsIgnoreCase("null")) {
                continue;
            }
            temizParcalar.add(temiz);
        }

        for (int i = temizParcalar.size(); i >= 1; i--) {
            String aday = String.join(" / ", temizParcalar.subList(0, i));
            if (!adaylar.contains(aday)) {
                adaylar.add(aday);
            }
        }

        return adaylar;
    }

    private Map<String, Double> ozelIlceMerkeziKoordinatiBul(String konum) {
        if (konum == null) return null;

        // Konumu temizleyerek sadece ilçe adını bırakıyoruz (Örn: "Kocaeli / Gölcük" -> "gölcük")
        String temizKonum = konum.toLowerCase(new Locale("tr", "TR"))
                .replace("kocaeli", "")
                .replace("/", "")
                .trim();

        return OZEL_ILCE_MERKEZLERI.get(temizKonum);
    }


    private Map<String, Double> koordinatlariHazirla(String konum, Map<String, Double> mevcutKoordinatlar) {
        Map<String, Double> ozelKoordinat = ozelIlceMerkeziKoordinatiBul(konum);
        if (ozelKoordinat != null) {
            return ozelKoordinat;
        }

        if (mevcutKoordinatlar != null && !mevcutKoordinatlar.isEmpty()) {
            return mevcutKoordinatlar;
        }
        if (konum == null || konum.trim().isEmpty()) {
            return null;
        }
        return geocodingService.konumuGeocode(konum);
    }

    private String konumKayitIcinNormalizeEt(String konum) {
        if (konum == null || konum.trim().isEmpty()) {
            return konum;
        }

        String temizKonum = konum.trim();
        if (temizKonum.toLowerCase(new Locale("tr", "TR")).contains("kocaeli")) {
            return temizKonum;
        }

        return "Kocaeli / " + temizKonum;
    }

    private boolean dahaIyiKonumMu(String eskiKonum, String yeniKonum) {
        if (yeniKonum == null || yeniKonum.isEmpty() || yeniKonum.equals("Kocaeli")) {
            return false;
        }
        if (eskiKonum == null || eskiKonum.isEmpty() || eskiKonum.equals("Kocaeli")) {
            return true;
        }

        if (yeniKonum.contains("/") && !eskiKonum.contains("/")) {
            return true;
        }

        if (yeniKonum.length() > eskiKonum.length() + 3) {
            return true;
        }

        return false;
    }

    private Haber benzerHaberBul(List<Double> yeniEmbedding, String haberTuru, String yeniKonum) {
        if (yeniEmbedding == null || yeniEmbedding.isEmpty()) return null;

        List<Haber> ayniKategoridekiHaberler = haberRepository.findByHaberTuru(haberTuru);

        Haber enBenzerHaber = null;
        double enYuksekBenzerlik = 0.0;
        double BENZERLIK_ESIGI = 0.9;

        for (Haber dbHaberi : ayniKategoridekiHaberler) {
            List<Double> dbEmbedding = dbHaberi.getEmbedding();

            if (dbEmbedding != null && !dbEmbedding.isEmpty()) {
                double benzerlikSkoru = embeddingService.kosinusBenzerligiHesapla(yeniEmbedding, dbEmbedding);

                if (benzerlikSkoru > enYuksekBenzerlik) {
                    enYuksekBenzerlik = benzerlikSkoru;
                    if (benzerlikSkoru >= BENZERLIK_ESIGI) {
                        enBenzerHaber = dbHaberi;
                    }
                }
            }
        }
        return enBenzerHaber;
    }

    private static class KonumCozumlemeSonucu {
        private final String konumMetni;
        private final Map<String, Double> koordinatlar;

        private KonumCozumlemeSonucu(String konumMetni, Map<String, Double> koordinatlar) {
            this.konumMetni = konumMetni;
            this.koordinatlar = koordinatlar;
        }
    }
}