package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
import naumen.java.project.service.IndustryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления индустриями
 *
 * @author Daniil Mezev
 */
@Service
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository repository;
    private final IndustryMapper mapper;

    public IndustryServiceImpl(IndustryRepository repository, IndustryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<IndustryResponse> findAll() {
        List<Industry> entities = repository.findAll();
        List<IndustryResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return responses;
    }

    @Override
    public IndustryResponse findById(Long id) {
        Industry entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));
        IndustryResponse response = mapper.toResponse(entity);
        return response;
    }

    @Override
    public IndustryResponse create(IndustryRequest request) {
        Long reqId = request.id();
        boolean exists = repository.existsById(reqId);
        if (exists) {
            throw new IllegalArgumentException("Industry already exists: " + reqId);
        }

        Industry toSave = mapper.toEntity(request);
        Industry saved = repository.save(toSave);
        IndustryResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public IndustryResponse update(Long id, IndustryRequest request) {
        Industry existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));

        String newName = request.name();
        existing.setName(newName);

        Industry saved = repository.save(existing);
        IndustryResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public void delete(Long id) {
        Industry existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));
        repository.delete(existing);
    }

}
