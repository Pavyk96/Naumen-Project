package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тесты для CountryService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    private static final String ID_RAW = " ru ";
    private static final String ID_NORM = "RU";
    private static final String NAME = "Russia";
    private static final String NAME_UPDATED = "Russian Federation";

    private final CountryRepository repositoryMock;
    private final CountryService service;

    public CountryServiceTest(@Mock CountryRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new CountryService(repositoryMock);
    }

    /** Хелпер для создания страны */
    private Country entity(String id, String name) {
        return new Country(id, name);
    }

    /** Возвращает список стран */
    @Test
    void testFindAllReturnsAllCountries() {
        Country c1 = entity("US", "United States");
        Country c2 = entity(ID_NORM, NAME);
        Mockito.when(repositoryMock.findAll()).thenReturn(List.of(c1, c2));

        List<Country> result = service.findAll();

        assertEquals(2, result.size());
        assertSame(c1, result.get(0));
        assertSame(c2, result.get(1));
    }

    /** Нормализует id и возвращает страну */
    @Test
    void testFindByIdReturnsCountryWithNormalization() throws ResourceNotFoundException {
        Country stored = entity(ID_NORM, NAME);
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(stored));

        Country result = service.findById(ID_RAW);

        assertSame(stored, result);
        assertEquals(ID_NORM, result.getId());
        assertEquals(NAME, result.getName());
        Mockito.verify(repositoryMock).findById(ID_NORM);
    }

    /** Кидает ResourceNotFoundException, если страна не найдена */
    @Test
    void testFindByIdThrowsIfCountryNotFound() {
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(ID_RAW));

        Mockito.verify(repositoryMock).findById(ID_NORM);
    }

    /** Нормализует id и сохраняет страну */
    @Test
    void testCreateNormalizesIdAndSavesCountry() {
        Country toCreate = entity(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repositoryMock.save(toCreate)).thenReturn(toCreate);

        Country result = service.create(toCreate);

        assertEquals(ID_NORM, toCreate.getId());
        assertSame(toCreate, result);
        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock).save(toCreate);
    }

    /** Кидает IllegalArgumentException, если id уже существует */
    @Test
    void testCreateThrowsIfCountryAlreadyExists() {
        Country toCreate = entity(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        assertEquals("Страна с id = " + ID_NORM + " уже существует", ex.getMessage());
        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Кидает IllegalArgumentException, если id в пути и теле не совпадают */
    @Test
    void testUpdateThrowsIfIdsDoNotMatch() {
        Country body = entity("us", NAME_UPDATED);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID_RAW, body)
        );

        assertEquals(
                "Идентификатор в пути (RU) не совпадает с идентификатором в теле запроса (US)",
                ex.getMessage()
        );
        Mockito.verify(repositoryMock, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Кидает ResourceNotFoundException, если страна не найдена */
    @Test
    void testUpdateThrowsIfCountryNotFound() {
        Country body = entity(ID_RAW, NAME_UPDATED);
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(ID_RAW, body));

        Mockito.verify(repositoryMock).findById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).save(Mockito.any());
    }

    /** Обновляет имя и сохраняет ту же сущность */
    @Test
    void testUpdateUpdatesNameAndSavesSameInstance() throws ResourceNotFoundException {
        Country existing = entity(ID_NORM, NAME);
        Country body = entity(ID_RAW, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        Country result = service.update(ID_RAW, body);

        assertSame(existing, result);
        assertEquals(ID_NORM, existing.getId());
        assertEquals(NAME_UPDATED, existing.getName());
        Mockito.verify(repositoryMock).findById(ID_NORM);
        Mockito.verify(repositoryMock).save(existing);
    }

    /** Кидает ResourceNotFoundException, если страны нет */
    @Test
    void testDeleteThrowsIfCountryNotFound() {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ID_RAW));

        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock, Mockito.never()).deleteById(Mockito.anyString());
    }

    /** Удаляет страну, если она существует */
    @Test
    void testDeleteDeletesCountryIfExists() throws ResourceNotFoundException {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        Mockito.verify(repositoryMock).existsById(ID_NORM);
        Mockito.verify(repositoryMock).deleteById(ID_NORM);
    }
}
