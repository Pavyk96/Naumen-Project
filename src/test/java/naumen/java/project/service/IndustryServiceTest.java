package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndustryServiceTest {

    @Mock
    private IndustryRepository repository;

    @InjectMocks
    private IndustryService service;

    private static final Long ID = 10L;
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

    private Industry entity(Long id, String name) {
        return new Industry(id, name);
    }

    private IndustryRequest req(Long id, String name) {
        return new IndustryRequest(id, name);
    }

    @Test
    @DisplayName("findAll: проксирование в репозиторий")
    void findAll_ok() {
        when(repository.findAll()).thenReturn(List.of(entity(ID, NAME)));

        List<Industry> all = service.findAll();

        assertEquals(1, all.size());
        assertEquals(ID, all.get(0).getId());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById: найден")
    void findById_found() {
        when(repository.findById(ID)).thenReturn(Optional.of(entity(ID, NAME)));

        Industry result = service.findById(ID);

        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        verify(repository).findById(ID);
    }

    @Test
    @DisplayName("findById: не найден -> EntityNotFoundException")
    void findById_notFound() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.findById(ID));
        assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        verify(repository).findById(ID);
    }

    @Test
    @DisplayName("update: несовпадение path/body -> IllegalArgumentException")
    void update_idMismatch() {
        IndustryRequest body = req(777L, NAME_UPDATED);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(ID, body));
        assertTrue(ex.getMessage().contains("Path id and body id must be equal"));
        verify(repository, never()).findById(anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: не найден -> EntityNotFoundException")
    void update_notFound() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.update(ID, req(ID, NAME_UPDATED)));
        assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        verify(repository).findById(ID);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: обновляет имя и сохраняет")
    void update_ok() {
        Industry existing = entity(ID, NAME);
        when(repository.findById(ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(Industry.class))).thenAnswer(inv -> inv.getArgument(0));

        Industry updated = service.update(ID, req(ID, NAME_UPDATED));

        assertEquals(ID, updated.getId());
        assertEquals(NAME_UPDATED, updated.getName());
        verify(repository).findById(ID);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("delete: не существует -> EntityNotFoundException")
    void delete_notFound() {
        when(repository.existsById(ID)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(ID));
        assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        verify(repository).existsById(ID);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("delete: удаляет по id")
    void delete_ok() {
        when(repository.existsById(ID)).thenReturn(true);

        service.delete(ID);

        verify(repository).existsById(ID);
        verify(repository).deleteById(ID);
    }

}
