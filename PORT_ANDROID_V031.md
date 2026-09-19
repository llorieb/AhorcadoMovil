# Ahorcado Android v0.3.1 — Ajuste de botonera inferior

Corrección puntual sobre v0.3.0.

## Botones inferiores
- `Jugar`: peso 0.90
- `Detener`: peso 1.00
- `Arriesgar`: peso 1.15
- Se fuerza `singleLine=true`.
- `minWidth=0dp` para evitar mínimos internos de Material.
- Menor padding horizontal.
- Menor separación entre ícono y texto.
- Íconos ligeramente más compactos.

Objetivo: evitar que `Arriesgar` se parta en dos líneas sin achicar
innecesariamente toda la botonera ni perder la estética de escritorio.
