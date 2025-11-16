package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /** Возвращает контрагента по идентификатору */
    public Contractor findById(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Контрагент",
                        id
                ));
    }

    /** Возвращает контрагента по идентификатору со сделками (если нужно) */
    public Contractor findByIdWithDeals(String id) throws ResourceNotFoundException {
        return repository.findWithDealsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Контрагент",
                        id
                ));
    }

    /** Создаёт нового контрагента */
    public Contractor create(Contractor contractor) {
        String id = contractor.getId();

        if (repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Контрагент с id = " + id + " уже существует"
            );
        }

        return repository.save(contractor);
    }

    /** Обновляет существующего контрагента */
    public Contractor update(String id, Contractor contractor) throws ResourceNotFoundException {
        String bodyId = contractor.getId();

        if (!id.equals(bodyId)) {
            throw new IllegalArgumentException(
                    "Идентификатор в пути ("
                            + id + ") не совпадает с идентификатором в теле запроса ("
                            + bodyId + ")"
            );
        }

        Contractor existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Контрагент",
                        id
                ));

        existing.setName(contractor.getName());
        existing.setCountry(contractor.getCountry());
        existing.setIndustry(contractor.getIndustry());
        existing.setOrgForm(contractor.getOrgForm());

        return repository.save(existing);
    }

    /** Удаляет контрагента */
    public void delete(String id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Контрагент",
                    id
            );
        }
        repository.deleteById(id);
    }
}
