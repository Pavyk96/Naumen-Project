package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация сервиса для управления странами
 *
 * @author Daniil Mezev
 */
@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    /** Возвращает все страны */
    @Transactional(readOnly = true)
    public List<Country> findAll() {
        return repository.findAll();
    }

    /** Возвращает страну по идентификатору */
    @Transactional(readOnly = true)
    public Country findById(String id) {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found: " + normId));
    }

    /** Создаёт новую страну */
    @Transactional
    public Country create(CountryRequest request) {
        String normId = normalize(request.id());
        if (repository.existsById(normId)) {
            throw new IllegalArgumentException("Country already exists: " + normId);
        }
        Country entity = new Country(normId, request.name());
        return repository.save(entity);
    }

    /** Обновляет страну по идентификатору */
    @Transactional
    public Country update(String id, CountryRequest request) {
        String normPathId = normalize(id);
        String normBodyId = normalize(request.id());
        if (!normPathId.equals(normBodyId)) {
            throw new IllegalArgumentException("Path id and body id must be equal");
        }

        Country existing = repository.findById(normPathId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found: " + normPathId));

        existing.setName(request.name());
        return repository.save(existing);
    }

    /** Удаляет страну по идентификатору */
    @Transactional
    public void delete(String id) {
        String normId = normalize(id);
        if (!repository.existsById(normId)) {
            throw new EntityNotFoundException("Country not found: " + normId);
        }
        repository.deleteById(normId);
    }

    /** Нормализация айди */
    private static String normalize(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }

}
