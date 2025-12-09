package naumen.java.project.service;

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

    /**
     * Возвращает страну по идентификатору
     * @throws ResourceNotFoundException если страна с указанным id не найдена в БД
     */
    public Country findById(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Страна", id));
    }

    /**
     * Сохраняет страну
     */
    public Country save(Country country) {
        return repository.save(country);
    }

    /**
     * Удаляет страну по идентификатору
     * @throws ResourceNotFoundException если страна с указанным id отсутствует в БД
     */
    public void delete(String id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Страна", id);
        }
        repository.deleteById(id);
    }
}
