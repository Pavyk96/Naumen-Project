package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @DisplayName("GET /org_form/all")
    void getAll_minimal() throws Exception {
        OrgForm e = entity(ID, NAME);
        when(service.findAll()).thenReturn(List.of(e));
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/org_form/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID))
                .andExpect(jsonPath("$[0].name").value(NAME));

        verify(service).findAll();
    }

    @Test
    @DisplayName("GET /org_form/{id}")
    void getById_minimal() throws Exception {
        OrgForm e = entity(ID, NAME);
        when(service.findById(ID)).thenReturn(e);
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/org_form/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).findById(ID);
    }

    @Test
    @DisplayName("POST /org_form")
    void create_minimal() throws Exception {
        OrgFormRequest req = new OrgFormRequest(ID, NAME);
        OrgForm created = entity(ID, NAME);
        when(service.create(any(OrgFormRequest.class))).thenReturn(created);
        when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(post("/org_form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).create(any(OrgFormRequest.class));
    }

    @Test
    @DisplayName("PUT /org_form/{id}")
    void update_minimal() throws Exception {
        OrgFormRequest req = new OrgFormRequest(ID, NAME_UPDATED);
        OrgForm updated = entity(ID, NAME_UPDATED);
        when(service.update(eq(ID), any(OrgFormRequest.class))).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(put("/org_form/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME_UPDATED));

        verify(service).update(eq(ID), any(OrgFormRequest.class));
    }

    @Test
    @DisplayName("DELETE /org_form/delete/{id}")
    void delete_minimal() throws Exception {
        mockMvc.perform(delete("/org_form/delete/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).delete(ID);
    }

}
