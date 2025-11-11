package naumen.java.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Индустрия
 *
 * @author Daniil Mezev
 */
@Entity
@Table(name = "industry")
public class Industry {

    @Id
    @Column(name = "id", length = 10, nullable = false, unique = true)
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

    /** Возвращает идентификатор */
    public Long getId() { return id; }

    /** Устанавливает идентификатор */
    public void setId(Long id) { this.id = id; }

    /** Возвращает название */
    public String getName() { return name; }

    /** Устанавливает название */
    public void setName(String name) { this.name = name; }

}
