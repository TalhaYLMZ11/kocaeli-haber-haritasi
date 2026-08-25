package tr.edu.kocaeli.proje_1.controller;

import org.springframework.web.bind.annotation.*;
import tr.edu.kocaeli.proje_1.model.FinansVerisi;
import tr.edu.kocaeli.proje_1.service.FinansService;

@RestController
@RequestMapping("/api/finans")
public class FinansController {

    private final FinansService finansService;

    public FinansController(FinansService finansService) {
        this.finansService = finansService;
    }

    @GetMapping
    public FinansVerisi getPiyasa() {
        return finansService.getEnGuncelVeri();
    }

    @GetMapping("/tetikle")
    public FinansVerisi forceUpdate() {
        return finansService.verileriGuncelle();
    }
}