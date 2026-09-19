# Ahorcado Android v0.5.3

Corrección de contraste en dispositivos configurados con tema oscuro.

Causa:
- La aplicación usaba `Theme.Material3.DayNight.NoActionBar`.
- La interfaz de Ahorcado es actualmente clara, pero algunos controles Material
  heredaban colores del modo oscuro del teléfono.
- En el overlay de Arriesgar, el texto escrito podía quedar gris muy claro sobre
  la tarjeta blanca.

Corrección:
- El tema actual pasa a `Theme.Material3.Light.NoActionBar`.
- Se fijan explícitamente `colorOnSurface` y `colorOnSurfaceVariant`.
- El TextInputLayout de Arriesgar usa fondo blanco explícito.
- El TextInputEditText fija texto principal y hint con los colores de Ahorcado.
- La misma configuración se aplica también bajo `values-night`, para que el
  tema del teléfono no altere la paleta mientras no exista un dark mode propio.

No se modifican layout adaptativo, sonidos, lógica ni overlays de resultado.
