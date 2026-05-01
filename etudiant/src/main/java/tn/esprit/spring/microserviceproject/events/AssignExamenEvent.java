package tn.esprit.spring.microserviceproject.events;

public class AssignExamenEvent {

     private Long enseignantId;
    private Long examenId;

    public AssignExamenEvent() {}

    public AssignExamenEvent(Long enseignantId, Long examenId) {
        this.enseignantId = enseignantId;
        this.examenId = examenId;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }

    public Long getExamenId() {
        return examenId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }
    
}
