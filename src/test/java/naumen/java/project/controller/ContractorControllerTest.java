package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.contractor.ContractorRequestDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.mapper.ContractorMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.service.ContractorService;
import naumen.java.project.service.CountryService;
import naumen.java.project.service.IndustryService;
import naumen.java.project.service.OrgFormService;
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
import java.util.UUID;

/**
 * Юнит-тесты для ContractorController
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class ContractorControllerTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String NAME = "Acme LLC";
    private static final String NAME_UPDATED = "Acme Updated";
    private static final String COUNTRY_ID = "RU";
    private static final Long INDUSTRY_ID = 10L;
    private static final String ORGFORM_ID = "OOO";

    private final ContractorService contractorServiceMock;
    private final CountryService countryServiceMock;
    private final IndustryService industryServiceMock;
    private final OrgFormService orgFormServiceMock;

    private final Contractor contractor;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContractorControllerTest(@Mock ContractorService contractorServiceMock,
                                    @Mock CountryService countryServiceMock,
                                    @Mock IndustryService industryServiceMock,
                                    @Mock OrgFormService orgFormServiceMock) {
        this.contractorServiceMock = contractorServiceMock;
        this.countryServiceMock = countryServiceMock;
        this.industryServiceMock = industryServiceMock;
        this.orgFormServiceMock = orgFormServiceMock;

        ContractorMapper contractorMapper = new ContractorMapper();

        ContractorController controller = new ContractorController(
                contractorServiceMock,
                contractorMapper,
                countryServiceMock,
                industryServiceMock,
                orgFormServiceMock
        );

        Country country = new Country(COUNTRY_ID, "Russia");
        Industry industry = new Industry(INDUSTRY_ID, "IT");
        OrgForm orgForm = new OrgForm(ORGFORM_ID, "ООО");

        contractor = new Contractor(NAME, country, industry, orgForm);
        contractor.setId(ID);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /** Проверяет успешное получение списка контрагентов */
    @Test
    void testGetAllReturnsListOfContractors() throws Exception {
        Mockito.when(contractorServiceMock.findAll()).thenReturn(List.of(contractor));

        mockMvc.perform(MockMvcRequestBuilders.get("/contractor/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(NAME));
    }

    /** Проверяет успешное получение контрагента по идентификатору */
    @Test
    void testGetContractorById() throws Exception {
        Mockito.when(contractorServiceMock.findById(ID)).thenReturn(contractor);

        mockMvc.perform(MockMvcRequestBuilders.get("/contractor/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет, что при отсутствии контрагента возвращается 404 */
    @Test
    void testGetNoExistContractor() throws Exception {
        Mockito.when(contractorServiceMock.findById(ID))
                .thenThrow(new ResourceNotFoundException("Контрагент", String.valueOf(ID)));

        mockMvc.perform(MockMvcRequestBuilders.get("/contractor/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Контрагент с id = " + ID + " не найден(а)"));
    }

    /** Проверяет успешное создание контрагента */
    @Test
    void testCreateContractor() throws Exception {
        ContractorRequestDTO request = new ContractorRequestDTO(String.valueOf(ID), NAME, COUNTRY_ID,
                INDUSTRY_ID, ORGFORM_ID);

        Country country = new Country(COUNTRY_ID, "Russia");
        Industry industry = new Industry(INDUSTRY_ID, "IT");
        OrgForm orgForm = new OrgForm(ORGFORM_ID, "ООО");

        Mockito.when(countryServiceMock.findById(COUNTRY_ID)).thenReturn(country);
        Mockito.when(industryServiceMock.findById(INDUSTRY_ID)).thenReturn(industry);
        Mockito.when(orgFormServiceMock.findById(ORGFORM_ID)).thenReturn(orgForm);
        Mockito.when(contractorServiceMock.save(Mockito.any(Contractor.class))).thenReturn(contractor);

        mockMvc.perform(MockMvcRequestBuilders.post("/contractor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME));
    }

    /** Проверяет, что при несуществующей стране при создании возвращается 404 */
    @Test
    void testCreateContractorWithOutCountry() throws Exception {
        ContractorRequestDTO request = new ContractorRequestDTO(String.valueOf(ID), NAME, COUNTRY_ID,
                INDUSTRY_ID, ORGFORM_ID);

        Mockito.when(countryServiceMock.findById(COUNTRY_ID))
                .thenThrow(new ResourceNotFoundException("Страна", COUNTRY_ID));

        mockMvc.perform(MockMvcRequestBuilders.post("/contractor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Страна с id = " + COUNTRY_ID + " не найден(а)"));
    }

    /** Проверяет успешное обновление контрагентов */
    @Test
    void testUpdateUpdatesContractor() throws Exception {
        ContractorRequestDTO request = new ContractorRequestDTO(String.valueOf(ID), NAME_UPDATED,
                COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);

        Country country = new Country(COUNTRY_ID, "Russia");
        Industry industry = new Industry(INDUSTRY_ID, "IT");
        OrgForm orgForm = new OrgForm(ORGFORM_ID, "ООО");

        Contractor updateContractor = new Contractor(NAME_UPDATED, country, industry, orgForm);
        updateContractor.setId(ID);

        Mockito.when(countryServiceMock.findById(COUNTRY_ID)).thenReturn(country);
        Mockito.when(industryServiceMock.findById(INDUSTRY_ID)).thenReturn(industry);
        Mockito.when(orgFormServiceMock.findById(ORGFORM_ID)).thenReturn(orgForm);
        Mockito.when(contractorServiceMock.findById(ID)).thenReturn(contractor);
        Mockito.when(contractorServiceMock.save(contractor)).thenReturn(updateContractor);

        mockMvc.perform(MockMvcRequestBuilders.put("/contractor/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(NAME_UPDATED));
    }

    /** Проверяет успешное удаление контрагента */
    @Test
    void testDeleteContractor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/contractor/{id}", ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
