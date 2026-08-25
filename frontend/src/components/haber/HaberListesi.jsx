import HaberKarti from './HaberKarti.jsx';

const HaberListesi = ({ haberler, loading, selectedHaber, onHaberSec }) => {
    const icerik = () => {
        if (loading) return <p className="loading">Haberler yükleniyor...</p>;
        if (haberler.length === 0) return <p className="no-results">Sonuç bulunamadı</p>;

        return haberler.map((haber) => {
            const id = haber.id || haber._id;
            return (
                <HaberKarti
                    key={id}
                    haber={haber}
                    aktif={selectedHaber?.id === id}
                    onClick={() => onHaberSec(haber)}
                />
            );
        });
    };

    return (
        <div className="haberler-list-section" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            <h2 style={{ marginBottom: '15px', color: '#667eea' }}>
                📰 Haberler ({haberler.length})
            </h2>
            <div className="haberler-list" style={{ flex: 1, overflowY: 'auto' }}>
                {icerik()}
            </div>
        </div>
    );
};

export default HaberListesi;