package lt.pcbuilder.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BuildPlannerTest {
    private final BuildPlanner planner = new BuildPlanner();
    private final List<Part> catalog = DemoCatalog.forTenant("pcstart");

    @Test void normalBuildIsCompleteCompatibleAndWithinBudget() {
        Plan result = planner.recommend(catalog,new PlanInput(1000,"gaming",null,List.of()));
        assertEquals(7,result.parts().size());
        assertEquals(965,result.totalEur());
        assertEquals(84.0,result.score());
        assertTrue(planner.compatible(result.parts()));
        assertTrue(result.parts().stream().allMatch(p -> p.tenantId().equals("pcstart")));
    }
    @Test void lowerBudgetUsesDifferentRealPartsAndKeepsPriceLimit() {
        Plan result = planner.recommend(catalog,new PlanInput(800,"gaming",null,List.of()));
        assertEquals(7,result.parts().size());
        assertEquals(786,result.totalEur());
        assertTrue(planner.compatible(result.parts()));
        assertTrue(result.parts().stream().allMatch(p -> p.sourceUrl()!=null && p.sourceUrl().startsWith("https://")));
        assertTrue(result.parts().stream().anyMatch(p -> p.name().contains("Ryzen 5 5600")));
    }
    @Test void conflictingPreferredPartGetsValidReplacement() {
        Plan result=planner.recommend(catalog,new PlanInput(1000,"gaming","a-gpu-5050",List.of()));
        assertEquals("a-gpu-5050",result.replacedPartId());
        assertEquals("a-gpu-mid",result.replacementPartId());
        assertTrue(result.totalEur() <= 1000);
        assertTrue(planner.compatible(result.parts()));
    }
    @Test void impossibleBudgetReturnsExplicitError() {
        PlanningException ex=assertThrows(PlanningException.class,
            () -> planner.recommend(catalog,new PlanInput(200,"gaming",null,List.of())));
        assertEquals("NO_BUILD",ex.code());
    }
    @Test void foreignPartIdIsRejected() {
        PlanningException ex=assertThrows(PlanningException.class,
            () -> planner.recommend(catalog,new PlanInput(1000,"gaming","b-cpu-basic",List.of())));
        assertEquals("PART_NOT_FOUND",ex.code());
    }
    @Test void ownedStorageIsFreeButStillIncluded() {
        Plan result=planner.recommend(catalog,new PlanInput(1000,"gaming",null,List.of("a-ssd-1000")));
        assertTrue(result.parts().stream().anyMatch(p -> p.id().equals("a-ssd-1000")));
        int paid = result.parts().stream().filter(p -> !p.id().equals("a-ssd-1000"))
            .mapToInt(Part::priceEur).sum();
        assertEquals(paid,result.totalEur());
    }
}
