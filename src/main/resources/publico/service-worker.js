const CACHE_NAME = 'encuesta-cache-v9';

// Archivos a cachear
const URLS_TO_CACHE = [
    '/resources/listar-encuestas.html', // ✅ Página principal sin conexión
    '/login',
    '/Registro',
    '/encuestas/crear',
    '/Encuesta/Pendientes',
    '/publico/css/styles.css',
    '/publico/img/logo.png',
    'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css',
    'https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/localforage/1.10.0/localforage.min.js',
    'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css'
];

// ✅ Instalación y almacenamiento de archivos en caché
self.addEventListener('install', event => {
    console.log("📦 Instalando Service Worker y cacheando archivos...");

    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            console.log("📦 Cache abierta:", CACHE_NAME);
            return cache.addAll(URLS_TO_CACHE);
        }).catch(error => console.error("❌ Error cacheando archivos:", error))
    );
});

// ✅ Estrategia: Cargar desde la red primero, si falla usar caché
self.addEventListener('fetch', event => {
    console.log("🔍 Interceptando solicitud:", event.request.url);

    event.respondWith(
        fetch(event.request) // Intentamos obtener desde la red primero
            .then(networkResponse => {
                console.log("🌍 Cargando desde la red:", event.request.url);
                return caches.open(CACHE_NAME).then(cache => {
                    cache.put(event.request, networkResponse.clone()); // Actualizamos la caché
                    return networkResponse;
                });
            })
            .catch(() => {
                console.warn("⚠️ No hay conexión, intentando servir desde caché:", event.request.url);
                return caches.match(event.request) || caches.match('/resources/listar-encuestas.html'); // Si no está en caché, servimos la página offline
            })
    );
});

// ✅ Borrar cachés antiguas cuando se active un nuevo Service Worker
self.addEventListener('activate', event => {
    console.log("🧹 Activando nuevo Service Worker y limpiando cachés antiguas...");

    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.filter(cacheName => cacheName !== CACHE_NAME)
                    .map(cacheName => {
                        console.log("🗑 Borrando caché antigua:", cacheName);
                        return caches.delete(cacheName);
                    })
            );
        })
    );
});
