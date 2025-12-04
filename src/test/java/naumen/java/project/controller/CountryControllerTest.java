package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.CountryRequestDTO;
import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.CountryMapper;
import naumen.java.project.model.Country;
import naumen.java.project.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * Юнит-тесты для CountryController
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    private static final String ID = "RU";
    private static final String NAME = "Russia";
    private static final String NAME_UPDATED = "Russian Federation";

    private final CountryService countryServiceMock;
    private final CountryMapper countryMapper;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private Country country;
    private Country countryUpdated;
    private CountryResponseDTO countryDto;
    private CountryResponseDTO countryUpdatedDto;
    private CountryRequestDTO createRequest;
    private CountryRequestDTO updateRequest;

    public CountryControllerTest(@Mock CountryService countryServiceMock) {
        this.countryServiceMock = countryServiceMock;
        this.countryMapper = new CountryMapper();

        CountryController controller = new CountryController(countryServiceMock, countryMapper);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    /**
     * Инициализация переменных для тестов
     */
    @BeforeEach
    void setUpData() {
        country = new Country(ID, NAME);
        countryUpdated = new Country(ID, NAME_UPDATED);

        countryDto = new CountryResponseDTO(ID, NAME);
        countryUpdatedDto = new CountryResponseDTO(ID, NAME_UPDATED);

        createRequest = new CountryRequestDTO(ID, NAME);
        updateRequest = new CountryRequestDTO(ID, NAME_UPDATED);
    }

    /** Проверяет успешное получение списка стран */
    @Test
    void testGetAllReturnsListOfCountries() throws Exception {
        Mockito.when(countryServiceMock.findAll()).thenReturn(List.of(country));

        mockMvc.perform(MockMvcRequestBuilders.get("/country/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));
    }

    /** Проверяет успешное получение страны по идентификатору */
    @Test
    void testGetByIdReturnsCountry() throws Exception {
        Mockito.when(countryServiceMock.findById(ID)).thenReturn(country);

        mockMvc.perform(MockMvcRequestBuilders.get("/country/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет, что при отсутствии страны возвращается 404 */
    @Test
    void testGetByIdReturns404IfCountryNotFound() throws Exception {
        Mockito.when(countryServiceMock.findById(ID))
                .thenThrow(new ResourceNotFoundException("Страна", ID));

        mockMvc.perform(MockMvcRequestBuilders.get("/country/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Страна с id = " + ID + " не найден(а)"));
    }

    /** Проверяет успешное создание страны */
    @Test
    void testCreateCreatesCountry() throws Exception {
        Mockito.when(countryServiceMock.save(Mockito.any(Country.class))).thenReturn(country);

        mockMvc.perform(MockMvcRequestBuilders.post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет успешное обновление страны */
    @Test
    void testUpdateUpdatesCountry() throws Exception {
        Mockito.when(countryServiceMock.findById(ID)).thenReturn(country);
        Mockito.when(countryServiceMock.save(Mockito.any(Country.class)))
                .thenReturn(countryUpdated);

        mockMvc.perform(MockMvcRequestBuilders.put("/country/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));
    }

    /** Проверяет успешное удаление страны */
    @Test
    void testDeleteDeletesCountry() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/country/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
