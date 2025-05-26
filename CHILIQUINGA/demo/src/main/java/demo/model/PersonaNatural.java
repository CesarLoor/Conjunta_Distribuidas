package demo.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class PersonaNatural extends Cliente {

    private int edad;
    private double ingresoMensual;

    @Override
    public double getIngresoReferencial() {
        return ingresoMensual;
    }

    @Override
    public boolean esAptoParaCredito() {
        return getPuntajeCrediticio() > 650;
    }
}
