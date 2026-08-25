package tr.edu.kocaeli.proje_1.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tr.edu.kocaeli.proje_1.model.FinansVerisi;

public interface FinansRepository extends MongoRepository<FinansVerisi, String> {
    FinansVerisi findTopByOrderByGuncellenmeTarihiDesc();
}