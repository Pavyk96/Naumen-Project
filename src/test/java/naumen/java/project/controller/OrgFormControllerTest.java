package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.OrgFormService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

/**
 * Тесты OrgFormController
 *
 * @author Daniil Mezev
 */
@WebMvcTest(OrgFormController.class)
class OrgFormControllerTest {

    private static final String ID = "OOO";
    private static final String NAME = "Общество с ограниченной ответственностью";
    private static final String NAME_UPDATED = "ООО (обновлено)";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private OrgFormService service;
    @MockitoBean private OrgFormMapper mapper;

    private OrgForm entity(String id, String name) { return new OrgForm(id, name); }
    private OrgFormResponse dto(String id, String name) { return new OrgFormResponse(id, name); }

    /** Проверяет корректную работу получения списка объектов */
    @Test
    @DisplayName("GET /org_form/all")
    void getAll_minimal() throws Exception {
        OrgForm e = entity(ID, NAME);
        Mockito.when(service.findAll()).thenReturn(List.of(e));
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/org_form/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));

        Mockito.verify(service).findAll();
    }

    /** Проверяет корректную работу получения объекта по идентификатору */
    @Test
    @DisplayName("GET /org_form/{id}")
    void getById_minimal() throws Exception {
        OrgForm e = entity(ID, NAME);
        Mockito.when(service.findById(ID)).thenReturn(e);
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/org_form/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).findById(ID);
    }

    /** Проверяет корректное создание нового объекта */
    @Test
    @DisplayName("POST /org_form")
    void create_minimal() throws Exception {
        OrgFormRequest req = new OrgFormRequest(ID, NAME);
        OrgForm created = entity(ID, NAME);
        Mockito.when(service.create(ArgumentMatchers.any(OrgFormRequest.class))).thenReturn(created);
        Mockito.when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.post("/org_form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).create(ArgumentMatchers.any(OrgFormRequest.class));
    }

    /** Проверяет корректное обновление существующего объекта */
    @Test
    @DisplayName("PUT /org_form/{id}")
    void update_minimal() throws Exception {
        OrgFormRequest req = new OrgFormRequest(ID, NAME_UPDATED);
        OrgForm updated = entity(ID, NAME_UPDATED);
        Mockito.when(service.update(
                ArgumentMatchers.eq(ID),
                ArgumentMatchers.any(OrgFormRequest.class)
        )).thenReturn(updated);
        Mockito.when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(MockMvcRequestBuilders.put("/org_form/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));

        Mockito.verify(service).update(ArgumentMatchers.eq(ID), ArgumentMatchers.any(OrgFormRequest.class));
    }

    /** Проверяет корректное удаление объекта */
    @Test
    @DisplayName("DELETE /org_form/delete/{id}")
    void delete_minimal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/org_form/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(service).delete(ID);
    }

}
