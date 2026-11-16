package lt.pcbuilder.domain;

import java.util.List;

public record Plan(List<Part> parts, int totalEur, double score, List<String> notes,
                   String replacedPartId, String replacementPartId) {
    public Plan {
        parts = List.copyOf(parts);
        notes = List.copyOf(notes);
    }
}
