import { useState, useEffect, useMemo } from 'react';
import './App.css';
import './utils/leafletSetup';

import { useHaberler } from './hooks/useHaberler';
import { useFinans } from './hooks/useFinans';
import { useHaberFiltre } from './hooks/useHaberFiltre';
import { useDialog } from './hooks/useDialog';
import { useHaritaOdakla } from './hooks/useHaritaOdakla';
import { useSmartPinClick } from './hooks/useSmartPinClick';
import { haberleriGruplandir } from './utils/haberGruplama';

import AppHeader from './components/layout/AppHeader';
import FiltrePaneli from './components/filtre/FiltrePaneli';
import BildirimDialog from './components/ortak/BildirimDialog';
import VeriCekButonu from './components/ortak/VeriCekButonu';
import HaberListesi from './components/haber/HaberListesi';
import HaberDetay from './components/haber/HaberDetay';
import HaritaGorunumu from './components/harita/HaritaGorunumu';
import SonDakikaSeridi from './components/serit/SonDakikaSeridi';

const App = () => {
  const { haberler, loading, yenidenTara } = useHaberler();
  const { filters, setFilters, filtredHaberler, filtreleriTemizle } = useHaberFiltre(haberler);
  const finans = useFinans();
  const { dialog, showDialog, closeDialog } = useDialog();

  const [selectedHaber, setSelectedHaber] = useState(null);
  const [mapRef, setMapRef] = useState(null);
  const [buttonState, setButtonState] = useState('idle');

  const gruplar = useMemo(() => haberleriGruplandir(filtredHaberler), [filtredHaberler]);
  const { activePopupData, setActivePopupData } = useSmartPinClick(gruplar);

  useHaritaOdakla(mapRef, selectedHaber);

  useEffect(() => {
    setActivePopupData(null);
  }, [filters, haberler, setActivePopupData]);

  const handleScrapeData = async () => {
    setButtonState('loading');
    try {
      await yenidenTara();
      setButtonState('success');
      showDialog(
          'İşlem Tamamlandı!',
          'Haber ajansları tarandı ve yeni veriler başarıyla aktarıldı.',
          'success'
      );
      setTimeout(() => setButtonState('ready'), 3000);
    } catch (error) {
      console.error('Scraping işlemi başarısız:', error);
      showDialog('Bağlantı Hatası', 'Veri çekilirken bir hata oluştu.', 'error');
      setButtonState('ready');
    }
  };

  return (
      <div className="app-container">
        <FiltrePaneli
            filters={filters}
            setFilters={setFilters}
            onTemizle={filtreleriTemizle}
        />

        <BildirimDialog dialog={dialog} onClose={closeDialog} />

        <AppHeader />

        <div className="app-layout">
          <aside className="sidebar">
            <div style={{ marginBottom: '20px' }}>
              <VeriCekButonu durum={buttonState} onClick={handleScrapeData} />
            </div>

            <HaberListesi
                haberler={filtredHaberler}
                loading={loading}
                selectedHaber={selectedHaber}
                onHaberSec={setSelectedHaber}
            />
          </aside>

          <main className="main-content">
            <HaritaGorunumu
                gruplar={gruplar}
                selectedHaber={selectedHaber}
                setMapRef={setMapRef}
                activePopupData={activePopupData}
                onPopupClose={() => setActivePopupData(null)}
                onHaberSec={setSelectedHaber}
            />

            <HaberDetay haber={selectedHaber} onClose={() => setSelectedHaber(null)} />
          </main>
        </div>

        <SonDakikaSeridi
            haberler={filtredHaberler}
            finans={finans}
            onHaberSec={setSelectedHaber}
        />
      </div>
  );
};

export default App;