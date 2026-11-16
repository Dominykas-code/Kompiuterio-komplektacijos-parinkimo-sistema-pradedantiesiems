package lt.pcbuilder.service;

import lt.pcbuilder.ai.AiExplanationService;
import lt.pcbuilder.data.*;
import lt.pcbuilder.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;
import java.util.UUID;

@Service
public class BuildService {
    private final ComponentRepository components;
    private final BuildRepository builds;
    private final AiExplanationService explanations;
    private final JsonMapper mapper;
    private final BuildPlanner planner = new BuildPlanner();
    public BuildService(ComponentRepository components, BuildRepository builds,
                        AiExplanationService explanations, JsonMapper mapper) {
        this.components=components; this.builds=builds; this.explanations=explanations; this.mapper=mapper;
    }
    @Transactional(readOnly=true)
    public List<Part> catalog(String tenant) {
        return components.findAllByTenantIdOrderByTypeAscIdAsc(tenant)
            .stream().map(ComponentEntity::toPart).toList();
    }
    @Transactional
    public BuildView create(String tenant, String label, PlanInput input) {
        Plan plan = planner.recommend(catalog(tenant), input);
        var explanation = explanations.explain(input, plan);
        String id = UUID.randomUUID().toString();
        String actualLabel = label == null || label.isBlank() ? "Nauja komplektacija" : label.trim();
        BuildEntity entity = new BuildEntity(id, tenant, actualLabel,
            mapper.writeValueAsString(plan), explanation.text(), explanation.aiUsed(), explanation.status());
        builds.save(entity);
        return view(entity);
    }
    @Transactional(readOnly=true)
    public List<BuildView> list(String tenant) {
        return builds.findAllByTenantIdOrderByLabelAsc(tenant).stream().map(this::view).toList();
    }
    @Transactional(readOnly=true)
    public BuildView read(String tenant, String id) { return view(scoped(tenant, id)); }
    @Transactional
    public BuildView rename(String tenant, String id, String label) {
        if (label == null || label.isBlank() || label.length() > 80)
            throw new PlanningException("INVALID_LABEL", "Pavadinimas turi būti 1–80 simbolių ilgio.");
        var entity = scoped(tenant, id);
        entity.setLabel(label.trim());
        return view(entity);
    }
    private BuildEntity scoped(String tenant, String id) {
        // This is the data isolation boundary: even a known foreign ID cannot be read or edited.
        return builds.findByIdAndTenantId(id, tenant).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Komplektacija nerasta šio kliento kontekste."));
    }
    private BuildView view(BuildEntity e) {
        return new BuildView(e.getId(),e.getLabel(), mapper.readValue(e.getPlanJson(),Plan.class),
                             e.getExplanation(),e.isAiUsed(),e.getAiStatus());
    }
}
