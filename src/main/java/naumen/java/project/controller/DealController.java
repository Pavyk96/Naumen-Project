package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.DealStatus;
import naumen.java.project.service.DealService;
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
        return ResponseEntity.ok(dealMapper.toResponse(dealService.createOrUpdate(request)));
    }

    /**
     * Возвращает информацию о сделке
     */
    @GetMapping("/{id}")
    public ResponseEntity<DealResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dealMapper.tolResponse(dealService.findByIdWithContractors(id)));
    }

    /**
     * Удаляет сделку
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dealService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает все сделки
     */
    @GetMapping("/all")
    public ResponseEntity<List<DealResponse>> findAll() {
        return ResponseEntity.ok(dealMapper.toListResponse(dealService.findAllWithContractors()));
    }

    /**
     * Меняет статус сделки
     */
    @PatchMapping("/change/status/{id}/{status}")
    public ResponseEntity<DealResponse> changeStatus(@PathVariable UUID id,
                                                     @PathVariable DealStatus status) {
        return ResponseEntity.ok(dealMapper.tolResponse(dealService.changeStatus(id, status)));
    }

}
