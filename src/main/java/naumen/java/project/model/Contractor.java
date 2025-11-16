package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_form_id", nullable = false)
    private OrgForm orgForm;

    @ManyToMany(mappedBy = "contractors", fetch = FetchType.LAZY)
    private Set<Deal> deals = new HashSet<>();

    protected Contractor() { }

    public Contractor(String id, String name, Country country, Industry industry, OrgForm orgForm) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.industry = industry;
        this.orgForm = orgForm;
    }

    /** Возвращает идентификатор */
    public String getId() { return id; }

    /** Устанавливает идентификатор */
    public void setId(String id) { this.id = id; }

    /** Возвращает название */
    public String getName() { return name; }

    /** Устанавливает название */
    public void setName(String name) { this.name = name; }

    /** Возвращает страну */
    public Country getCountry() { return country; }

    /** Устанавливает страну */
    public void setCountry(Country country) { this.country = country; }

    /** Возвращает индустрию */
    public Industry getIndustry() { return industry; }

    /** Устанавливает индустрию */
    public void setIndustry(Industry industry) { this.industry = industry; }

    /** Возвращает ОПФ */
    public OrgForm getOrgForm() { return orgForm; }

    /** Устанавливает ОПФ */
    public void setOrgForm(OrgForm orgForm) { this.orgForm = orgForm; }

    /** Возвращает сделки */
    public Set<Deal> getDeals() { return deals; }
}
