package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.mapper.CountryMapper;
import naumen.java.project.model.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import naumen.java.project.service.CountryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для CountryController
 *
 * @author Daniil Mezev
 */
@WebMvcTest(CountryController.class)
class CountryControllerTest {

    private static final String ID = "RU";
    private static final String NAME = "Russia";
    private static final String NAME_UPDATED = "Russian Federation";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private CountryService service;
    @MockitoBean private CountryMapper mapper;

    private Country entity(String id, String name) {
        return new Country(id, name);
    }

    private CountryResponse dto(String id, String name) {
        return new CountryResponse(id, name);
    }

    /** Проверяет корректную работу получения списка объектов */
    @Test
    @DisplayName("GET /country/all")
    void getAll_ok_minimal() throws Exception {
        Country e = entity(ID, NAME);
        when(service.findAll()).thenReturn(List.of(e));
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/country/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID))
                .andExpect(jsonPath("$[0].name").value(NAME));

        verify(service).findAll();
    }

    /** Проверяет корректную работу получения объекта по идентификатору */
    @Test
    @DisplayName("GET /country/{id}")
    void getById_ok_minimal() throws Exception {
        Country e = entity(ID, NAME);
        when(service.findById(ID)).thenReturn(e);
        when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(get("/country/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).findById(ID);
    }

    /** Проверяет корректное создание нового объекта */
    @Test
    @DisplayName("POST /country")
    void create_ok_minimal() throws Exception {
        CountryRequest req = new CountryRequest(ID, NAME);
        Country created = entity(ID, NAME);
        when(service.create(any(CountryRequest.class))).thenReturn(created);
        when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(service).create(any(CountryRequest.class));
    }

    /** Проверяет корректное обновление существующего объекта */
    @Test
    @DisplayName("PUT /country/{id}")
    void update_ok_checkOnlyChangedField() throws Exception {
        CountryRequest req = new CountryRequest(ID, NAME_UPDATED);
        Country updated = entity(ID, NAME_UPDATED);
        when(service.update(eq(ID), any(CountryRequest.class))).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(put("/country/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME_UPDATED));

        verify(service).update(eq(ID), any(CountryRequest.class));
    }

    /** Проверяет корректное удаление объекта */
    @Test
    @DisplayName("DELETE /country/delete/{id}")
    void delete_ok() throws Exception {
        mockMvc.perform(delete("/country/delete/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).delete(ID);
    }

}
