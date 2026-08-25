import { useState, useEffect } from 'react';
import { finansGetir, dovizGetir } from '../api/finansService';

const BASLANGIC = {
    gram: 'Yükleniyor...',
    bist: 'Yükleniyor...',
    usd: 'Yükleniyor...',
    eur: 'Yükleniyor...',
};

const FINANS_ARALIK = 60000;   // 1 dakika
const DOVIZ_ARALIK = 300000;   // 5 dakika

export const useFinans = () => {
    const [finans, setFinans] = useState(BASLANGIC);

    useEffect(() => {
        const cek = async () => {
            try {
                const data = await finansGetir();
                setFinans((prev) => ({
                    ...prev,
                    gram: data.gramAltin || '0', gramYon: data.gramYon || 'eq',
                    bist: data.bist100 || '0',   bistYon: data.bistYon || 'eq',
                    usd: data.usd || '0',        usdYon: data.usdYon || 'eq',
                    eur: data.eur || '0',        eurYon: data.eurYon || 'eq',
                }));
            } catch (error) {
                console.error('Finans API hatası:', error);
            }
        };

        cek();
        const interval = setInterval(cek, FINANS_ARALIK);
        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        const cek = async () => {
            try {
                const { usd, eur } = await dovizGetir();
                setFinans((prev) => ({ ...prev, usd, eur }));
            } catch (error) {
                console.error('Döviz kurları çekilemedi:', error);
                setFinans((prev) => ({ ...prev, usd: 'Hata', eur: 'Hata' }));
            }
        };

        cek();
        const interval = setInterval(cek, DOVIZ_ARALIK);
        return () => clearInterval(interval);
    }, []);

    return finans;
};