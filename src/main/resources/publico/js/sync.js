document.addEventListener('DOMContentLoaded', function () {
    if ('serviceWorker' in navigator && 'SyncManager' in window) {
        navigator.serviceWorker.ready.then(swRegistration => {
            return localforage.getItem('surveyData').then(encuestas => {
                if (encuestas && encuestas.length > 0) {
                    return swRegistration.sync.register('syncEncuestas');
                }
            });
        });
    }
});
