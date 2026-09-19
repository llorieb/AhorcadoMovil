# Ahorcado Android v0.5.0

Dos mejoras principales sobre v0.4.0.

## 1. Overlay propio para Arriesgar
- Se elimina el AlertDialog estándar.
- Overlay interno oscuro, consistente con los overlays de resultado.
- Tarjeta central con símbolo `?`, título, advertencia y campo outlined.
- Animación fade + scale de 180 ms.
- `Cancelar` reanuda la partida.
- `Confirmar` / Enter envían la respuesta.
- El contador se pausa realmente mientras el overlay está abierto.
- Back cancela el overlay y reanuda el contador.

## 2. Aprovechamiento adaptativo del alto de pantalla
- En pantallas bajas se mantienen las medidas mínimas actuales y el contenido puede hacer scroll.
- En pantallas altas, sólo una parte del espacio extra agranda las tarjetas superiores.
- El panel del personaje conserva siempre la relación 244:300.
- El crecimiento de las tarjetas está limitado a +54dp.
- El espacio restante se coloca entre teclado y botonera, acercando las acciones a la parte inferior.
- No se agregan elementos artificiales para rellenar la pantalla.
- En tablets se mantiene el ancho máximo de contenido existente.

Se conserva íntegramente la lógica, corpus, sonidos y overlays de resultado de v0.4.0.
