package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы со справочником организационно-правовых форм
 *
 * @author Daniil Mezev
 */
@Service
@Transactional
public class OrgFormService {

    private final OrgFormRepository repository;

    public OrgFormService(OrgFormRepository repository) {
        this.repository = repository;
    }

    /** Возвращает все организационно-правовые формы */
    public List<OrgForm> findAll() {
        return repository.findAll();
    }

    /** Ищет организационно-правовую форму по идентификатору */
    public OrgForm findById(String id) {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new EntityNotFoundException("OrgForm not found: " + normId));
    }

    /** Создаёт новую организационно-правовую форму */
    public OrgForm create(OrgFormRequest request) {
        String normId = normalize(request.id());
        if (repository.existsById(normId)) {
            throw new IllegalArgumentException("OrgForm already exists: " + normId);
        }
        OrgForm entity = new OrgForm(normId, request.name());
        return repository.save(entity);
    }

    /** Обновляет существующую организационно-правовую форму */
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

    /** Удаляет организационно-правовую форму */
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
