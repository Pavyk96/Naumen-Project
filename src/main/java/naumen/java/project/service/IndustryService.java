package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
import org.springframework.stereotype.Service;

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
    public List<Industry> findAll() {
        return repository.findAll();
    }

    /**
     * Возвращает индустрию по идентификатору
     * @throws ResourceNotFoundException если индустрия с указанным id не найдена в БД
     */
    public Industry findById(Long id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Индустрия",
                        String.valueOf(id)
                ));
    }

    /**
     * Сохраняет индустрию
     */
    public Industry save(Industry industry) {
        return repository.save(industry);
    }

    /**
     * Удаляет индустрию по идентификатору
     * @throws ResourceNotFoundException если индустрия с указанным id отсутствует в БД
     */
    public void delete(Long id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Индустрия",
                    String.valueOf(id)
            );
        }
        repository.deleteById(id);
    }
}
