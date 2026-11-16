package lt.pcbuilder.domain;

import java.util.List;

public final class CoreSelfTest {
    private static final BuildPlanner planner = new BuildPlanner();
    public static void main(String[] args) {
        var alpha = DemoCatalog.forTenant("pcstart");
        Plan good = planner.recommend(alpha, new PlanInput(1000, "gaming", null, List.of()));
        check(good.totalEur() <= 1000 && good.parts().size() == 7 && planner.compatible(good.parts()), "normal build");
        Plan conflict = planner.recommend(alpha, new PlanInput(1000, "gaming", "a-gpu-5050", List.of()));
        check("a-gpu-5050".equals(conflict.replacedPartId()) && conflict.replacementPartId() != null, "fallback alternative");
        check(!conflict.parts().stream().anyMatch(p -> p.id().equals("a-gpu-5050")), "incompatible choice removed");
        expect("NO_BUILD", () -> planner.recommend(alpha, new PlanInput(200, "gaming", null, List.of())));
        expect("PART_NOT_FOUND", () -> planner.recommend(alpha, new PlanInput(1000, "gaming", "b-cpu-basic", List.of())));
        Plan owned = planner.recommend(alpha, new PlanInput(1000, "gaming", null, List.of("a-ssd-1000")));
        check(owned.parts().stream().anyMatch(p -> p.id().equals("a-ssd-1000")), "owned SSD included");
        check(owned.totalEur() == owned.parts().stream().filter(p -> !p.id().equals("a-ssd-1000")).mapToInt(Part::priceEur).sum(), "owned SSD excluded from paid price");
        Plan beta = planner.recommend(DemoCatalog.forTenant("buildlab"), new PlanInput(1000,"office",null,List.of()));
        check(beta.parts().stream().allMatch(p -> p.tenantId().equals("buildlab")), "tenant catalog separate");
        System.out.println("CORE CHECKS PASSED (6 checks/groups)");
        System.out.println("Alpha: " + good.totalEur() + " EUR, score " + good.score());
        System.out.println("Beta: " + beta.totalEur() + " EUR, score " + beta.score());
    }
    private static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
    private static void expect(String code, Runnable operation) {
        try { operation.run(); } catch (PlanningException ex) {
            check(code.equals(ex.code()), "expected " + code + " but got " + ex.code()); return;
        }
        throw new AssertionError("Expected error " + code);
    }
}
