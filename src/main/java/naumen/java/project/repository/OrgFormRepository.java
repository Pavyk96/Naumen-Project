package naumen.java.project.repository;

import naumen.java.project.model.OrgForm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для доступа к справочнику организационно-правовых форм.
 *
 * @author Daniil Mezev
 */
public interface OrgFormRepository extends JpaRepository<OrgForm, String> { }