package lt.pcbuilder.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lt.pcbuilder.domain.PlanInput;
import lt.pcbuilder.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BuildController {
    private final TenantService tenants;
    private final BuildService builds;
    public BuildController(TenantService tenants, BuildService builds) {
        this.tenants=tenants;this.builds=builds;
    }
    public record BuildRequest(@Min(1) int budgetEur,
                               @NotBlank String purpose,
                               String preferredPartId, List<String> ownedPartIds,
                               String label) { }
    public record RenameRequest(@NotBlank @Size(max=80) String label) { }
    @GetMapping("/health") public Map<String, String> health() { return Map.of("status","ok"); }
    @GetMapping("/tenant") public Map<String,String> tenant(@RequestHeader("X-Demo-Key") String key) {
        return Map.of("tenantId",tenants.requireTenant(key));
    }
    @GetMapping("/catalog") public Object catalog(@RequestHeader("X-Demo-Key") String key) {
        return builds.catalog(tenants.requireTenant(key));
    }
    @PostMapping("/builds")
    @ResponseStatus(HttpStatus.CREATED)
    public BuildView create(@RequestHeader("X-Demo-Key") String key,
                            @Valid @RequestBody BuildRequest req) {
        return builds.create(tenants.requireTenant(key),req.label(),
            new PlanInput(req.budgetEur(),req.purpose(),req.preferredPartId(),req.ownedPartIds()));
    }
    @GetMapping("/builds") public List<BuildView> list(@RequestHeader("X-Demo-Key") String key) {
        return builds.list(tenants.requireTenant(key));
    }
    @GetMapping("/builds/{id}") public BuildView read(@RequestHeader("X-Demo-Key") String key,
                                                       @PathVariable String id) {
        return builds.read(tenants.requireTenant(key),id);
    }
    @PatchMapping("/builds/{id}") public BuildView rename(@RequestHeader("X-Demo-Key") String key,
                                 @PathVariable String id, @Valid @RequestBody RenameRequest body) {
        return builds.rename(tenants.requireTenant(key),id,body.label());
    }
}
