package naumen.java.project.service;

import naumen.java.project.dto.CountryRequestDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления странами
 *
 * @author Daniil Mezev
 */
@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    /** Возвращает все страны */
    public List<Country> findAll() {
        return repository.findAll();
    }

    /** Возвращает страну по идентификатору */
    public Country findById(String id) throws ResourceNotFoundException {
        String normId = normalize(id);
        return repository.findById(normId)
                .orElseThrow(() -> new ResourceNotFoundException("Страна", normId));
    }

    /** Создаёт новую страну */
    public Country create(Country country) {
        String normId = normalize(country.getId());
        country.setId(normId);

        if (repository.existsById(normId)) {
            throw new IllegalArgumentException(
                    "Страна с id = " + normId + " уже существует"
            );
        }

        return repository.save(country);
    }

    /** Обновляет страну по идентификатору */
    public Country update(String id, Country country) throws ResourceNotFoundException {
        String normPathId = normalize(id);
        String normBodyId = normalize(country.getId());

        if (!normPathId.equals(normBodyId)) {
            throw new IllegalArgumentException(
                    "Идентификатор в пути (" +
                            normPathId + ") не совпадает с идентификатором в теле запроса ("
                            + normBodyId + ")"
            );
        }

        Country existing = repository.findById(normPathId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Страна",
                        normPathId
                ));

        existing.setName(country.getName());

        return repository.save(existing);
    }

    /** Удаляет страну по идентификатору */
    public void delete(String id) throws ResourceNotFoundException {
        String normId = normalize(id);
        if (!repository.existsById(normId)) {
            throw new ResourceNotFoundException("Страна", normId);
        }
        repository.deleteById(normId);
    }

    /** Нормализация айди */
    private String normalize(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }
}
