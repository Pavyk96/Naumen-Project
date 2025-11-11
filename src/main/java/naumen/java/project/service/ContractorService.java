package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.contractor.ContractorRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация сервиса для управления контрагентами (без маппера; маппинг перенесён в контроллер)
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
    @Transactional(readOnly = true)
    public List<Contractor> findAll() {
        return repository.findAll();
    }

    /** Возвращает контрагента по идентификатору */
    @Transactional(readOnly = true)
    public Contractor findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found: " + id));
    }

    /**
     * Возвращает контрагента по идентификатору с сделками
     */
    @Transactional(readOnly = true)
    public Contractor findByIdWithDeals(String id) {
        return repository.findWithDealsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found: " + id));
    }

    /** Создаёт нового контрагента */
    public Contractor create(ContractorRequest req) {
        if (repository.existsById(req.id())) {
            throw new IllegalArgumentException("Contractor already exists: " + req.id());
        }
        Contractor entity = new Contractor(
                req.id(),
                req.name(),
                req.countryId(),
                req.industryId(),
                req.orgFormId()
        );
        return repository.save(entity);
    }

    /** Обновляет существующего контрагента по id переданными данными сущности */
    public Contractor update(String id, ContractorRequest req) {
        if (!id.equals(req.id())) {
            throw new IllegalArgumentException("Path id and body id must be equal");
        }
        Contractor existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found: " + id));

        existing.setName(req.name());
        existing.setCountryId(req.countryId());
        existing.setIndustryId(req.industryId());
        existing.setOrgFormId(req.orgFormId());

        return repository.save(existing);
    }

    /** Удаляет контрагента */
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Contractor not found: " + id);
        }
        if (!findByIdWithDeals(id).getDeals().isEmpty()) {
            throw new IllegalStateException("Contractor use in deal");
        }
        repository.deleteById(id);
    }

}
