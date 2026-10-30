# Matriz inicial de pruebas

Proyecto: Alerta temprana de rendimiento académico (Grupo 5). Entregable de Calidad del Sprint 1 (Jira: SCRUM-9, SCRUM-36 y subtareas de calidad de RF-02), revisado por Backend contra el contrato REST (SCRUM-7 y SCRUM-14).

Las pruebas se ejecutan directamente contra la API, sin pasar por la interfaz (RNF-04). Los resultados esperados salen del contrato de Backend.

## RF-01: Registrar y consultar materias

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-12 | CA-01.1 | Éxito | Nombre, docente (texto), horario (días, horaInicio, horaFin) y salón válidos / POST /api/materias | 201; devuelve la materia con su id | Respuesta + GET materias |
| CP-13 | CA-01.2, 01.4 | Dato faltante | Materia sin salón / POST materias | 400 "Falta el salón de la materia." | Respuesta |
| CP-14 | CA-01.3 | Éxito (consulta) | Estudiante con materias registradas / GET /api/materias | 200; la lista incluye la materia registrada | Respuesta |
| CP-21 | CA-01.2, 01.4 | Dato faltante | Materia sin nombre / POST materias | 400 que indica el dato faltante (mensaje según contrato) | Respuesta |
| CP-22 | CA-01.2, 01.4 | Dato faltante | Materia sin docente / POST materias | 400 que indica el dato faltante | Respuesta |
| CP-23 | CA-01.2, 01.4 | Dato faltante | Horario incompleto (sin horaFin) / POST materias | 400 que indica el dato faltante | Respuesta |
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
| CP-08 | CA-02.3 | Dato inválido | Porcentaje 0 o 101 / POST notas | 400 que indica el dato inválido (porcentaje mayor que 0 y hasta 100); texto exacto pendiente | Respuesta |
| CP-09 | RF-02 | Materia inexistente | materiaId inexistente / POST notas | 404 "La materia no existe." | Respuesta |
| CP-10 | CA-02.5 | Éxito | Materia con notas en los cortes 1 y 2 / GET /api/materias/{id}/notas | 200; notas agrupadas por corte | Respuesta |
| CP-11 | CA-02.5 | Materia inexistente | materiaId inexistente / GET notas | 404 | Respuesta |
| CP-26 | CA-02.3 | Dato inválido | Corte 4 (fuera de 1, 2 o 3) / POST notas | 400 que indica el dato inválido (mensaje según contrato) | Respuesta |
| CP-27 | CA-02.6 | Dato faltante | Nota sin valor / POST notas | 400 que indica el dato faltante | Respuesta |
| CP-28 | CA-02.6 | Dato faltante | Nota sin corte / POST notas | 400 que indica el dato faltante | Respuesta |
| CP-29 | CA-02.4 | Asociación | Nota de corte 2 registrada en la materia A / GET notas de A y de B | Aparece solo en la materia A, dentro del corte 2 | Respuesta de ambos GET |
| CP-30 | CA-02.6 | No modificación | Después de CP-27 o CP-28 | Las notas de la materia quedan iguales; no se registró el dato | GET antes/después |

## RF-04: Información incompleta

Regla propuesta, pendiente de confirmar por Producto: una materia tiene información completa cuando cada corte que tenga notas suma 100%.

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| CP-15 | CA-04.1, 04.2, 04.4, 04.5 | Éxito (incompleta) | Materia con notas del corte 2 que suman 60% / GET /api/materias/{id}/estado | 200; "INFORMACION_INCOMPLETA"; alertaGenerada: false; indica corte y porcentaje faltante; mensaje "Faltan notas del corte 2 (llevas 60% de 100%). Completa tus notas para calcular el estado." | Respuesta (CA-04.6, RNF-04) |
| CP-16 | CA-04.1, 04.2, 04.4, 04.5 | Sin notas | Materia sin ninguna nota / GET estado | 200; "INFORMACION_INCOMPLETA" y alertaGenerada: false | Respuesta |
| CP-17 | CA-04.3 | Éxito (completa) | Cortes con notas que suman 100% cada uno / GET estado | 200; estado "COMPLETA" (sin nivel de riesgo todavía, RF-03) | Respuesta |
| CP-18 | CA-04.3, RNF-03 | Antes/después | Materia de CP-15; se registra una nota de 40% en el corte 2 / GET estado otra vez | Pasa de "INFORMACION_INCOMPLETA" a "COMPLETA" | Comparación antes/después |
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

## Pendientes

- Texto exacto del mensaje de error de CP-08 (consultado a Backend).
- Si la descripción de la nota es obligatoria, y qué devuelve el GET de notas de una materia sin notas.
- Confirmación de Producto de la regla del 100% por corte de RF-04. Duda adicional: con la regla literal, una materia con solo el corte 1 completo saldría como COMPLETA.
