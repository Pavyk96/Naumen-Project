package naumen.java.project.service;

import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.model.Industry;

import java.util.List;

/**
 * Сервис для работы со справочником индустрий
 *
 * @author Daniil Mezev
 */
public interface IndustryService {

    /** Возвращает все индустрии */
    List<Industry> findAll();

    /** Возвращает индустрию по идентификатору */
    Industry findById(Long id);

    /** Создаёт новую индустрию */
    Industry create(IndustryRequest request);

    /** Обновляет индустрию по идентификатору */
    Industry update(Long id, IndustryRequest request);

    /** Удаляет индустрию по идентификатору */
    void delete(Long id);

}
