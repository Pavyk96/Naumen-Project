package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.mapper.CountryMapper;
import naumen.java.project.model.Country;
import naumen.java.project.service.CountryService;
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
        Mockito.when(service.findAll()).thenReturn(List.of(e));
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/country/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));

        Mockito.verify(service).findAll();
    }

    /** Проверяет корректную работу получения объекта по идентификатору */
    @Test
    @DisplayName("GET /country/{id}")
    void getById_ok_minimal() throws Exception {
        Country e = entity(ID, NAME);
        Mockito.when(service.findById(ID)).thenReturn(e);
        Mockito.when(mapper.toResponse(e)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.get("/country/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).findById(ID);
    }

    /** Проверяет корректное создание нового объекта */
    @Test
    @DisplayName("POST /country")
    void create_ok_minimal() throws Exception {
        CountryRequest req = new CountryRequest(ID, NAME);
        Country created = entity(ID, NAME);
        Mockito.when(service.create(ArgumentMatchers.any(CountryRequest.class))).thenReturn(created);
        Mockito.when(mapper.toResponse(created)).thenReturn(dto(ID, NAME));

        mockMvc.perform(MockMvcRequestBuilders.post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));

        Mockito.verify(service).create(ArgumentMatchers.any(CountryRequest.class));
    }

    /** Проверяет корректное обновление существующего объекта */
    @Test
    @DisplayName("PUT /country/{id}")
    void update_ok_checkOnlyChangedField() throws Exception {
        CountryRequest req = new CountryRequest(ID, NAME_UPDATED);
        Country updated = entity(ID, NAME_UPDATED);
        Mockito.when(service.update(
                ArgumentMatchers.eq(ID),
                ArgumentMatchers.any(CountryRequest.class)
        )).thenReturn(updated);
        Mockito.when(mapper.toResponse(updated)).thenReturn(dto(ID, NAME_UPDATED));

        mockMvc.perform(MockMvcRequestBuilders.put("/country/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));

        Mockito.verify(service).update(ArgumentMatchers.eq(ID), ArgumentMatchers.any(CountryRequest.class));
    }

    /** Проверяет корректное удаление объекта */
    @Test
    @DisplayName("DELETE /country/delete/{id}")
    void delete_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/country/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(service).delete(ID);
    }
}
