# Ahorcado Android v0.7.0 — Español + English

Base: v0.6.9.

## Internacionalización estructural

- Inglés pasa a ser el recurso Android por defecto (`values/strings.xml`).
- Español vive en `values-es/strings.xml`.
- Un dispositivo en español muestra la app en español.
- Un dispositivo en inglés muestra la app en inglés.
- Un dispositivo con un idioma no soportado (alemán, francés, etc.) cae en inglés.
- `WordRepository` usa inglés como fallback universal.

## Selector de idioma

Preferencias incorpora:
- Automático / Automatic
- Español
- English

El selector utiliza las APIs de locales de AppCompat:
- Android 13+ se sincroniza con la preferencia de idioma por aplicación del sistema.
- Android 12 e inferiores usan el almacenamiento automático de AppCompat.

## Corpus

`words.tsv` contiene:
- 548 entradas `es`
- 548 entradas `en`
- 1.096 filas jugables en total

Los IDs son idénticos entre idiomas. Marcas y bandas conservan sus nombres propios;
países y ciudades usan nombres habituales en inglés.

## Teclado

- Español: A–Z + Ñ.
- English: A–Z.

## Ayuda

Todo el contenido de “Cómo jugar”, overlays, resultados, Preferencias y menús
fue movido a recursos localizables. Ya no quedan textos españoles hardcodeados
en los layouts principales.
