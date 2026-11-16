package lt.pcbuilder.domain;

import java.util.List;

public record PlanInput(int budgetEur, String purpose, String preferredPartId, List<String> ownedPartIds) {
    public PlanInput {
        ownedPartIds = ownedPartIds == null ? List.of() : List.copyOf(ownedPartIds);
    }
}
