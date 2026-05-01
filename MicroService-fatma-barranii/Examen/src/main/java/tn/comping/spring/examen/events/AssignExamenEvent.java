package tn.comping.spring.examen.events;

public class AssignExamenEvent  {

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

    public Long getExamenId() {
        return examenId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }
}