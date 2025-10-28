package naumen.java.project.repository;

import naumen.java.project.model.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий доступа к данным контрагентов
 *
 * @author Daniil Mezev
 */
public interface ContractorRepository extends JpaRepository<Contractor, String> { }
