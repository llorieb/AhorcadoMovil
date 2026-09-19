# Ahorcado Android v0.6.9

Base: v0.6.8.

## Prueba sobre la animación inicial del sistema

El splash dedicado de v0.6.8 se conserva sin cambios.

Se redujo un 25% el contenido del `adaptive launcher icon foreground`
en todas las densidades, manteniendo el mismo canvas y el mismo fondo.

Objetivo:
comprobar si el teléfono real está utilizando el adaptive launcher icon
durante la primera fase de la animación de arranque y evitar que el personaje
quede visualmente recortado por la máscara del sistema.

También se actualizó la versión monochrome/themed icon con la misma escala.

Los iconos legacy y el splash dedicado no fueron modificados.
