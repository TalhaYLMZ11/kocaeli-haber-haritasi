package tr.edu.kocaeli.proje_1.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "haberler")
public class Haber {

    @Id
    private String id;

    private String haberTuru;
    private String baslik;
    private String icerik;
    private String konumMetni;
    
    @Field("enlem")
    private Double enlem;
    
    @Field("boylam")
    private Double boylam;
    
    private LocalDateTime yayinTarihi;


    private List<Double> embedding;

    private List<HaberKaynagi> kaynaklar = new ArrayList<>();

    @Data
    public static class HaberKaynagi {
        private String siteAdi;
        private String url;

        public HaberKaynagi(String siteAdi, String url) {
            this.siteAdi = siteAdi;
            this.url = url;
        }
    }

    public void kaynakEkle(String siteAdi, String url) {
        boolean linkVarMi = kaynaklar.stream().anyMatch(k -> k.getUrl().equals(url));
        if (!linkVarMi) {
            this.kaynaklar.add(new HaberKaynagi(siteAdi, url));
        }
    }
}
