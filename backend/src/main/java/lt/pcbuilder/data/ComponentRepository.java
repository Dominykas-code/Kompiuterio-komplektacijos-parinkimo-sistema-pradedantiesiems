package lt.pcbuilder.data;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComponentRepository extends JpaRepository<ComponentEntity, String> {
    List<ComponentEntity> findAllByTenantIdOrderByTypeAscIdAsc(String tenantId);
}
