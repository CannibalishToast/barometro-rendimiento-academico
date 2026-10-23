# Alerta Temprana de Rendimiento Académico

Sistema de apoyo académico orientado a identificar posibles dificultades en el rendimiento de los estudiantes mediante el registro de materias y calificaciones, la consulta del estado de riesgo por asignatura y la generación de pronósticos y alertas tempranas.

## Descripción del proyecto

El proyecto busca facilitar el seguimiento del rendimiento académico y ayudar a los estudiantes a reconocer oportunamente las materias en las que podrían necesitar apoyo. Para ello, se propone centralizar la información académica relevante y utilizarla para identificar situaciones de riesgo, orientar al estudiante y facilitar el acceso a tutorías.

Este proyecto se desarrolla en el marco de la asignatura **Desarrollo de Proyecto de Software**.

## Objetivos

- Facilitar el registro y seguimiento de materias y calificaciones.
- Identificar posibles riesgos en el rendimiento académico.
- Generar alertas oportunas que permitan actuar antes del cierre de cada corte académico.
- Detectar información incompleta para evitar evaluaciones y alertas incorrectas.
- Facilitar el acceso a tutorías como mecanismo de acompañamiento académico.
- Permitir consultar la evolución del promedio académico entre cortes.

## Funcionalidades previstas

### Gestión de información académica
- Registro de materias con docente, horario y salón.
- Registro de calificaciones por corte académico.

### Evaluación de riesgo académico
- Consulta del estado de riesgo por materia.
- Clasificación del estado mediante los niveles **Despejado, Nublado y Tormenta**.
- Identificación de información incompleta.
- Cálculo del pronóstico general del rendimiento académico.
- Generación de alertas tempranas.

### Gestión de tutorías
- Solicitud de tutorías académicas.
- Consulta del historial y estado de las solicitudes.

### Seguimiento académico
- Consulta de la evolución del promedio por corte.

> Estas funcionalidades corresponden al alcance definido del proyecto; su implementación se realizará progresivamente según las prioridades y los sprints establecidos por el equipo.

## Alcance del MVP

El Producto Mínimo Viable (MVP) prioriza las funcionalidades esenciales para registrar la información académica y evaluar posibles riesgos:

- **RF-01:** Registrar materias con docente, horario y salón.
- **RF-02:** Registrar notas por corte en cada materia.
- **RF-03:** Mostrar el estado de riesgo de cada materia.
- **RF-04:** Identificar información incompleta y evitar alertas incorrectas.
- **RF-05:** Calcular el pronóstico general y generar alertas de riesgo.

Las funcionalidades de tutorías y evolución del promedio se desarrollarán de acuerdo con sus prioridades y la planificación del proyecto.

## Reglas de negocio principales

- No se deben generar alertas de riesgo cuando la información necesaria esté incompleta.
- Las alertas de riesgo deben incluir la opción de solicitar una tutoría.
- El sistema debe aclarar que no es una fuente oficial de calificaciones.
- El acceso a la información y a las funcionalidades debe respetar los permisos correspondientes a cada rol.

## Metodología de trabajo

La organización del proyecto se realiza mediante **Jira**, donde se gestionan los requisitos, las tareas técnicas, las prioridades y los sprints.

El control de versiones se realiza mediante **Git y GitHub**, siguiendo esta estrategia de ramas:

- `main`: versión estable del proyecto.
- `develop`: integración y pruebas conjuntas.
- `feature/nombre-funcionalidad`: desarrollo individual de cada funcionalidad.

Los cambios se integran mediante Pull Requests, con revisión previa y pruebas correspondientes.

## Estado del proyecto

El proyecto se encuentra en desarrollo. Las funcionalidades se implementarán progresivamente conforme al backlog priorizado y a los objetivos definidos para cada sprint.

## Equipo de desarrollo

Proyecto académico desarrollado en equipo para la asignatura Desarrollo de Proyecto de Software.

## Licencia

La licencia del proyecto está pendiente de definición por parte del equipo.
