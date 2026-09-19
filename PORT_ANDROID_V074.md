# Ahorcado Android v0.7.4

Base: v0.7.3.

## Preferencias: proporciones finales

Ajuste visual para equilibrar el espacio inferior del overlay:

- separación entre la sección Idioma y Cancelar/Guardar: 4dp -> 12dp;
- altura del overlay de Preferencias: 680dp -> 620dp;
- se restaura `ScrollView` con `layout_height="0dp"` y `layout_weight="1"`
  para conservar scroll real en pantallas pequeñas;
- la tarjeta continúa centrada mediante `Gravity.CENTER` en `showOverlay()`.

La menor altura evita el gran espacio vacío que existía con 680dp, mientras
que los 12dp dan una separación visual más natural entre opciones y botones.

No se modificó lógica de juego, idiomas, corpus, sonidos, splash ni iconos.
