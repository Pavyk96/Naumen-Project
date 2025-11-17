package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.IndustryRequestDTO;
import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import naumen.java.project.service.IndustryService;
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
 * Юнит-тесты для IndustryController
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class IndustryControllerTest {

    private static final Long ID = 10L;
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

    private final IndustryService industryServiceMock;
    private final IndustryMapper industryMapperMock;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private Industry industry;
    private Industry industryUpdated;
    private IndustryResponseDTO industryDto;
    private IndustryResponseDTO industryUpdatedDto;
    private IndustryRequestDTO createRequest;
    private IndustryRequestDTO updateRequest;

    public IndustryControllerTest(@Mock IndustryService industryServiceMock,
                                  @Mock IndustryMapper industryMapperMock) {
        this.industryServiceMock = industryServiceMock;
        this.industryMapperMock = industryMapperMock;

        IndustryController controller = new IndustryController(industryServiceMock, industryMapperMock);

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
        industry = new Industry(ID, NAME);
        industryUpdated = new Industry(ID, NAME_UPDATED);

        industryDto = new IndustryResponseDTO(ID, NAME);
        industryUpdatedDto = new IndustryResponseDTO(ID, NAME_UPDATED);

        createRequest = new IndustryRequestDTO(ID, NAME);
        updateRequest = new IndustryRequestDTO(ID, NAME_UPDATED);
    }

    /** Проверяет успешное получение списка индустрий */
    @Test
    void testGetAllReturnsListOfIndustries() throws Exception {
        Mockito.when(industryServiceMock.findAll()).thenReturn(List.of(industry));
        Mockito.when(industryMapperMock.toResponse(industry)).thenReturn(industryDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));
    }

    /** Проверяет успешное получение индустрии по идентификатору */
    @Test
    void testGetByIdReturnsIndustry() throws Exception {
        Mockito.when(industryServiceMock.findById(ID)).thenReturn(industry);
        Mockito.when(industryMapperMock.toResponse(industry)).thenReturn(industryDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет, что при отсутствии индустрии возвращается 404 */
    @Test
    void testGetByIdReturns404IfIndustryNotFound() throws Exception {
        Mockito.when(industryServiceMock.findById(ID))
                .thenThrow(new ResourceNotFoundException("Индустрия", String.valueOf(ID)));

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Индустрия с id = " + ID + " не найдена"));
    }

    /** Проверяет успешное создание индустрии */
    @Test
    void testCreateCreatesIndustry() throws Exception {
        Mockito.when(industryServiceMock.create(Mockito.any(Industry.class))).thenReturn(industry);
        Mockito.when(industryMapperMock.toResponse(industry)).thenReturn(industryDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/industry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет успешное обновление индустрии */
    @Test
    void testUpdateUpdatesIndustry() throws Exception {
        Mockito.when(industryServiceMock.update(Mockito.eq(ID), Mockito.any(Industry.class)))
                .thenReturn(industryUpdated);
        Mockito.when(industryMapperMock.toResponse(industryUpdated))
                .thenReturn(industryUpdatedDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/industry/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));
    }

    /** Проверяет успешное удаление индустрии */
    @Test
    void testDeleteDeletesIndustry() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/industry/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
