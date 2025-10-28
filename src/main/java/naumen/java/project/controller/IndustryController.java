package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.service.IndustryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления справочником индустрий
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/industry")
public class IndustryController {

    private final IndustryService service;

    public IndustryController(IndustryService service) {
        this.service = service;
    }

    /** Возвращает список всех индустрий из справочника. */
    @GetMapping("/all")
    public ResponseEntity<List<IndustryResponse>> getAll() {
        List<IndustryResponse> responses = service.findAll();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает индустрию по её идентификатору. */
    @GetMapping("/{id}")
    public ResponseEntity<IndustryResponse> getById(@PathVariable Long id) {
        IndustryResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }


    /** Создаёт новую запись индустрии в справочнике. */
    @PostMapping
    public ResponseEntity<IndustryResponse> create(@Valid @RequestBody IndustryRequest req) {
        IndustryResponse created = service.create(req);
        return ResponseEntity.ok(created);
    }

    /** Обновляет существующую запись индустрии по идентификатору. */
    @PutMapping("/{id}")
    public ResponseEntity<IndustryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody IndustryRequest req) {
        IndustryResponse updated = service.update(id, req);
        return ResponseEntity.ok(updated);
    }

    /** Удаляет запись индустрии по идентификатору. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
