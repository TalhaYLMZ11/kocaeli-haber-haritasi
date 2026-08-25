import axios from 'axios';
import { api } from './client';

export const finansGetir = async () => {
    const response = await api.get('/api/finans');
    return response.data;
};

export const dovizGetir = async () => {
    const response = await axios.get('https://open.er-api.com/v6/latest/USD');
    const kurlar = response.data.rates;
    return {
        usd: kurlar.TRY.toFixed(2),
        eur: (kurlar.TRY / kurlar.EUR).toFixed(2),
    };
};