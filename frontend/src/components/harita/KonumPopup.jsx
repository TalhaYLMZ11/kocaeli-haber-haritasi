import { Popup } from 'react-leaflet';
import { getTypeInfo } from '../../constants/haberTurleri';

const KonumPopup = ({ veri, onClose, onHaberSec }) => (
    <Popup position={[veri.lat, veri.lng]} onClose={onClose}>
        <div className="popup-multi-content">
            <h4 style={{ marginBottom: '8px', color: '#667eea', fontSize: '14px' }}>
                {veri.type} ({veri.haberler.length})
            </h4>
            <p style={{ fontSize: '11px', color: '#888', marginBottom: '5px' }}>
                📍 {veri.haberler[0].konumMetni}
            </p>
            <ul style={{ listStyleType: 'none', padding: 0, margin: 0, maxHeight: '150px', overflowY: 'auto', borderTop: '1px solid #eee', paddingTop: '5px' }}>
                {veri.haberler.map((h) => (
                    <li
                        key={h.id || h._id}
                        onClick={() => onHaberSec(h)}
                        style={{ cursor: 'pointer', color: '#333', marginBottom: '5px', padding: '5px', backgroundColor: '#f9f9f9', borderRadius: '4px', borderLeft: '3px solid #667eea' }}
                    >
            <span style={{ fontSize: '11px', fontWeight: 'bold', display: 'block', color: getTypeInfo(h.haberTuru).color }}>
              {getTypeInfo(h.haberTuru).emoji} {h.haberTuru}
            </span>
                        <span style={{ fontSize: '11px' }}>{h.baslik.substring(0, 40)}...</span>
                    </li>
                ))}
            </ul>
        </div>
    </Popup>
);

export default KonumPopup;