package co.edu.autonoma.barometro.model;

import java.time.LocalDateTime;

/**
 * Nota de una materia en un corte (RF-02), según el modelo de datos de SCRUM-8.
 *
 * <p>Clase Java simple, sin anotaciones JPA: en este sprint los datos viven en
 * memoria. El id lo asigna {@code NotaRepository} al guardar.</p>
 *
 * <p>Las reglas (valor de 0.0 a 5.0, porcentaje mayor que 0 y el corte sin pasar
 * del 100%) las valida Backend antes de guardar (CA-02.2, CA-02.3). Los métodos
 * {@code tiene...} son ayudas que Backend puede usar.</p>
 */
public class Nota {

    public static final int CORTE_MINIMO = 1;
    public static final int CORTE_MAXIMO = 3;
    public static final double VALOR_MINIMO = 0.0;
    public static final double VALOR_MAXIMO = 5.0;
    public static final double PORCENTAJE_MAXIMO = 100.0;

    private Long id;
    private Long idMateria;
    private Integer corte;
    private String descripcion;
    private Double valor;
    private Double porcentaje;
    private LocalDateTime fechaRegistro;

    public Nota() {
    }

    public Nota(Long id, Long idMateria, Integer corte, String descripcion,
                Double valor, Double porcentaje, LocalDateTime fechaRegistro) {
        this.id = id;
        this.idMateria = idMateria;
        this.corte = corte;
        this.descripcion = descripcion;
        this.valor = valor;
        this.porcentaje = porcentaje;
        this.fechaRegistro = fechaRegistro;
    }

    /** true si el corte es 1, 2 o 3. */
    public boolean tieneCorteValido() {
        return corte != null && corte >= CORTE_MINIMO && corte <= CORTE_MAXIMO;
    }

    /** true si el valor está entre 0.0 y 5.0, incluidos. */
    public boolean tieneValorEnRango() {
        return valor != null && valor >= VALOR_MINIMO && valor <= VALOR_MAXIMO;
    }

    /** true si el porcentaje es mayor que 0 y no pasa de 100. */
    public boolean tienePorcentajeValido() {
        return porcentaje != null && porcentaje > 0 && porcentaje <= PORCENTAJE_MAXIMO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Long idMateria) {
        this.idMateria = idMateria;
    }

    public Integer getCorte() {
        return corte;
    }

    public void setCorte(Integer corte) {
        this.corte = corte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
