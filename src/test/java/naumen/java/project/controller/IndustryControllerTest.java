package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import naumen.java.project.service.IndustryService;
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
        Mockito.when(service.findAll()).thenReturn(List.of(e));
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));

        Mockito.verify(service).findAll();
    }

    /** Проверяет корректную работу получения объекта по идентификатору */
    @Test
    @DisplayName("GET /industry/{id}")
    void getById_minimal() throws Exception {
        Industry e = entity(ID, NAME);
        Mockito.when(service.findById(ID)).thenReturn(e);
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).findById(ID);
    }

    /** Проверяет корректное создание нового объекта */
    @Test
    @DisplayName("POST /industry")
    void create_minimal() throws Exception {
        IndustryRequest req = new IndustryRequest(ID, NAME);
        Industry created = entity(ID, NAME);
        Mockito.when(service.create(ArgumentMatchers.any(IndustryRequest.class))).thenReturn(created);
        Mockito.when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.post("/industry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).create(ArgumentMatchers.any(IndustryRequest.class));
    }

    /** Проверяет корректное обновление существующего объекта */
    @Test
    @DisplayName("PUT /industry/{id}")
    void update_minimal() throws Exception {
        IndustryRequest req = new IndustryRequest(ID, NAME_UPDATED);
        Industry updated = entity(ID, NAME_UPDATED);
        Mockito.when(service.update(ArgumentMatchers.eq(ID), ArgumentMatchers.any(IndustryRequest.class)))
                .thenReturn(updated);
        Mockito.when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(MockMvcRequestBuilders.put("/industry/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));

        Mockito.verify(service).update(ArgumentMatchers.eq(ID), ArgumentMatchers.any(IndustryRequest.class));
    }

    /** Проверяет корректное удаление объекта */
    @Test
    @DisplayName("DELETE /industry/delete/{id}")
    void delete_minimal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/industry/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(service).delete(ID);
    }

}
