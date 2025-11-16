package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.IndustryRequestDTO;
import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import naumen.java.project.service.IndustryService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final IndustryMapper mapper;

    public IndustryController(IndustryService service, IndustryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /** Возвращает список всех индустрий из справочника. */
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<IndustryResponseDTO>> getAll() {
        List<Industry> entities = service.findAll();
        List<IndustryResponseDTO> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает индустрию по её идентификатору. */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<IndustryResponseDTO> getById(@PathVariable Long id)
            throws ResourceNotFoundException {
        Industry entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись индустрии в справочнике. */
    @PostMapping
    @Transactional
    public ResponseEntity<IndustryResponseDTO> create(@Valid @RequestBody IndustryRequestDTO req) {
        Industry toCreate = new Industry(req.id(), req.name());

        Industry created = service.create(toCreate);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись индустрии по идентификатору. */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<IndustryResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody IndustryRequestDTO req)
            throws ResourceNotFoundException {
        Industry toUpdate = new Industry();
        toUpdate.setId(req.id());
        toUpdate.setName(req.name());

        Industry updated = service.update(id, toUpdate);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    /** Удаляет запись индустрии по идентификатору. */
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id)
            throws ResourceNotFoundException {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
