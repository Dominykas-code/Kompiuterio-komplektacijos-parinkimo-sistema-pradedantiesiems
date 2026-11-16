package lt.pcbuilder.ai;

import lt.pcbuilder.domain.Plan;
import lt.pcbuilder.domain.PlanInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Service
public class AiExplanationService {
    private final String apiKey;
    private final String model;
    private final JsonMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    public AiExplanationService(@Value("${app.openai.api-key:}") String apiKey,
                                @Value("${app.openai.model:gpt-4.1-mini}") String model,
                                JsonMapper mapper) {
        this.apiKey=apiKey;this.model=model;this.mapper=mapper;
    }

    public Explanation explain(PlanInput input, Plan plan) {
        String fallback = "Komplektacija parinkta pagal biudžetą, CPU lizdą, RAM tipą, " +
            "maitinimo šaltinio galią ir korpuso apribojimus. " + String.join(" ", plan.notes());
        if (apiKey == null || apiKey.isBlank()) return new Explanation(fallback,false,"NO_API_KEY_FALLBACK");
        try {

            String context = mapper.writeValueAsString(Map.of(
                "purpose", input.purpose(), "budgetEur", input.budgetEur(),
                "totalEur", plan.totalEur(), "score", plan.score(), "validatedParts", plan.parts(),
                "validatedNotes", plan.notes(),
                "allowedReplacementId", plan.replacementPartId()==null ? "" : plan.replacementPartId()
            ));
            String instructions = "Esi kompiuterių komplektacijos paaiškinimo asistentas. " +
                "Paaiškink tik perduotus patikrintus faktus paprasta lietuvių kalba, nekurk naujų komponentų " +
                "ir nepridėk kainų ar techninių pažadų. Grąžink TIK vieną JSON objektą su laukais " +
                "summary (iki 450 simbolių) ir suggestedComponentId (tik allowedReplacementId arba tuščia eilutė).";
            String payload = mapper.writeValueAsString(Map.of(
                "model", model, "instructions", instructions, "input", context
            ));
            HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/responses"))
                .timeout(Duration.ofSeconds(14))
                .header("Authorization","Bearer "+apiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
            HttpResponse<String> response = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return new Explanation(fallback,false,"API_HTTP_"+response.statusCode());
            JsonNode root = mapper.readTree(response.body());
            String output = null;
            JsonNode messages = root.path("output");
            if (messages.isArray()) {
                for (JsonNode message : messages) {
                    JsonNode content = message.path("content");
                    if (content.isArray()) for (JsonNode item : content) {
                        if ("output_text".equals(item.path("type").asText())) {
                            output = item.path("text").asText(); break;
                        }
                    }
                    if (output != null) break;
                }
            }
            if (output == null) return new Explanation(fallback,false,"INVALID_AI_RESPONSE");
            int start = output.indexOf('{'), end = output.lastIndexOf('}');
            if (start < 0 || end <= start) return new Explanation(fallback,false,"INVALID_AI_RESPONSE");
            JsonNode parsed = mapper.readTree(output.substring(start,end+1));
            String summary = parsed.path("summary").asText();
            String suggestedId = parsed.path("suggestedComponentId").isMissingNode() ? "" : parsed.path("suggestedComponentId").asText();
            if (!AiOutputPolicy.valid(summary,suggestedId,plan))
                return new Explanation(fallback,false,"AI_REJECTED_BY_RULES");
            return new Explanation(summary,true,"AI_VALIDATED");
        } catch (Exception ex) {
            return new Explanation(fallback,false,"AI_ERROR_FALLBACK");
        }
    }
}
