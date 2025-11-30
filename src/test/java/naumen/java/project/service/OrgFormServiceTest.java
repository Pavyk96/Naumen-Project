package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
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

    private static final String ID = "OOO";

    private final OrgFormRepository repositoryMock;
    private final OrgFormService service;

    public OrgFormServiceTest(@Mock OrgFormRepository repositoryMock) {
        this.repositoryMock = repositoryMock;
        this.service = new OrgFormService(repositoryMock);
    }

    /** Кидает ResourceNotFoundException, если формы нет */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(repositoryMock.findById(ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(ID)
        );

        Assertions.assertEquals("Организационно-правовая форма с id = "
                + ID + " не найден(а)", ex.getMessage());
    }

    /** Кидает ResourceNotFoundException, если формы нет при existsById */
    @Test
    void testResourceNotFoundExceptionByExistsById() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(ID)
        );

        Assertions.assertEquals("Организационно-правовая форма с id = "
                + ID + " не найден(а)", ex.getMessage());
    }

    /** Успешное удаление формы, когда запись существует */
    @Test
    void testDeleteSuccess() {
        Mockito.when(repositoryMock.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> service.delete(ID));

        Mockito.verify(repositoryMock).deleteById(ID);
    }

}
