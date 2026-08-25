import { getYonColor, getYonIcon } from '../../utils/finansFormat';

const KALEMLER = [
    { etiket: 'GRAM', deger: 'gram', yon: 'gramYon', birim: '₺' },
    { etiket: 'BIST', deger: 'bist', yon: 'bistYon', birim: '' },
    { etiket: 'USD',  deger: 'usd',  yon: 'usdYon',  birim: '₺' },
    { etiket: 'EUR',  deger: 'eur',  yon: 'eurYon',  birim: '₺' },
];

const FinansKutusu = ({ finans }) => (
    <div style={{
        display: 'flex', background: '#2c3e50', padding: '0 20px', height: '100%',
        alignItems: 'center', gap: '15px', zIndex: 10, boxShadow: '-2px 0 10px rgba(0,0,0,0.5)',
    }}>
        {KALEMLER.map((k) => (
            <div key={k.etiket}>
                {k.etiket}
                <span style={{ fontWeight: 'bold', marginLeft: '5px' }}>
          {finans[k.deger]}{k.birim}
        </span>
                <span style={{ color: getYonColor(finans[k.yon]), marginLeft: '4px' }}>
          {getYonIcon(finans[k.yon])}
        </span>
            </div>
        ))}
    </div>
);

export default FinansKutusu;