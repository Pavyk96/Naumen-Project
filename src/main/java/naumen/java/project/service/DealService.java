package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.DealRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса для управления сделками
 *
 * @author Daria
 */
@Service
public class DealService {

    private final DealRepository repository;
    private final DealMapper mapper;

    public DealService(DealRepository repository, DealMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /** Сохраняет сделку  */
    @Transactional
    public Deal save(Deal deal) {
        return repository.save(deal);
    }

    /** Возвращает сделку по идентификатору */
    @Transactional(readOnly = true)
    public Deal findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found"));
    }

    /** Возвращает все сделки */
    @Transactional(readOnly = true)
    public List<Deal> findAll() {
        return repository.findAll();
    }

    /** Удаляет сделку по идентификатору */
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Deal not found");
        }
        if (!findByIdWithContractors(id).getContractors().isEmpty()) {
            throw new IllegalStateException("Deal use in contractor");
        }
        repository.deleteById(id);
    }

    /** Сохраняет сделку (создание или обновление) */
    public Deal createOrUpdate(DealRequest request) {
        if (request.id() != null) {
            // Обновление существующей сделки
            Deal existingDeal = findById(UUID.fromString(request.id()));
            return save(mapper.toEntity(existingDeal, request));
        } else {
            // Создание новой сделки
            return save(mapper.toEntity(request));
        }
    }

    /** Возвращает сделку по идентификатору с контрагентами*/
    @Transactional(readOnly = true)
    public Deal findByIdWithContractors(UUID id) {
        return repository.findWithContractorsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found"));
    }


    /** Возвращает все сделки с контрагентами*/
    @Transactional(readOnly = true)
    public List<Deal> findAllWithContractors() {
        return repository.findAllWithContractors();
    }

    /** Меняет статус сделки */
    @Transactional
    public Deal changeStatus(UUID id, DealStatus newStatus) {
        Deal deal = findByIdWithContractors(id);
        deal.setStatus(newStatus);
        return repository.save(deal);
    }
}
