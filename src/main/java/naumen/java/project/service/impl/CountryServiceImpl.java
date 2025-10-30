package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import naumen.java.project.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления странами
 *
 * @author Daniil Mezev
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    private final CountryRepository repository;

    public CountryServiceImpl(CountryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Country> findAll() {
        return repository.findAll();
    }

    @Override
    public Country findById(String id) {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found: " + normId));
    }

    @Override
    public Country create(CountryRequest request) {
        String normId = normalize(request.id());
        if (repository.existsById(normId)) {
            throw new IllegalArgumentException("Country already exists: " + normId);
        }
        Country entity = new Country(normId, request.name());
        return repository.save(entity);
    }

    @Override
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

    @Override
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
