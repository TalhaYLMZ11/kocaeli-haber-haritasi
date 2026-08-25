const BildirimDialog = ({ dialog, onClose }) => {
    if (!dialog) return null;

    return (
        <div className={`dialog-overlay ${dialog.isExiting ? 'fade-out' : ''}`}>
            <div className={`modern-dialog ${dialog.type} ${dialog.isExiting ? 'fade-out' : ''}`}>
                <button className="dialog-close-btn" onClick={onClose}>✕</button>
                <div className="dialog-icon">{dialog.type === 'success' ? '✅' : '⚠️'}</div>
                <h3 className="dialog-title">{dialog.title}</h3>
                <p className="dialog-message">{dialog.message}</p>
                <div className="dialog-progress-container">
                    <div className="dialog-progress-bar"></div>
                </div>
            </div>
        </div>
    );
};

export default BildirimDialog;