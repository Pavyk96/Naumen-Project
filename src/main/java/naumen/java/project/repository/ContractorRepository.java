package naumen.java.project.repository;

import naumen.java.project.model.Contractor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий доступа к данным контрагентов
 *
 * @author Daniil Mezev
 */
public interface ContractorRepository extends JpaRepository<Contractor, UUID> {

    /**
     * Возвращает всех контрагентов вместе с их сделками
     * Использует EntityGraph для жадной загрузки коллекции deals,
     * чтобы избежать N+1 и не выполнять дополнительные запросы
     */
    @EntityGraph(attributePaths = "deals")
    @Query("SELECT c FROM Contractor c")
    List<Contractor> findAllWithDeals();
}
