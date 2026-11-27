# Matriz inicial de pruebas

Proyecto: Alerta temprana de rendimiento académico (Grupo 5). Entregable de Calidad del Sprint 1 (Jira: SCRUM-9, SCRUM-36 y subtareas de calidad de RF-02), revisado por Backend contra el contrato REST (SCRUM-7 y SCRUM-14).

Las pruebas se ejecutan directamente contra la API, sin pasar por la interfaz (RNF-04). Los resultados esperados salen del contrato de Backend.

## RF-01: Registrar y consultar materias

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-12 | CA-01.1 | Éxito | Nombre, docente (texto), horario (días, horaInicio, horaFin) y salón válidos / POST /api/materias | 201; devuelve la materia con su id | Respuesta + GET materias |
| CP-13 | CA-01.2, 01.4 | Dato faltante | Materia sin salón / POST materias | 400 "Falta el salón de la materia." | Respuesta |
| CP-14 | CA-01.3 | Éxito (consulta) | Estudiante con materias registradas / GET /api/materias | 200; la lista incluye la materia registrada | Respuesta |
| CP-21 | CA-01.2, 01.4 | Dato faltante | Materia sin nombre / POST materias | 400 "Falta el nombre de la materia." | Respuesta |
| CP-22 | CA-01.2, 01.4 | Dato faltante | Materia sin docente / POST materias | 400 "Falta el docente de la materia." | Respuesta |
| CP-23 | CA-01.2, 01.4 | Dato faltante | Horario incompleto (sin horaFin) / POST materias | 400 "Falta la hora de fin del horario." | Respuesta |
| CP-24 | CA-01.4 | No modificación | Después de CP-13 o CP-21 a CP-23 | La lista de materias queda igual; no se guardó el registro incompleto | GET antes/después |
| CP-25 | CA-01.3 | Asociar notas | Materia recién registrada / POST /api/materias/{id}/notas con una nota válida | 201 (la materia queda disponible para asociarle notas) | Respuesta |

## RF-02: Registrar y consultar notas

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-01 | CA-02.1, 02.4 | Éxito | Materia existente; corte 1, descripción, valor 4.0, porcentaje 30 / POST /api/materias/{id}/notas | 201; devuelve la nota con su id | Respuesta + GET de notas |
| CP-02 | CA-02.2, 02.3 | Rechazo | Valor fuera de 0.0 a 5.0 (por ejemplo 5.1) / POST notas | 400 "La nota debe estar entre 0.0 y 5.0. No se guardó." | Respuesta |
| CP-03 | CA-02.2, 02.3 | No modificación | Después de CP-02 | Las notas de la materia quedan iguales | GET antes/después |
| CP-04 | CA-02.6 | Dato faltante | Nota sin porcentaje / POST notas | 400 "Falta el porcentaje de la nota." | Respuesta |
| CP-05 | CA-02.2, 02.3 | Rechazo | Corte con 80% acumulado; se registra una nota de 30% | 400 "Los porcentajes del corte no pueden superar el 100%. No se guardó." | Respuesta |
| CP-06 | CA-02.2, 02.3 | No modificación | Después de CP-05 | Las notas del corte quedan iguales | GET antes/después |
| CP-07 | CA-02.2, 02.3 | Límite | Corte con 70% acumulado; se registra una nota de 30% (suma 100%) | 201 | Respuesta |
| CP-08 | CA-02.3 | Dato inválido | Porcentaje 0 o 101 / POST notas | 400 "El porcentaje debe ser mayor que 0 y hasta 100. No se guardó." | Respuesta |
| CP-09 | RF-02 | Materia inexistente | materiaId inexistente / POST notas | 404 "La materia no existe." | Respuesta |
| CP-10 | CA-02.5 | Éxito | Materia con notas en los cortes 1 y 2 / GET /api/materias/{id}/notas | 200; notas agrupadas por corte | Respuesta |
| CP-11 | CA-02.5 | Materia inexistente | materiaId inexistente / GET notas | 404 | Respuesta |
| CP-26 | CA-02.3 | Dato inválido | Corte 4 (fuera de 1, 2 o 3) / POST notas | 400 que indica el dato inválido (mensaje según contrato) | Respuesta |
| CP-27 | CA-02.6 | Dato faltante | Nota sin valor / POST notas | 400 que indica el dato faltante | Respuesta |
| CP-28 | CA-02.6 | Dato faltante | Nota sin corte / POST notas | 400 que indica el dato faltante | Respuesta |
| CP-29 | CA-02.4 | Asociación | Nota de corte 2 registrada en la materia A / GET notas de A y de B | Aparece solo en la materia A, dentro del corte 2 | Respuesta de ambos GET |
| CP-30 | CA-02.6 | No modificación | Después de CP-27 o CP-28 | Las notas de la materia quedan iguales; no se registró el dato | GET antes/después |

## RF-03: Nivel de riesgo

Regla: documento «Definición y validación de la fórmula de riesgo académico» (aprobado por Producto) y arts. 37 y 39 del Reglamento Estudiantil de la UAM. Cortes de 30%, 35% y 35%; la nota de cada corte se redondea a un decimal; nota necesaria = (2.95 − Na) / (Pr / 100); Despejado ≤ 3.5 < Nublado ≤ 4.5 < Tormenta; sin porcentaje pendiente se redondea la nota final a un decimal y se compara con 3.0. La regla completa está en docs/modelo-datos.md.

Datos de prueba usados: materias 6 a 16 de SCRUM-65 (una nota de 100% por corte) y las materias 1, 2 y 4. Los valores esperados están calculados a mano y comprobados con `service/CalculadoraRiesgo`.

Los cambios de categoría justo encima de cada umbral (3.51 y 4.51) no se pueden producir con notas de un decimal por la API: los cubren las pruebas unitarias de Backend (`CalculadoraRiesgoTest`, SCRUM-64). La materia inexistente (404) es el mismo endpoint de CP-20.

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-31 | CA-03.1, 03.2, 03.3 | Despejado | Materia 6 (Física Mecánica), cortes 3.0 · 2.5: Ejemplo A del documento / GET /api/materias/6/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 1.775; porcentajeRestante 35; notaNecesaria 3.36 | Respuesta |
| CP-32 | CA-03.1, 03.2, 03.3 | Nublado | Materia 7 (Química General), cortes 2.5 · 2.5: Ejemplo B del documento / GET /api/materias/7/estado | 200; COMPLETA; nivelRiesgo NUBLADO; notaAcumulada 1.625; porcentajeRestante 35; notaNecesaria 3.79 | Respuesta |
| CP-33 | CA-03.1, 03.2, 03.3 | Despejado | Materia 8 (Estadística), cortes 1.9 · 3.3: Umbral 3.5 incluido; con double da 3.5000000000000004, prueba la tolerancia / GET /api/materias/8/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 1.725; porcentajeRestante 35; notaNecesaria 3.5 | Respuesta |
| CP-34 | CA-03.1, 03.2, 03.3 | Nublado | Materia 9 (Probabilidad), cortes 0.5 · 3.5: Umbral 4.5 incluido; con double da 4.500000000000001, prueba la tolerancia / GET /api/materias/9/estado | 200; COMPLETA; nivelRiesgo NUBLADO; notaAcumulada 1.375; porcentajeRestante 35; notaNecesaria 4.5 | Respuesta |
| CP-35 | CA-03.1, 03.2, 03.3 | Tormenta | Materia 10 (Física Eléctrica), cortes 0.5 · 3.0: Máximo de la escala / GET /api/materias/10/estado | 200; COMPLETA; nivelRiesgo TORMENTA; notaAcumulada 1.2; porcentajeRestante 35; notaNecesaria 5 | Respuesta |
| CP-36 | CA-03.1, 03.2, 03.3 | Tormenta | Materia 11 (Ecuaciones Diferenciales), cortes 1.5 · 2.0: Más de 5.0: aprobar ya no es posible / GET /api/materias/11/estado | 200; COMPLETA; nivelRiesgo TORMENTA; notaAcumulada 1.15; porcentajeRestante 35; notaNecesaria 5.14 | Respuesta |
| CP-37 | CA-03.1, 03.2, 03.3 | Despejado | Materia 12 (Inglés I), cortes 5.0 · 5.0: Nota necesaria ≤ 0 (−0.86): ya aprobó; se muestra 0 / GET /api/materias/12/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 3.25; porcentajeRestante 35; notaNecesaria 0 | Respuesta |
| CP-38 | CA-03.1, 03.2, 03.3 | Despejado | Materia 13 (Ética), cortes 3.0 · 3.0 · 3.0: Sin porcentaje pendiente, nota final 3.0; con double da 2.9999999999999996, prueba la tolerancia / GET /api/materias/13/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 3; porcentajeRestante 0; notaNecesaria null | Respuesta |
| CP-39 | CA-03.1, 03.2, 03.3 | Despejado | Materia 14 (Contabilidad), cortes 2.6 · 3.1 · 3.1: Nota final 2.95 se redondea a 3.0 (art. 39) / GET /api/materias/14/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 2.95; porcentajeRestante 0; notaNecesaria null | Respuesta |
| CP-40 | CA-03.1, 03.2, 03.3 | Tormenta | Materia 15 (Economía), cortes 2.8 · 3.0 · 3.0: Nota final 2.94 se redondea a 2.9 / GET /api/materias/15/estado | 200; COMPLETA; nivelRiesgo TORMENTA; notaAcumulada 2.94; porcentajeRestante 0; notaNecesaria null | Respuesta |
| CP-41 | CA-03.1, 03.2, 03.3 | Tormenta | Materia 16 (Sistemas Operativos), cortes 2.9 · 2.9 · 2.9: Nota final 2.9 / GET /api/materias/16/estado | 200; COMPLETA; nivelRiesgo TORMENTA; notaAcumulada 2.9; porcentajeRestante 0; notaNecesaria null | Respuesta |
| CP-42 | CA-03.1, 03.2, 03.3 | Despejado | Materia 4 (Estructuras de Datos), cortes 3.8 · 3.8 · 3.8: Materia completa de RF-04 (CP-17) con su nivel / GET /api/materias/4/estado | 200; COMPLETA; nivelRiesgo DESPEJADO; notaAcumulada 3.8; porcentajeRestante 0; notaNecesaria null | Respuesta |
| CP-43 | CA-03.4, RN-01 | Incompleta | Materia 2: corte 2 suma 60% / GET estado | 200; INFORMACION_INCOMPLETA; nivelRiesgo, notaAcumulada, porcentajeRestante y notaNecesaria en null | Respuesta |
| CP-44 | CA-03.4, RN-01 | Sin notas | Materia 1, sin notas / GET estado | 200; INFORMACION_INCOMPLETA; los cuatro campos del riesgo en null | Respuesta |
| CP-45 | CA-03.5, RN-03 | Aviso | Toda respuesta de CP-31 a CP-44 | Incluye "Este sistema no es la fuente oficial de notas. Verifica en intraUAM." | Respuesta |

## RF-04: Información incompleta

Regla confirmada:

1. Un corte con notas está completo si sus porcentajes suman 100%.
2. Un corte sin notas se ignora.
3. Una materia sin ninguna nota es INFORMACION_INCOMPLETA.

Datos de prueba usados: materia 1 (sin notas), materia 2 (corte 1 al 100%, corte 2 al 60%, corte 3 sin notas) y materia 4 (los tres cortes al 100%).

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-15 | CA-04.1, 04.2, 04.4, 04.5 | Éxito (incompleta) | Materia 2: corte 2 suma 60% / GET /api/materias/{id}/estado | 200; "INFORMACION_INCOMPLETA"; alertaGenerada: false; indica corte y porcentaje faltante; mensaje "Faltan notas del corte 2 (llevas 60% de 100%). Completa tus notas para calcular el estado." | Respuesta (CA-04.6, RNF-04) |
| CP-16 | CA-04.1, 04.2, 04.4, 04.5 | Sin notas | Materia 1, sin ninguna nota (regla 3) / GET estado | 200; "INFORMACION_INCOMPLETA" y alertaGenerada: false | Respuesta |
| CP-17 | CA-04.3 | Éxito (completa) | Materia 4: los tres cortes suman 100% (regla 1) / GET estado | 200; estado "COMPLETA" (su nivel de riesgo se verifica en CP-42) | Respuesta |
| CP-18 | CA-04.3, RNF-03 | Antes/después | Materia 2 de CP-15; se registra una nota de 40% en el corte 2 / GET estado otra vez | Pasa de "INFORMACION_INCOMPLETA" a "COMPLETA" aunque el corte 3 no tenga notas (regla 2) | Comparación antes/después |
| CP-19 | RN-03 | Aviso | Cualquier respuesta de estado (CP-15, CP-16, CP-17) | Incluye "Este sistema no es la fuente oficial de notas. Verifica en intraUAM." | Respuesta |
| CP-20 | RF-04 | Materia inexistente | materiaId inexistente / GET estado | 404 "La materia no existe." | Respuesta |

## Casos diferidos

Todavía no tienen contrato o requieren autenticación.

| Caso | CA | Dado / Cuando | Resultado esperado | Por qué se difiere |
|---|---|---|---|---|
| CP-D3 | Alerta de riesgo (RF-05) | Notas completas y promedio entre 3.0 y 3.5 (provisional) | Alerta con opción de asesoría, registrada con fecha, corte y días | Criterios de alerta sin definir |
| CP-D4 | Alerta de riesgo (RF-05) | Notas completas y promedio por encima de 3.5 | No se genera alerta | Igual |
| CP-D5 | RN-04, RNF-02 | Usuario distinto al dueño registra una nota | 403 "Solo el estudiante dueño de la materia puede registrar notas." | Se prueba cuando exista autenticación |
| CP-D6 | RN-04 | Usuario distinto al dueño consulta el estado | 403 "Solo el estudiante dueño de la materia puede consultar su estado." | Igual |

CP-D1 y CP-D2 se activaron con el contrato de RF-04 y ahora son CP-15 y CP-16.

## Ejecución automatizada

Los casos de RF-01, RF-02, RF-03 y RF-04 están en la colección de Postman `docs/pruebas/barometro-matriz.postman_collection.json`. Se corre en orden (RF-01, RF-02, RF-03 y RF-04) con el servicio recién iniciado, porque usa los datos de prueba de SCRUM-38 y SCRUM-65 (materias 1 a 16). RF-03 va antes de RF-04 porque CP-18 completa la materia 2, que CP-43 necesita incompleta. Los mensajes de CP-08 salen de `openapi.yaml`, y los de CP-21 a CP-23 de las validaciones de Backend (SCRUM-41).

## Decisiones cerradas

- La descripción de la nota es obligatoria: si falta, 400 "Falta la descripción de la nota." (validación de Backend, SCRUM-43).
- El GET de notas de una materia sin notas devuelve [] (CP-29).
- Regla de información completa de RF-04 confirmada (ver arriba).
