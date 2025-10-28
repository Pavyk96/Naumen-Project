package naumen.java.project.service.impl;

import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.ContractorRepository;
import naumen.java.project.repository.CountryRepository;
import naumen.java.project.repository.IndustryRepository;
import naumen.java.project.repository.OrgFormRepository;
import naumen.java.project.service.ContractorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления контрагентами
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository contractorRepository;
    private final CountryRepository countryRepository;
    private final IndustryRepository industryRepository;
    private final OrgFormRepository orgFormRepository;

    private final ContractorMapper mapper;

    public ContractorServiceImpl(ContractorRepository contractorRepository,
                                 CountryRepository countryRepository,
                                 IndustryRepository industryRepository,
                                 OrgFormRepository orgFormRepository,
                                 ContractorMapper mapper) {
        this.contractorRepository = contractorRepository;
        this.countryRepository = countryRepository;
        this.industryRepository = industryRepository;
        this.orgFormRepository = orgFormRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ContractorResponse> findAll() {
        List<Contractor> contractors = contractorRepository.findAll();
        List<ContractorResponse> responses = contractors.stream()
                .map(this::buildResponse)
                .toList();
        return responses;
    }

    @Override
    public ContractorResponse findById(String id) {
        Contractor contractor = contractorRepository.findById(id).orElseThrow();
        ContractorResponse response = buildResponse(contractor);
        return response;
    }

    @Override
    public ContractorResponse create(Contractor contractor) {
        Contractor saved = contractorRepository.save(contractor);
        ContractorResponse response = buildResponse(saved);
        return response;
    }

    @Override
    public ContractorResponse update(String id, Contractor contractor) {
        Contractor existing = contractorRepository.findById(id).orElseThrow();

        existing.setName(contractor.getName());
        existing.setCountryId(contractor.getCountryId());
        existing.setIndustryId(contractor.getIndustryId());
        existing.setOrgFormId(contractor.getOrgFormId());

        Contractor saved = contractorRepository.save(existing);
        ContractorResponse response = buildResponse(saved);
        return response;
    }

    @Override
    public void delete(String id) {
        contractorRepository.deleteById(id);
    }

    private ContractorResponse buildResponse(Contractor contractor) {
        Country country = countryRepository.findById(contractor.getCountryId()).orElseThrow();
        Industry industry = industryRepository.findById(contractor.getIndustryId()).orElseThrow();
        OrgForm orgForm = orgFormRepository.findById(contractor.getOrgFormId().toString()).orElseThrow();

        ContractorResponse response = mapper.toResponse(contractor, country, industry, orgForm);
        return response;
    }

}
