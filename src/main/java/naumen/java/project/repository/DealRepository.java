package naumen.java.project.repository;

import naumen.java.project.model.Deal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий доступа к данным сделок
 *
 * @author Daria
 */
public interface DealRepository extends JpaRepository<Deal, UUID> {
    /** Загружает сделки с контрагентами через EntityGraph */
    @EntityGraph(attributePaths = "contractors")
    Optional<Deal> findWithContractorsById(UUID id);

    @EntityGraph(attributePaths = "contractors")
    @Query("SELECT d FROM Deal d")
    List<Deal> findAllWithContractors();
}