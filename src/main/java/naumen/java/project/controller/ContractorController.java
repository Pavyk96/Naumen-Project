package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.ContractorRequest;
import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.ContractorService;
import naumen.java.project.service.CountryService;
import naumen.java.project.service.IndustryService;
import naumen.java.project.service.OrgFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления контрагентами (маппинг DTO вынесен в контроллер)
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/contractor")
public class ContractorController {

    private final ContractorService contractorService;
    private final CountryService countryService;
    private final IndustryService industryService;
    private final OrgFormService orgFormService;
    private final ContractorMapper mapper;

    public ContractorController(ContractorService contractorService,
                                CountryService countryService,
                                IndustryService industryService,
                                OrgFormService orgFormService,
                                ContractorMapper mapper) {
        this.contractorService = contractorService;
        this.countryService = countryService;
        this.industryService = industryService;
        this.orgFormService = orgFormService;
        this.mapper = mapper;
    }

    /** Возвращает всех контрагентов */
    @GetMapping("/all")
    public ResponseEntity<List<ContractorResponse>> getAll() {
        List<Contractor> entities = contractorService.findAll();
        List<ContractorResponse> dtos = entities.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /** Возвращает контрагента по идентификатору */
    @GetMapping("/{id}")
    public ResponseEntity<ContractorResponse> getById(@PathVariable String id) {
        Contractor entity = contractorService.findById(id);
        return ResponseEntity.ok(mapToResponse(entity));
    }

    /** Создаёт нового контрагента */
    @PostMapping
    public ResponseEntity<ContractorResponse> create(@Valid @RequestBody ContractorRequest request) {
        Contractor created = contractorService.create(request);
        return ResponseEntity.ok(mapToResponse(created));
    }

    /** Обновляет существующего контрагента */
    @PutMapping("/{id}")
    public ResponseEntity<ContractorResponse> update(@PathVariable String id,
                                                     @Valid @RequestBody ContractorRequest request) {
        Contractor updated = contractorService.update(id, request);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    /** Удаляет контрагента */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        contractorService.delete(id);
        return ResponseEntity.ok().build();
    }

    /** Хелпер для сборки ContractorResponse из сущности */
    private ContractorResponse mapToResponse(Contractor entity) {
        Country country = countryService.findById(entity.getCountryId());
        Industry industry = industryService.findById(entity.getIndustryId());
        OrgForm orgForm = orgFormService.findById(entity.getOrgFormId());
        return mapper.toResponse(entity, country, industry, orgForm);
    }

}
