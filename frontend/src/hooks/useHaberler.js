import { useState, useEffect, useCallback } from 'react';
import { haberleriGetir, scrapingBaslat } from '../api/haberService';

export const useHaberler = () => {
    const [haberler, setHaberler] = useState([]);
    const [loading, setLoading] = useState(false);

    const yukle = useCallback(async () => {
        try {
            setLoading(true);
            const veri = await haberleriGetir();
            setHaberler(veri);
        } catch (error) {
            console.error('Hata:', error);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        yukle();
    }, [yukle]);

    const yenidenTara = useCallback(async () => {
        await scrapingBaslat();
        await yukle();
    }, [yukle]);

    return { haberler, loading, yukle, yenidenTara };
};