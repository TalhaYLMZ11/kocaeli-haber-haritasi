package tr.edu.kocaeli.proje_1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.edu.kocaeli.proje_1.service.ScrapingService;

@RestController
@RequestMapping("/api/scraping")
public class ScrapingController {

    private final ScrapingService scrapingService;

    public ScrapingController(ScrapingService scrapingService) {
        this.scrapingService = scrapingService;
    }

    @GetMapping("/cagdas")
    public String cagdasKocaeliTetikle() {
        System.out.println("Veri çekme işlemi başlatılıyor...");
        scrapingService.tumHaberleriCek(3);
        return "Çağdaş Kocaeli sitesinden veri çekme işlemi tamamlandı! Lütfen IDE konsolunu veya MongoDB veritabanını kontrol edin.";
    }
}