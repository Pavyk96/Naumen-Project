package naumen.java.project.repository;

import naumen.java.project.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий доступа к справочнику стран
 *
 * @author Daniil Mezev
 */
public interface CountryRepository extends JpaRepository<Country, String> {
}
