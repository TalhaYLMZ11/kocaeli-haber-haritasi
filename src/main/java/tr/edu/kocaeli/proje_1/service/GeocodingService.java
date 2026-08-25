package tr.edu.kocaeli.proje_1.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class GeocodingService {

    @Value("${google.maps.api.key}")
    private String mapsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Double> konumuGeocode(String konum) {
        if (konum == null || konum.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = konum.split("/");
            StringBuilder reversedAddress = new StringBuilder();

            for (int i = parts.length - 1; i >= 0; i--) {
                String parca = parts[i].trim();

                if (parca.contains("Mahallesi") && !parca.contains(",")) {
                    parca = parca.replace("Mahallesi ", "Mahallesi, ");
                } else if (parca.contains("Mah.") && !parca.contains(",")) {
                    parca = parca.replace("Mah. ", "Mahallesi, ");
                }

                reversedAddress.append(parca);
                if (i > 0) {
                    reversedAddress.append(", ");
                }
            }

            String searchQuery = reversedAddress.toString();

            Locale trLocale = Locale.forLanguageTag("tr-TR");
            String lowerSearchQuery = searchQuery.toLowerCase(trLocale);

            if (!lowerSearchQuery.contains("kocaeli")) {
                searchQuery += ", Kocaeli";
            }
            if (!lowerSearchQuery.contains("turkey") && !lowerSearchQuery.contains("türkiye")) {
                searchQuery += ", Turkey";
            }

            System.out.println("   🔍 Geocoding Sorgusu: " + searchQuery);

            String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}&language=tr&region=tr";

            String response = restTemplate.getForObject(apiUrl, String.class, searchQuery, mapsApiKey);

            if (response == null || response.isEmpty()) {
                System.out.println("   ⚠️  Geocoding: Boş response");
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            if (status.equals("OVER_QUERY_LIMIT")) {
                System.out.println("   ⚠️  Geocoding: API Limitine Takıldın");
                return null;
            }

            if (status.equals("REQUEST_DENIED")) {
                System.out.println("   ⚠️  Geocoding: API Key izni yok");
                return null;
            }

            if (!status.equals("OK")) {
                System.out.println("   ⚠️  Geocoding Status: " + status);
                return null;
            }

            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() == 0) return null;

            JSONObject firstResult = results.getJSONObject(0);
            JSONObject geometry = firstResult.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            boolean isPartialMatch = firstResult.optBoolean("partial_match", false);
            String locationType = geometry.optString("location_type", "");

            if (isPartialMatch) {
                System.out.println("   ⚠️  Geocoding: Eksik eşleşme (Partial Match). Adres tam doğrulanamadı, reddediliyor.");
                return null;
            }

            double enlem = location.getDouble("lat");
            double boylam = location.getDouble("lng");

            Map<String, Double> result = new HashMap<>();
            result.put("enlem", enlem);
            result.put("boylam", boylam);

            System.out.println("   ✅ Geocoding OK (" + locationType + "): (" + enlem + ", " + boylam + ")");
            return result;

        } catch (Exception e) {
            System.out.println("   ❌ Geocoding Exception: " + e.getMessage());
            return null;
        }
    }
}