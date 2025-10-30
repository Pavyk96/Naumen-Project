package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import naumen.java.project.dto.IndustryRequest;
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
@Transactional
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository repository;

    public IndustryServiceImpl(IndustryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Industry> findAll() {
        return repository.findAll();
    }

    @Override
    public Industry findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));
    }

    @Override
    public Industry create(IndustryRequest request) {
        // Важно: при IDENTITY игнорируем request.id()
        Industry toSave = new Industry(request.name());
        return repository.save(toSave);
    }

    @Override
    public Industry update(Long id, IndustryRequest request) {
        // Строго требуем совпадение id из path и тела, чтобы не допустить смену PK через тело
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("Path id and body id must be equal");
        }

        Industry existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Industry not found: " + id));

        existing.setName(request.name());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Industry not found: " + id);
        }
        repository.deleteById(id);
    }

}
