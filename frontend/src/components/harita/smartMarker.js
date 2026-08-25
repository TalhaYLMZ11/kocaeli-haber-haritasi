import L from 'leaflet';
import { getTypeInfo } from '../../constants/haberTurleri';

const PIN_BOYUT = 40;
const DAL_YARICAP = 55;

export const createSmartMarker = (baseKey, groupData, selectedHaber) => {
    const typesList = Object.keys(groupData.byType);
    const isMixed = typesList.length > 1;
    const totalNews = groupData.total;

    const isSelectedGroup =
        selectedHaber &&
        selectedHaber.enlem === groupData.lat &&
        selectedHaber.boylam === groupData.lng;

    const mainEmoji = isMixed ? '📌' : getTypeInfo(typesList[0]).emoji;
    const mainColor = isMixed ? '#2c3e50' : getTypeInfo(typesList[0]).color;
    const mainClickTarget = isMixed ? 'ALL' : typesList[0];

    const mainPinSelectedStyle =
        !isMixed && isSelectedGroup
            ? 'box-shadow: 0 0 0 6px rgba(241, 196, 15, 0.6); border-color: #f1c40f;'
            : '';

    const forceOpenStyle =
        isMixed && isSelectedGroup
            ? 'opacity: 1 !important; visibility: visible !important;'
            : '';

    let html = `
  <div class="spider-wrap" style="position: relative; width: 40px; height: 40px;">
    <style>
      .spider-wrap .s-main-pin {
        position: absolute; top: 0; left: 0; width: 40px; height: 40px;
        background: white; border-radius: 50%; display: flex; align-items: center; justify-content: center;
        font-size: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.4); cursor: pointer; 
        z-index: 100; /* ANA PİN EN ÜSTTE */
        transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); border: 3px solid ${mainColor}; pointer-events: auto;
      }
      .spider-wrap .s-main-pin:hover { transform: scale(1.15); z-index: 200; }
      
      .spider-wrap .s-badge {
        position: absolute; top: -6px; right: -6px; background: #e74c3c; color: white;
        border-radius: 50%; width: 22px; height: 22px; font-size: 12px; font-weight: bold;
        display: flex; align-items: center; justify-content: center; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);
      }
      
      .spider-wrap .s-branches {
        position: absolute; top: 0; left: 0; width: 40px; height: 40px;
        opacity: 0; visibility: hidden; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); 
        z-index: 90; /* DALLAR ANA PİNİN BİR ALTINDA */
      }
      .spider-wrap .s-main-pin:hover ~ .s-branches,
      .spider-wrap .s-branches:hover { opacity: 1; visibility: visible; }
      
      .spider-wrap .s-sub-pin {
        position: absolute; width: 34px; height: 34px; background: white; border-radius: 50%;
        display: flex; align-items: center; justify-content: center; font-size: 16px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.3); cursor: pointer; pointer-events: auto;
        transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        z-index: 10; /* ALT PİNLER ÇİZGİLERİN ÜSTÜNDE */
      }
      .spider-wrap .s-sub-pin:hover { transform: scale(1.2); z-index: 110; }
    </style>

    <div class="s-main-pin" style="${mainPinSelectedStyle}" onclick="window.dispatchEvent(new CustomEvent('onSmartPinClick', { detail: { key: '${baseKey}', type: '${mainClickTarget}' } })); event.stopPropagation();">
      ${mainEmoji}
      ${totalNews > 1 ? `<span class="s-badge">${totalNews}</span>` : ''}
    </div>
  `;

    if (isMixed) {
        html += `<div class="s-branches" style="${forceOpenStyle}">`;
        html += `<div style="position: absolute; top: 0px; left: -40px; width: 120px; height: 90px; background: transparent; pointer-events: auto;"></div>`;

        html += `<svg width="40" height="40" style="position: absolute; top: 0; left: 0; overflow: visible; pointer-events: none; z-index: 1;">`;
        typesList.forEach((type, i) => {
            const { tx, ty } = dalKoordinati(typesList.length, i);
            const info = getTypeInfo(type);
            html += `<line x1="20" y1="20" x2="${tx}" y2="${ty}" stroke="${info.color}" stroke-width="3" />`;
        });
        html += `</svg>`;

        typesList.forEach((type, i) => {
            const { tx, ty } = dalKoordinati(typesList.length, i);
            const info = getTypeInfo(type);
            const count = groupData.byType[type].length;

            const px = tx - 17;
            const py = ty - 17;

            const isSelectedType = isSelectedGroup && selectedHaber.haberTuru === type;
            const subPinSelectedStyle = isSelectedType
                ? 'box-shadow: 0 0 0 4px rgba(241, 196, 15, 0.6) !important; border-color: #f1c40f !important;'
                : '';

            html += `
        <div class="s-sub-pin" style="top: ${py}px; left: ${px}px; border: 2px solid ${info.color}; ${subPinSelectedStyle}"
             onclick="window.dispatchEvent(new CustomEvent('onSmartPinClick', { detail: { key: '${baseKey}', type: '${type}' } })); event.stopPropagation();">
          ${info.emoji}
          <span class="s-badge" style="width: 18px; height: 18px; font-size: 10px; top: -6px; right: -6px; background: ${info.color};">${count}</span>
        </div>
      `;
        });
        html += `</div>`;
    }
    html += `</div>`;

    return L.divIcon({
        html,
        className: 'tamamen-rastgele-bir-isim',
        iconSize: [PIN_BOYUT, PIN_BOYUT],
        iconAnchor: [PIN_BOYUT / 2, PIN_BOYUT / 2],
        popupAnchor: [0, -PIN_BOYUT / 2],
    });
};

function dalKoordinati(toplam, i) {
    const angleDeg = 90 - (toplam - 1) * 25 + i * 50;
    const angleRad = angleDeg * (Math.PI / 180);
    return {
        tx: 20 + Math.cos(angleRad) * DAL_YARICAP,
        ty: 20 + Math.sin(angleRad) * DAL_YARICAP,
    };
}