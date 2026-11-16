package lt.pcbuilder.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TenantService {
    private final String pcstartKey;
    private final String buildlabKey;
    public TenantService(@Value("${app.demo-key.pcstart}") String pcstartKey,
                         @Value("${app.demo-key.buildlab}") String buildlabKey) {
        this.pcstartKey=pcstartKey; this.buildlabKey=buildlabKey;
    }
    public String requireTenant(String key) {
        if (key != null && key.equals(pcstartKey)) return "pcstart";
        if (key != null && key.equals(buildlabKey)) return "buildlab";
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Neteisingas demonstracinio kliento raktas.");
    }
}
