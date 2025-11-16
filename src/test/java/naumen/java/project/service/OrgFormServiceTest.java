package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    private OrgForm entity(String id, String name) {
        return new OrgForm(id, name);
    }

    /** Возвращает список форм */
    @Test
    void testFindAllReturnsAllOrgForms() {
        OrgForm of1 = entity("PJSC", "Публичное акционерное общество");
        OrgForm of2 = entity(ID_NORM, NAME);

        Mockito.when(repositoryMock.findAll()).thenReturn(List.of(of1, of2));

        List<OrgForm> all = service.findAll();

        assertEquals(2, all.size());
        assertSame(of1, all.get(0));
        assertSame(of2, all.get(1));
    }

    /** Возвращает форму с нормализацией id */
    @Test
    void testFindByIdReturnsOrgFormWithNormalization() throws ResourceNotFoundException {
        OrgForm stored = entity(ID_NORM, NAME);
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(stored));

        OrgForm result = service.findById(ID_RAW);

        assertSame(stored, result);
        assertEquals(ID_NORM, result.getId());
        assertEquals(NAME, result.getName());
        Mockito.verify(repositoryMock).findById(ID_NORM);
    }

    /** Кидает ResourceNotFoundException, если формы нет */
    @Test
    void testFindByIdThrowsIfNotFound() {
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_RAW));

        Mockito.verify(repositoryMock).findById(ID_NORM);
    }

    /** Нормализует id и сохраняет форму */
    @Test
    void testCreateNormalizesIdAndSavesOrgForm() {
        OrgForm toCreate = entity(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repositoryMock.save(toCreate)).thenReturn(toCreate);

        OrgForm result = service.create(toCreate);

        assertSame(toCreate, result);
        assertEquals(ID_NORM, toCreate.getId());
        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock).save(toCreate);
    }

    /** Кидает IllegalArgumentException, если форма уже существует */
    @Test
    void testCreateThrowsIfOrgFormAlreadyExists() {
        OrgForm toCreate = entity(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        assertEquals(
                "Организационно-правовая форма с id = " + ID_NORM + " уже существует",
                ex.getMessage()
        );
        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Обновляет name и сохраняет ту же сущность */
    @Test
    void testUpdateUpdatesNameAndSavesSameInstance() throws ResourceNotFoundException {
        OrgForm existing = entity(ID_NORM, NAME);
        OrgForm body = entity(ID_RAW, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        OrgForm result = service.update(ID_RAW, body);

        assertSame(existing, result);
        assertEquals(ID_NORM, existing.getId());
        assertEquals(NAME_UPDATED, existing.getName());
        Mockito.verify(repositoryMock).findById(ID_NORM);
        Mockito.verify(repositoryMock).save(existing);
    }

    /** Кидает IllegalArgumentException, если id в пути и теле разные */
    @Test
    void testUpdateThrowsIfIdsDoNotMatch() {
        OrgForm body = entity("pjsc", NAME_UPDATED);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID_RAW, body)
        );

        assertEquals(
                "Идентификатор в пути (" +
                        ID_NORM + ") не совпадает с идентификатором в теле запроса (PJSC)",
                ex.getMessage()
        );
        Mockito.verify(repositoryMock, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Кидает ResourceNotFoundException, если формы нет */
    @Test
    void testUpdateThrowsIfOrgFormNotFound() {
        OrgForm body = entity(ID_RAW, NAME_UPDATED);
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(ID_RAW, body));

        Mockito.verify(repositoryMock).findById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Удаляет форму, если она существует */
    @Test
    void testDeleteDeletesOrgFormIfExists() throws ResourceNotFoundException {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock).deleteById(ID_NORM);
    }

    /** Кидает ResourceNotFoundException, если формы нет */
    @Test
    void testDeleteThrowsIfOrgFormNotFound() {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_RAW));

        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).deleteById(Mockito.anyString());
    }
}
