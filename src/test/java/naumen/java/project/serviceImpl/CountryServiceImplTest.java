package naumen.java.project.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
import naumen.java.project.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private CountryRepository repository;

    @InjectMocks
    private CountryServiceImpl service;

    private static final String ID_RAW = " ru ";
    private static final String ID_NORM = "RU";
    private static final String NAME = "Russia";
    private static final String NAME_UPDATED = "Russian Federation";

    private Country entity(String id, String name) {
        return new Country(id, name);
    }

    private CountryRequest req(String id, String name) {
        return new CountryRequest(id, name);
    }

    @Test
    @DisplayName("findAll: проксирование в репозиторий")
    void findAll_ok() {
        when(repository.findAll()).thenReturn(List.of(entity(ID_NORM, NAME)));

        List<Country> all = service.findAll();

        assertEquals(1, all.size());
        assertEquals(ID_NORM, all.get(0).getId());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById: нормализует id и возвращает страну")
    void findById_ok_withNormalization() {
        when(repository.findById(ID_NORM)).thenReturn(Optional.of(entity(ID_NORM, NAME)));

        Country c = service.findById(ID_RAW);

        assertEquals(ID_NORM, c.getId());
        verify(repository).findById(ID_NORM);
    }

    @Test
    @DisplayName("findById: не найден -> EntityNotFoundException")
    void findById_notFound() {
        when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.findById(ID_RAW));
        assertTrue(ex.getMessage().contains(ID_NORM));
        verify(repository).findById(ID_NORM);
    }

    @Test
    @DisplayName("create: конфликт id -> IllegalArgumentException")
    void create_conflict() {
        when(repository.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(req(ID_RAW, NAME)));
        assertTrue(ex.getMessage().contains(ID_NORM));
        verify(repository).existsById(ID_NORM);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: нормализует id и сохраняет")
    void create_ok() {
        when(repository.existsById(ID_NORM)).thenReturn(false);
        when(repository.save(any(Country.class))).thenAnswer(inv -> inv.getArgument(0));

        Country created = service.create(req(ID_RAW, NAME));

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(repository).save(captor.capture());
        Country toSave = captor.getValue();

        assertEquals(ID_NORM, toSave.getId());
        assertEquals(NAME, toSave.getName());
        assertEquals(ID_NORM, created.getId());
    }

    @Test
    @DisplayName("update: несовпадение path/body после нормализации -> IllegalArgumentException")
    void update_idMismatch() {
        CountryRequest body = req("us", NAME_UPDATED);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(ID_RAW, body));
        assertTrue(ex.getMessage().contains("Path id and body id must be equal"));
        verify(repository, never()).findById(anyString());
    }

    @Test
    @DisplayName("update: не найден -> EntityNotFoundException")
    void update_notFound() {
        when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.update(ID_RAW, req(ID_RAW, NAME_UPDATED)));
        assertTrue(ex.getMessage().contains(ID_NORM));
        verify(repository).findById(ID_NORM);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: обновляет имя и сохраняет")
    void update_ok() {
        Country existing = entity(ID_NORM, NAME);
        when(repository.findById(ID_NORM)).thenReturn(Optional.of(existing));
        when(repository.save(any(Country.class))).thenAnswer(inv -> inv.getArgument(0));

        Country updated = service.update(ID_RAW, req(ID_RAW, NAME_UPDATED));

        assertEquals(ID_NORM, updated.getId());
        assertEquals(NAME_UPDATED, updated.getName());
        verify(repository).findById(ID_NORM);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("delete: не найден -> EntityNotFoundException")
    void delete_notFound() {
        when(repository.existsById(ID_NORM)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.delete(ID_RAW));
        assertTrue(ex.getMessage().contains(ID_NORM));
        verify(repository).existsById(ID_NORM);
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("delete: нормализует id и удаляет")
    void delete_ok() {
        when(repository.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        verify(repository).existsById(ID_NORM);
        verify(repository).deleteById(ID_NORM);
    }

}
