# Ahorcado Android v0.5.2

Ajuste fino del borde inferior de la botonera.

Problema:
- En algunos teléfonos los bordes inferiores de Jugar / Detener / Arriesgar
  quedaban demasiado próximos al límite útil de pantalla.

Corrección:
- 8dp de padding inferior real dentro de la columna principal.
- El cálculo adaptativo conserva además 8dp de margen de seguridad.
- Resultado: aproximadamente 16dp de respiración visual inferior.
- No se modifican tamaño, ancho, tipografía ni distribución de los botones.

Se mantienen todos los cambios de v0.5.1.
