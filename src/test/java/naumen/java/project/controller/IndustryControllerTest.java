package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import naumen.java.project.service.IndustryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты IndustryController
 *
 * @author Daniil Mezev
 */
@WebMvcTest(IndustryController.class)
class IndustryControllerTest {

    private static final Long ID = 10L;
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private IndustryService service;
    @MockitoBean private IndustryMapper mapper;

    private Industry entity(Long id, String name) {
        return new Industry(id, name);
    }

    private IndustryResponse dto(Long id, String name) {
        return new IndustryResponse(id, name);
    }

    /** Проверяет корректную работу получения списка объектов */
    @Test
    @DisplayName("GET /industry/all")
    void getAll_minimal() throws Exception {
        Industry e = entity(ID, NAME);
        when(service.findAll()).thenReturn(List.of(e));
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/industry/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID.intValue()))
                .andExpect(jsonPath("$[0].name").value(NAME));

        verify(service).findAll();
    }

    /** Проверяет корректную работу получения объекта по идентификатору */
    @Test
    @DisplayName("GET /industry/{id}")
    void getById_minimal() throws Exception {
        Industry e = entity(ID, NAME);
        when(service.findById(ID)).thenReturn(e);
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/industry/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.intValue()))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).findById(ID);
    }

    /** Проверяет корректное создание нового объекта */
    @Test
    @DisplayName("POST /industry")
    void create_minimal() throws Exception {
        IndustryRequest req = new IndustryRequest(ID, NAME);
        Industry created = entity(ID, NAME);
        when(service.create(any(IndustryRequest.class))).thenReturn(created);
        when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(post("/industry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.intValue()))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).create(any(IndustryRequest.class));
    }

    /** Проверяет корректное обновление существующего объекта */
    @Test
    @DisplayName("PUT /industry/{id}")
    void update_minimal() throws Exception {
        IndustryRequest req = new IndustryRequest(ID, NAME_UPDATED);
        Industry updated = entity(ID, NAME_UPDATED);
        when(service.update(eq(ID), any(IndustryRequest.class))).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(put("/industry/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.intValue()))
                .andExpect(jsonPath("$.name").value(NAME_UPDATED));

        verify(service).update(eq(ID), any(IndustryRequest.class));
    }

    /** Проверяет корректное удаление объекта */
    @Test
    @DisplayName("DELETE /industry/delete/{id}")
    void delete_minimal() throws Exception {
        mockMvc.perform(delete("/industry/delete/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).delete(ID);
    }

}
