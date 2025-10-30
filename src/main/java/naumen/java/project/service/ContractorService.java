package naumen.java.project.service;

import naumen.java.project.dto.ContractorRequest;
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
    List<Contractor> findAll();

    /** Возвращает контрагента по идентификатору */
    Contractor findById(String id);

    /** Создаёт нового контрагента */
    Contractor create(ContractorRequest contractor);

    /** Обновляет существующего контрагента по id переданными данными сущности */
    Contractor update(String id, ContractorRequest contractor);

    /** Удаляет контрагента */
    void delete(String id);

}
