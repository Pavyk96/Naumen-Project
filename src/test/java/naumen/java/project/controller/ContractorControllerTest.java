package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.ContractorRequest;
import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.ContractorService;
import naumen.java.project.service.CountryService;
import naumen.java.project.service.IndustryService;
import naumen.java.project.service.OrgFormService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для ContractorController
 *
 * @author Daniil Mezev
 */
@WebMvcTest(ContractorController.class)
class ContractorControllerTest {

    private static final String ID = "c-1";
    private static final String NAME = "Acme LLC";
    private static final String NAME_UPDATED = "Acme Updated";
    private static final String COUNTRY_ID = "RU";
    private static final Long INDUSTRY_ID = 10L;
    private static final String ORGFORM_ID = "OOO"; // <-- теперь строка

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private ContractorService contractorService;
    @MockitoBean private CountryService countryService;
    @MockitoBean private IndustryService industryService;
    @MockitoBean private OrgFormService orgFormService;
    @MockitoBean private ContractorMapper mapper;

    /** Хелпер для сборки сущности */
    private Contractor contractor(String name) {
        return new Contractor(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    private void stubMapping(Contractor entity) {
        Country country = new Country(COUNTRY_ID, "Russia");
        Industry industry = new Industry(INDUSTRY_ID, "IT");
        OrgForm orgForm = new OrgForm(ORGFORM_ID, "ООО"); // id строка

        when(countryService.findById(COUNTRY_ID)).thenReturn(country);
        when(industryService.findById(INDUSTRY_ID)).thenReturn(industry);
        when(orgFormService.findById(ORGFORM_ID)).thenReturn(orgForm);

        when(mapper.toResponse(eq(entity), eq(country), eq(industry), eq(orgForm)))
                .thenAnswer(inv -> new ContractorResponse(
                        entity.getId(),
                        entity.getName(),
                        new CountryResponse(country.getId(), country.getName()),
                        new IndustryResponse(industry.getId(), industry.getName()),
                        new OrgFormResponse(orgForm.getId(), orgForm.getName())
                ));
    }

    @Test
    @DisplayName("GET /contractor/all")
    void getAll_ok_minimal() throws Exception {
        Contractor entity = contractor(NAME);
        when(contractorService.findAll()).thenReturn(List.of(entity));
        stubMapping(entity);

        mockMvc.perform(get("/contractor/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID))
                .andExpect(jsonPath("$[0].name").value(NAME))
                .andExpect(jsonPath("$[0].country.id").value(COUNTRY_ID))
                .andExpect(jsonPath("$[0].industry.id").value(INDUSTRY_ID.intValue()))
                .andExpect(jsonPath("$[0].orgForm.id").value(ORGFORM_ID));

        verify(contractorService).findAll();
        verify(mapper, atLeastOnce()).toResponse(any(), any(), any(), any());
    }

    @Test
    @DisplayName("GET /contractor/{id}")
    void getById_ok_minimal() throws Exception {
        Contractor entity = contractor(NAME);
        when(contractorService.findById(ID)).thenReturn(entity);
        stubMapping(entity);

        mockMvc.perform(get("/contractor/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    @DisplayName("POST /contractor")
    void create_ok_minimal() throws Exception {
        ContractorRequest req = new ContractorRequest(ID, NAME, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
        Contractor created = contractor(NAME);
        when(contractorService.create(any(ContractorRequest.class))).thenReturn(created);
        stubMapping(created);

        mockMvc.perform(post("/contractor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    @DisplayName("PUT /contractor/{id}")
    void update_ok_checkOnlyChangedField() throws Exception {
        ContractorRequest req = new ContractorRequest(ID, NAME_UPDATED, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
        Contractor updated = contractor(NAME_UPDATED);
        when(contractorService.update(eq(ID), any(ContractorRequest.class))).thenReturn(updated);
        stubMapping(updated);

        mockMvc.perform(put("/contractor/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME_UPDATED));
    }

    @Test
    @DisplayName("DELETE /contractor/delete/{id}")
    void delete_ok() throws Exception {
        mockMvc.perform(delete("/contractor/delete/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(contractorService).delete(ID);
        verifyNoInteractions(countryService, industryService, orgFormService, mapper);
    }

}
