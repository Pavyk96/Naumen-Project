package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
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

    private static final String ID = "RU";

    private final CountryRepository repositoryMock;
    private final CountryService service;

    public CountryServiceTest(@Mock CountryRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new CountryService(repositoryMock);
    }

    /** Кидает ResourceNotFoundException, если страна не найдена */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(ID)
        );

        Assertions.assertEquals("Страна с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Кидает ResourceNotFoundException, если страны нет при existsById */
    @Test
    void testResourceNotFoundExceptionByExistsById() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(ID)
        );

        Assertions.assertEquals("Страна с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Успешное удаление страны, когда запись существует */
    @Test
    void testDeleteSuccess() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> service.delete(ID));

        Mockito.verify(repositoryMock).deleteById(ID);
    }


}
