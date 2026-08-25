import { MapContainer, TileLayer, Marker } from 'react-leaflet';
import { createSmartMarker } from './smartMarker.js';
import KonumPopup from './KonumPopup.jsx';

const MERKEZ = [40.765, 29.940];
const ZOOM = 10;

const HaritaGorunumu = ({
                            gruplar,
                            selectedHaber,
                            setMapRef,
                            activePopupData,
                            onPopupClose,
                            onHaberSec,
                        }) => (
    <div className="map-section">
        <h2 style={{ padding: '15px 20px', color: '#667eea', borderBottom: '1px solid #eee' }}>
            🗺️ Harita Görünümü
        </h2>

        <MapContainer center={MERKEZ} zoom={ZOOM} className="map-container" ref={setMapRef}>
            <TileLayer
                url="https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}"
                attribution="© Google Maps"
            />

            {Object.keys(gruplar).map((key) => (
                <Marker
                    key={key}
                    position={[gruplar[key].lat, gruplar[key].lng]}
                    icon={createSmartMarker(key, gruplar[key], selectedHaber)}
                />
            ))}

            {activePopupData && (
                <KonumPopup
                    veri={activePopupData}
                    onClose={onPopupClose}
                    onHaberSec={onHaberSec}
                />
            )}
        </MapContainer>
    </div>
);

export default HaritaGorunumu;