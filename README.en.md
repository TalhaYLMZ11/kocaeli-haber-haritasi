# Kocaeli News Map

*[Türkçe](README.md) · English*

A full-stack application that automatically collects news from local news sites, classifies it by category, extracts location names from Turkish text, converts them into coordinates and displays everything on an interactive map.

Built as the first project of the Software Laboratory II course, Computer Engineering, Kocaeli University.

![Application screen](docs/ekran.png)

## How it works

```
1. ScrapingService        →  news site HTML is parsed with jsoup
2. ClassificationService  →  each article is assigned to a category
3. LocationService        →  district / neighbourhood names are extracted from Turkish text
4. GeocodingService       →  coordinates are resolved via the Google Maps API
5. EmbeddingService       →  a 768-dimensional vector is generated with Google Gemini
6. Cosine similarity      →  the same story published on another site is detected
7. MongoDB                →  a new article is inserted, or a source is appended to an existing record
```

When several outlets cover the same event, the story is not duplicated: a single record is kept with multiple sources listed under it.

## Requirements

- Java 17+
- Node.js 18+
- MongoDB
- Google Maps API key (Geocoding API must be enabled)
- Google Gemini API key

## Setup

### 1. Environment variables

Copy `.env.example` in the project root to `.env` and fill in your own keys:

```
MONGODB_URI=mongodb://localhost:27017/kentsel_haber_db
GEMINI_API_KEY=your-gemini-key-here
GOOGLE_MAPS_API_KEY=your-maps-key-here
```

`application.properties` reads these as environment variables; no keys are stored in the file.

**Important:** Spring Boot does not read the `.env` file on its own. The values have to be exposed to the runtime environment.

In IntelliJ IDEA, paste them semicolon-separated into `Run > Edit Configurations > Proje1Application > Environment variables`.

From the terminal (PowerShell):

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/kentsel_haber_db"
$env:GEMINI_API_KEY="..."
$env:GOOGLE_MAPS_API_KEY="..."
```

Do the same for the frontend: copy `frontend/.env.example` to `frontend/.env`. Vite reads this file automatically, no extra configuration needed.

### 2. Backend

```bash
./mvnw spring-boot:run          # Linux / macOS
.\mvnw.cmd spring-boot:run      # Windows
```

On startup the application begins collecting data and prints its progress to the console:

```
[NEW RECORD]
 ├─ Type   : Traffic Accident
 ├─ Location : Kocaeli / Gölcük / Tepe Mahallesi
 ├─ GPS    : (40.7746943, 30.0026651)
 └─ Source : Bizim Yaka Kocaeli
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

## Project structure

```
proje_1/
├── pom.xml
├── .env.example
│
├── src/main/java/tr/edu/kocaeli/proje_1/
│   ├── Proje1Application.java
│   ├── config/
│   │   └── CorsConfig.java              CORS configuration
│   ├── controller/
│   │   ├── HaberController.java         news endpoints
│   │   ├── ScrapingController.java      triggers a crawl
│   │   └── FinansController.java        financial data
│   ├── service/
│   │   ├── ScrapingService.java         HTML parsing with jsoup
│   │   ├── ClassificationService.java   category assignment
│   │   ├── LocationService.java         location extraction from text
│   │   ├── GeocodingService.java        coordinate resolution
│   │   ├── EmbeddingService.java        vector generation
│   │   └── FinansService.java
│   ├── model/
│   │   ├── Haber.java                   news article
│   │   └── FinansVerisi.java            financial data point
│   └── repository/
│       ├── HaberRepository.java
│       └── FinansRepository.java
│
└── frontend/
    ├── .env.example
    └── src/
        ├── App.jsx                      composes the components
        ├── App.css
        │
        ├── api/                         all HTTP calls
        │   ├── client.js                axios instance, base URL
        │   ├── haberService.js
        │   └── finansService.js
        │
        ├── constants/
        │   └── haberTurleri.js          category, emoji and colour definitions
        │
        ├── utils/                       pure helper functions
        │   ├── leafletSetup.js
        │   ├── haberGruplama.js         groups articles sharing a coordinate
        │   ├── finansFormat.js
        │   └── tarihFiltre.js
        │
        ├── hooks/                       state and side-effect logic
        │   ├── useHaberler.js           loading articles and crawling
        │   ├── useHaberFiltre.js        category, search, location, date filters
        │   ├── useFinans.js             periodic financial updates
        │   ├── useDialog.js
        │   ├── useHaritaOdakla.js       flying to a selected article
        │   └── useSmartPinClick.js      map pin interactions
        │
        └── components/
            ├── layout/AppHeader.jsx
            ├── ortak/                   shared dialog and crawl button
            ├── filtre/                  filter panel + filtre.css
            ├── haber/                   news card, list and detail view
            ├── harita/                  map view, location popup, smart marker
            └── serit/                   breaking-news ticker, finance box + serit.css
```

Responsibilities in the frontend are split into layers: `api` talks to the server, `hooks` manage state, `components` only render. No component makes an HTTP call directly.

## Interface

**Map.** Articles sharing a coordinate are collapsed into a single pin. If the group contains one category, that category's icon is shown; if it contains several, a mixed pin is used, and hovering fans the categories out with the article count on each branch.

**Sidebar.** The filtered list of articles. Clicking one flies the map to its coordinate and opens a detail panel below.

**Filters.** A drawer on the left with category, location, free-text search and date filters.

**Bottom ticker.** Scrolling breaking-news headlines and live financial indicators. The ticker pauses on hover.

## API endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/haberler` | All articles |
| GET | `/api/haberler/{id}` | A single article |
| GET | `/api/haberler/turu/{turu}` | Filter by category |
| GET | `/api/haberler/harita/konumlu` | Only articles with coordinates |
| GET | `/api/haberler/istatistik` | Numeric summary |
| GET | `/api/scraping/cagdas` | Starts a new crawl |
| GET | `/api/finans` | Gold, BIST index, USD, EUR |

## Data model

```javascript
{
  "_id": "ObjectId",
  "haberTuru": "Trafik Kazası",
  "baslik": "İzmit'te trafik kazası...",
  "icerik": "...",
  "konumMetni": "Kocaeli / İzmit / Yeşilbahçe Mahallesi",
  "enlem": 40.7746943,
  "boylam": 30.0026651,
  "yayinTarihi": "2026-03-30T10:30:00",
  "embedding": [0.123, 0.456, "..."],
  "kaynaklar": [
    { "siteAdi": "Özgür Kocaeli",  "url": "https://..." },
    { "siteAdi": "Çağdaş Kocaeli", "url": "https://..." }
  ]
}
```

## Tech stack

Spring Boot, MongoDB, jsoup, Google Maps Geocoding API, Google Gemini API, Maven

React, Vite, Leaflet, react-leaflet, Axios

## Troubleshooting

**`Could not resolve placeholder 'MONGODB_URI'`** — Environment variables are not set. See the setup step above.

**Empty map, no articles** — Check that the backend is running on `localhost:8080`, look for CORS errors in the browser console, and verify `VITE_API_URL` in `frontend/.env`.

**Geocoding returns `REQUEST_DENIED`** — The Geocoding API is not enabled in the Google Cloud Console, or a key restriction is blocking the request.

**MongoDB connection error** — Check that the MongoDB service is running and the connection string is correct.

## Notes

The user interface and the collected content are in Turkish, as the project targets Turkish local news sources. The location extraction step is built specifically around Turkish place names and their inflected forms.

This project was developed for educational purposes. News content belongs to the respective news outlets and is always displayed with a link back to the original source.
