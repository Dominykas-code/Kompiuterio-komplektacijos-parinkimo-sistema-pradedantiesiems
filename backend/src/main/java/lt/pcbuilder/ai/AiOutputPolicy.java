package lt.pcbuilder.ai;

import lt.pcbuilder.domain.Plan;
import java.util.Set;
import java.util.stream.Collectors;


public final class AiOutputPolicy {
    private AiOutputPolicy() { }
    public static boolean valid(String summary, String suggestedComponentId, Plan plan) {
        if (summary == null || summary.isBlank() || summary.length() > 450) return false;
        if (summary.indexOf('\u0000') >= 0) return false;
        if (suggestedComponentId == null || suggestedComponentId.isBlank()) return true;
        Set<String> approved = plan.parts().stream().map(p -> p.id()).collect(Collectors.toSet());
        return approved.contains(suggestedComponentId) &&
            (plan.replacementPartId() == null || plan.replacementPartId().equals(suggestedComponentId));
    }
}
