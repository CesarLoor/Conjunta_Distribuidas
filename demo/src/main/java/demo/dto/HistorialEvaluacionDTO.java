package demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class HistorialEvaluacionDTO {

    private Long id;
    private String clienteNombre;
    private String tipoCliente;
    private double montoSolicitado;
    private int plazoEnMeses;
    private String nivelRiesgo;
    private boolean aprobado;
    private LocalDateTime fechaConsulta;
}
