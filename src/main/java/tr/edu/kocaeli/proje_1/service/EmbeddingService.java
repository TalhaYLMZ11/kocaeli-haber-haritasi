package tr.edu.kocaeli.proje_1.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> vektorOlustur(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent?key=" + apiKey;

        try {
            JSONObject part = new JSONObject();
            part.put("text", text);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "models/gemini-embedding-001");
            requestBody.put("content", content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

            String response = restTemplate.postForObject(apiUrl, requestEntity, String.class);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray valuesArray = jsonResponse.getJSONObject("embedding").getJSONArray("values");

            List<Double> embeddingList = new ArrayList<>();
            for (int i = 0; i < valuesArray.length(); i++) {
                embeddingList.add(valuesArray.getDouble(i));
            }

            return embeddingList;

        } catch (Exception e) {
            System.err.println("❌ Vektör oluşturulurken hata: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public double kosinusBenzerligiHesapla(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.isEmpty() || vectorB.isEmpty() || vectorA.size() != vectorB.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}