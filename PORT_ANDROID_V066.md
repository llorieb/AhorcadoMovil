# Ahorcado Android v0.6.6

Base: v0.6.5.

## Ajuste del splash

El splash ya no utiliza el ícono completo de Ahorcado dentro de la máscara
del sistema de Android 12+.

Ahora se utiliza un recurso específico:
- sólo el personaje;
- fondo transparente;
- sin tarjeta celeste;
- sin marco;
- sin esquinas que puedan ser recortadas por la máscara del sistema.

El fondo general del splash sigue siendo `surface_game`, por lo que la
transición hacia la pantalla principal mantiene la misma continuidad visual.

También se aplica el mismo personaje transparente al splash legacy
(Android 6–11) para mantener consistencia.

El launcher icon de la aplicación no fue modificado.
