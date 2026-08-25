import { useState, useCallback } from 'react';

const GORUNME_SURESI = 3000;
const CIKIS_SURESI = 300;

export const useDialog = () => {
    const [dialog, setDialog] = useState(null);

    const closeDialog = useCallback(() => {
        setDialog((prev) => (prev ? { ...prev, isExiting: true } : null));
        setTimeout(() => setDialog(null), CIKIS_SURESI);
    }, []);

    const showDialog = useCallback(
        (title, message, type = 'success') => {
            setDialog({ title, message, type, isExiting: false });
            setTimeout(() => closeDialog(), GORUNME_SURESI);
        },
        [closeDialog]
    );

    return { dialog, showDialog, closeDialog };
};