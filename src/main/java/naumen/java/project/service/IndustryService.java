package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления индустриями
 *
 * @author Daniil Mezev
 */
@Service
public class IndustryService {

    private final IndustryRepository repository;

    public IndustryService(IndustryRepository repository) {
        this.repository = repository;
    }

    /** Возвращает все индустрии */
    @Transactional(readOnly = true)
    public List<Industry> findAll() {
        return repository.findAll();
    }

    /** Возвращает индустрию по идентификатору */
    @Transactional(readOnly = true)
    public Industry findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));
    }

    /** Создаёт новую индустрию */
    @Transactional
    public Industry create(IndustryRequest request) {
        Industry toSave = new Industry(request.name());
        return repository.save(toSave);
    }

    /** Обновляет индустрию по идентификатору */
    @Transactional
    public Industry update(Long id, IndustryRequest request) {
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("Path id and body id must be equal");
        }

        Industry existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));

        existing.setName(request.name());
        return repository.save(existing);
    }

    /** Удаляет индустрию по идентификатору */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Industry not found: " + id);
        }
        repository.deleteById(id);
    }

}
