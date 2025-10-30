package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import naumen.java.project.service.OrgFormService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для работы со справочником организационно-правовых форм
 *
 * @author Daniil Mezev
 */
@Service
@Transactional
public class OrgFormServiceImpl implements OrgFormService {

    private final OrgFormRepository repository;

    public OrgFormServiceImpl(OrgFormRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OrgForm> findAll() {
        return repository.findAll();
    }

    @Override
    public OrgForm findById(String id) {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normId));
    }

    @Override
    public OrgForm create(OrgFormRequest request) {
        String normId = normalize(request.id());
        if (repository.existsById(normId)) {
            throw new IllegalArgumentException("OrgForm already exists: " + normId);
        }
        OrgForm entity = new OrgForm(normId, request.name());
        return repository.save(entity);
    }

    @Override
    public OrgForm update(String id, OrgFormRequest request) {
        String normPathId = normalize(id);
        String normBodyId = normalize(request.id());
        if (!normPathId.equals(normBodyId)) {
            throw new IllegalArgumentException("Path id and body id must be equal");
        }

        OrgForm existing = repository.findById(normPathId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normPathId));

        existing.setName(request.name());
        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        String normId = normalize(id);
        if (!repository.existsById(normId)) {
            throw new EntityNotFoundException("OrgForm not found: " + normId);
        }
        repository.deleteById(normId);
    }

    private static String normalize(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }

}
