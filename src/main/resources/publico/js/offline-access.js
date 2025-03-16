if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/publico/js/service-worker.js')
        .then(reg => {
            console.log("✅ Service Worker registrado correctamente.");

            if ('SyncManager' in window) {
                navigator.serviceWorker.ready.then(swRegistration => {
                    return localforage.getItem('surveyData').then(encuestas => {
                        if (encuestas && encuestas.length > 0) {
                            return swRegistration.sync.register('syncEncuestas');
                        }
                    });
                });
            }
        })
        .catch(err => console.error("❌ Error registrando el Service Worker:", err));
}

// ✅ Cuando se recupere la conexión, sincronizar datos pendientes
window.addEventListener('online', function() {
    console.log("🔄 Conexión restaurada, sincronizando encuestas pendientes...");
    syncEncuestas();
});
