import { useEffect } from 'react';

const GECIKME = 250;
const ODAK_ZOOM = 14;

export const useHaritaOdakla = (mapRef, selectedHaber) => {
    useEffect(() => {
        if (!mapRef) return undefined;

        const timer = setTimeout(() => {
            mapRef.invalidateSize();

            if (selectedHaber && selectedHaber.enlem && selectedHaber.boylam) {
                mapRef.flyTo([selectedHaber.enlem, selectedHaber.boylam], ODAK_ZOOM, {
                    animate: true,
                    duration: 1.5,
                });
            }
        }, GECIKME);

        return () => clearTimeout(timer);
    }, [selectedHaber, mapRef]);
};