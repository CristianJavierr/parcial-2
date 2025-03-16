const CACHE_NAME = 'app-cache-v1';
// Define las rutas que deseas cachear.
// Recuerda que estas rutas deben haber sido visitadas mientras estabas online para tener contenido actualizado.
const urlsToCache = [
    '/',                          // Página principal (listar encuestas)
    '/encuestas/crear',           // Formulario de creación
    '/Encuesta/Pendientes',       // Ejemplo de otra página
    '/templates/listar-encuestas.html', // Plantilla si la sirves como recurso estático (si aplica)
    // Agrega más rutas o recursos (CSS, JS, imágenes, etc.) que quieras tener disponibles offline
];

// Durante la instalación del Service Worker, se cachean las rutas definidas.
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => {
                console.log('Abriendo caché e instalando archivos');
                return cache.addAll(urlsToCache);
            })
    );
    self.skipWaiting();
});

// En la activación, se toma control inmediato de la página
self.addEventListener('activate', event => {
    event.waitUntil(self.clients.claim());
});

// Estrategia de fetch: si falla la conexión, se intenta servir la versión cacheada e inyectar un banner.
self.addEventListener('fetch', event => {
    if (event.request.mode === 'navigate') {
        event.respondWith(
            fetch(event.request)
                .then(response => {
                    // Si la respuesta es correcta, se puede actualizar la caché de forma dinámica (opcional)
                    return response;
                })
                .catch(() => {
                    // Si falla la conexión, se busca en la caché
                    return caches.match(event.request)
                        .then(response => {
                            if (response) {
                                // Leer el contenido cacheado en texto
                                return response.text().then(text => {
                                    // Inyectar el banner de offline justo después de <body>
                                    const offlineBanner = `<div id="offline-notification" style="position: fixed; top: 0; left: 0; width: 100%; background-color: #ff4444; color: #fff; text-align: center; padding: 10px; z-index: 9999;">Estás offline. La aplicación no tiene conexión a Internet.</div>`;
                                    const modifiedText = text.replace(/<body([^>]*)>/i, `<body$1>${offlineBanner}`);
                                    return new Response(modifiedText, {
                                        headers: response.headers
                                    });
                                });
                            }
                            // Si no hay respuesta en la caché, se devuelve un fallback básico
                            return new Response(
                                `<!doctype html>
                                <html lang="es">
                                  <head>
                                    <meta charset="UTF-8">
                                    <title>Offline</title>
                                  </head>
                                  <body>
                                    <div id="offline-notification" style="position: fixed; top: 0; left: 0; width: 100%; background-color: #ff4444; color: #fff; text-align: center; padding: 10px; z-index: 9999;">Estás offline. La aplicación no tiene conexión a Internet.</div>
                                    <h1>Contenido no disponible</h1>
                                    <p>Lo sentimos, la página no se pudo cargar.</p>
                                  </body>
                                </html>`,
                                { headers: { 'Content-Type': 'text/html' } }
                            );
                        });
                })
        );
    } else {
        // Para otros tipos de solicitudes (CSS, JS, etc.) se intenta la red, pero se usa la caché como respaldo.
        event.respondWith(
            fetch(event.request).catch(() => caches.match(event.request))
        );
    }
});
