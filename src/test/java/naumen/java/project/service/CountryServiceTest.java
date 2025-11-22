package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;


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

    /** Кидает ResourceNotFoundException, если страна не найдена */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(ID_RAW)
        );

        Assertions.assertEquals("Страна с id = " + ID_NORM + " не найден(а)", ex.getMessage());
    }

    /** Нормализует id и сохраняет страну */
    @Test
    void testNormalizeAndSaveCountry() {
        Country toCreate = new Country(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repositoryMock.save(toCreate)).thenReturn(toCreate);

        Country result = service.create(toCreate);

        Assertions.assertEquals(ID_NORM, toCreate.getId());
        Assertions.assertSame(toCreate, result);
    }

    /** Кидает IllegalArgumentException, если id уже существует */
    @Test
    void testIllegalArgumentExceptionIfCountryAlreadyExists() {
        Country toCreate = new Country(ID_RAW, NAME);
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        Assertions.assertEquals("Страна с id = " + ID_NORM + " уже существует", ex.getMessage());
    }

    /** Кидает IllegalArgumentException, если id в пути и в теле не совпадают */
    @Test
    void testIllegalArgumentException() {
        Country body = new Country("us", NAME_UPDATED);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID_RAW, body)
        );

        Assertions.assertEquals(
                "Идентификатор в пути (RU) не совпадает с идентификатором в теле запроса (US)",
                ex.getMessage()
        );
    }

    /** Обновляет имя и сохраняет ту же сущность */
    @Test
    void testUpdateUpdatesNameAndSavesSameInstance() throws ResourceNotFoundException {
        Country existing = new Country(ID_NORM, NAME);
        Country body = new Country(ID_RAW, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        Country result = service.update(ID_RAW, body);

        Assertions.assertSame(existing, result);
        Assertions.assertEquals(ID_NORM, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
    }

    /** Кидает ResourceNotFoundException, если страны нет при existsById */
    @Test
    void testResourceNotFoundExceptionByExistsById() {
        Mockito.when(repositoryMock.existsById(ID_NORM)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(ID_RAW)
        );

        Assertions.assertEquals("Страна с id = " + ID_NORM + " не найден(а)", ex.getMessage());
    }

}
