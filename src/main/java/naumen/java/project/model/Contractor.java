package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

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
    private String orgFormId;

    @ManyToMany(mappedBy = "contractors", fetch = FetchType.LAZY)
    private Set<Deal> deals = new HashSet<>();

    public Contractor() { }

    public Contractor(String id, String name, String countryId, Long industryId, String orgFormId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.industryId = industryId;
        this.orgFormId = orgFormId;
    }

    /** Возвращает идентификатор */
    public String getId() { return id; }

    /** Устанавливает идентификатор */
    public void setId(String id) { this.id = id; }

    /** Возвращает название */
    public String getName() { return name; }

    /** Устанавливает название */
    public void setName(String name) { this.name = name; }

    /** Возвращает идентификатор страны */
    public String getCountryId() { return countryId; }

    /** Устанавливает идентификатор страны */
    public void setCountryId(String countryId) { this.countryId = countryId; }

    /** Возвращает идентификатор отрасли */
    public Long getIndustryId() { return industryId; }

    /** Устанавливает идентификатор отрасли */
    public void setIndustryId(Long industryId) { this.industryId = industryId; }

    /** Возвращает идентификатор формы организации */
    public String getOrgFormId() { return orgFormId; }

    /** Устанавливает идентификатор формы организации */
    public void setOrgFormId(String orgFormId) { this.orgFormId = orgFormId; }

    /** Возвращает сделки */
    public Set<Deal> getDeals() { return deals; }
}
