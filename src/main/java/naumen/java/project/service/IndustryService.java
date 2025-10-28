package naumen.java.project.service;

import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import java.util.List;

/**
 * Сервис для работы со справочником индустрий
 *
 * @author Daniil Mezev
 */
public interface IndustryService {

    /** Возвращает все индустрии */
    List<IndustryResponse> findAll();

    /** Возвращает индустрию по идентификатору */
    IndustryResponse findById(Long id);

    /** Создаёт новую индустрию */
    IndustryResponse create(IndustryRequest request);

    /** Обновляет индустрию по идентификатору */
    IndustryResponse update(Long id, IndustryRequest request);

    /** Удаляет индустрию по идентификатору */
    void delete(Long id);

}
