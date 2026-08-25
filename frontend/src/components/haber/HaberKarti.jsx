const BASLIK_UZUNLUK = 50;

const HaberKarti = ({ haber, aktif, onClick }) => (
    <div className={`haber-card ${aktif ? 'active' : ''}`} onClick={onClick}>
        <div className="haber-type-badge">{haber.haberTuru}</div>
        <h3>{haber.baslik.substring(0, BASLIK_UZUNLUK)}...</h3>
        <p className="haber-location">📍 {haber.konumMetni}</p>
        {haber.enlem && haber.boylam && <span className="gps-indicator">✅ GPS</span>}
    </div>
);

export default HaberKarti;