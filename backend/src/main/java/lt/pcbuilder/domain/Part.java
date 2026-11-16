package lt.pcbuilder.domain;

public record Part(
    String id, String tenantId, String name, PartType type,
    int priceEur, int performance, String socket, String ramType,
    int drawWatts, int supplyWatts, String formFactor,
    int gpuLengthMm, int caseGpuLimitMm, String sourceUrl
) { }
