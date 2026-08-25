import { useState, useMemo } from 'react';
import { BOS_FILTRE } from '../constants/haberTurleri';
import { gunFarkiEsit } from '../utils/tarihFiltre';

export const useHaberFiltre = (haberler) => {
    const [filters, setFilters] = useState(BOS_FILTRE);

    const filtredHaberler = useMemo(() => {
        let filtered = haberler;

        if (filters.type) {
            filtered = filtered.filter((h) => h.haberTuru === filters.type);
        }

        if (filters.search) {
            const s = filters.search.toLowerCase();
            filtered = filtered.filter(
                (h) =>
                    h.baslik.toLowerCase().includes(s) ||
                    h.icerik.toLowerCase().includes(s)
            );
        }

        if (filters.location) {
            const k = filters.location.toLowerCase();
            filtered = filtered.filter((h) => h.konumMetni.toLowerCase().includes(k));
        }

        if (filters.date !== '' && filters.date !== 'Tüm Zamanlar') {
            const gunOnce = parseInt(filters.date, 10);
            filtered = filtered.filter((h) => gunFarkiEsit(h.yayinTarihi, gunOnce));
        }

        return filtered;
    }, [filters, haberler]);

    const filtreleriTemizle = () =>
        setFilters({ type: '', search: '', location: '', date: 'Tüm Zamanlar' });

    return { filters, setFilters, filtredHaberler, filtreleriTemizle };
};