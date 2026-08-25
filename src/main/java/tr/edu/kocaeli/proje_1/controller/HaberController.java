package tr.edu.kocaeli.proje_1.controller;

import org.springframework.web.bind.annotation.*;
import tr.edu.kocaeli.proje_1.model.Haber;
import tr.edu.kocaeli.proje_1.repository.HaberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/haberler")
@CrossOrigin(origins = "http://localhost:3000")
public class HaberController {

    private final HaberRepository haberRepository;

    public HaberController(HaberRepository haberRepository) {
        this.haberRepository = haberRepository;
    }

    @GetMapping
    public List<Haber> getAllHaberler() {
        return haberRepository.findAll();
    }

    @GetMapping("/{id}")
    public Haber getHaberById(@PathVariable String id) {
        return haberRepository.findById(id).orElse(null);
    }

    @GetMapping("/turu/{turu}")
    public List<Haber> getHaberlerByType(@PathVariable String turu) {
        return haberRepository.findByHaberTuru(turu);
    }

    @GetMapping("/songun/{gun}")
    public List<Haber> getHaberlerSonGunlerde(@PathVariable int gun) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(gun);
        return haberRepository.findByYayinTarihiAfter(startDate);
    }

    @GetMapping("/harita/konumlu")
    public List<Haber> getHaberlerKonumuyla() {
        return haberRepository.findAll()
                .stream()
                .filter(h -> h.getEnlem() != null && h.getBoylam() != null)
                .toList();
    }

    @GetMapping("/istatistik")
    public HaberIstatistik getIstatistik() {
        List<Haber> allHaberler = haberRepository.findAll();
        
        return new HaberIstatistik(
            allHaberler.size(),
            (int) allHaberler.stream().filter(h -> h.getEnlem() != null && h.getBoylam() != null).count(),
            (int) allHaberler.stream().filter(h -> h.getEmbedding() != null && !h.getEmbedding().isEmpty()).count(),
            (int) allHaberler.stream().filter(h -> h.getHaberTuru().equals("Trafik Kazası")).count(),
            (int) allHaberler.stream().filter(h -> h.getHaberTuru().equals("Yangın")).count(),
            (int) allHaberler.stream().filter(h -> h.getHaberTuru().equals("Kültürel Etkinlikler")).count(),
            (int) allHaberler.stream().filter(h -> h.getHaberTuru().equals("Hırsızlık")).count(),
            (int) allHaberler.stream().filter(h -> h.getHaberTuru().equals("Elektrik Kesintisi")).count()
        );
    }

    public static class HaberIstatistik {
        public int toplamHaber;
        public int konumlHaber;
        public int embeddingliHaber;
        public int trafikKazasi;
        public int yangin;
        public int kultur;
        public int hirsizlik;
        public int elektrik;

        public HaberIstatistik(int toplamHaber, int konumlHaber, int embeddingliHaber,
                              int trafikKazasi, int yangin, int kultur, int hirsizlik, int elektrik) {
            this.toplamHaber = toplamHaber;
            this.konumlHaber = konumlHaber;
            this.embeddingliHaber = embeddingliHaber;
            this.trafikKazasi = trafikKazasi;
            this.yangin = yangin;
            this.kultur = kultur;
            this.hirsizlik = hirsizlik;
            this.elektrik = elektrik;
        }

    }
}

