package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.ContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления контрагентами
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/contractor")
public class ContractorController {

    private final ContractorService service;

    public ContractorController(ContractorService service) {
        this.service = service;
    }

    /** Возвращает всех контрагентов */
    @GetMapping("/all")
    public ResponseEntity<List<ContractorResponse>> getAll() {
        List<ContractorResponse> contractors = service.findAll();
        return ResponseEntity.ok(contractors);
    }

    /** Возвращает контрагента по идентификатору */
    @GetMapping("/{id}")
    public ResponseEntity<ContractorResponse> getById(@PathVariable String id) {
        ContractorResponse contractor = service.findById(id);
        return ResponseEntity.ok(contractor);
    }

    /** Создаёт нового контрагента */
    @PostMapping
    public ResponseEntity<ContractorResponse> create(@Valid @RequestBody Contractor contractor) {
        ContractorResponse created = service.create(contractor);
        return ResponseEntity.ok(created);
    }

    /** Обновляет существующего контрагента */
    @PutMapping("/{id}")
    public ResponseEntity<ContractorResponse> update(@PathVariable String id,
                                                     @Valid @RequestBody Contractor contractor) {
        ContractorResponse updated = service.update(id, contractor);
        return ResponseEntity.ok(updated);
    }

    /** Удаляет контрагента */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
