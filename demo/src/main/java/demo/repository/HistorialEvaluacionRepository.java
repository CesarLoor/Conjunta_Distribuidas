package demo.repository;

import demo.model.HistorialEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEvaluacionRepository extends JpaRepository<HistorialEvaluacion, Long> {
    List<HistorialEvaluacion> findByClienteId(Long clienteId);
}
