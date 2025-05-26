package demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int puntajeCrediticio;
    private double montoSolicitado;
    private int plazoEnMeses;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Deuda> deudasActuales;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<HistorialEvaluacion> historialEvaluaciones;

    public double getMontoDeudas() {
        return deudasActuales != null ?
                deudasActuales.stream()
                        .mapToDouble(Deuda::getMonto)
                        .sum() : 0.0;
    }

    public abstract double getIngresoReferencial();

    public abstract boolean esAptoParaCredito();
}
