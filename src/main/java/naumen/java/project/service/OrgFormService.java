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

    /**
     * Ищет организационно-правовую форму по идентификатору
     * @throws ResourceNotFoundException если форма с указанным id не найдена в БД
     */
    public OrgForm findById(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Организационно-правовая форма",
                        id
                ));
    }

    /**
     * Сохраняет организационно-правовую форму
     */
    public OrgForm save(OrgForm orgForm) {
        return repository.save(orgForm);
    }

    /**
     * Удаляет организационно-правовую форму
     * @throws ResourceNotFoundException если форма с указанным id отсутствует в БД
     */
    public void delete(String id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Организационно-правовая форма",
                    id
            );
        }
        repository.deleteById(id);
    }
}
