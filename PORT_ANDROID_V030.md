# Ahorcado Android v0.3.0 — Desktop refined

Segunda pasada visual, basada directamente en la versión desktop final.

## Pantalla principal
- Proporción de tarjetas personaje/reloj basada en 270:160 de desktop.
- Degradé diagonal del personaje `#1399D7 → blanco`.
- Tarjeta de reloj con `TIEMPO`, reloj y número a 24sp.
- Categoría vuelve a estar dentro del panel de palabra.
- Panel de palabra con fondo `#F8FAFC`, borde suave y celdas estilo desktop.
- Celdas reveladas con azul claro y borde azul.
- Teclado en 8 columnas como desktop:
  - A–H
  - I–O (incluyendo Ñ)
  - P–W
  - X Y Z centradas
- Teclas blancas con borde gris; acertadas verdes e incorrectas rojas.
- Botonera inferior con la paleta y los íconos originales de desktop.
- Contenido central con ancho máximo para no deformarse en tablets.
- Alturas adaptativas mediante `dimens.xml` y `values-sw600dp`.

## Funcionalidad
- Conserva la lógica y el audio de v0.1.2.
- No se modificó GameEngine ni WordRepository.
