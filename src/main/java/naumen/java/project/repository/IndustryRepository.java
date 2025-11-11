package naumen.java.project.repository;

import naumen.java.project.model.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий доступа к справочнику индустрий
 *
 * @author Daniil Mezev
 */
public interface IndustryRepository extends JpaRepository<Industry, Long> { }
