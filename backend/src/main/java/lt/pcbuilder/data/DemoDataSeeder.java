package lt.pcbuilder.data;

import lt.pcbuilder.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class DemoDataSeeder implements CommandLineRunner {
    private final ComponentRepository components;
    private final BuildRepository builds;
    private final JsonMapper mapper;
    public DemoDataSeeder(ComponentRepository components, BuildRepository builds, JsonMapper mapper) {
        this.components=components; this.builds=builds; this.mapper=mapper;
    }
    @Override public void run(String... args) {
        if (components.count() > 0) return;
        BuildPlanner planner = new BuildPlanner();
        for (String tenant : List.of("pcstart", "buildlab")) {
            var parts = DemoCatalog.forTenant(tenant);
            components.saveAll(parts.stream().map(ComponentEntity::new).toList());
            var plan = planner.recommend(parts, new PlanInput(1000,"gaming",null,List.of()));
            builds.save(new BuildEntity("demo-"+tenant+"-001",tenant,"Rekomenduojama komplektacija",
                mapper.writeValueAsString(plan),"Komplektacija sudaryta pagal biudžetą ir pagrindinius suderinamumo kriterijus.",
                false,"DEMO_SEEDED"));
        }
    }
}
