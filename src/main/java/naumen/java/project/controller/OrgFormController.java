package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.OrgFormRequestDTO;
import naumen.java.project.dto.OrgFormResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.OrgFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final OrgFormMapper mapper;

    public OrgFormController(OrgFormService service, OrgFormMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /** Возвращает список всех организационно-правовых форм из справочника */
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<OrgFormResponseDTO>> getAll() {
        List<OrgForm> entities = service.findAll();
        List<OrgFormResponseDTO> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает организационно-правовую форму по её идентификатору */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<OrgFormResponseDTO> getById(@PathVariable String id)
            throws ResourceNotFoundException {
        OrgForm entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись организационно-правовой формы в справочнике */
    @PostMapping
    @Transactional
    public ResponseEntity<OrgFormResponseDTO> create(@Valid @RequestBody OrgFormRequestDTO req) {
        OrgForm toCreate = new OrgForm(req.id(), req.name());

        OrgForm created = service.save(toCreate);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись организационно-правовой формы по идентификатору */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<OrgFormResponseDTO> update(@PathVariable String id,
                                                     @Valid @RequestBody OrgFormRequestDTO req)
            throws ResourceNotFoundException {

        OrgForm toUpdate = service.findById(id);

        toUpdate.setName(req.name());

        OrgForm updated = service.save(toUpdate);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /** Удаляет запись организационно-правовой формы по идентификатору */
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable String id)
            throws ResourceNotFoundException {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
