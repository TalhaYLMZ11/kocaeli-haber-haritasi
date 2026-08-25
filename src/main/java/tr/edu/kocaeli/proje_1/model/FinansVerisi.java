package tr.edu.kocaeli.proje_1.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Document(collection = "finans_verileri")
public class FinansVerisi {

    @Id
    private String id;

    private String gramAltin;
    private String bist100;
    private String usd;
    private String eur;
    private LocalDateTime guncellenmeTarihi;

    private String gramYon;
    private String bistYon;
    private String usdYon;
    private String eurYon;
}