# Formato de `words.tsv`

El corpus móvil usa un TSV preparado para N idiomas:

```text
id    category    locale    text    aliases
```

- `id`: identificador estable del concepto. Se repite entre idiomas.
- `category`: clave interna neutra (`countries`, `cities`, `car_brands`, `rock_bands`).
- `locale`: código de idioma (`es`, `en`, etc.).
- `text`: respuesta mostrada al jugador.
- `aliases`: respuestas completas alternativas, separadas por `;` (opcional).

Ejemplo:

```text
country_003    countries    es    ALEMANIA
country_003    countries    en    GERMANY
```

## Ahorcado Mobile 0.7.0

El corpus contiene 548 entradas en español y las mismas 548 entidades en inglés.

El idioma efectivo de la aplicación y el corpus se mantienen sincronizados:
- locale español (`es*`) → español;
- inglés → inglés;
- cualquier idioma no soportado → inglés.

El fallback interno de `WordRepository` es `en`.
