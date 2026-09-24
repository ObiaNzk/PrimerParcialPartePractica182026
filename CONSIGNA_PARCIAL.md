# Primer Parcial Parte Práctica: Auditoría y Refactorización en Jetpack Compose
**Materia:** Programación de Aplicaciones Móviles / Desarrollo de Aplicaciones I  
**Institución:** Universidad Argentina de la Empresa (UADE)  
**Facultad:** Facultad de Ingeniería y Cs. Exactas  

---

## 1. Contexto y Objetivos

La empresa *PokeCorp* ha recibido un prototipo funcional de su nueva aplicación móvil de Pokédex (**PokeCompose**), construida en **Kotlin** y **Jetpack Compose con Material 3**. La aplicación utiliza un repositorio local con los 251 Pokémon de Kanto y Johto, permite buscar por nombre o por número de Pokédex, calcular el porcentaje de completitud y los Pokémon restantes, marcar Pokémon capturados o liberarlos del equipo, y compartir el progreso.

Aunque el código compila y la aplicación puede iniciar, el equipo de desarrollo junior ha incurrido en **10 errores conceptuales y de lógica de programación graves** que violan los principios fundamentales del modelo declarativo de Compose, el tipado de Kotlin, la lógica booleana y el manejo de colecciones vistos en los **Ejercicios**.

Tu objetivo en esta parte del exámen es actuar como **Revisor Senior de Código (Code Reviewer)**:
1. **Identificar** los 10 errores analizando el código fuente (no es obligatorio disponer de un emulador, los errores son evidentes mediante lectura estática de código).
2. **Justificar** por qué cada fragmento representa un error conceptual o de lógica.
3. **Corregir** cada error en el código para que el proyecto funcione de forma coherente y siga las mejores prácticas de la materia.

---

## 2. Pautas de Trabajo

- **Archivos a inspeccionar:**
  - `app/src/main/java/com/uade/pokecompose/ui/PokemonScreen.kt`
  - `app/src/main/java/com/uade/pokecompose/logic/PokemonLogic.kt`
  - `app/src/main/res/values/strings.xml`
- No agregues librerías externas adicionales ni alteres las versiones de Gradle. No es necesario.
- Cada error resuelto y justificado correctamente tiene un valor de **2,5 puntos** (Total: 25 puntos).
- Si utilizás un asistente de Inteligencia Artificial para consultas conceptuales, deberan completar el **Registro Obligatorio de uso de IA**

---

## 3. Matriz 

Completá la siguiente tabla para cada uno de los 10 errores encontrados:

### Error 1
- **Archivo y línea aproximada:** `PokemonScreen.kt - L#39`
- **Concepto evaluado (Ejercicio de la guía):** `mutableListOf y como manejar la mutabilidad de las variables (no entendi lo del ejercicio a que se refiere)`
- **¿Por qué es un error conceptual / de lógica?:** `Es una lista que usa mutableListOf, no usa remember, siempre se reconstruye vacia`
- **Código de corrección:**
```kotlin
    val capturados = remember { mutableStateListOf<Pokemon>() }
```

### Error 2
- **Archivo y línea aproximada:** `PokemonScreen.kt - L#37`
- **Concepto evaluado (Ejercicio de la guía):** `MutableStateOf y como guardamos los valores aunque haya recomposición`
- **¿Por qué es un error conceptual / de lógica?:** `Como en el anterior, no se recompone y no se acepta el valor que se escribe`
- **Código de corrección:**
```kotlin
    var busqueda by remember { mutableStateOf("") }
```

### Error 3
- **Archivo y línea aproximada:** `PokemonLogic.kt L#21`
- **Concepto evaluado (Ejercicio de la guía):** `ninguno, la función resta mal, es un error de logica`
- **¿Por qué es un error conceptual / de lógica?:** `capturados - total da negativo, los restantes son total-capturados, es un error de logica`
- **Código de corrección:**
```kotlin
    fun calcularRestantes(total: Int, capturados: Int): Int {
  return total - capturados
}
```

### Error 4
- **Archivo y línea aproximada:** `PokemonScreen.kt L#57`
- **Concepto evaluado (Ejercicio de la guía):** `Manejo de operadores logicos`
- **¿Por qué es un error conceptual / de lógica?:** `Deberia ser Filtro o Nombre que contenga, en este caso es "Y" que contenga`
- **Código de corrección:**
```kotlin
        todosLosPokemons.filter { pokemon ->
  pokemon.name.contains(busqueda, ignoreCase = true) || (idFiltro != null && pokemon.id == idFiltro)
}
```

### Error 5
- **Archivo y línea aproximada:** `PokemonScreen.kt L#67`
- **Concepto evaluado (Ejercicio de la guía):** `Uso de recursos strings.xml`
- **¿Por qué es un error conceptual / de lógica?:** `Tengo un idioma que capaz no es para todos, si tengo el idioma koreano deberia mostrarlo en ese idioma`
- **Código de corrección:**
```kotlin
title = {
  Text(
    text = stringResource(id = R.string.{Titulo}),
    fontWeight = FontWeight.Bold
  )
}
```

### Error 6
- **Archivo y línea aproximada:** `PokemonScreen.kt L#121, L#100, L#104, L#109 y basicamente todos los que muestran textos`
- **Concepto evaluado (Ejercicio de la guía):** `Uso de recursos strings.xml y uso de context/intent`
- **¿Por qué es un error conceptual / de lógica?:** `Como en el de arriba, mostramos un value en español, la diferencia es que al ser un intent usamos el context para hacer la operacion porque esta dentro del intent`
- **Código de corrección:**
```kotlin
                        onClick = {
  val sendIntent = Intent(Intent.ACTION_SEND).apply {
    putExtra(
      Intent.EXTRA_TEXT,
      context.getString(R.string.{capture}, capturados.size)
    )
  }
  ```

### Error 7
- **Archivo y línea aproximada:** `PokemonScreen.kt L#117`
- **Concepto evaluado (Ejercicio de la guía):** `intent y tipo de mime`
- **¿Por qué es un error conceptual / de lógica?:** `Los intent necesitan un mime type, en este caso de txt`
- **Código de corrección:**
```kotlin
val sendIntent = Intent(Intent.ACTION_SEND).apply {
  type = "text/plain"
  putExtra(
    Intent.EXTRA_TEXT,
    "¡He capturado ${capturados.size} Pokémon en mi Pokédex!"
  )
}
```

### Error 8
- **Archivo y línea aproximada:** `PokemonScreen.kt L#79`
- **Concepto evaluado (Ejercicio de la guía):** `Variables no utilizadas`
- **¿Por qué es un error conceptual / de lógica?:** `Es un logger que no se usa y se crea adentro del scaffolding, deberia estar en el top level`
- **Código de corrección:**
```kotlin
            val diagnosticLogger = DiagnosticLogger()
            // usarlo en algun lugar que quieras logear y moverlo arriba de todo
```

### Error 9
- **Archivo y línea aproximada:** `PokemonScreen.kt L#171`
- **Concepto evaluado (Ejercicio de la guía):** `Orden de los modifiers`
- **¿Por qué es un error conceptual / de lógica?:** `El modifier aplica en un orden contrario, primero hace el background y despues el clip, deberia ser alreves, que el background afecte solo al clip`
- **Código de corrección:**
```kotlin
                                modifier = Modifier
  .size(64.dp)
  .clip(CircleShape) // Cambie el orden aca
  .background(Color.LightGray),

```

### Error 10
- **Archivo y línea aproximada:** `PokemonScreen.kt L#195`
- **Concepto evaluado (Ejercicio de la guía):** `Uso de indices en arrays`
- **¿Por qué es un error conceptual / de lógica?:** `Eliminamos la posición 0 siempre sin importar lo que pase, deberiamos hacer lo que hace el add que hace add(pokemon) pero con remove`
- **Código de corrección:**
```kotlin
    capturados.remove(pokemon) // como el add, pero con remove
```

### Error 10-a (ya se que son 10 pero separe los del strings.xml en 2 y despues me di cuenta que eran mas)
- **Archivo y línea aproximada:** `PokemonLogic.kt L#16`
- **Concepto evaluado (Ejercicio de la guía):** `Tipo de variables (int en este caso)`
- **¿Por qué es un error conceptual / de lógica?:** `int/int = int, 0.25 en int es 0, 0*100 = 0, nunca muestra bien`
- **Código de corrección:**
```kotlin
        return ((capturados.toDouble() / total) * 100) // Esto ya fuerza que sea double la respuesta
```

## 5. Registro a completar sobre el uso de Asistentes de IA

Si utilizaste asistentes de Inteligencia Artificial para consultar dudas conceptuales, completá el siguiente registro:

### Registro de Uso de IA
- **1. Problema o duda conceptual:** `Por el error que varios teniamos del java 21 no tenia acceso al ide con el helper`
- **2. Prompt enviado a la IA:** `esta bien val capturados by remember { mutableStateListOf<Pokemon>() }`
- **3. Explicación útil que aportó:** ` val capturados = remember { mutableStateListOf<Pokemon>() }`
- **4. Decisión y código propio aplicado:** `no recordaba que los val a diferencia de var usan = en vez de by`

> **Recordatorio:** El objetivo didáctico no es penalizar el uso de IA, sino validar que comprendas plenamente el razonamiento detrás de cada corrección aplicada.
