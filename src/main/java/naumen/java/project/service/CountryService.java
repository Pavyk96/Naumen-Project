package naumen.java.project.service;

import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;

import java.util.List;

/**
 * Сервис для работы со справочником стран
 *
 * @author Daniil Mezev
 */
public interface CountryService {

    /** Возвращает все страны */
    List<CountryResponse> findAll();

    /** Возвращает страну по идентификатору */
    CountryResponse findById(String id);

    /** Создаёт новую страну */
    CountryResponse create(CountryRequest request);

    /** Обновляет страну по идентификатору */
    CountryResponse update(String id, CountryRequest request);

    /** Удаляет страну по идентификатору */
    void delete(String id);

}
