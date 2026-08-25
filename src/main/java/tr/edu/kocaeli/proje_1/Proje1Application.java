package tr.edu.kocaeli.proje_1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import tr.edu.kocaeli.proje_1.service.ScrapingService;

@SpringBootApplication
@EnableScheduling
public class Proje1Application {

    public static void main(String[] args) {
        SpringApplication.run(Proje1Application.class, args);
    }
}