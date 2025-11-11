package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import naumen.java.project.service.OrgFormService;


import java.util.List;

/**
 * REST-контроллер для работы со справочником организационно-правовых форм
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/org_form")
public class OrgFormController {

    private final OrgFormService service;
    private final OrgFormMapper mapper;

    public OrgFormController(OrgFormService service, OrgFormMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /** Возвращает список всех организационно-правовых форм из справочника */
    @GetMapping("/all")
    public ResponseEntity<List<OrgFormResponse>> getAll() {
        List<OrgForm> entities = service.findAll();
        List<OrgFormResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает организационно-правовую форму по её идентификатору */
    @GetMapping("/{id}")
    public ResponseEntity<OrgFormResponse> getById(@PathVariable String id) {
        OrgForm entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись организационно-правовой формы в справочнике */
    @PostMapping
    public ResponseEntity<OrgFormResponse> create(@Valid @RequestBody OrgFormRequest req) {
        OrgForm created = service.create(req);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись организационно-правовой формы по идентификатору */
    @PutMapping("/{id}")
    public ResponseEntity<OrgFormResponse> update(@PathVariable String id,
                                                  @Valid @RequestBody OrgFormRequest req) {
        OrgForm updated = service.update(id, req);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /** Удаляет запись организационно-правовой формы по идентификатору */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
