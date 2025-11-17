package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
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
    public OrgForm findById(String id) throws ResourceNotFoundException {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Организационно-правовая форма",
                        normId
                ));
    }

    /** Создаёт новую организационно-правовую форму */
    public OrgForm create(OrgForm orgForm) {
        String normId = normalize(orgForm.getId());
        orgForm.setId(normId);

        if (repository.existsById(normId)) {
            throw new IllegalArgumentException(
                    "Организационно-правовая форма с id = " + normId + " уже существует"
            );
        }

        return repository.save(orgForm);
    }

    /** Обновляет существующую организационно-правовую форму */
    public OrgForm update(String id, OrgForm orgForm) throws ResourceNotFoundException {
        String normPathId = normalize(id);
        String normBodyId = normalize(orgForm.getId());

        if (!normPathId.equals(normBodyId)) {
            throw new IllegalArgumentException(
                    "Идентификатор в пути (" +
                            normPathId + ") не совпадает с идентификатором в теле запроса (" +
                            normBodyId + ")"
            );
        }

        OrgForm existing = repository.findById(normPathId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Организационно-правовая форма",
                        normPathId
                ));

        existing.setName(orgForm.getName());

        return repository.save(existing);
    }

    /** Удаляет организационно-правовую форму */
    public void delete(String id) throws ResourceNotFoundException {
        String normId = normalize(id);
        if (!repository.existsById(normId)) {
            throw new ResourceNotFoundException(
                    "Организационно-правовая форма",
                    normId
            );
        }
        repository.deleteById(normId);
    }

    /**
     * Нормализация текста
     */
    private String normalize(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }
}
