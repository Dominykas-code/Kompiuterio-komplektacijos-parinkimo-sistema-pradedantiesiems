package lt.pcbuilder.data;

import jakarta.persistence.*;
import lt.pcbuilder.domain.*;

@Entity
@Table(name="catalog_components", indexes=@Index(name="ix_component_tenant", columnList="tenant_id"))
public class ComponentEntity {
    @Id private String id;
    @Column(name="tenant_id", nullable=false) private String tenantId;
    private String name;
    @Enumerated(EnumType.STRING) private PartType type;
    private int priceEur;
    private int performance;
    private String socket;
    private String ramType;
    private int drawWatts;
    private int supplyWatts;
    private String formFactor;
    private int gpuLengthMm;
    private int caseGpuLimitMm;
    @Column(length=1200) private String sourceUrl;

    protected ComponentEntity() { }
    public ComponentEntity(Part p) {
        id=p.id(); tenantId=p.tenantId(); name=p.name(); type=p.type(); priceEur=p.priceEur();
        performance=p.performance(); socket=p.socket(); ramType=p.ramType();
        drawWatts=p.drawWatts(); supplyWatts=p.supplyWatts(); formFactor=p.formFactor();
        gpuLengthMm=p.gpuLengthMm(); caseGpuLimitMm=p.caseGpuLimitMm(); sourceUrl=p.sourceUrl();
    }
    public Part toPart() { return new Part(id, tenantId, name, type, priceEur, performance, socket,
            ramType, drawWatts, supplyWatts, formFactor, gpuLengthMm, caseGpuLimitMm, sourceUrl); }
}
