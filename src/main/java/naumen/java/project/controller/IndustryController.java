package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import naumen.java.project.service.IndustryService;

import java.util.List;

/**
 * REST-контроллер для управления справочником индустрий
 *
 * @author Daniil Meзев
 */
@RestController
@RequestMapping("/industry")
public class IndustryController {

    private final IndustryService service;
    private final IndustryMapper mapper;

    public IndustryController(IndustryService service, IndustryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /** Возвращает список всех индустрий из справочника. */
    @GetMapping("/all")
    public ResponseEntity<List<IndustryResponse>> getAll() {
        List<Industry> entities = service.findAll();
        List<IndustryResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает индустрию по её идентификатору. */
    @GetMapping("/{id}")
    public ResponseEntity<IndustryResponse> getById(@PathVariable Long id) {
        Industry entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись индустрии в справочнике. */
    @PostMapping
    public ResponseEntity<IndustryResponse> create(@Valid @RequestBody IndustryRequest req) {
        Industry created = service.create(req);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись индустрии по идентификатору. */
    @PutMapping("/{id}")
    public ResponseEntity<IndustryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody IndustryRequest req) {
        Industry updated = service.update(id, req);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /** Удаляет запись индустрии по идентификатору. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
