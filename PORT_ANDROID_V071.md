# Ahorcado Android v0.7.1

Base: v0.7.0 bilingüe.

## Corrección de compilación

Se corrigió el import de `ConfigurationCompat`.

Incorrecto:
```java
import androidx.core.content.res.ConfigurationCompat;
```

Correcto:
```java
import androidx.core.os.ConfigurationCompat;
```

No se modificó ninguna otra parte de la implementación bilingüe.
