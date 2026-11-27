package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Datos precargados en memoria para desarrollo y pruebas (SCRUM-38).
 *
 * <p>Se cargan al iniciar el servicio y quedan disponibles para
 * GET /api/materias. Son datos inventados, sin información personal real.</p>
 *
 * <p>El estudiante de prueba fijo tiene id {@link #ESTUDIANTE_PRUEBA_ID}; Backend
 * debe usar esta constante mientras no haya login.</p>
 *
 * <p>Materias de ejemplo (los ids se asignan en este orden), pensadas para los
 * casos de la matriz de pruebas (SCRUM-9):</p>
 * <ul>
 *   <li><b>1 Cálculo Diferencial:</b> sin notas.</li>
 *   <li><b>2 Álgebra Lineal:</b> corte 1 suma 100% y corte 2 suma 60%
 *       (información incompleta; notas agrupadas por corte).</li>
 *   <li><b>3 Programación Orientada a Objetos:</b> corte 1 suma 80% (agregar 30%
 *       supera el 100%).</li>
 *   <li><b>4 Estructuras de Datos:</b> los cortes 1, 2 y 3 suman 100% cada uno
 *       (información completa).</li>
 *   <li><b>5 Bases de Datos:</b> corte 1 suma 70% (agregar 30% llega justo a 100%).</li>
 * </ul>
 *
 * <p>Materias para el nivel de riesgo (SCRUM-65, RF-03). Tienen información completa
 * y cada corte con notas lleva una sola nota de 100%, así que la nota del corte es esa
 * nota. Los cortes pesan 30%, 35% y 35% (art. 37) y la nota necesaria se calcula hacia
 * 2.95 (art. 39), según el documento de la fórmula de riesgo:</p>
 * <ul>
 *   <li><b>6 Física Mecánica</b> (3.0 · 2.5): nota necesaria 3.36, Despejado (ejemplo A).</li>
 *   <li><b>7 Química General</b> (2.5 · 2.5): 3.79, Nublado (ejemplo B).</li>
 *   <li><b>8 Estadística</b> (1.9 · 3.3): 3.5 en el límite, Despejado (con double da
 *       3.5000000000000004; prueba la tolerancia).</li>
 *   <li><b>9 Probabilidad</b> (0.5 · 3.5): 4.5 en el límite, Nublado (con double da
 *       4.500000000000001).</li>
 *   <li><b>10 Física Eléctrica</b> (0.5 · 3.0): 5.0, Tormenta.</li>
 *   <li><b>11 Ecuaciones Diferenciales</b> (1.5 · 2.0): 5.14, Tormenta (aprobar ya no es posible).</li>
 *   <li><b>12 Inglés I</b> (5.0 · 5.0): nota necesaria menor que 0, Despejado (ya aprobó).</li>
 *   <li><b>13 Ética</b> (3.0 · 3.0 · 3.0): sin porcentaje pendiente, nota final 3.0, Despejado
 *       (con double da 2.9999999999999996).</li>
 *   <li><b>14 Contabilidad</b> (2.6 · 3.1 · 3.1): nota final 2.95, que redondea a 3.0, Despejado.</li>
 *   <li><b>15 Economía</b> (2.8 · 3.0 · 3.0): nota final 2.94, que redondea a 2.9, Tormenta.</li>
 *   <li><b>16 Sistemas Operativos</b> (2.9 · 2.9 · 2.9): nota final 2.9, Tormenta.</li>
 * </ul>
 *
 * <p>Para desactivar la carga (por ejemplo, en pruebas de Backend que necesiten
 * empezar vacías) se usa la propiedad {@code barometro.datos-prueba.habilitados=false}.
 * Si no se define, los datos se cargan.</p>
 */
@Component
@ConditionalOnProperty(name = "barometro.datos-prueba.habilitados",
        havingValue = "true", matchIfMissing = true)
public class DatosDePrueba implements CommandLineRunner {

    public static final Long ESTUDIANTE_PRUEBA_ID = 1L;

    private final MateriaRepository materias;
    private final NotaRepository notas;

    public DatosDePrueba(MateriaRepository materias, NotaRepository notas) {
        this.materias = materias;
        this.notas = notas;
    }

    @Override
    public void run(String... args) {
        cargar();
    }

    /** Carga las materias y notas de ejemplo si el estudiante de prueba aún no tiene materias. */
    public void cargar() {
        if (!materias.listarPorEstudiante(ESTUDIANTE_PRUEBA_ID).isEmpty()) {
            return;
        }

        materia("Cálculo Diferencial", "Docente de ejemplo 1",
                List.of("LUNES", "MIERCOLES"), "07:00", "09:00", "A-101");
        Materia algebra = materia("Álgebra Lineal", "Docente de ejemplo 2",
                List.of("MARTES", "JUEVES"), "09:00", "11:00", "A-102");
        Materia poo = materia("Programación Orientada a Objetos", "Docente de ejemplo 3",
                List.of("LUNES", "VIERNES"), "11:00", "13:00", "B-201");
        Materia estructuras = materia("Estructuras de Datos", "Docente de ejemplo 4",
                List.of("MARTES", "JUEVES"), "14:00", "16:00", "B-202");
        Materia bases = materia("Bases de Datos", "Docente de ejemplo 5",
                List.of("MIERCOLES", "VIERNES"), "14:00", "16:00", "C-301");

        // Cálculo Diferencial: sin notas a propósito.

        // Álgebra Lineal: corte 1 completo (100%) y corte 2 incompleto (60%).
        nota(algebra, 1, "Parcial 1", 3.5, 30.0);
        nota(algebra, 1, "Taller 1", 4.0, 30.0);
        nota(algebra, 1, "Quiz 1", 3.0, 40.0);
        nota(algebra, 2, "Parcial 2", 2.8, 30.0);
        nota(algebra, 2, "Taller 2", 3.2, 30.0);

        // Programación Orientada a Objetos: corte 1 suma 80%.
        nota(poo, 1, "Parcial", 3.0, 30.0);
        nota(poo, 1, "Taller", 3.5, 30.0);
        nota(poo, 1, "Proyecto", 4.0, 20.0);

        // Estructuras de Datos: los tres cortes suman 100%.
        for (int corte = 1; corte <= 3; corte++) {
            nota(estructuras, corte, "Parcial " + corte, 3.6, 30.0);
            nota(estructuras, corte, "Taller " + corte, 4.1, 30.0);
            nota(estructuras, corte, "Proyecto " + corte, 3.8, 40.0);
        }

        // Bases de Datos: corte 1 suma 70%.
        nota(bases, 1, "Parcial", 3.8, 40.0);
        nota(bases, 1, "Taller", 4.2, 30.0);

        cargarMateriasDeRiesgo();
    }

    /** Una materia por cada caso del documento de la fórmula de riesgo (SCRUM-65, RF-03). */
    private void cargarMateriasDeRiesgo() {
        materiaConCortes("Física Mecánica", "D-101", 3.0, 2.5);
        materiaConCortes("Química General", "D-102", 2.5, 2.5);
        materiaConCortes("Estadística", "D-103", 1.9, 3.3);
        materiaConCortes("Probabilidad", "D-104", 0.5, 3.5);
        materiaConCortes("Física Eléctrica", "D-105", 0.5, 3.0);
        materiaConCortes("Ecuaciones Diferenciales", "D-106", 1.5, 2.0);
        materiaConCortes("Inglés I", "D-107", 5.0, 5.0);
        materiaConCortes("Ética", "D-108", 3.0, 3.0, 3.0);
        materiaConCortes("Contabilidad", "D-109", 2.6, 3.1, 3.1);
        materiaConCortes("Economía", "D-110", 2.8, 3.0, 3.0);
        materiaConCortes("Sistemas Operativos", "D-111", 2.9, 2.9, 2.9);
    }

    /** Materia con una nota de 100% en cada corte, en orden (corte 1, 2 y 3). */
    private void materiaConCortes(String nombre, String salon, double... notaDeCadaCorte) {
        Materia materia = materia(nombre, "Docente de ejemplo", List.of("SABADO"),
                "08:00", "10:00", salon);
        for (int i = 0; i < notaDeCadaCorte.length; i++) {
            int corte = i + 1;
            nota(materia, corte, "Definitiva del corte " + corte, notaDeCadaCorte[i], 100.0);
        }
    }

    private Materia materia(String nombre, String docente, List<String> dias,
                            String horaInicio, String horaFin, String salon) {
        return materias.guardar(new Materia(null, ESTUDIANTE_PRUEBA_ID, nombre, docente, dias,
                LocalTime.parse(horaInicio), LocalTime.parse(horaFin), salon));
    }

    private void nota(Materia materia, int corte, String descripcion,
                      double valor, double porcentaje) {
        notas.guardar(new Nota(null, materia.getId(), corte, descripcion,
                valor, porcentaje, null));
    }
}
