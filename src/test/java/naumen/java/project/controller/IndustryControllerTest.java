package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.IndustryRequestDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.IndustryMapper;
import naumen.java.project.model.Industry;
import naumen.java.project.service.IndustryService;
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

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final Industry industry;

    public IndustryControllerTest(@Mock IndustryService industryServiceMock) {
        this.industryServiceMock = industryServiceMock;
        IndustryMapper industryMapper = new IndustryMapper();

        IndustryController controller = new IndustryController(industryServiceMock, industryMapper);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        industry = new Industry(ID, NAME);
        this.objectMapper = new ObjectMapper();
    }

    /** Проверяет успешное получение списка индустрий */
    @Test
    void testGetAllReturnsListOfIndustries() throws Exception {
        Mockito.when(industryServiceMock.findAll()).thenReturn(List.of(industry));

        mockMvc.perform(MockMvcRequestBuilders.get("/industry/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));
    }

    /** Проверяет успешное получение индустрии по идентификатору */
    @Test
    void testGetByIdReturnsIndustry() throws Exception {
        Mockito.when(industryServiceMock.findById(ID)).thenReturn(industry);

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
                        .value("Индустрия с id = " + ID + " не найден(а)"));
    }

    /** Проверяет успешное создание индустрии */
    @Test
    void testCreateCreatesIndustry() throws Exception {
        Mockito.when(industryServiceMock.save(Mockito.any(Industry.class))).thenReturn(industry);

        mockMvc.perform(MockMvcRequestBuilders.post("/industry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IndustryRequestDTO(ID, NAME))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.intValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет успешное обновление индустрии */
    @Test
    void testUpdateUpdatesIndustry() throws Exception {
        Mockito.when(industryServiceMock.findById(ID)).thenReturn(industry);
        Mockito.when(industryServiceMock.save(Mockito.any(Industry.class)))
                .thenReturn(new Industry(ID, NAME_UPDATED));

        mockMvc.perform(MockMvcRequestBuilders.put("/industry/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IndustryRequestDTO(ID, NAME_UPDATED))))
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
