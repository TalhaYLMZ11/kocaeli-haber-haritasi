import FinansKutusu from './FinansKutusu.jsx';
import './serit.css';

const SonDakikaSeridi = ({ haberler, finans, onHaberSec }) => (
    <div style={{
        position: 'fixed', bottom: 0, left: 0, width: '100vw', height: '40px',
        background: '#1a202c', color: 'white', display: 'flex', alignItems: 'center',
        fontFamily: 'sans-serif', fontSize: '13px', zIndex: 99999, overflow: 'hidden',
    }}>
        <div style={{
            background: 'linear-gradient(135deg, #e52d27 0%, #b31217 100%)',
            padding: '0 25px', height: '100%', display: 'flex', alignItems: 'center',
            justifyContent: 'center', fontWeight: '800', fontSize: '14px', zIndex: 10,
            boxShadow: '4px 0 15px rgba(0,0,0,0.4)', letterSpacing: '2px', gap: '10px',
            borderRight: '1px solid rgba(255,255,255,0.1)',
        }}>
            <div style={{
                width: '8px', height: '8px', backgroundColor: '#fff',
                borderRadius: '50%', animation: 'live-pulse 1.5s infinite',
            }}></div>
            SON DAKİKA
        </div>

        <div className="t-news-container" style={{ flex: 1, overflow: 'hidden', position: 'relative', height: '100%' }}>
            <div className="t-news-track">
                {haberler && haberler.length > 0 ? (
                    haberler.map((haber) => (
                        <div
                            key={haber.id || haber._id}
                            className="t-item"
                            onClick={() => onHaberSec(haber)}
                        >
                            ⚡ {haber.baslik}
                        </div>
                    ))
                ) : (
                    <div className="t-item">Gösterilecek haber bulunamadı...</div>
                )}
            </div>
        </div>

        <FinansKutusu finans={finans} />
    </div>
);

export default SonDakikaSeridi;