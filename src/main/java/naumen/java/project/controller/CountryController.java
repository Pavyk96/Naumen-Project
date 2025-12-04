package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.CountryRequestDTO;
import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.CountryMapper;
import naumen.java.project.model.Country;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import naumen.java.project.service.CountryService;

import java.util.List;

/**
 * REST-контроллер для управления справочником стран
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/country")
public class CountryController {

    private final CountryService service;
    private final CountryMapper mapper;

    public CountryController(CountryService service, CountryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /** Возвращает список всех стран из справочника. */
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CountryResponseDTO>> getAll() {
        List<Country> entities = service.findAll();
        List<CountryResponseDTO> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает страну по её идентификатору. */
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponseDTO> getById(@PathVariable String id)
            throws ResourceNotFoundException {
        Country entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись страны в справочнике. */
    @PostMapping
    @Transactional
    public ResponseEntity<CountryResponseDTO> create(@Valid @RequestBody CountryRequestDTO req) {
        Country toCreate = new Country(req.id(), req.name());

        Country created = service.save(toCreate);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись страны по идентификатору. */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<CountryResponseDTO> update(@PathVariable String id,
                                                     @Valid @RequestBody CountryRequestDTO req)
            throws ResourceNotFoundException {

        Country oldCountry = service.findById(id);

        oldCountry.setName(req.name());

        Country updated = service.save(oldCountry);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    /** Удаляет запись страны по идентификатору. */
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable String id)
            throws ResourceNotFoundException {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
