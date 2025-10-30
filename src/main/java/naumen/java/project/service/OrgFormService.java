package naumen.java.project.service;

import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.model.OrgForm;

import java.util.List;

/**
 * Сервис для работы со справочником организационно-правовых форм.
 *
 * @author Daniil Mezev
 */
public interface OrgFormService {

    /** Возвращает все организационно-правовые формы */
    List<OrgForm> findAll();

    /** Ищет организационно-правовую форму по идентификатору */
    OrgForm findById(String id);

    /** Создаёт новую организационно-правовую форму */
    OrgForm create(OrgFormRequest request);

    /** Обновляет существующую организационно-правовую форму */
    OrgForm update(String id, OrgFormRequest request);

    /** Удаляет организационно-правовую форму */
    void delete(String id);

}
