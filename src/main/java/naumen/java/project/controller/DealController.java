package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.service.DealService;
import naumen.java.project.validation.ValidUuid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для управления сделками
 *
 * @author Daria
 */
@RestController
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;
    private final DealMapper dealMapper;

    public DealController(DealService dealService, DealMapper dealMapper) {
        this.dealService = dealService;
        this.dealMapper = dealMapper;
    }

    /**
     * Создаёт/обновляет сделки
     */
    @PostMapping("/save")
    public ResponseEntity<DealShortResponse> save(@Valid @RequestBody DealRequest request) {
        Deal deal = dealService.createOrUpdate(request);
        DealShortResponse dealShortResponse = dealMapper.toResponse(deal);
        return ResponseEntity.ok(dealShortResponse);
    }

    /**
     * Возвращает информацию о сделке
     */
    @GetMapping("/{id}")
    public ResponseEntity<DealResponse> getById(@PathVariable @ValidUuid String id) {
        Deal deal = dealService.findByIdWithContractors(UUID.fromString(id));
        DealResponse dealResponse = dealMapper.tolResponse(deal);
        return ResponseEntity.ok(dealResponse);
    }

    /**
     * Удаляет сделку
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        dealService.delete(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает все сделки
     */
    @GetMapping("/all")
    public ResponseEntity<List<DealResponse>> findAll() {
        List<Deal> dealList = dealService.findAllWithContractors();
        List<DealResponse> dealResponseList = dealMapper.toListResponse(dealList);
        return ResponseEntity.ok(dealResponseList);
    }

    /**
     * Меняет статус сделки
     */
    @PatchMapping("/change/status/{id}/{status}")
    public ResponseEntity<DealResponse> changeStatus(@PathVariable @ValidUuid String id,
                                                     @PathVariable String status) {
        Deal deal = dealService.changeStatus(UUID.fromString(id), DealStatus.fromString(status));
        DealResponse dealResponse = dealMapper.tolResponse(deal);
        return ResponseEntity.ok(dealResponse);
    }

}
