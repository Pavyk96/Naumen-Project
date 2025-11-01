package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Сделка
 *
 * @author Daria
 */
@Entity
@Table(name = "deal")
public class Deal {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "description", length = 500)
    private String description;

    @NotBlank
    @Column(name = "agreement_number", length = 50, nullable = false)
    private String agreementNumber;


    @Column(name = "agreement_date", nullable = false)
    private LocalDate agreementDate;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private DealType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private DealStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "deal_contractor",
            joinColumns = @JoinColumn(name = "deal_id"),
            inverseJoinColumns = @JoinColumn(name = "contractor_id")
    )
    private Set<Contractor> contractors = new HashSet<>();

    public Deal() {
    }

    public Deal(UUID id, String description, String agreementNumber,
                LocalDate agreementDate, LocalDateTime openedAt,
                LocalDateTime closedAt, DealType type, DealStatus status) {
        this.id = id;
        this.description = description;
        this.agreementNumber = agreementNumber;
        this.agreementDate = agreementDate;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.type = type;
        this.status = status;
    }

    /**
     * Возвращает идентификатор
     */
    public UUID getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Возвращает описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает номер соглашения
     */
    public String getAgreementNumber() {
        return agreementNumber;
    }

    /**
     * Устанавливает номер соглашения
     */
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    /**
     * Возвращает дату соглашения
     */
    public LocalDate getAgreementDate() {
        return agreementDate;
    }

    /**
     * Устанавливает дату соглашения
     */
    public void setAgreementDate(LocalDate agreementDate) {
        this.agreementDate = agreementDate;
    }

    /**
     * Возвращает дату открытия
     */
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    /**
     * Устанавливает дату открытия
     */
    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    /**
     * Возвращает дату закрытия
     */
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    /**
     * Устанавливает дату закрытия
     */
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    /**
     * Возвращает тип
     */
    public DealType getType() {
        return type;
    }

    /**
     * Устанавливает тип
     */
    public void setType(DealType type) {
        this.type = type;
    }

    /**
     * Возвращает статус
     */
    public DealStatus getStatus() {
        return status;
    }

    /**
     * Устанавливает статус
     */
    public void setStatus(DealStatus status) {
        this.status = status;
    }

    /**
     * Возвращает контрагентов
     */
    public Set<Contractor> getContractors() {
        return contractors;
    }

    /**
     * Устанавливает контрагентов
     */
    public void setContractors(Set<Contractor> contractors) {
        this.contractors = contractors;
    }

    /**
     * Добавляет контрагента
     */
    public void addContractor(Contractor contractor) {
        this.contractors.add(contractor);
        contractor.getDeals().add(this);
    }

    /**
     * Удаляет контрагента
     */
    public void removeContractor(Contractor contractor) {
        this.contractors.remove(contractor);
        contractor.getDeals().remove(this);
    }
}