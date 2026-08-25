export const haberleriGruplandir = (haberler) => {
    const gruplar = {};

    haberler.forEach((h) => {
        if (!h.enlem || !h.boylam) return;

        const anahtar = `${h.enlem}_${h.boylam}`;
        if (!gruplar[anahtar]) {
            gruplar[anahtar] = { lat: h.enlem, lng: h.boylam, total: 0, byType: {} };
        }

        const turAnahtari = h.haberTuru || 'Diğer';
        if (!gruplar[anahtar].byType[turAnahtari]) {
            gruplar[anahtar].byType[turAnahtari] = [];
        }

        gruplar[anahtar].byType[turAnahtari].push(h);
        gruplar[anahtar].total += 1;
    });

    return gruplar;
};