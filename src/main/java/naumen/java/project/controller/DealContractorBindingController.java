package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.DealContractorRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorBindingService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления связью между сделками и контрагентами
 *
 * @author Daria
 */
@RestController
@RequestMapping("/deal-contractor")
public class DealContractorBindingController {
    private final DealContractorBindingService dealContractorBindingService;
    private final DealMapper dealMapper;

    public DealContractorBindingController(DealContractorBindingService dealContractorBindingService, DealMapper dealMapper) {
        this.dealContractorBindingService = dealContractorBindingService;
        this.dealMapper = dealMapper;
    }

    /** Создает связь сделка-контрагент */
    @Transactional
    @PostMapping("/save")
    public ResponseEntity<DealResponseDTO> save(@Valid @RequestBody DealContractorRequestDTO request) {
        Deal deal = dealContractorBindingService.addContractorToDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        DealResponseDTO dealResponseDTO = dealMapper.toDetailResponse(deal);
        return ResponseEntity.ok(dealResponseDTO);
    }

    /** Удаляет контрагента из сделки */
    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<DealResponseDTO> delete(@Valid @RequestBody DealContractorRequestDTO request) {
        Deal deal = dealContractorBindingService.deleteContractorFromDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        DealResponseDTO dealResponseDTO = dealMapper.toDetailResponse(deal);
        return ResponseEntity.ok(dealResponseDTO);
    }
}
