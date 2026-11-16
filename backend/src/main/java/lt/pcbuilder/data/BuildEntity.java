package lt.pcbuilder.data;

import jakarta.persistence.*;

@Entity
@Table(name="saved_builds", indexes=@Index(name="ix_build_tenant", columnList="tenant_id"))
public class BuildEntity {
    @Id private String id;
    @Column(name="tenant_id", nullable=false) private String tenantId;
    private String label;
    @Lob private String planJson;
    @Lob private String explanation;
    private boolean aiUsed;
    private String aiStatus;

    protected BuildEntity() { }
    public BuildEntity(String id, String tenantId, String label, String planJson,
                       String explanation, boolean aiUsed, String aiStatus) {
        this.id=id; this.tenantId=tenantId; this.label=label; this.planJson=planJson;
        this.explanation=explanation; this.aiUsed=aiUsed; this.aiStatus=aiStatus;
    }
    public String getId() {return id;}
    public String getTenantId() {return tenantId;}
    public String getLabel() {return label;}
    public void setLabel(String label) {this.label=label;}
    public String getPlanJson() {return planJson;}
    public String getExplanation() {return explanation;}
    public boolean isAiUsed() {return aiUsed;}
    public String getAiStatus() {return aiStatus;}
}
