package naumen.java.project.repository;

import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий доступа к данным сделок
 *
 * @author Daria
 */
public interface DealRepository extends JpaRepository<Deal, UUID> {
    /**
     * Загружает сделки с контрагентами через EntityGraph
     */
    @EntityGraph(attributePaths = "contractors")
    Optional<Deal> findWithContractorsById(UUID id);

    /**
     * Извлекает все существующие сделки, инициализируя список контрагентов
     */
    @EntityGraph(attributePaths = "contractors")
    @Query("SELECT d FROM Deal d")
    List<Deal> findAllWithContractors();

    /**
     * Выполняет поиск сделок по набору фильтров с глубокой загрузкой связанных данных
     */
    @Query("SELECT d FROM Deal d " +
            "LEFT JOIN FETCH d.contractors c " +
            "LEFT JOIN FETCH c.industry " +
            "WHERE (d.type IN :types) " +
            "AND (d.status IN :statuses) " +
            "AND (d.openedAt >= :openedFrom) " +
            "AND (d.openedAt <= :openedTo) " +
            "AND (d.agreementDate >= :agreementFrom) " +
            "AND (d.agreementDate <= :agreementTo)"
    )
    List<Deal> findDealsWithFilters(
            @Param("types") List<DealType> types,
            @Param("statuses") List<DealStatus> statuses,
            @Param("openedFrom") LocalDateTime openedFrom,
            @Param("openedTo") LocalDateTime openedTo,
            @Param("agreementFrom") LocalDate agreementFrom,
            @Param("agreementTo") LocalDate agreementTo
    );

}