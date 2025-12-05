package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.contractor.ContractorRequestDTO;
import naumen.java.project.dto.contractor.ContractorResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.ContractorService;
import naumen.java.project.validation.ValidUuid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.CountryService;
import naumen.java.project.service.IndustryService;
import naumen.java.project.service.OrgFormService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для управления контрагентами
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/contractor")
public class ContractorController {

    private final ContractorService contractorService;
    private final ContractorMapper contractorMapper;

    private final CountryService countryService;
    private final IndustryService industryService;
    private final OrgFormService orgFormService;

    public ContractorController(ContractorService contractorService,
                                ContractorMapper contractorMapper,
                                CountryService countryService,
                                IndustryService industryService,
                                OrgFormService orgFormService) {
        this.contractorService = contractorService;
        this.contractorMapper = contractorMapper;
        this.countryService = countryService;
        this.industryService = industryService;
        this.orgFormService = orgFormService;
    }

    /** Возвращает всех контрагентов */
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ContractorResponseDTO>> getAll() {
        List<Contractor> entities = contractorService.findAll();
        List<ContractorResponseDTO> dtos = entities.stream()
                .map(contractorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /** Возвращает контрагента по идентификатору */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ContractorResponseDTO> getById(@PathVariable @ValidUuid String id) throws ResourceNotFoundException {
        Contractor entity = contractorService.findById(UUID.fromString(id));
        return ResponseEntity.ok(contractorMapper.toResponse(entity));
    }

    /** Сохраняет нового контрагента */
    @PostMapping
    @Transactional
    public ResponseEntity<ContractorResponseDTO> create(
            @Valid @RequestBody ContractorRequestDTO req
    ) throws ResourceNotFoundException {

        Country country = countryService.findById(req.countryId());
        Industry industry = industryService.findById(req.industryId());
        OrgForm orgForm = orgFormService.findById(req.orgFormId());

        Contractor contractor = new Contractor(
                req.name(),
                country,
                industry,
                orgForm
        );
        contractor.setCreateDate(LocalDate.now());
        Contractor created = contractorService.save(contractor);

        return ResponseEntity.ok(contractorMapper.toResponse(created));
    }

    /** Обновляет существующего контрагента */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ContractorResponseDTO> update(@PathVariable @ValidUuid String id,
                                                        @Valid @RequestBody ContractorRequestDTO req)
            throws ResourceNotFoundException {
        Country country = countryService.findById(req.countryId());
        Industry industry = industryService.findById(req.industryId());
        OrgForm orgForm = orgFormService.findById(req.orgFormId());

        Contractor oldContractor = contractorService.findById(UUID.fromString(id));

        oldContractor.setName(req.name());
        oldContractor.setCountry(country);
        oldContractor.setIndustry(industry);
        oldContractor.setOrgForm(orgForm);

        Contractor updateContractor = contractorService.save(oldContractor);

        return ResponseEntity.ok(contractorMapper.toResponse(updateContractor));
    }


    /** Удаляет контрагента */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable @ValidUuid String id) throws ResourceNotFoundException {
        contractorService.delete(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

}
