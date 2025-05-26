package demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResultadoEvaluacion {

    private String nivelRiesgo;
    private boolean aprobado;
    private int puntajeFinal;
    private String mensaje;
    private double tasaInteres;
    private int plazoAprobado;
}
