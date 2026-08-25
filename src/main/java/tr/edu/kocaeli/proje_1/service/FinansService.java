package tr.edu.kocaeli.proje_1.service;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tr.edu.kocaeli.proje_1.model.FinansVerisi;
import tr.edu.kocaeli.proje_1.repository.FinansRepository;

import java.time.LocalDateTime;

@Service
public class FinansService {

    private final FinansRepository finansRepository;

    public FinansService(FinansRepository finansRepository) {
        this.finansRepository = finansRepository;
    }

    public FinansVerisi getEnGuncelVeri() {
        FinansVerisi sonVeri = finansRepository.findTopByOrderByGuncellenmeTarihiDesc();

        if (sonVeri == null) {
            sonVeri = new FinansVerisi();
            sonVeri.setUsd("0.00"); sonVeri.setEur("0.00");
            sonVeri.setGramAltin("0.00"); sonVeri.setBist100("0.00");
            sonVeri.setGuncellenmeTarihi(LocalDateTime.now());
        }
        return sonVeri;
    }

    @PostConstruct
    public void ilkCalistirma() {
        verileriGuncelle();
    }

    @Scheduled(fixedRate = 300000)
    public FinansVerisi verileriGuncelle() {
        FinansVerisi yeniVeri = new FinansVerisi();
        FinansVerisi eskiVeri = getEnGuncelVeri();

        try {
            Document doc = Jsoup.connect("https://www.doviz.com/").timeout(5000).get();

            String usd = formatlaTemizle(doc.select("span[data-socket-key='USD']").text());
            String eur = formatlaTemizle(doc.select("span[data-socket-key='EUR']").text());
            String gram = formatlaTemizle(doc.select("span[data-socket-key='gram-altin']").text());
            String bist = formatlaTemizle(doc.select("span[data-socket-key='XU100']").text());

            yeniVeri.setUsd(usd); yeniVeri.setEur(eur);
            yeniVeri.setGramAltin(gram); yeniVeri.setBist100(bist);
            yeniVeri.setGuncellenmeTarihi(LocalDateTime.now());

            yeniVeri.setUsdYon(hesaplaYon(usd, eskiVeri.getUsd()));
            yeniVeri.setEurYon(hesaplaYon(eur, eskiVeri.getEur()));
            yeniVeri.setGramYon(hesaplaYon(gram, eskiVeri.getGramAltin()));
            yeniVeri.setBistYon(hesaplaYon(bist, eskiVeri.getBist100()));

            System.out.println("Finans verileri arka planda karşılaştırmalı olarak güncellendi!");
            return finansRepository.save(yeniVeri);

        } catch (Exception e) {
            System.err.println("Hata: " + e.getMessage());
            return eskiVeri;
        }
    }

    private String formatlaTemizle(String hamVeri) {
        if (hamVeri == null || hamVeri.isEmpty()) return "0,00";

        String temizVeri = hamVeri.split(" ")[0].trim();

        if (temizVeri.contains(",")) {
            String[] parcalar = temizVeri.split(",");
            String tamKisim = parcalar[0];
            String ondalikKisim = parcalar[1];

            if (ondalikKisim.length() > 2) {
                ondalikKisim = ondalikKisim.substring(0, 2);
            }

            return tamKisim + "," + ondalikKisim;
        }

        return temizVeri;
    }

    private double parseDouble(String val) {
        if (val == null || val.isEmpty()) return 0.0;
        try {
            String clean = val.replace(".", "").replace(",", ".");
            return Double.parseDouble(clean);
        } catch(Exception e) { return 0.0; }
    }

    private String hesaplaYon(String yeniDeger, String eskiDeger) {
        if (eskiDeger == null || eskiDeger.isEmpty()) return "up";

        double yeni = parseDouble(yeniDeger);
        double eski = parseDouble(eskiDeger);

        if (yeni > eski) return "up";
        if (yeni < eski) return "down";
        return "eq";
    }
}