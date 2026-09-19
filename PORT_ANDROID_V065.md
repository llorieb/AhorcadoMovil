# Ahorcado Android v0.6.5

Base: v0.6.4.

## Splash screen profesional

Se separó el splash screen del launcher/adaptive icon.

### Android 12+
- Fondo igual al de la aplicación (`surface_game`).
- Se muestra el ícono completo de Ahorcado, centrado y dentro de la zona segura.
- Ya no se utiliza sólo el foreground del adaptive icon.

### Android 6–11
- Se agregó un `windowBackground` de inicio con el mismo fondo
  y el ícono completo centrado.

### Tema
- `MainActivity` arranca con `Theme.Ahorcado.Starting`.
- Antes de crear la Activity se cambia inmediatamente a `Theme.Ahorcado`,
  por lo que el tema de splash sólo existe durante el lanzamiento.

### Limpieza
- Se eliminan recursos `ic_launcher*.webp` residuales si existieran,
  evitando duplicados con los PNG actuales.

No se modificó la lógica, audio, overlays ni interfaz principal.
