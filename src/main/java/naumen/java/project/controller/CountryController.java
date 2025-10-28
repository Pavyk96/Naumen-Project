package naumen.java.project.controller;

import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public CountryController(CountryService service) {
        this.service = service;
    }

    /** Возвращает список всех стран из справочника. */
    @GetMapping("/all")
    public ResponseEntity<List<CountryResponse>> getAll() {
        List<CountryResponse> responses = service.findAll();
        return ResponseEntity.ok(responses);
    }

    /** Возвращает страну по её идентификатору. */
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getById(@PathVariable String id) {
        String normId = id.toUpperCase();
        CountryResponse response = service.findById(normId);
        return ResponseEntity.ok(response);
    }

    /** Создаёт новую запись страны в справочнике. */
    @PostMapping
    public ResponseEntity<CountryResponse> create(@Valid @RequestBody CountryRequest req) {
        CountryResponse created = service.create(req);
        return ResponseEntity.ok(created);
    }

    /** Обновляет существующую запись страны по идентификатору. */
    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> update(@PathVariable String id,
                                                  @Valid @RequestBody CountryRequest req) {
        String normId = id.toUpperCase();
        CountryResponse updated = service.update(normId, req);
        return ResponseEntity.ok(updated);
    }

    /** Удаляет запись страны по идентификатору. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String normId = id.toUpperCase();
        service.delete(normId);
        return ResponseEntity.ok().build();
    }

}
