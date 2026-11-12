package co.edu.autonoma.barometro.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Materia que cursa un estudiante (RF-01), según el modelo de datos de SCRUM-8.
 *
 * <p>Clase Java simple, sin anotaciones JPA: en este sprint los datos viven en
 * memoria. El id lo asigna {@code MateriaRepository} al guardar.</p>
 *
 * <p>Obligatorios (CA-01.2): nombre, docente, días, hora de inicio, hora de fin
 * y salón. {@link #tieneCamposObligatorios()} permite comprobarlo; el mensaje de
 * error al usuario lo devuelve Backend (CA-01.4).</p>
 */
public class Materia {

    private Long id;
    private Long idEstudiante;
    private String nombre;
    private String docente;
    private List<String> dias = new ArrayList<>();
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String salon;

    public Materia() {
    }

    public Materia(Long id, Long idEstudiante, String nombre, String docente,
                   List<String> dias, LocalTime horaInicio, LocalTime horaFin,
                   String salon) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.docente = docente;
        this.dias = copiar(dias);
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.salon = salon;
    }

    /** true si están todos los campos obligatorios de CA-01.2. */
    public boolean tieneCamposObligatorios() {
        return tieneTexto(nombre)
                && tieneTexto(docente)
                && dias != null && !dias.isEmpty()
                && horaInicio != null
                && horaFin != null
                && tieneTexto(salon);
    }

    private static List<String> copiar(List<String> dias) {
        return dias == null ? new ArrayList<>() : new ArrayList<>(dias);
    }

    private static boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Long idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public List<String> getDias() {
        return dias;
    }

    public void setDias(List<String> dias) {
        this.dias = copiar(dias);
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }
}
