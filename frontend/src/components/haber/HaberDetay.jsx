const kaynakStili = {
    color: '#667eea', fontWeight: 'bold', textDecoration: 'none', fontSize: '13px',
    display: 'block', backgroundColor: '#f0f2ff', padding: '4px 8px',
    borderRadius: '4px', borderLeft: '3px solid #667eea',
};

const HaberDetay = ({ haber, onClose }) => {
    if (!haber) return null;

    return (
        <div className="detail-section">
            <div className="detail-header" style={{ padding: '10px 20px', minHeight: 'auto' }}>
                <h2 style={{ fontSize: '18px', margin: 0 }}>{haber.baslik}</h2>
                <button className="close-btn" onClick={onClose}>✕</button>
            </div>

            <div className="detail-info">
                <div className="info-row">
                    <span className="info-label">📋 Tür:</span>
                    <span className="info-value">{haber.haberTuru}</span>
                </div>

                <div className="info-row">
                    <span className="info-label">📍 Konum:</span>
                    <span className="info-value">{haber.konumMetni}</span>
                </div>

                {haber.kaynaklar && haber.kaynaklar.length > 0 && (
                    <div className="info-row" style={{ alignItems: 'flex-start' }}>
                        <span className="info-label">🔗 Kaynaklar:</span>
                        <div className="info-value" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            {haber.kaynaklar.map((kaynak, index) => (
                                <a
                                    key={index}
                                    href={kaynak.url}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    style={kaynakStili}
                                >
                                    {kaynak.siteAdi || 'Haber Kaynağı'} ↗
                                </a>
                            ))}
                        </div>
                    </div>
                )}

                {haber.enlem && haber.boylam && (
                    <div className="info-row">
                        <span className="info-label">🧭 GPS Koordinatları:</span>
                        <span className="info-value">
              {haber.enlem.toFixed(6)}, {haber.boylam.toFixed(6)}
                            <a
                                href={`https://www.google.com/maps/search/?api=1&query=${haber.enlem},${haber.boylam}`}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="google-maps-link"
                            >
                Google Maps'te Aç
              </a>
            </span>
                    </div>
                )}

                <div className="info-row">
                    <span className="info-label">📅 Yayın Tarihi:</span>
                    <span className="info-value">
            {new Date(haber.yayinTarihi).toLocaleDateString('tr-TR')}
          </span>
                </div>
            </div>

            <div className="detail-content">
                <h3>📄 İçerik</h3>
                <p>{haber.icerik}</p>
            </div>
        </div>
    );
};

export default HaberDetay;