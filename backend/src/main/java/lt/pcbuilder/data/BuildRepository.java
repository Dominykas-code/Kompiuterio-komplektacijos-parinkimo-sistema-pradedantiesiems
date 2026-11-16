package lt.pcbuilder.data;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BuildRepository extends JpaRepository<BuildEntity, String> {
    List<BuildEntity> findAllByTenantIdOrderByLabelAsc(String tenantId);
    Optional<BuildEntity> findByIdAndTenantId(String id, String tenantId);
}
