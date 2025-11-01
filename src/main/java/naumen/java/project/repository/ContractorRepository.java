package naumen.java.project.repository;

import naumen.java.project.model.Contractor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий доступа к данным контрагентов
 *
 * @author Daniil Mezev
 */
public interface ContractorRepository extends JpaRepository<Contractor, String> {

    /** Загружает контрагента со сделками через EntityGraph */
    @EntityGraph(attributePaths = "deals")
    Optional<Contractor> findWithDealsById(String id);
}
