package naumen.java.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Контрагент для работы со сделками
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "contractor")
public class Contractor {

    @Id
    @Column(name = "id", length = 36, nullable = false, unique = true)
    private String id;

    @NotBlank
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @NotBlank
    @Size(min = 2, max = 3)
    @Column(name = "country_id", length = 3, nullable = false)
    private String countryId;

    @NotNull
    @Column(name = "industry_id", nullable = false)
    private Long industryId;

    @NotNull
    @Column(name = "org_form_id", nullable = false)
    private Long orgFormId;

    public Contractor() { }

    public Contractor(String id, String name, String countryId, Long industryId, Long orgFormId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.industryId = industryId;
        this.orgFormId = orgFormId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }

    public Long getIndustryId() { return industryId; }
    public void setIndustryId(Long industryId) { this.industryId = industryId; }

    public Long getOrgFormId() { return orgFormId; }
    public void setOrgFormId(Long orgFormId) { this.orgFormId = orgFormId; }
}
