package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Industry;
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
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

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

    /** Кидает IllegalArgumentException, если индустрия уже существует */
    @Test
    void testIllegalArgumentExceptionIfIndustryAlreadyExists() {
        Industry toCreate = new Industry(ID, NAME);
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(toCreate)
        );

        Assertions.assertEquals("Индустрия с id = " + ID + " уже существует", ex.getMessage());
    }

    /** Обновляет имя и сохраняет ту же сущность */
    @Test
    void testUpdateAndSavesSameInstance() throws ResourceNotFoundException {
        Industry existing = new Industry(ID, NAME);
        Industry body = new Industry(ID, NAME_UPDATED);

        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.of(existing));
        Mockito.when(repositoryMock.save(existing)).thenReturn(existing);

        Industry result = service.update(ID, body);

        Assertions.assertSame(existing, result);
        Assertions.assertEquals(ID, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
    }

    /** Кидает IllegalArgumentException, если id в пути и теле разные */
    @Test
    void testIllegalArgumentException() {
        Industry body = new Industry(20L, NAME_UPDATED);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.update(ID, body)
        );

        Assertions.assertEquals(
                "Идентификатор в пути (" + ID +
                        ") не совпадает с идентификатором в теле запроса (" + body.getId() + ")",
                ex.getMessage()
        );
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

}
