const AppHeader = () => (
    <header
        className="app-header"
        style={{ padding: '10px 20px', minHeight: 'auto', display: 'flex', flexDirection: 'column', alignItems: 'center' }}
    >
        <h1 style={{ fontSize: '22px', margin: '0 0 5px 0' }}>🗺️ Kocaeli Haber Haritası</h1>
        <p style={{ margin: 0, fontSize: '13px' }}>
            Kocaeli'deki güncel haberleri harita ve liste görünümünde izleyin
        </p>
    </header>
);

export default AppHeader;