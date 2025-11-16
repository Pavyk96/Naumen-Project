package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты IndustryService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class IndustryServiceTest {

    private static final Long ID = 10L;
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

    private final IndustryRepository repositoryMock;
    private final IndustryService service;

    public IndustryServiceTest(@Mock IndustryRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new IndustryService(repositoryMock);
    }

    private Industry entity(Long id, String name) {
        return new Industry(id, name);
    }

    /** Возвращает список индустрий */
    @Test
    void testFindAllReturnsAllIndustries() {
        Industry i1 = entity(1L, "Finance");
        Industry i2 = entity(ID, NAME);
        Mockito.when(repositoryMock.findAll()).thenReturn(List.of(i1, i2));

        List<Industry> all = service.findAll();

        assertEquals(2, all.size());
        assertSame(i1, all.get(0));
        assertSame(i2, all.get(1));
    }

    /** Возвращает индустрию при наличии */
    @Test
    void testFindByIdReturnsIndustry() throws ResourceNotFoundException {
        Industry stored = entity(ID, NAME);
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.of(stored));

        Industry result = service.findById(ID);

        assertSame(stored, result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        Mockito.verify(repositoryMock).findById(ID);
    }

    /** Кидает ResourceNotFoundException, если записи нет */
    @Test
    void testFindByIdThrowsIfNotFound() {
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID));

        Mockito.verify(repositoryMock).findById(ID);
    }

    /** Сохраняет новую индустрию */
    @Test
    void testCreateSavesNewIndustry() {
        Industry toCreate = entity(ID, NAME);
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(false);
        Mockito.when(repositoryMock.save(toCreate)).thenReturn(toCreate);

        Industry result = service.create(toCreate);

        assertSame(toCreate, result);
        Mockito.verify(repositoryMock).existsById(ID);
        Mockito.verify(repositoryMock).save(toCreate);
    }

    /** Кидает IllegalArgumentException, если индустрия уже существует */
    @Test
    void testCreateThrowsIfIndustryAlreadyExists() {
        Industry toCreate = entity(ID, NAME);
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        assertEquals("Индустрия с id = " + ID + " уже существует", ex.getMessage());
        Mockito.verify(repositoryMock).existsById(ID);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Обновляет имя и сохраняет ту же сущность */
    @Test
    void testUpdateUpdatesNameAndSavesSameInstance() throws ResourceNotFoundException {
        Industry existing = entity(ID, NAME);
        Industry body = entity(ID, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        Industry result = service.update(ID, body);

        assertSame(existing, result);
        assertEquals(ID, existing.getId());
        assertEquals(NAME_UPDATED, existing.getName());
        Mockito.verify(repositoryMock).findById(ID);
        Mockito.verify(repositoryMock).save(existing);
    }

    /** Кидает IllegalArgumentException, если id в пути и теле разные */
    @Test
    void testUpdateThrowsIfIdsDoNotMatch() {
        Industry body = entity(20L, NAME_UPDATED);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID, body)
        );

        assertEquals(
                "Идентификатор в пути (" + ID +
                        ") не совпадает с идентификатором в теле запроса (" + body.getId() + ")",
                ex.getMessage()
        );
        Mockito.verify(repositoryMock, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Кидает ResourceNotFoundException, если индустрия не найдена */
    @Test
    void testUpdateThrowsIfIndustryNotFound() {
        Industry body = entity(ID, NAME_UPDATED);
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(ID, body));

        Mockito.verify(repositoryMock).findById(ID);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Удаляет индустрию, если она существует */
    @Test
    void testDeleteDeletesIndustryIfExists() throws ResourceNotFoundException {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        service.delete(ID);

        Mockito.verify(repositoryMock).existsById(ID);
        Mockito.verify(repositoryMock).deleteById(ID);
    }

    /** Кидает ResourceNotFoundException, если индустрии нет */
    @Test
    void testDeleteThrowsIfIndustryNotFound() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID));

        Mockito.verify(repositoryMock).existsById(ID);
        Mockito.verify(repositoryMock, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
