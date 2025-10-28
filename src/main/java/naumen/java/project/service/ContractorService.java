package naumen.java.project.service;

import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.model.Contractor;

import java.util.List;

/**
 * Сервис для управления контрагентами
 *
 * @author Daniil Mezev
 */
public interface ContractorService {

    /** Возвращает всех контрагентов */
    List<ContractorResponse> findAll();

    /** Возвращает контрагента по идентификатору */
    ContractorResponse findById(String id);

    /** Создаёт нового контрагента */
    ContractorResponse create(Contractor contractor);

    /** Обновляет существующего контрагента по id переданными данными сущности */
    ContractorResponse update(String id, Contractor contractor);

    /** Удаляет контрагента */
    void delete(String id);

}
