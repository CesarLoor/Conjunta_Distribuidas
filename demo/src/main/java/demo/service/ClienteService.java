package demo.service;

import demo.dto.ClienteRequestDTO;
import demo.dto.DeudaDTO;
import demo.dto.ResultadoEvaluacionDTO;
import demo.dto.HistorialEvaluacionDTO;
import demo.model.*;
import demo.repository.ClienteRepository;
import demo.repository.HistorialEvaluacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final HistorialEvaluacionRepository historialRepository;

    private final List<EvaluadorRiesgo> evaluadores = List.of(
            new EvaluadorRiesgoBajo(),
            new EvaluadorRiesgoMedio(),
            new EvaluadorRiesgoAlto()
    );

    public ResultadoEvaluacionDTO evaluarRiesgo(ClienteRequestDTO dto) {
        // 1️⃣ Construir el cliente
        Cliente cliente;
        if ("NATURAL".equalsIgnoreCase(dto.getTipoCliente())) {
            PersonaNatural natural = new PersonaNatural();
            natural.setEdad(dto.getEdad());
            natural.setIngresoMensual(dto.getIngresoMensual());
            cliente = natural;
        } else {
            PersonaJuridica juridica = new PersonaJuridica();
            juridica.setAntiguedadAnios(dto.getAntiguedadAnios());
            juridica.setIngresoAnual(dto.getIngresoAnual());
            juridica.setEmpleados(dto.getEmpleados());
            cliente = juridica;
        }

        cliente.setNombre(dto.getNombre());
        cliente.setPuntajeCrediticio(dto.getPuntajeCrediticio());
        cliente.setMontoSolicitado(dto.getMontoSolicitado());
        cliente.setPlazoEnMeses(dto.getPlazoEnMeses());
        cliente.setDeudasActuales(dto.getDeudasActuales().stream()
                .map(d -> {
                    Deuda deuda = new Deuda();
                    deuda.setMonto(d.getMonto());
                    deuda.setPlazoMeses(d.getPlazoMeses());
                    return deuda;
                })
                .collect(Collectors.toList())
        );

        clienteRepository.save(cliente);

        // 2️⃣ Calcular puntaje final y nivel real
        int puntajeFinal = calcularPuntajeFinal(cliente);
        String nivelRiesgo = determinarNivelRiesgo(puntajeFinal);

        // 3️⃣ Seleccionar el evaluador final según nivel real
        EvaluadorRiesgo evaluadorFinal = switch (nivelRiesgo) {
            case "BAJO" -> new EvaluadorRiesgoBajo();
            case "MEDIO" -> new EvaluadorRiesgoMedio();
            default -> new EvaluadorRiesgoAlto();
        };

        // 4️⃣ Generar resultado final (usando evaluador final)
        ResultadoEvaluacion resultado = evaluadorFinal.evaluar(cliente);
        resultado.setPuntajeFinal(puntajeFinal);

        // 5️⃣ Guardar historial
        HistorialEvaluacion historial = new HistorialEvaluacion();
        historial.setCliente(cliente);
        historial.setClienteNombre(cliente.getNombre());
        historial.setTipoCliente(dto.getTipoCliente());
        historial.setMontoSolicitado(cliente.getMontoSolicitado());
        historial.setPlazoEnMeses(cliente.getPlazoEnMeses());
        historial.setNivelRiesgo(resultado.getNivelRiesgo());
        historial.setAprobado(resultado.isAprobado());
        historial.setFechaConsulta(LocalDateTime.now());

        historialRepository.save(historial);

        // 6️⃣ Preparar DTO de respuesta
        ResultadoEvaluacionDTO response = new ResultadoEvaluacionDTO();
        response.setNivelRiesgo(resultado.getNivelRiesgo());
        response.setAprobado(resultado.isAprobado());
        response.setPuntajeFinal(puntajeFinal);
        response.setMensaje(resultado.getMensaje());
        response.setTasaInteres(resultado.getTasaInteres());
        response.setPlazoAprobado(resultado.getPlazoAprobado());

        return response;
    }

    private int calcularPuntajeFinal(Cliente cliente) {
        int puntaje = 100;

        // Penalización por puntaje crediticio
        if (cliente.getPuntajeCrediticio() < 650) {
            puntaje -= 30;
        }

        double ingreso = cliente.getIngresoReferencial();
        double deuda = cliente.getMontoDeudas();
        double montoSolicitado = cliente.getMontoSolicitado();

        if (cliente instanceof PersonaNatural) {
            if (deuda / ingreso > 0.4) puntaje -= 15;
            if (montoSolicitado / ingreso > 0.5) puntaje -= 10;
        } else if (cliente instanceof PersonaJuridica) {
            if (deuda / ingreso > 0.35) puntaje -= 20;
            if (montoSolicitado / ingreso > 0.3) puntaje -= 15;
        }

        return puntaje;
    }

    private String determinarNivelRiesgo(int puntaje) {
        if (puntaje >= 80) {
            return "BAJO";
        } else if (puntaje >= 60) {
            return "MEDIO";
        } else {
            return "ALTO";
        }
    }

    public List<HistorialEvaluacionDTO> obtenerHistorial(Long clienteId) {
        return historialRepository.findByClienteId(clienteId)
                .stream()
                .map(h -> {
                    HistorialEvaluacionDTO dto = new HistorialEvaluacionDTO();
                    dto.setId(h.getId());
                    dto.setClienteNombre(h.getClienteNombre());
                    dto.setTipoCliente(h.getTipoCliente());
                    dto.setMontoSolicitado(h.getMontoSolicitado());
                    dto.setPlazoEnMeses(h.getPlazoEnMeses());
                    dto.setNivelRiesgo(h.getNivelRiesgo());
                    dto.setAprobado(h.isAprobado());
                    dto.setFechaConsulta(h.getFechaConsulta());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
