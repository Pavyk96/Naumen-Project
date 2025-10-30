package naumen.java.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import naumen.java.project.dto.ContractorRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import naumen.java.project.service.ContractorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления контрагентами (без маппера; маппинг перенесён в контроллер)
 *
 * @author Daniil Mezev
 */
@Service
@Transactional
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository repository;

    public ContractorServiceImpl(ContractorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Contractor> findAll() {
        return repository.findAll();
    }

    @Override
    public Contractor findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found: " + id));
    }

    @Override
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

    @Override
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

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Contractor not found: " + id);
        }
        repository.deleteById(id);
    }

}
