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
                .collect(Collectors.toList()));

        // 2️⃣ Evaluar el riesgo
        ResultadoEvaluacion resultado = evaluarRiesgoCliente(cliente);

        // 3️⃣ Guardar en base de datos
        cliente = clienteRepository.save(cliente);
        
        // 4️⃣ Guardar en el historial
        HistorialEvaluacion historial = new HistorialEvaluacion();
        historial.setCliente(cliente);
        historial.setMontoSolicitado(dto.getMontoSolicitado());
        historial.setPlazoEnMeses(dto.getPlazoEnMeses());
        historial.setNivelRiesgo(resultado.getNivelRiesgo());
        historial.setAprobado(resultado.isAprobado());
        historial.setFechaConsulta(LocalDateTime.now());
        historialRepository.save(historial);

        // 5️⃣ Retornar el DTO de resultado
        return convertirAResultadoDTO(resultado);
    }

    private ResultadoEvaluacion evaluarRiesgoCliente(Cliente cliente) {
        return evaluadores.stream()
                .filter(evaluador -> evaluador.aplica(cliente))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se pudo evaluar el riesgo del cliente"))
                .evaluar(cliente);
    }

    private ResultadoEvaluacionDTO convertirAResultadoDTO(ResultadoEvaluacion resultado) {
        ResultadoEvaluacionDTO dto = new ResultadoEvaluacionDTO();
        dto.setNivelRiesgo(resultado.getNivelRiesgo());
        dto.setAprobado(resultado.isAprobado());
        dto.setPuntajeFinal(resultado.getPuntajeFinal());
        dto.setMensaje(resultado.getMensaje());
        dto.setTasaInteres(resultado.getTasaInteres());
        dto.setPlazoAprobado(resultado.getPlazoAprobado());
        return dto;
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
