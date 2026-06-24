package com.edunet.etudiant.Dtos;

public class ParticipationDTO {
    private Long id;
    private Long examenId;
    private Double note;
    private String statut;

    public ParticipationDTO() {}

    public ParticipationDTO(Long id, Long examenId, Double note, String statut) {
        this.id = id;
        this.examenId = examenId;
        this.note = note;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamenId() {
        return examenId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
