package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.service.OrgFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public OrgFormController(OrgFormService service) {
        this.service = service;
    }

    /** Возвращает список всех организационно-правовых форм из справочника. */
    @GetMapping("/all")
    public ResponseEntity<List<OrgFormResponse>> getAll() {
        List<OrgFormResponse> responses = service.findAll();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает организационно-правовую форму по её идентификатору. */
    @GetMapping("/{id}")
    public ResponseEntity<OrgFormResponse> getById(@PathVariable String id) {
        OrgFormResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    /** Создаёт новую запись организационно-правовой формы в справочнике. */
    @PostMapping
    public ResponseEntity<OrgFormResponse> create(@Valid @RequestBody OrgFormRequest req) {
        OrgFormResponse created = service.create(req);
        return ResponseEntity.ok(created);
    }

    /** Обновляет существующую запись организационно-правовой формы по идентификатору. */
    @PutMapping("/{id}")
    public ResponseEntity<OrgFormResponse> update(@PathVariable String id,
                                                  @Valid @RequestBody OrgFormRequest req) {
        OrgFormResponse updated = service.update(id, req);
        return ResponseEntity.ok(updated);
    }

    /** Удаляет запись организационно-правовой формы по идентификатору. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
