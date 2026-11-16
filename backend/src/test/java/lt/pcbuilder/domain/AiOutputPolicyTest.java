package lt.pcbuilder.domain;

import lt.pcbuilder.ai.AiOutputPolicy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AiOutputPolicyTest {
    @Test void rejectsHallucinatedPartIdAndAcceptsOnlyApprovedReplacement() {
        Plan p = new BuildPlanner().recommend(DemoCatalog.forTenant("pcstart"),
                new PlanInput(1000,"gaming","a-gpu-5050",List.of()));
        assertFalse(AiOutputPolicy.valid("Pasiūlau naują GPU","unknown-future-gpu",p));
        assertTrue(AiOutputPolicy.valid("Pakeitimas atitinka biudžetą.","a-gpu-mid",p));
        assertFalse(AiOutputPolicy.valid(" ","a-gpu-mid",p));
    }
}
