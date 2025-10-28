package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import naumen.java.project.service.OrgFormService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления организационно правовыми формами
 *
 * @author Daniil Mezev
 */
@Service
public class OrgFormServiceImpl implements OrgFormService {

    private final OrgFormRepository repository;
    private final OrgFormMapper mapper;

    public OrgFormServiceImpl(OrgFormRepository repository, OrgFormMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<OrgFormResponse> findAll() {
        List<OrgForm> entities = repository.findAll();
        List<OrgFormResponse> responses = entities.stream()
                .map(mapper::toResponse)
                .toList();
        return responses;
    }

    @Override
    public OrgFormResponse findById(String id) {
        String normId = id.toUpperCase();
        OrgForm entity = repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normId));
        OrgFormResponse response = mapper.toResponse(entity);
        return response;
    }

    @Override
    public OrgFormResponse create(OrgFormRequest request) {
        String normId = request.id().toUpperCase();
        boolean exists = repository.existsById(normId);
        if (exists) {
            throw new IllegalArgumentException("OrgForm already exists: " + normId);
        }

        OrgForm toSave = mapper.toEntity(request);
        OrgForm saved = repository.save(toSave);
        OrgFormResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public OrgFormResponse update(String id, OrgFormRequest request) {
        String normId = id.toUpperCase();
        OrgForm existing = repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normId));

        String newName = request.name();
        existing.setName(newName);

        OrgForm saved = repository.save(existing);
        OrgFormResponse response = mapper.toResponse(saved);
        return response;
    }

    @Override
    public void delete(String id) {
        String normId = id.toUpperCase();
        OrgForm existing = repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normId));
        repository.delete(existing);
    }

}
