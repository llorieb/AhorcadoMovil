# Ahorcado Android v0.5.1

Corrección del layout vertical adaptativo.

Problema observado en v0.5.0:
- El `adaptiveSpacer` se calculaba una sola vez.
- Al detener una partida aparecía el texto "Partida detenida" mientras
  permanecían visibles las celdas de la palabra.
- El panel central aumentaba de altura pero el spacer conservaba su tamaño,
  empujando la botonera por debajo del viewport.

Corrección:
- Se recalcula el layout al iniciar, detener, finalizar una partida,
  volver al estado inicial y cambiar preferencias.
- Antes de cada cálculo se restauran las tarjetas y el spacer a sus mínimos.
- Después de crecer las tarjetas se realiza una segunda medición real.
- El spacer recibe únicamente el espacio que verdaderamente queda libre.

No se modifican estética, sonidos, overlays, GameEngine ni corpus.
