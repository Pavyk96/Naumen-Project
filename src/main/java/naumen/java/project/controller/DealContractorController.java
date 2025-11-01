package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.service.DealContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления связью между сделками и контрагентами
 *
 * @author Daria
 */
@RestController
@RequestMapping("/deal-contractor")
public class DealContractorController {
    private final DealContractorService dealContractorService;
    private final DealMapper dealMapper;

    public DealContractorController(DealContractorService dealContractorService, DealMapper dealMapper) {
        this.dealContractorService = dealContractorService;
        this.dealMapper = dealMapper;
    }

    /** Создает связь сделка-контрагент */
    @PostMapping("/save")
    public ResponseEntity<DealResponse> save(@Valid @RequestBody DealContractorRequest request) {
        return ResponseEntity.ok(
                dealMapper.tolResponse(dealContractorService.addContractorToDeal(request)));
    }

    /** Удаляет контрагента из сделки */
    @PostMapping("/delete")
    public ResponseEntity<DealResponse> delete(@Valid @RequestBody DealContractorRequest request) {
        return ResponseEntity.ok(
                dealMapper.tolResponse(dealContractorService.deleteContractorFromDeal(request)));
    }
}
