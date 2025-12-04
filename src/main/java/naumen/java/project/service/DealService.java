package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса для управления сделками
 *
 * @author Daria
 */
@Service
public class DealService {

    private final DealRepository dealRepository;

    public DealService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    /** Сохраняет сделку  */
    public Deal save(Deal deal) {
        return dealRepository.save(deal);
    }

    /**
     * Возвращает сделку по идентификатору
     *
     * @throws ResourceNotFoundException если сделка не найдена
     */
    public Deal findById(UUID id) throws ResourceNotFoundException {
        return dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Сделка",
                        String.valueOf(id)
                ));
    }


    /** Возвращает все сделки
     *
     * @throws ResourceNotFoundException если сделка не найдена
     */
    public List<Deal> findAll() {
        return dealRepository.findAll();
    }

    /** Удаляет сделку по идентификатору
     *
     * @throws ResourceNotFoundException если сделка не найдена
     * @throws IllegalStateException если связь между сделкой и контрагентом существует
     */
    public void delete(UUID id) throws ResourceNotFoundException {
        if (!dealRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Сделка",
                    String.valueOf(id)
            );
        }

        if (!findByIdWithContractors(id).getContractors().isEmpty()) {
            throw new IllegalStateException(
                    "Нельзя удалить сделку с id = "
                            + id + ", так как к ней привязаны контрагенты"
            );
        }

        dealRepository.deleteById(id);
    }

    /** Возвращает сделку по идентификатору с контрагентами
     *
     * @throws ResourceNotFoundException если сделка не найдена
     */
    public Deal findByIdWithContractors(UUID id) throws ResourceNotFoundException {
        return dealRepository.findWithContractorsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Сделка",
                        String.valueOf(id)
                ));
    }



    /** Возвращает все сделки с контрагентами*/
    public List<Deal> findAllWithContractors() {
        return dealRepository.findAllWithContractors();
    }

    /** Меняет статус сделки
     *
     * @throws ResourceNotFoundException если сделка не найдена
     */
    public Deal changeStatus(UUID id, DealStatus newStatus) throws ResourceNotFoundException {
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
