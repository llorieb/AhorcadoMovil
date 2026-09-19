# Ahorcado Android v0.7.3

Base: v0.7.2.

## Ajuste visual de Preferencias

Se eliminó el espacio vacío excesivo entre la sección de idioma y los botones.

Cambios:
- el `ScrollView` deja de usar `layout_height="0dp"` + `layout_weight="1"`;
- ahora usa `layout_height="wrap_content"`;
- el contenido ocupa sólo la altura que necesita;
- margen antes de Cancelar/Guardar: 6dp -> 4dp;
- altura máxima del overlay: 720dp -> 680dp;
- se mantiene el `ScrollView` como fallback si una pantalla más pequeña
  necesita desplazamiento.

No se modificó la lógica del juego, idiomas, corpus, sonidos, splash ni iconos.
