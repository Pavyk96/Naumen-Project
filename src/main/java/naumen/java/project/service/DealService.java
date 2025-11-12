package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
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
@Transactional
public class DealService {

    private final DealRepository dealRepository;

    public DealService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    /** Сохраняет сделку  */
    public Deal save(Deal deal) {
        return dealRepository.save(deal);
    }

    /** Возвращает сделку по идентификатору */
    public Deal findById(UUID id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found"));
    }

    /** Возвращает все сделки */
    public List<Deal> findAll() {
        return dealRepository.findAll();
    }

    /** Удаляет сделку по идентификатору */
    public void delete(UUID id) {
        if (!dealRepository.existsById(id)) {
            throw new EntityNotFoundException("Deal not found");
        }
        if (!findByIdWithContractors(id).getContractors().isEmpty()) {
            throw new IllegalStateException("Deal use in contractor");
        }
        dealRepository.deleteById(id);
    }

    /** Сохраняет сделку (создание или обновление) */
    public Deal createOrUpdate(Deal deal) {
        if (deal.getId() != null && !existsById(deal.getId())) {
            throw new EntityNotFoundException("Deal not found");
        }
        return save(deal);
    }

    /** Возвращает сделку по идентификатору с контрагентами*/
    public Deal findByIdWithContractors(UUID id) {
        return dealRepository.findWithContractorsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found"));
    }


    /** Возвращает все сделки с контрагентами*/
    public List<Deal> findAllWithContractors() {
        return dealRepository.findAllWithContractors();
    }

    /** Меняет статус сделки */
    public Deal changeStatus(UUID id, DealStatus newStatus) {
        Deal deal = findByIdWithContractors(id);
        deal.setStatus(newStatus);
        return dealRepository.save(deal);
    }

    /**
     * Проверяет существование сделки по ID
     */
    public boolean existsById(UUID id) {
        return dealRepository.existsById(id);
    }
}
