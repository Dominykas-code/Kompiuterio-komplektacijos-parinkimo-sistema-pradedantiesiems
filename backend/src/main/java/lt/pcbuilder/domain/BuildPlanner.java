package lt.pcbuilder.domain;

import java.util.*;
import java.util.stream.Collectors;

public final class BuildPlanner {
    private static final List<PartType> REQUIRED = List.of(
        PartType.CPU, PartType.MOTHERBOARD, PartType.RAM, PartType.GPU,
        PartType.STORAGE, PartType.PSU, PartType.CASE);

    public Plan recommend(List<Part> catalog, PlanInput input) {
        if (input.budgetEur() <= 0) throw new PlanningException("INVALID_BUDGET", "Biudžetas turi būti didesnis už 0 €.");
        var weights = weights(input.purpose());
        if (catalog == null || catalog.isEmpty()) throw new PlanningException("EMPTY_CATALOG", "Komponentų katalogas tuščias.");

        Map<String, Part> byId = catalog.stream().collect(Collectors.toMap(Part::id, x -> x, (a,b) -> a));
        Map<PartType, Part> owned = new EnumMap<>(PartType.class);
        for (String id : input.ownedPartIds()) {
            Part p = byId.get(id);
            if (p == null) throw new PlanningException("PART_NOT_FOUND", "Turimas komponentas nerastas šiame kataloge: " + id);
            if (owned.putIfAbsent(p.type(), p) != null)
                throw new PlanningException("DUPLICATE_TYPE", "Negalima pažymėti dviejų turimų tos pačios kategorijos komponentų.");
        }

        Part preferred = null;
        if (input.preferredPartId() != null && !input.preferredPartId().isBlank()) {
            preferred = byId.get(input.preferredPartId());
            if (preferred == null) throw new PlanningException("PART_NOT_FOUND", "Pageidaujamas komponentas nepriklauso pasirinktam katalogui.");
            Part ownedPart = owned.get(preferred.type());
            if (ownedPart != null && !ownedPart.id().equals(preferred.id()))
                throw new PlanningException("CONFLICTING_SELECTION", "Pageidaujamas ir turimas tos pačios kategorijos komponentai skiriasi.");
        }

        List<List<Part>> groups = new ArrayList<>();
        for (PartType type : REQUIRED) {
            List<Part> options = catalog.stream().filter(p -> p.type() == type)
                .sorted(Comparator.comparing(Part::id)).toList();
            if (owned.containsKey(type)) options = List.of(owned.get(type));
            if (options.isEmpty()) throw new PlanningException("MISSING_CATEGORY", "Kataloge nėra kategorijos: " + type);
            groups.add(options);
        }

        Search search = new Search(input.budgetEur(), owned.keySet(), weights);
        enumerate(groups, 0, new ArrayList<>(), 0, search);
        if (search.best == null) throw new PlanningException("NO_BUILD", "Pagal šį biudžetą ir reikalavimus suderinamos komplektacijos nėra.");

        Candidate winner = search.best;
        List<String> notes = new ArrayList<>();
        String replaced = null, replacement = null;
        final Part requestedPref = preferred;
        if (requestedPref != null && winner.parts.stream().noneMatch(p -> p.id().equals(requestedPref.id()))) {

            Search forced = new Search(input.budgetEur(), owned.keySet(), weights);
            final Part requested = preferred;
            List<List<Part>> forcedGroups = groups.stream().map(g ->
                g.get(0).type() == requested.type() ? List.of(requested) : g).toList();
            enumerate(forcedGroups, 0, new ArrayList<>(), 0, forced);
            if (forced.best != null) winner = forced.best;
            else {
                replaced = preferred.id();
                replacement = winner.parts.stream().filter(p -> p.type() == requestedPref.type())
                    .map(Part::id).findFirst().orElse(null);
                notes.add("Pageidaujamo komponento „" + preferred.name() + "“ negalima naudoti pagal biudžeto arba suderinamumo taisykles.");
                if (replacement != null) notes.add("Siūloma patikrinta alternatyva: " +
                    winner.parts.stream().filter(p -> p.type() == requestedPref.type()).findFirst().orElseThrow().name() + ".");
            }
        }
        notes.add("Patikrinti CPU lizdas, RAM tipas, PSU galios atsarga, korpuso dydis ir biudžetas.");
        return new Plan(winner.parts, winner.price, Math.round(winner.score * 100.0)/100.0,
                        notes, replaced, replacement);
    }

    private void enumerate(List<List<Part>> groups, int index, List<Part> current, int subtotal, Search state) {
        if (index == groups.size()) {
            if (!compatible(current)) return;
            double score = score(current, state.weights);
            Candidate candidate = new Candidate(List.copyOf(current), subtotal, score);
            if (state.best == null || score > state.best.score + 0.000001 ||
                (Math.abs(score - state.best.score) < 0.000001 && subtotal < state.best.price))
                state.best = candidate;
            return;
        }
        for (Part part : groups.get(index)) {
            int addition = state.ownedTypes.contains(part.type()) ? 0 : part.priceEur();
            if (subtotal + addition > state.budget) continue;
            current.add(part);
            enumerate(groups, index + 1, current, subtotal + addition, state);
            current.remove(current.size() - 1);
        }
    }

    public boolean compatible(List<Part> parts) {
        Map<PartType, Part> m = new EnumMap<>(PartType.class);
        for (Part p : parts) m.put(p.type(), p);
        if (m.size() != REQUIRED.size()) return false;
        Part cpu = m.get(PartType.CPU), mb = m.get(PartType.MOTHERBOARD);
        Part ram = m.get(PartType.RAM), gpu = m.get(PartType.GPU);
        Part psu = m.get(PartType.PSU), computerCase = m.get(PartType.CASE);
        if (cpu.socket() == null || !cpu.socket().equals(mb.socket())) return false;
        if (ram.ramType() == null || !ram.ramType().equals(mb.ramType())) return false;
        if (computerCase.formFactor() == null || mb.formFactor() == null ||
            !Arrays.asList(computerCase.formFactor().split(",")).contains(mb.formFactor())) return false;
        if (gpu.gpuLengthMm() > computerCase.caseGpuLimitMm()) return false;
        int totalDraw = parts.stream().mapToInt(Part::drawWatts).sum();
        return psu.supplyWatts() * 10L >= totalDraw * 12L; // >= 120 %, integer-safe
    }

    private double score(List<Part> parts, Map<PartType, Double> weights) {
        return parts.stream().mapToDouble(p -> p.performance() * weights.getOrDefault(p.type(), 0.0)).sum();
    }

    private Map<PartType, Double> weights(String purpose) {
        return switch (purpose == null ? "" : purpose.toLowerCase(Locale.ROOT)) {
            case "gaming" -> Map.of(PartType.GPU, 0.50, PartType.CPU, 0.30, PartType.RAM, 0.10, PartType.STORAGE, 0.10);
            case "office" -> Map.of(PartType.GPU, 0.05, PartType.CPU, 0.40, PartType.RAM, 0.25, PartType.STORAGE, 0.30);
            case "universal" -> Map.of(PartType.GPU, 0.25, PartType.CPU, 0.35, PartType.RAM, 0.20, PartType.STORAGE, 0.20);
            default -> throw new PlanningException("INVALID_PURPOSE", "Paskirtis turi būti gaming, office arba universal.");
        };
    }

    private static class Search {
        final int budget;
        final Set<PartType> ownedTypes;
        final Map<PartType, Double> weights;
        Candidate best;
        Search(int budget, Set<PartType> ownedTypes, Map<PartType, Double> weights) {
            this.budget = budget; this.ownedTypes = ownedTypes; this.weights = weights;
        }
    }
    private record Candidate(List<Part> parts, int price, double score) { }
}
