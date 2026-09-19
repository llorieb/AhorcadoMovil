# Ahorcado Android v0.4.0

Seis mejoras importantes sobre v0.3.1.

1. Personaje
- El panel interno usa la proporción exacta de desktop: 244 × 300.
- Queda centrado dentro de la tarjeta y más angosto, evitando que la soga quede visualmente recortada.

2. Overlays de resultado
- Se eliminan los AlertDialog de victoria, derrota y timeout.
- Overlay interno oscuro (42 %), tarjeta central de hasta 356dp.
- Fade + escala de 180 ms.
- Estados:
  - timeout: borde ámbar + reloj final;
  - derrota: borde rojo suave + personaje final;
  - victoria: borde verde suave + personaje inicial.
- Botones Jugar de nuevo / Cerrar.

3. Sonido de letra incorrecta
- SoundPool sigue a volumen 1.0.
- El WAV fue comprimido dinámicamente para subir volumen percibido sin clipping.
- Antes: pico -2.41 dBFS / RMS -12.99 dBFS
- Ahora: pico -0.80 dBFS / RMS -8.11 dBFS

4. Espacio inferior
- Padding inferior del contenido: 28dp → 4dp.
- Se conserva únicamente el espacio obligatorio de la barra de navegación del sistema.

5. Estados visuales de controles
- Jugar / Detener / Arriesgar / Preferencias usan alpha 1.0 habilitado y 0.38 deshabilitado.
- El estado visual acompaña explícitamente el estado lógico de la partida.

6. Feedback de letra equivocada
- El fondo del panel del personaje pasa a rojo mientras dura el sonido (220 ms).
- El teclado queda temporalmente bloqueado durante ese feedback.
- En el sexto error se muestra directamente el overlay de derrota, igual que desktop.

No se modificaron GameEngine, WordRepository ni el corpus.
