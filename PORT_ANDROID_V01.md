# Ahorcado Android 0.1.0

Primera adaptación jugable del Ahorcado Desktop a Android nativo Java + XML + Material.

## Reutilizado del Desktop

- Las 548 palabras y las cuatro categorías.
- Normalización de tildes/diéresis conservando Ñ.
- Comparación de respuestas completas ignorando espacios y puntuación.
- 6 intentos.
- Tiempos de 15/30/45/60 segundos.
- Imágenes del personaje 100..106.
- Imágenes del reloj sw0..sw12.
- Los tres sonidos de error, victoria y derrota.

## Nuevo diseño interno

- `GameEngine`: lógica Java pura, sin Android.
- `WordRepository`: lee `res/raw/words.tsv` y mantiene el corpus en memoria.
- `words.tsv`: formato multilenguaje `id/category/locale/text/aliases`.
- `SharedPreferences`: conserva categoría y tiempo.
- `CountDownTimer`: temporizador Android.
- `SoundPool`: reproducción de efectos cortos.
- `FlowLayout`: ajusta bloques de palabras a varias líneas sin cortar cada palabra.

## Estado de esta entrega

La intención es validar el primer flujo completo en un dispositivo/emulador:
Jugar → elegir letras → errores → reloj → arriesgar → victoria/derrota/timeout.

La siguiente etapa será refinamiento visual móvil, ayuda/acerca de, icono adaptativo,
internacionalización completa y preparación para Google Play.
