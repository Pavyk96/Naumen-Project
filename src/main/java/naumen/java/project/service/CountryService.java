package naumen.java.project.service;

import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.model.Country;

import java.util.List;

/**
 * Сервис для работы со справочником стран
 *
 * @author Daniil Mezev
 */
public interface CountryService {

    /** Возвращает все страны */
    List<Country> findAll();

    /** Возвращает страну по идентификатору */
    Country findById(String id);

    /** Создаёт новую страну */
    Country create(CountryRequest request);

    /** Обновляет страну по идентификатору */
    Country update(String id, CountryRequest request);

    /** Удаляет страну по идентификатору */
    void delete(String id);

}
