package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.deal.DealRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.service.DealService;
import naumen.java.project.validation.ValidEnum;
import naumen.java.project.validation.ValidUuid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    @Transactional
    @PostMapping("/save")
    public ResponseEntity<DealShortResponseDTO> save(@Valid @RequestBody DealRequestDTO request)
            throws ResourceNotFoundException {
        Deal deal;
        if (request.id() != null) {
            deal = dealService.findById(UUID.fromString(request.id()));
        } else {
            deal = new Deal();
        }
        deal.setDescription(request.description());
        deal.setAgreementNumber(request.agreementNumber());
        deal.setAgreementDate(parseLocalDate(request.agreementDate()));
        deal.setOpenedAt(parseLocalDateTime(request.openedAt()));
        deal.setClosedAt(parseLocalDateTime(request.closedAt()));
        deal.setType(DealType.valueOf(request.type()));
        deal.setStatus(parseDealStatus(request.status()));

        Deal dealSave = dealService.save(deal);
        DealShortResponseDTO dealShortResponseDTO = dealMapper.toShortResponse(dealSave);
        return ResponseEntity.ok(dealShortResponseDTO);
    }

    /**
     * Возвращает информацию о сделке
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<DealResponseDTO> getById(@PathVariable @ValidUuid String id)
            throws ResourceNotFoundException {
        Deal deal = dealService.findByIdWithContractors(UUID.fromString(id));
        DealResponseDTO dealResponseDTO = dealMapper.toDetailResponse(deal);
        return ResponseEntity.ok(dealResponseDTO);
    }

    /**
     * Удаляет сделку
     */
    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws ResourceNotFoundException {
        dealService.delete(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает все сделки
     */
    @Transactional(readOnly = true)
    @GetMapping("/all")
    public ResponseEntity<List<DealResponseDTO>> findAll() {
        List<Deal> dealList = dealService.findAllWithContractors();
        List<DealResponseDTO> dealResponseDTOList = dealMapper.toListResponse(dealList);
        return ResponseEntity.ok(dealResponseDTOList);
    }

    /**
     * Меняет статус сделки
     */
    @Transactional
    @PatchMapping("/change/status/{id}/{status}")
    public ResponseEntity<DealResponseDTO> changeStatus(@PathVariable @ValidUuid String id,
                                                        @PathVariable @ValidEnum(enumClass = DealStatus.class) String status
    ) throws ResourceNotFoundException {
        Deal deal = dealService.changeStatus(UUID.fromString(id), DealStatus.valueOf(status));
        DealResponseDTO dealResponseDTO = dealMapper.toDetailResponse(deal);
        return ResponseEntity.ok(dealResponseDTO);
    }

    /** Парсит LocalDate */
    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd, got: " + dateString);
        }
    }

    /** Парсит LocalDateTime */
    private LocalDateTime parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.replace("Z", ""));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format. Expected: yyyy-MM-ddTHH:mm:ss, got: " + dateTimeString);
        }
    }

    /** Парсит DealStatus */
    private DealStatus parseDealStatus(String statusString) {
        if (statusString == null || statusString.isBlank()) {
            return DealStatus.DRAFT;
        }
        return DealStatus.valueOf(statusString);
    }

}
