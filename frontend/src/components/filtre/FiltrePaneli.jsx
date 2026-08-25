import { useState } from 'react';
import { HABER_TURLERI, TARIH_SECENEKLERI } from '../../constants/haberTurleri';
import './filtre.css';

const PANEL_GENISLIK = 340;

const FiltrePaneli = ({ filters, setFilters, onTemizle }) => {
    const [isHovered, setIsHovered] = useState(false);
    const [isMobileOpen, setIsMobileOpen] = useState(false);

    const acik = isHovered || isMobileOpen;

    const alanDegistir = (alan) => (e) =>
        setFilters({ ...filters, [alan]: e.target.value });

    return (
        <div
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            style={{
                position: 'fixed', top: 0, left: 0, height: '100vh', zIndex: 9999,
                display: 'flex', alignItems: 'center',
                transform: acik ? 'translateX(0)' : `translateX(-${PANEL_GENISLIK}px)`,
                transition: 'transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1)',
            }}
        >
            <div style={{
                width: `${PANEL_GENISLIK}px`, height: '100vh',
                backgroundColor: 'rgba(255, 255, 255, 0.98)',
                boxShadow: '10px 0 30px rgba(0,0,0,0.2)',
                padding: '30px 20px', overflowY: 'auto', borderRight: '1px solid #eee',
            }}>
                <h2 style={{ color: '#667eea', marginBottom: '20px', borderBottom: '2px solid #f0f0f0', paddingBottom: '10px', fontSize: '20px' }}>
                    🔍 Detaylı Filtreler
                </h2>

                <div style={{ marginBottom: '15px' }}>
                    <label className="filter-label">Tarih:</label>
                    <select
                        className="custom-modern-input custom-modern-select"
                        value={filters.date}
                        onChange={alanDegistir('date')}
                    >
                        {TARIH_SECENEKLERI.map((s) => (
                            <option key={s.deger} value={s.deger}>{s.etiket}</option>
                        ))}
                    </select>
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label className="filter-label">Haber Türü:</label>
                    <select
                        className="custom-modern-input custom-modern-select"
                        value={filters.type}
                        onChange={alanDegistir('type')}
                    >
                        <option value="">Tümü</option>
                        {HABER_TURLERI.map((t) => (
                            <option key={t.etiket} value={t.etiket}>
                                {t.emoji} {t.etiket}
                            </option>
                        ))}
                    </select>
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label className="filter-label">Konum:</label>
                    <input
                        className="custom-modern-input"
                        type="text"
                        placeholder="İlçe veya mahalle adı..."
                        value={filters.location}
                        onChange={alanDegistir('location')}
                    />
                </div>

                <div style={{ marginBottom: '20px' }}>
                    <label className="filter-label">Arama:</label>
                    <input
                        className="custom-modern-input"
                        type="text"
                        placeholder="Başlık veya içerik ara..."
                        value={filters.search}
                        onChange={alanDegistir('search')}
                    />
                </div>

                <button className="btn-temizle" onClick={onTemizle}>
                    Filtreleri Temizle
                </button>
            </div>

            <div
                onClick={() => setIsMobileOpen(!isMobileOpen)}
                style={{
                    width: '45px', height: '130px',
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white',
                    borderRadius: '0 16px 16px 0', display: 'flex', alignItems: 'center', justifyContent: 'center',
                    cursor: 'pointer', boxShadow: '5px 0 15px rgba(0, 0, 0, 0.2)',
                    paddingLeft: isHovered ? '10px' : '0', transition: 'padding-left 0.2s ease',
                }}
            >
        <span style={{ writingMode: 'vertical-rl', transform: 'rotate(180deg)', fontWeight: 700, letterSpacing: '3px', fontSize: '15px' }}>
          FİLTRELER
        </span>
            </div>
        </div>
    );
};

export default FiltrePaneli;