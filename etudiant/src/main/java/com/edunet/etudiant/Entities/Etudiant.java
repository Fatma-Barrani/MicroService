package com.edunet.etudiant.Entities;

import lombok.*;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Getter
@Setter
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String nom;

    @NonNull
    private String prenom;

    @NonNull
    private String email;

    @NonNull
    private String filiere;

    @NonNull
    private Integer anneeInscription;

    @NonNull
    private Double moyenneGenerale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NonNull String getNom() {
        return nom;
    }

    public void setNom(@NonNull String nom) {
        this.nom = nom;
    }

    public @NonNull String getPrenom() {
        return prenom;
    }

    public void setPrenom(@NonNull String prenom) {
        this.prenom = prenom;
    }

    public @NonNull String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public @NonNull String getFiliere() {
        return filiere;
    }

    public void setFiliere(@NonNull String filiere) {
        this.filiere = filiere;
    }

    public @NonNull Integer getAnneeInscription() {
        return anneeInscription;
    }

    public void setAnneeInscription(@NonNull Integer anneeInscription) {
        this.anneeInscription = anneeInscription;
    }

    public @NonNull Double getMoyenneGenerale() {
        return moyenneGenerale;
    }

    public void setMoyenneGenerale(@NonNull Double moyenneGenerale) {
        this.moyenneGenerale = moyenneGenerale;
    }
}
