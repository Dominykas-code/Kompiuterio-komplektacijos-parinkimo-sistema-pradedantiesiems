package lt.pcbuilder.service;

import lt.pcbuilder.domain.Plan;

public record BuildView(String id, String label, Plan plan, String explanation,
                        boolean aiUsed, String aiStatus) { }
