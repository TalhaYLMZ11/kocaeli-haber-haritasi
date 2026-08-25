package tr.edu.kocaeli.proje_1.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocationService {

    private final List<String> ilceler = Arrays.asList(
            "izmit", "gebze", "darıca", "gölcük", "kartepe", "derince",
            "başiskele", "çayırova", "karamürsel", "dilovası", "körfez", "kandıra"
    );

    private final Locale trLocale = new Locale("tr", "TR");

    public String konumBul(String baslik, String icerik) {
        return konumBul(baslik, icerik, null);
    }

    public String konumBul(String baslik, String icerik, String haberTuru) {
        String kucukBaslik = baslik.toLowerCase(trLocale);
        String kucukIcerik = icerik.toLowerCase(trLocale);
        String orijinalMetin = baslik + " " + icerik;

        boolean kocaeliGeciyorMu = tamKelimeIceriyorMu(kucukBaslik, "kocaeli")
                || tamKelimeIceriyorMu(kucukIcerik, "kocaeli");
        boolean trafikAnahtarVarMi = trafikAnahtarGeciyorMu(kucukBaslik, kucukIcerik);

        String bulunanIlce = "";
        String detayliKonum = "";

        for (String ilce : ilceler) {
            if (tamKelimeIceriyorMu(kucukBaslik, ilce)) {
                bulunanIlce = ilkHarfiBuyut(ilce);
                break;
            }
        }

        if (bulunanIlce.isEmpty()) {
            for (String ilce : ilceler) {
                if (tamKelimeIceriyorMu(kucukIcerik, ilce)) {
                    bulunanIlce = ilkHarfiBuyut(ilce);
                    break;
                }
            }
        }
        if (!kocaeliGeciyorMu && bulunanIlce.isEmpty()) {
            return null;
        }

        if ("Kültürel Etkinlikler".equals(haberTuru)) {
            detayliKonum = kulturelEtkinlikKonumBul(orijinalMetin);
        }

        if (detayliKonum.isEmpty() && trafikAnahtarVarMi) {
            detayliKonum = trafikYoluKonumBul(orijinalMetin);
        }

        if (detayliKonum.isEmpty() || detayliKonum.equalsIgnoreCase("kavşak") || detayliKonum.equalsIgnoreCase("geçit")) {
            String regex = "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+){1,3}"
                    + "(?i)(Mahallesi|Mahalle|Mah\\.?|Caddesi|Cadde|Cad\\.?|Sokağı|Sokak|Sok\\.?|"
                    + "Kavşağı|Kavşak|Mevkii|Mevki|Bulvarı|Bulvar|Bul\\.?|Yolu|Gişeleri|Gişeler|Gişelerinde)";
            Matcher matcher = Pattern.compile(regex).matcher(orijinalMetin);

            if (matcher.find()) {
                detayliKonum = matcher.group().trim();
                detayliKonum = detayliKonum.replaceAll("(?U)(?i)\\bMah\\.?$", "Mahallesi");
                detayliKonum = detayliKonum.replaceAll("(?U)(?i)\\bCad\\.?$", "Caddesi");
                detayliKonum = detayliKonum.replaceAll("(?U)(?i)\\bSok\\.?$", "Sokağı");
                detayliKonum = detayliKonum.replaceAll("(?U)(?i)\\bBul\\.?$", "Bulvarı");
                detayliKonum = detayliKonum.replaceAll("(?U)(?i)\\b(gişelerinde|gişeler)\\b", "Gişeleri");
            }
        }

        String sonKonum = "";

        if (!bulunanIlce.isEmpty() && !detayliKonum.isEmpty()) {
            if (detayliKonum.toLowerCase(trLocale).contains(bulunanIlce.toLowerCase(trLocale))) {
                sonKonum = detayliKonum;
            } else {
                sonKonum = bulunanIlce + " / " + detayliKonum;
            }
        } else if (!detayliKonum.isEmpty()) {
            sonKonum = detayliKonum;
        } else if (!bulunanIlce.isEmpty()) {
            sonKonum = bulunanIlce;
        }

        if (!sonKonum.isEmpty() && !sonKonum.toLowerCase(trLocale).contains("kocaeli")) {
            return "Kocaeli / " + sonKonum;
        }

        if (sonKonum.isEmpty() && kocaeliGeciyorMu) {
            return "Kocaeli";
        }

        return sonKonum;
    }

    private String kulturelEtkinlikKonumBul(String orijinalMetin) {
        String[] patterns = {
                "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+[Ss]ahne(si)?",

                "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+(Kültür|Sanat|Kongre|Spor|İhtisas)(\\s+[A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+)*\\s+(Merkezi|Sarayı|Evi|Kompleksi)",

                "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+(Müzesi|Museum)",

                "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+[Mm]ahallesi(,|'de)?\\s+([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+(Caddesi|Sokağı|Bulvarı|Yolu)",

                "([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+)+(Caddesi|Cadde|Sokağı|Sokak|Bulvarı|Bulvar)\\s+No\\.?\\s*\\d+"
        };

        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(orijinalMetin);
            if (matcher.find()) {
                String bulunan = matcher.group().trim();
                return bulunan.replaceAll("['’][a-zçğıöşü]+$", "");
            }
        }

        return "";
    }

    private boolean trafikAnahtarGeciyorMu(String kucukBaslik, String kucukIcerik) {
        String[] anahtarlar = {
                "otoban", "otobanı",
                "otoyol", "otoyolu",
                "geçit", "gecit", "geçidi", "gecidi",
                "kavşak", "kavşağı", "kavsak",
                // DÜZELTME: "gişe" kelimesinin ek almış halleri eklendi.
                "gişe", "gişeler", "gişeleri", "gişelerinde"
        };

        for (String anahtar : anahtarlar) {
            if (tamKelimeIceriyorMu(kucukBaslik, anahtar) || tamKelimeIceriyorMu(kucukIcerik, anahtar)) {
                return true;
            }
        }
        return false;
    }

    private String trafikYoluKonumBul(String orijinalMetin) {
        String[] oncelikliPatternler = {
                "(?i)\\b(TEM|D\\s*-?\\s*100|E\\s*-?\\s*5|O\\s*-?\\s*4)\\b(?:\\s+(otoyolu|otoyol|otobanı|otoban))?(?:\\s+(kavşağı|kavşak|kavsak|geçidi|geçit|gecit|gecidi))?",
                "(?i)([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+){1,3}(otoyolu|otoyol|otobanı|otoban)(?:\\s+(kavşağı|kavşak|kavsak|geçidi|geçit|gecit|gecidi))?",
                "(?i)([A-ZÇĞİÖŞÜ][a-zçğıöşü0-9-]+\\s+){1,3}(kavşağı|kavşak|kavsak|geçidi|geçit|gecit|gecidi)"
        };

        for (String pattern : oncelikliPatternler) {
            Matcher matcher = Pattern.compile(pattern).matcher(orijinalMetin);
            if (matcher.find()) {
                return trafikKonumunuNormalizeEt(matcher.group());
            }
        }

        String lowerText = orijinalMetin.toLowerCase(trLocale);
        if (lowerText.contains("tem")) return "TEM otoyolu";
        if (lowerText.contains("d-100") || lowerText.contains("d100")) return "D-100 otoyolu";
        if (lowerText.contains("e-5") || lowerText.contains("e5")) return "E-5 otoyolu";
        if (lowerText.contains("o-4") || lowerText.contains("o4")) return "O-4 otoyolu";

        return "";
    }

    private String trafikKonumunuNormalizeEt(String hamKonum) {
        String normalize = hamKonum.trim().replaceAll("\\s+", " ")
                .replaceAll("(?U)(?i)\\botoyolu\\b", "otoyolu")
                .replaceAll("(?U)(?i)\\botoyol\\b", "otoyol")
                .replaceAll("(?U)(?i)\\botobanı\\b", "otoban")
                .replaceAll("(?U)(?i)\\botoban\\b", "otoban")
                .replaceAll("(?U)(?i)\\bkavşağı\\b", "kavşak")
                .replaceAll("(?U)(?i)\\bkavsak\\b", "kavşak")
                .replaceAll("(?U)(?i)\\bgeçidi\\b", "geçit")
                .replaceAll("(?U)(?i)\\bgecit\\b", "geçit")
                .replaceAll("(?U)(?i)\\bgecidi\\b", "geçit");

        if (normalize.toLowerCase(trLocale).contains("tem") && !normalize.toLowerCase(trLocale).contains("otoyol")) {
            return "TEM otoyolu";
        }

        return normalize;
    }

    private boolean tamKelimeIceriyorMu(String metin, String arananKelime) {
        String regex = "(?U)(?i)(^|\\s|\\p{Punct}|\\p{IsPunctuation}|[’‘])"
                + Pattern.quote(arananKelime)
                + "(\\s|\\p{Punct}|\\p{IsPunctuation}|[’‘]|$)";
        return Pattern.compile(regex).matcher(metin).find();
    }

    private String ilkHarfiBuyut(String kelime) {
        if (kelime == null || kelime.isEmpty()) return kelime;
        return kelime.substring(0, 1).toUpperCase(trLocale) + kelime.substring(1).toLowerCase(trLocale);
    }
}