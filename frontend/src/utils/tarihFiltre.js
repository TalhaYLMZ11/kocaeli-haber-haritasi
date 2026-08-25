export const gunFarkiEsit = (yayinTarihi, gunOnce) => {
    if (!yayinTarihi) return false;

    const bugun = new Date();
    bugun.setHours(0, 0, 0, 0);

    const hDate = new Date(yayinTarihi);
    hDate.setHours(0, 0, 0, 0);

    const fark = Math.round((bugun.getTime() - hDate.getTime()) / (1000 * 3600 * 24));
    return fark === gunOnce;
};