package co.edu.autonoma.barometro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Alerta de riesgo de una materia en un corte (RF-05), según el contrato del Sprint 3.
 *
 * <p>Clase Java simple, sin anotaciones JPA: en este sprint los datos viven en
 * memoria. El id lo asigna {@code AlertaRepository} al guardar.</p>
 *
 * <p>Backend la crea al registrar una nota, solo si la materia tiene información
 * completa (RN-01) y queda en Nublado o Tormenta. Hay como máximo una alerta por
 * materia y corte. Guarda la fecha de generación y el corte para la trazabilidad
 * (CA-05.8) y los días que faltan para el cierre del corte en curso (CA-05.3).</p>
 */
public class Alerta {

    private Long id;
    private Long idMateria;
    private Integer corte;
    private NivelRiesgo nivelRiesgo;
    private Double notaNecesaria;
    private LocalDateTime fechaGeneracion;
    private LocalDate fechaCierreCorte;
    private Integer diasParaCierre;

    public Alerta() {
    }

    public Alerta(Long id, Long idMateria, Integer corte, NivelRiesgo nivelRiesgo,
                  Double notaNecesaria, LocalDateTime fechaGeneracion,
                  LocalDate fechaCierreCorte, Integer diasParaCierre) {
        this.id = id;
        this.idMateria = idMateria;
        this.corte = corte;
        this.nivelRiesgo = nivelRiesgo;
        this.notaNecesaria = notaNecesaria;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaCierreCorte = fechaCierreCorte;
        this.diasParaCierre = diasParaCierre;
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

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public Double getNotaNecesaria() {
        return notaNecesaria;
    }

    public void setNotaNecesaria(Double notaNecesaria) {
        this.notaNecesaria = notaNecesaria;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDate getFechaCierreCorte() {
        return fechaCierreCorte;
    }

    public void setFechaCierreCorte(LocalDate fechaCierreCorte) {
        this.fechaCierreCorte = fechaCierreCorte;
    }

    public Integer getDiasParaCierre() {
        return diasParaCierre;
    }

    public void setDiasParaCierre(Integer diasParaCierre) {
        this.diasParaCierre = diasParaCierre;
    }
}
