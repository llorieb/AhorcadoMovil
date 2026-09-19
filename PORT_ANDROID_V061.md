# Ahorcado Android v0.6.1

Corrección puntual sobre v0.6.0.

## Fix
Android interpreta los estilos:
- `Text.Ahorcado.HelpTitle`
- `Text.Ahorcado.HelpBody`

como hijos implícitos de `Text.Ahorcado`.

Se agregó el estilo padre:

`Text.Ahorcado`

con base `@android:style/Widget.TextView`.

No se modificó ningún layout, lógica, audio ni comportamiento funcional.
