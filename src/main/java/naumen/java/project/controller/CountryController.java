package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.mapper.CountryMapper;
import naumen.java.project.model.Country;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CountryResponse>> getAll() {
        List<Country> entities = service.findAll();
        List<CountryResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает страну по её идентификатору. */
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getById(@PathVariable String id) {
        Country entity = service.findById(id);
        return ResponseEntity.ok(mapper.toResponse(entity));
    }

    /** Создаёт новую запись страны в справочнике. */
    @PostMapping
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest req) {
        Country created = service.create(req);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    /** Обновляет существующую запись страны по идентификатору. */
    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> update(@PathVariable String id,
                                                  @Valid @RequestBody CountryRequest req) {
        Country updated = service.update(id, req);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /** Удаляет запись страны по идентификатору. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
