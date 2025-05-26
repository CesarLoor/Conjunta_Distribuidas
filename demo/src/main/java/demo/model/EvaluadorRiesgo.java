package demo.model;

public abstract class EvaluadorRiesgo {

    protected double puntajeBase = 100;

    public abstract boolean aplica(Cliente cliente);

    public ResultadoEvaluacion evaluar(Cliente cliente) {
        int puntajeFinal = calcularPuntajeFinal(cliente);
        String nivelRiesgo = determinarNivelRiesgo(puntajeFinal);

        // Validación de consistencia
        if (!nivelRiesgo.equals(getNivelRiesgo())) {
            throw new RuntimeException("El nivel real no corresponde a este evaluador.");
        }

        ResultadoEvaluacion resultado = new ResultadoEvaluacion();
        resultado.setPuntajeFinal(puntajeFinal);
        resultado.setNivelRiesgo(nivelRiesgo);

        if ("ALTO".equals(nivelRiesgo)) {
            resultado.setAprobado(false);
            resultado.setMensaje("Cliente no apto para préstamo");
            resultado.setTasaInteres(0);
            resultado.setPlazoAprobado(0);
        } else if ("MEDIO".equals(nivelRiesgo)) {
            resultado.setAprobado(true);
            resultado.setMensaje("Riesgo Medio: Crédito aprobado con condiciones ajustadas.");
            resultado.setTasaInteres(8.0);
            resultado.setPlazoAprobado(cliente.getPlazoEnMeses());
        } else {
            resultado.setAprobado(true);
            resultado.setMensaje("Riesgo Bajo: Crédito aprobado sin restricciones.");
            resultado.setTasaInteres(6.5);
            resultado.setPlazoAprobado(cliente.getPlazoEnMeses());
        }

        return resultado;
    }

    public int calcularPuntajeFinal(Cliente cliente) {
        int puntaje = (int) puntajeBase;

        // Penalización por puntaje crediticio
        if (cliente.getPuntajeCrediticio() < 650) {
            puntaje -= 30;
        }

        double ingreso = cliente.getIngresoReferencial();
        double deuda = cliente.getMontoDeudas();
        double montoSolicitado = cliente.getMontoSolicitado();

        // Penalizaciones específicas
        puntaje -= calcularPenalizaciones(cliente, ingreso, deuda, montoSolicitado);

        return puntaje;
    }

    protected abstract int calcularPenalizaciones(Cliente cliente, double ingreso, double deuda, double montoSolicitado);

    public String determinarNivelRiesgo(int puntaje) {
        if (puntaje >= 80) return "BAJO";
        else if (puntaje >= 60) return "MEDIO";
        else return "ALTO";
    }

    protected abstract String getNivelRiesgo();
}
