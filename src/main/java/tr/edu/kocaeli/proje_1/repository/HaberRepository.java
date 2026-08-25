package tr.edu.kocaeli.proje_1.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tr.edu.kocaeli.proje_1.model.Haber;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HaberRepository extends MongoRepository<Haber, String> {

    boolean existsByKaynaklarUrl(String url);

    List<Haber> findByHaberTuru(String haberTuru);

    List<Haber> findByHaberTuruAndYayinTarihiAfter(String haberTuru, LocalDateTime tarih);

    List<Haber> findByYayinTarihiAfter(LocalDateTime tarih);

    @Query("{ 'enlem': { $ne: null }, 'boylam': { $ne: null } }")
    List<Haber> findByEnlemNotNullAndBoylamNotNull();

    @Query("{ 'konumMetni': { $regex: ?0, $options: 'i' } }")
    List<Haber> findByKonumMetniContains(String konum);

    void deleteByYayinTarihiBefore(LocalDateTime tarih);
}