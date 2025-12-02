package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса для управления контрагентами
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorService {

    private final ContractorRepository repository;

    public ContractorService(ContractorRepository repository) {
        this.repository = repository;
    }

    /** Возвращает всех контрагентов */
    public List<Contractor> findAll() {
        return repository.findAll();
    }

    /**
     * Возвращает контрагента по идентификатору
     * @throws ResourceNotFoundException если контрагент с указанным id не найден в БД
     */
    public Contractor findById(UUID id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Контрагент",
                        String.valueOf(id)
                ));
    }

    /** Сохраняет контрагента */
    public Contractor save(Contractor contractor) {
        return repository.save(contractor);
    }

    /**
     * Удаляет контрагента
     * @throws ResourceNotFoundException если контрагент с указанным id не найден в БД
     */
    public void delete(UUID id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Контрагент",
                    String.valueOf(id)
            );
        }
        repository.deleteById(id);
    }
}
