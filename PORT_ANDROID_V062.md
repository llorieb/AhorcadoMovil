# Ahorcado Android v0.6.2

Base: v0.6.1.

## Ajuste de Salir

- Si NO hay una partida en curso:
  `Salir` cierra Ahorcado directamente.
- Si HAY una partida en curso:
  `Salir` muestra el overlay de confirmación.
- Si el usuario cancela la confirmación:
  la partida continúa y el temporizador se reanuda desde donde estaba.

No se modificó ningún otro aspecto de la interfaz, overlays, audio,
corpus o lógica de juego.
