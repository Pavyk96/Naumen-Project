package naumen.java.project.service;

import naumen.java.project.dto.IndustryRequestDTO;
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

    /** Возвращает индустрию по идентификатору */
    public Industry findById(Long id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Индустрия",
                        String.valueOf(id)
                ));
    }

    /** Создаёт новую индустрию */
    public Industry create(Industry industry) {
        Long id = industry.getId();

        if (repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Индустрия с id = " + id + " уже существует"
            );
        }

        return repository.save(industry);
    }

    /** Обновляет индустрию по идентификатору */
    public Industry update(Long id, Industry industry) throws ResourceNotFoundException {
        Long bodyId = industry.getId();

        if (!id.equals(bodyId)) {
            throw new IllegalArgumentException(
                    "Идентификатор в пути (" +
                            id + ") не совпадает с идентификатором в теле запроса (" + bodyId + ")"
            );
        }

        Industry existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Индустрия",
                        String.valueOf(id)
                ));

        existing.setName(industry.getName());

        return repository.save(existing);
    }


    /** Удаляет индустрию по идентификатору */
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
