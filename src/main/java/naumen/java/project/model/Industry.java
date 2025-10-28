package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Индустрия
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "ref_industry")
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    public Industry() {
    }

    public Industry(String name) {
        this.name = name;
    }

    public Industry(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
