import { api } from './client';

export const haberleriGetir = async () => {
    const response = await api.get('/api/haberler');
    return response.data;
};

export const scrapingBaslat = async () => {
    await api.get('/api/scraping/cagdas');
};