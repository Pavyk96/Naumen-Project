package naumen.java.project.repository;

import naumen.java.project.model.Contractor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    /**
     * Возвращает контрагентов с учётом фильтров
     */
    @EntityGraph(attributePaths = {"deals", "country", "industry", "orgForm"})
    @Query("""
        select distinct c from Contractor c
        left join c.deals d
        where (:countryIds is null or c.country.id in :countryIds)
          and (:industryIds is null or c.industry.id in :industryIds)
          and (:orgFormIds is null or c.orgForm.id in :orgFormIds)
          and c.createDate >= coalesce(:fromCreateDate, c.createDate)
          and c.createDate <= coalesce(:toCreateDate,   c.createDate)
        """)
    List<Contractor> findWithFilters(
            @Param("countryIds") List<String> countryIds,
            @Param("industryIds") List<Long> industryIds,
            @Param("orgFormIds") List<String> orgFormIds,
            @Param("fromCreateDate") LocalDate fromCreateDate,
            @Param("toCreateDate") LocalDate toCreateDate
    );
}
