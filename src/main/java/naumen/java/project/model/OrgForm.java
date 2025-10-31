package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Оргиназационно-правовая форма
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "ref_org_form")
public class OrgForm {

    @Id
    @Column(name = "id", length = 10, nullable = false, unique = true)
    private String id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    public OrgForm() { }

    public OrgForm(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /** Возвращает идентификатор */
    public String getId() { return id; }

    /** Устанавливает идентификатор */
    public void setId(String id) { this.id = id; }

    /** Возвращает название */
    public String getName() { return name; }

    /** Устанавливает название */
    public void setName(String name) { this.name = name; }
}
