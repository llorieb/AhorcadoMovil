# Ahorcado Android v0.7.2

Base: v0.7.1.

## Ajuste visual de Preferencias

Se compactó levemente el overlay de configuración para evitar que la selección
de idioma quede apenas fuera de pantalla en teléfonos normales.

Cambios:
- menor padding superior e inferior;
- menor separación entre secciones;
- filas de selección de 42dp a 38dp;
- botones inferiores de 44dp a 42dp;
- altura máxima del overlay de 690dp a 720dp;
- ScrollView conservado como fallback para pantallas más pequeñas.

No se modificó ninguna lógica de idioma, juego, corpus o navegación.
