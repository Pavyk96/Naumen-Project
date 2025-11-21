package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;

/**
 * Тесты OrgFormService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class OrgFormServiceTest {

    private static final String ID_RAW = " ooo ";
    private static final String ID_NORM = "OOO";
    private static final String NAME = "Общество с ограниченной ответственностью";
    private static final String NAME_UPDATED = "ООО (обновлено)";

    private final OrgFormRepository repositoryMock;
    private final OrgFormService service;

    public OrgFormServiceTest(@Mock OrgFormRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new OrgFormService(repositoryMock);
    }

    /** Кидает ResourceNotFoundException, если формы нет */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(ID_RAW)
        );

        Assertions.assertEquals("Организационно-правовая форма с id = " + ID_NORM + " не найден(а)", ex.getMessage());
    }

    /** Нормализует id и сохраняет форму */
    @Test
    void testNormalizesAndSavesOrgForm() {
        OrgForm toCreate = new OrgForm(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repositoryMock.save(toCreate)).thenReturn(toCreate);

        OrgForm result = service.create(toCreate);

        Assertions.assertSame(toCreate, result);
        Assertions.assertEquals(ID_NORM, toCreate.getId());
    }

    /** Кидает IllegalArgumentException, если форма уже существует */
    @Test
    void testIllegalArgumentExceptionIfOrgFormAlreadyExists() {
        OrgForm toCreate = new OrgForm(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        Assertions.assertEquals(
                "Организационно-правовая форма с id = " + ID_NORM + " уже существует",
                ex.getMessage()
        );
    }

    /** Обновляет name и сохраняет ту же сущность */
    @Test
    void testUpdateAndSavesSameInstance() throws ResourceNotFoundException {
        OrgForm existing = new OrgForm(ID_NORM, NAME);
        OrgForm body = new OrgForm(ID_RAW, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        OrgForm result = service.update(ID_RAW, body);

        Assertions.assertSame(existing, result);
        Assertions.assertEquals(ID_NORM, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
    }

    /** Кидает IllegalArgumentException, если id в пути и теле разные */
    @Test
    void testIllegalArgumentException() {
        OrgForm body = new OrgForm("pjsc", NAME_UPDATED);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID_RAW, body)
        );

        Assertions.assertEquals(
                "Идентификатор в пути (" +
                        ID_NORM + ") не совпадает с идентификатором в теле запроса (PJSC)",
                ex.getMessage()
        );
    }

    /** Кидает ResourceNotFoundException, если формы нет при existsById */
    @Test
    void testResourceNotFoundExceptionByExistsById() {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(ID_RAW)
        );

        Assertions.assertEquals("Организационно-правовая форма с id = "
                + ID_NORM + " не найден(а)", ex.getMessage());
    }
}
