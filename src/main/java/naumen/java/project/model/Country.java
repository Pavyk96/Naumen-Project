package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Страна
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "country")
public class Country {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @NotBlank
    private String id;

    @Column(name = "name", nullable = false, length = 128)
    @NotBlank
    private String name;

    protected Country() {
    }

    public Country(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
