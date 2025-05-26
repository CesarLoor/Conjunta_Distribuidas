package demo.controller;

import demo.dto.ClienteRequestDTO;
import demo.dto.ResultadoEvaluacionDTO;
import demo.dto.HistorialEvaluacionDTO;
import demo.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/evaluar-riesgo")
    public ResultadoEvaluacionDTO evaluarRiesgo(@RequestBody ClienteRequestDTO dto) {
        return clienteService.evaluarRiesgo(dto);
    }

    @GetMapping("/historial/{idCliente}")
    public List<HistorialEvaluacionDTO> obtenerHistorial(@PathVariable Long idCliente) {
        return clienteService.obtenerHistorial(idCliente);
    }
}
