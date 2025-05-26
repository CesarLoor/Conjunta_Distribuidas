package demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class HistorialEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteNombre;
    private String tipoCliente;
    private double montoSolicitado;
    private int plazoEnMeses;
    private String nivelRiesgo;
    private boolean aprobado;
    private LocalDateTime fechaConsulta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
