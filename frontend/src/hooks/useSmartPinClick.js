import { useState, useEffect } from 'react';

export const useSmartPinClick = (gruplar) => {
    const [activePopupData, setActivePopupData] = useState(null);

    useEffect(() => {
        const handlePinClick = (e) => {
            const { key, type } = e.detail;
            const groupData = gruplar[key];
            if (!groupData) return;

            const newsToShow =
                type === 'ALL'
                    ? Object.values(groupData.byType).flat()
                    : groupData.byType[type] || [];

            setActivePopupData({
                lat: groupData.lat,
                lng: groupData.lng,
                type: type === 'ALL' ? 'Karma Konum' : type,
                haberler: newsToShow,
            });
        };

        window.addEventListener('onSmartPinClick', handlePinClick);
        return () => window.removeEventListener('onSmartPinClick', handlePinClick);
    }, [gruplar]);

    return { activePopupData, setActivePopupData };
};