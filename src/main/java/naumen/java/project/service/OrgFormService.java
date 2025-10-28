package naumen.java.project.service;

import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import java.util.List;

/**
 * Сервис для работы со справочником организационно-правовых форм.
 *
 * @author Daniil Mezev
 */
public interface OrgFormService {

    /** Возвращает все организационно-правовые формы */
    List<OrgFormResponse> findAll();

    /** Ищет организационно-правовую форму по идентификатору */
    OrgFormResponse findById(String id);

    /** Создаёт новую организационно-правовую форму */
    OrgFormResponse create(OrgFormRequest request);

    /** Обновляет существующую организационно-правовую форму */
    OrgFormResponse update(String id, OrgFormRequest request);

    /** Удаляет организационно-правовую форму */
    void delete(String id);

}
