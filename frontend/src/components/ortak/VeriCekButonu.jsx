const TEMEL_STIL = {
    width: '100%', padding: '12px', borderRadius: '8px', fontSize: '15px', fontWeight: 'bold',
    display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px',
    transition: 'all 0.5s ease-in-out',
};

const YESIL = {
    backgroundColor: '#27ae60', color: 'white', border: '2px solid #27ae60',
    cursor: 'pointer', boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
};

const DURUMLAR = {
    idle: {
        text: '🌐 İnternetten Yeni Veri Çek',
        disabled: false,
        stil: YESIL,
    },
    loading: {
        text: '⏳ Sistem Çalışıyor...',
        disabled: true,
        stil: { backgroundColor: '#f8f9fa', color: '#764ba2', border: '2px solid #764ba2', cursor: 'wait', boxShadow: 'none' },
    },
    success: {
        text: '✅ Veri Çekme İşlemi Bitti',
        disabled: true,
        stil: { backgroundColor: '#2ecc71', color: 'white', border: '2px solid #2ecc71', cursor: 'default', boxShadow: '0 0 15px rgba(46, 204, 113, 0.4)' },
    },
    ready: {
        text: '🔄 Yeniden Veri Çek',
        disabled: false,
        stil: YESIL,
    },
};

const VeriCekButonu = ({ durum, onClick }) => {
    const { text, disabled, stil } = DURUMLAR[durum] || DURUMLAR.idle;

    return (
        <button onClick={onClick} disabled={disabled} style={{ ...TEMEL_STIL, ...stil }}>
            {text}
        </button>
    );
};

export default VeriCekButonu;