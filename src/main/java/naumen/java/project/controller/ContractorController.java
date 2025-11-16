package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.contractor.ContractorRequestDTO;
import naumen.java.project.dto.contractor.ContractorResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.ContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.CountryService;
import naumen.java.project.service.IndustryService;
import naumen.java.project.service.OrgFormService;


import java.util.List;

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
    public ResponseEntity<ContractorResponseDTO> getById(@PathVariable String id) throws ResourceNotFoundException {
        Contractor entity = contractorService.findById(id);
        return ResponseEntity.ok(contractorMapper.toResponse(entity));
    }

    /** Создаёт нового контрагента */
    @PostMapping
    @Transactional
    public ResponseEntity<ContractorResponseDTO> create(
            @Valid @RequestBody ContractorRequestDTO req
    ) throws ResourceNotFoundException {

        Country country = countryService.findById(req.countryId());
        Industry industry = industryService.findById(req.industryId());
        OrgForm orgForm = orgFormService.findById(req.orgFormId());

        Contractor toCreate = new Contractor(
                req.id(),
                req.name(),
                country,
                industry,
                orgForm
        );

        Contractor created = contractorService.create(toCreate);

        return ResponseEntity.ok(contractorMapper.toResponse(created));
    }

    /** Обновляет существующего контрагента */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ContractorResponseDTO> update(@PathVariable String id,
                                                        @Valid @RequestBody ContractorRequestDTO req)
            throws ResourceNotFoundException {
        Country country = countryService.findById(req.countryId());
        Industry industry = industryService.findById(req.industryId());
        OrgForm orgForm = orgFormService.findById(req.orgFormId());

        Contractor toUpdate = new Contractor(
                req.id(),
                req.name(),
                country,
                industry,
                orgForm
        );

        Contractor updated = contractorService.update(id, toUpdate);

        return ResponseEntity.ok(contractorMapper.toResponse(updated));
    }


    /** Удаляет контрагента */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable String id) throws ResourceNotFoundException {
        contractorService.delete(id);
        return ResponseEntity.ok().build();
    }

}
