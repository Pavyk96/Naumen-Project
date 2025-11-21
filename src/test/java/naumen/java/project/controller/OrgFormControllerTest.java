package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.OrgFormRequestDTO;
import naumen.java.project.dto.OrgFormResponseDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.OrgFormMapper;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.OrgFormService;
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
 * Юнит-тесты для OrgFormController
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class OrgFormControllerTest {

    private static final String ID = "OOO";
    private static final String NAME = "Общество с ограниченной ответственностью";
    private static final String NAME_UPDATED = "ООО (обновлено)";

    private final OrgFormService orgFormServiceMock;
    private final OrgFormMapper orgFormMapperMock;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private OrgForm orgForm;
    private OrgForm orgFormUpdated;
    private OrgFormResponseDTO orgFormDto;
    private OrgFormResponseDTO orgFormUpdatedDto;
    private OrgFormRequestDTO createRequest;
    private OrgFormRequestDTO updateRequest;

    public OrgFormControllerTest(@Mock OrgFormService orgFormServiceMock,
                                 @Mock OrgFormMapper orgFormMapperMock) {
        this.orgFormServiceMock = orgFormServiceMock;
        this.orgFormMapperMock = orgFormMapperMock;

        OrgFormController controller = new OrgFormController(orgFormServiceMock, orgFormMapperMock);

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
        orgForm = new OrgForm(ID, NAME);
        orgFormUpdated = new OrgForm(ID, NAME_UPDATED);

        orgFormDto = new OrgFormResponseDTO(ID, NAME);
        orgFormUpdatedDto = new OrgFormResponseDTO(ID, NAME_UPDATED);

        createRequest = new OrgFormRequestDTO(ID, NAME);
        updateRequest = new OrgFormRequestDTO(ID, NAME_UPDATED);
    }

    /** Проверяет успешное получение списка организационно-правовых форм */
    @Test
    void testGetAllReturnsListOfOrgForms() throws Exception {
        Mockito.when(orgFormServiceMock.findAll()).thenReturn(List.of(orgForm));
        Mockito.when(orgFormMapperMock.toResponse(orgForm)).thenReturn(orgFormDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/org_form/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));
    }

    /** Проверяет успешное получение организационно-правовой формы по идентификатору */
    @Test
    void testGetByIdReturnsOrgForm() throws Exception {
        Mockito.when(orgFormServiceMock.findById(ID)).thenReturn(orgForm);
        Mockito.when(orgFormMapperMock.toResponse(orgForm)).thenReturn(orgFormDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/org_form/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет, что при отсутствии организационно-правовой формы возвращается 404 */
    @Test
    void testGetByIdReturns404IfOrgFormNotFound() throws Exception {
        Mockito.when(orgFormServiceMock.findById(ID))
                .thenThrow(new ResourceNotFoundException("Организационно-правовая форма", ID));

        mockMvc.perform(MockMvcRequestBuilders.get("/org_form/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Организационно-правовая форма с id = " + ID + " не найден(а)"));
    }

    /** Проверяет успешное создание организационно-правовой формы */
    @Test
    void testCreateCreatesOrgForm() throws Exception {
        Mockito.when(orgFormServiceMock.create(Mockito.any(OrgForm.class))).thenReturn(orgForm);
        Mockito.when(orgFormMapperMock.toResponse(orgForm)).thenReturn(orgFormDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/org_form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет успешное обновление организационно-правовой формы */
    @Test
    void testUpdateUpdatesOrgForm() throws Exception {
        Mockito.when(orgFormServiceMock.update(Mockito.eq(ID), Mockito.any(OrgForm.class)))
                .thenReturn(orgFormUpdated);
        Mockito.when(orgFormMapperMock.toResponse(orgFormUpdated)).thenReturn(orgFormUpdatedDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/org_form/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));
    }

    /** Проверяет успешное удаление организационно-правовой формы */
    @Test
    void testDeleteDeletesOrgForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/org_form/delete/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
