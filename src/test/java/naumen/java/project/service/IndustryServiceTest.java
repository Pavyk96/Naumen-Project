package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.repository.IndustryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

/**
 * Тесты IndustryService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class IndustryServiceTest {

    private static final Long ID = 10L;

    private final IndustryRepository repositoryMock;
    private final IndustryService service;

    public IndustryServiceTest(@Mock IndustryRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new IndustryService(repositoryMock);
    }

    /** Кидает ResourceNotFoundException, если записи нет */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(ID)
        );

        Assertions.assertEquals("Индустрия с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Кидает ResourceNotFoundException, если индустрии нет при existsById */
    @Test
    void testResourceNotFoundExceptionIfExistsById() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(ID)
        );

        Assertions.assertEquals("Индустрия с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Успешное удаление индустрии, когда запись существует */
    @Test
    void testDeleteSuccess() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> service.delete(ID));

        Mockito.verify(repositoryMock).deleteById(ID);
    }

}
