package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.mapper.CountryMapper;
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
public class CountryServiceImpl implements CountryService {

    private final CountryRepository repository;
    private final CountryMapper mapper;

    public CountryServiceImpl(CountryRepository repository, CountryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<CountryResponse> findAll() {
        List<Country> entities = repository.findAll();
        List<CountryResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return responses;
    }

    @Override
    public CountryResponse findById(String id) {
        String normId = id.toUpperCase();
        Country entity = repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found: " + normId));
        CountryResponse response = mapper.toResponse(entity);
        return response;
    }

    @Override
    public CountryResponse create(CountryRequest request) {
        String normId = request.id().toUpperCase();
        boolean exists = repository.existsById(normId);
        if (exists) {
            throw new IllegalArgumentException("Country already exists: " + normId);
        }

        Country entityToSave = mapper.toEntity(request);
        Country saved = repository.save(entityToSave);
        CountryResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public CountryResponse update(String id, CountryRequest request) {
        String normId = id.toUpperCase();
        Country existing = repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found: " + normId));

        String newName = request.name();
        existing.setName(newName);

        Country saved = repository.save(existing);
        CountryResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public void delete(String id) {
        String normId = id.toUpperCase();
        boolean exists = repository.existsById(normId);
        if (!exists) {
            throw new EntityNotFoundException("Country not found: " + normId);
        }
        repository.deleteById(normId);
    }

}
