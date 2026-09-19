# Ahorcado Android v0.6.3

Base: v0.6.2.

## Ícono de aplicación

Se reemplazó el launcher icon por defecto de Android Studio por el ícono
oficial de Ahorcado.

Incluye:
- Iconos legacy para mdpi, hdpi, xhdpi, xxhdpi y xxxhdpi.
- `roundIcon` para launchers que utilizan iconos circulares.
- Adaptive Icon para Android 8+.
- Foreground basado en el personaje de Ahorcado.
- Fondo celeste coherente con la identidad visual del juego.
- Monochrome/Themed Icon para Android modernos.

El `AndroidManifest.xml` continúa utilizando:
- `@mipmap/ic_launcher`
- `@mipmap/ic_launcher_round`

No se modificó ninguna otra funcionalidad.
