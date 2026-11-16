package lt.pcbuilder.service;

import lt.pcbuilder.domain.PlanInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TenantIsolationTest {
    @Autowired BuildService service;
    @Autowired TenantService tenants;

    @Test void aTenantCannotReadOrModifyAnotherTenantsSavedBuild() {
        BuildView beta = service.read("buildlab","demo-buildlab-001");
        assertEquals("demo-buildlab-001",beta.id());
        var readError=assertThrows(ResponseStatusException.class,
            () -> service.read("pcstart",beta.id()));
        assertEquals(HttpStatus.NOT_FOUND,readError.getStatusCode());
        var updateError=assertThrows(ResponseStatusException.class,
            () -> service.rename("pcstart",beta.id(),"HACKED"));
        assertEquals(HttpStatus.NOT_FOUND,updateError.getStatusCode());
        assertEquals("Rekomenduojama komplektacija",service.read("buildlab",beta.id()).label());
    }
    @Test void catalogsAreTenantScopedAndKeysMapOnServer() {
        assertEquals("pcstart",tenants.requireTenant("pcstart-demo-key"));
        assertEquals("buildlab",tenants.requireTenant("buildlab-demo-key"));
        assertTrue(service.catalog("pcstart").stream().allMatch(p -> p.id().startsWith("a-")));
        assertTrue(service.catalog("buildlab").stream().allMatch(p -> p.id().startsWith("b-")));
        assertThrows(ResponseStatusException.class,()->tenants.requireTenant("wrong"));
    }
}
