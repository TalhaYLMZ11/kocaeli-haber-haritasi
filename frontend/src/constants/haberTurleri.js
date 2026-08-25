export const HABER_TURLERI = [
    { ad: 'Trafik Kazasi',        etiket: 'Trafik Kazası',        emoji: '🚗', renk: '#e74c3c' },
    { ad: 'Yangin',               etiket: 'Yangın',               emoji: '🔥', renk: '#e67e22' },
    { ad: 'Kulturel Etkinlikler', etiket: 'Kültürel Etkinlikler', emoji: '🎭', renk: '#9b59b6' },
    { ad: 'Hirsizlik',            etiket: 'Hırsızlık',            emoji: '🚨', renk: '#34495e' },
    { ad: 'Elektrik Kesintisi',   etiket: 'Elektrik Kesintisi',   emoji: '⚡', renk: '#f1c40f' },
];

export const VARSAYILAN_TUR = { emoji: '📰', renk: '#667eea' };

export const getTypeInfo = (type) => {
    const bulunan = HABER_TURLERI.find((t) => t.etiket === type);
    if (!bulunan) return { emoji: VARSAYILAN_TUR.emoji, color: VARSAYILAN_TUR.renk };
    return { emoji: bulunan.emoji, color: bulunan.renk };
};

export const TARIH_SECENEKLERI = [
    { deger: 'Tüm Zamanlar', etiket: 'Tüm Zamanlar' },
    { deger: '0',            etiket: 'Bugün' },
    { deger: '1',            etiket: 'Dün' },
    { deger: '2',            etiket: '2 Gün Önce' },
];

export const BOS_FILTRE = { type: '', search: '', location: '', date: '' };