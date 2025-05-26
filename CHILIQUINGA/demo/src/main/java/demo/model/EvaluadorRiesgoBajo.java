package demo.model;

public class EvaluadorRiesgoBajo extends EvaluadorRiesgo {

    @Override
    public boolean aplica(Cliente cliente) {
        int puntajeFinal = calcularPuntajeFinal(cliente);
        return determinarNivelRiesgo(puntajeFinal).equals("BAJO");
    }

    @Override
    protected int calcularPenalizaciones(Cliente cliente, double ingreso, double deuda, double montoSolicitado) {
        int penalizacion = 0;
        if (cliente instanceof PersonaNatural) {
            if (deuda / ingreso > 0.4) penalizacion += 15;
            if (montoSolicitado / ingreso > 0.5) penalizacion += 10;
        } else if (cliente instanceof PersonaJuridica) {
            if (deuda / ingreso > 0.35) penalizacion += 20;
            if (montoSolicitado / ingreso > 0.3) penalizacion += 15;
        }
        return penalizacion;
    }

    @Override
    protected String getNivelRiesgo() {
        return "BAJO";
    }
}
