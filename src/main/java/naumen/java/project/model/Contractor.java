package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Контрагент для работы со сделками
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "contractor")
public class Contractor {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

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

    @Column(name = "create_date")
    private LocalDate createDate;

    protected Contractor() { }

    public Contractor(String name, Country country, Industry industry, OrgForm orgForm) {
        this.name = name;
        this.country = country;
        this.industry = industry;
        this.orgForm = orgForm;
    }

    /** Возвращает идентификатор */
    public UUID getId() { return id; }

    /** Устанавливает идентификатор */
    public void setId(UUID id) { this.id = id; }

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

    /**
     * Вернуть дату создания
     */
    public LocalDate getCreateDate() {
        return createDate;
    }

    /**
     * Установит дату создания
     */
    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
