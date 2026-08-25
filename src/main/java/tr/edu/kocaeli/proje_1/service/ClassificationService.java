package tr.edu.kocaeli.proje_1.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClassificationService {

    public String haberTuruBelirle(String baslik, String icerik) {

        String metin = (baslik + " " + icerik).toLowerCase();

        if (metin.contains("cinayet") || metin.contains("öldürd") || metin.contains("öldürül")) {
            return null;
        }

        List<String> redListesi = Arrays.asList(
                "futbol", "basketbol", "voleybol", "maç", "lig", "şampiyon", "galibiyet",
                "altın", "döviz", "borsa", "kuyumcu", "ameliyat", "hastane"
        );
        if (redListesi.stream().anyMatch(kelime -> tamKelimeIceriyorMu(metin, kelime))) {
            return null;
        }

        List<String> burokrasiListesi = Arrays.asList(
                "yılını değerlendirdi", "hizmet süresini", "kamulaştırma", "imar uygulaması",
                "imar planı", "ihale", "sgk'ya borc", "faaliyet raporu"
        );
        if (burokrasiListesi.stream().anyMatch(metin::contains)) {
            return null;
        }

        List<String> hukukFiltresi = Arrays.asList(
                "davası", "duruşma", "hâkim", "savcı", "adliye", "mahkeme", "suçsuz buldu",
                "beraat", "tahliye", "tutukluluk", "jüri", "milletvekili", "istifa", "seçim", "silahlı saldırı"
        );

        if (hukukFiltresi.stream().anyMatch(kelime -> tamKelimeIceriyorMu(metin, kelime))) {
            if (!tamKelimeIceriyorMu(metin, "tiyatro") && !tamKelimeIceriyorMu(metin, "festival")) {
                return null;
            }
        }

        boolean kazaKelimeleri = metin.contains("trafik kazası") || metin.contains("zincirleme kaza");
        boolean kazaEylemi = metin.contains("çarpıştı") || metin.contains("takla attı") ||
                metin.contains("devrildi") || metin.contains("şarampole");

        if (kazaKelimeleri || (tamKelimeIceriyorMu(metin, "kaza") && kazaEylemi)) {
            if (!tamKelimeIceriyorMu(metin, "inisiyatif") && !tamKelimeIceriyorMu(metin, "ziyaret")) {
                return "Trafik Kazası";
            }
        }

        if (tamKelimeIceriyorMu(metin, "yangın") && (metin.contains("itfaiye") || metin.contains("alev"))) {

            // Gerçek yangın olmayan durumları filtrele (Eğitim, tatbikat veya varsayım)
            boolean gercekDisiYangin = metin.contains("yangın çıksa") ||
                    tamKelimeIceriyorMu(metin, "tatbikat") ||
                    tamKelimeIceriyorMu(metin, "tatbikatı") ||
                    tamKelimeIceriyorMu(metin, "eğitim") ||
                    tamKelimeIceriyorMu(metin, "eğitimi") ||
                    metin.contains("park tepkisi");

            boolean tadilatVarMi = tamKelimeIceriyorMu(metin, "tadilat") || tamKelimeIceriyorMu(metin, "restorasyon");

            if (!tadilatVarMi && !gercekDisiYangin) {
                return "Yangın";
            }
        }

        List<String> kultur = Arrays.asList("tiyatro", "konser", "sergi", "festival", "senfonik", "sanat", "orkestra");
        if (kultur.stream().anyMatch(kelime -> tamKelimeIceriyorMu(metin, kelime))) {

            boolean insaatSanati = metin.contains("sanat yapı");

            if (!metin.contains("suçsuz buldu") && !tamKelimeIceriyorMu(metin, "aday") && !insaatSanati) {
                return "Kültürel Etkinlikler";
            }
        }

        boolean sedasKesintisi = tamKelimeIceriyorMu(metin, "sedaş") &&
                (metin.contains("kesinti") || metin.contains("arıza") || metin.contains("planlı"));

        if (metin.contains("elektrik kesintisi") || sedasKesintisi) {
            return "Elektrik Kesintisi";
        }

        if (tamKelimeIceriyorMu(metin, "hırsızlık") || tamKelimeIceriyorMu(metin, "soygun") || metin.contains("evden çaldı") ) {
            return "Hırsızlık";
        }

        return null;
    }

    private boolean tamKelimeIceriyorMu(String metin, String arananKelime) {
        String regex = "(?i)(^|\\s|\\p{Punct})" + Pattern.quote(arananKelime) + "(\\s|\\p{Punct}|$)";
        return Pattern.compile(regex).matcher(metin).find();
    }
}