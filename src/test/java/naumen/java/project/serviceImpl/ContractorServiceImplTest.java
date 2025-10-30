package naumen.java.project.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.ContractorRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import naumen.java.project.service.ContractorService;
import naumen.java.project.service.impl.ContractorServiceImpl;
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
class ContractorServiceImplTest {

    @Mock
    private ContractorRepository repository;

    @InjectMocks
    private ContractorServiceImpl service;

    private static final String ID = "c-1";
    private static final String NAME = "Acme LLC";
    private static final String NAME_UPDATED = "Acme Updated";
    private static final String COUNTRY_ID = "RU";
    private static final Long INDUSTRY_ID = 10L;
    private static final String ORGFORM_ID = "OOO";

    private Contractor entity(String name) {
        return new Contractor(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    private ContractorRequest req(String name) {
        return new ContractorRequest(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    @Test
    @DisplayName("findAll: проксирует вызов репозитория")
    void findAll_ok() {
        when(repository.findAll()).thenReturn(List.of(entity(NAME)));

        List<Contractor> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(NAME, result.get(0).getName());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById: найден")
    void findById_found() {
        when(repository.findById(ID)).thenReturn(Optional.of(entity(NAME)));

        Contractor c = service.findById(ID);

        assertEquals(ID, c.getId());
        assertEquals(NAME, c.getName());
        verify(repository).findById(ID);
    }

    @Test
    @DisplayName("findById: не найден -> EntityNotFoundException")
    void findById_notFound() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.findById(ID));
        assertTrue(ex.getMessage().contains(ID));
        verify(repository).findById(ID);
    }

    @Test
    @DisplayName("create: если id уже существует -> IllegalArgumentException")
    void create_alreadyExists() {
        when(repository.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req(NAME)));
        assertTrue(ex.getMessage().contains(ID));
        verify(repository).existsById(ID);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create: сохраняет сущность, собранную из DTO")
    void create_ok() {
        when(repository.existsById(ID)).thenReturn(false);
        ArgumentCaptor<Contractor> captor = ArgumentCaptor.forClass(Contractor.class);
        when(repository.save(any(Contractor.class))).thenAnswer(inv -> inv.getArgument(0));

        Contractor created = service.create(req(NAME));

        verify(repository).existsById(ID);
        verify(repository).save(captor.capture());
        Contractor toSave = captor.getValue();
        assertEquals(ID, toSave.getId());
        assertEquals(NAME, toSave.getName());
        assertEquals(COUNTRY_ID, toSave.getCountryId());
        assertEquals(INDUSTRY_ID, toSave.getIndustryId());
        assertEquals(ORGFORM_ID, toSave.getOrgFormId());
        assertEquals(NAME, created.getName());
    }

    @Test
    @DisplayName("update: если path id != body id -> IllegalArgumentException")
    void update_idMismatch() {
        ContractorRequest otherReq = new ContractorRequest("other", NAME_UPDATED, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.update(ID, otherReq));
        assertTrue(ex.getMessage().contains("Path id"));
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: когда не найден -> EntityNotFoundException")
    void update_notFound() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.update(ID, req(NAME_UPDATED)));
        assertTrue(ex.getMessage().contains(ID));
        verify(repository).findById(ID);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update: обновляет изменяемые поля и сохраняет")
    void update_ok() {
        Contractor existing = entity(NAME);
        when(repository.findById(ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(Contractor.class))).thenAnswer(inv -> inv.getArgument(0));

        Contractor updated = service.update(ID, req(NAME_UPDATED));

        assertEquals(NAME_UPDATED, updated.getName());
        assertEquals(COUNTRY_ID, updated.getCountryId());
        assertEquals(INDUSTRY_ID, updated.getIndustryId());
        assertEquals(ORGFORM_ID, updated.getOrgFormId());
        verify(repository).findById(ID);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("delete: когда не существует -> EntityNotFoundException")
    void delete_notFound() {
        when(repository.existsById(ID)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.delete(ID));
        assertTrue(ex.getMessage().contains(ID));
        verify(repository).existsById(ID);
        verify(repository, never()).deleteById(any());
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
