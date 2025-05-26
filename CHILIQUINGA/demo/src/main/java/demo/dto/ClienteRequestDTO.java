package demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ClienteRequestDTO {

    private String tipoCliente; // NATURAL o JURIDICA
    private String nombre;
    private int puntajeCrediticio;
    private List<DeudaDTO> deudasActuales;
    private double montoSolicitado;
    private int plazoEnMeses;

    // Campos específicos para persona natural
    private Integer edad;
    private Double ingresoMensual;

    // Campos específicos para persona jurídica
    private Integer antiguedadAnios;
    private Double ingresoAnual;
    private Integer empleados;
}
