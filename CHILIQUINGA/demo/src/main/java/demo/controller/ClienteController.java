package demo.controller;

import demo.dto.ClienteRequestDTO;
import demo.dto.ResultadoEvaluacionDTO;
import demo.dto.HistorialEvaluacionDTO;
import demo.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/evaluar-riesgo")
    public ResponseEntity<ResultadoEvaluacionDTO> evaluarRiesgo(@RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.evaluarRiesgo(dto));
    }

    @GetMapping("/historial/{idCliente}")
    public ResponseEntity<List<HistorialEvaluacionDTO>> obtenerHistorial(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.obtenerHistorial(idCliente));
    }
}
