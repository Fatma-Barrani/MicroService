package tn.esprit.spring.microserviceproject.Services;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.spring.microserviceproject.Dtos.*;

@FeignClient(name = "Examen")
public interface ExamenClient {

    @GetMapping("/api/examens/GetAllExamens")
    List<ExamenDto> getAllExamens();

    @GetMapping("/api/examens/ExamenById/{id}")
    ExamenDto getExamenById(@PathVariable Long id);

    
}
