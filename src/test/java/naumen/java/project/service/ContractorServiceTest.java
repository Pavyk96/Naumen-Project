package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.repository.ContractorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.UUID;


/**
 * Юнит-тесты для ContractorService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class ContractorServiceTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final ContractorRepository contractorRepositoryMock;
    private final ContractorService contractorService;

    public ContractorServiceTest(@Mock ContractorRepository contractorRepositoryMock) {
        this.contractorRepositoryMock = contractorRepositoryMock;
        this.contractorService = new ContractorService(contractorRepositoryMock);
    }

    /** Выброс ResourceNotFoundException, если нет ресурса с данным id */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> contractorService.findById(ID)
        );

        Assertions.assertEquals("Контрагент с id = " + ID + " не найден(а)", ex.getMessage());
    }


    /** Кидает ResourceNotFoundException, при existsById */
    @Test
    void testResourceNotFoundExceptionByDelete() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> contractorService.delete(ID)
        );

        Assertions.assertEquals("Контрагент с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Успешное удаление, когда ресурс существует */
    @Test
    void testDeleteSuccess() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> contractorService.delete(ID));

        Mockito.verify(contractorRepositoryMock).deleteById(ID);
    }
}
