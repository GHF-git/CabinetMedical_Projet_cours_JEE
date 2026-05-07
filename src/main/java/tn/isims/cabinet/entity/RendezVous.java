package tn.isims.cabinet.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "RENDEZVOUS")
public class RendezVous implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Statut {
        PLANIFIE, TERMINE, ANNULE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // EAGER — required so JSP can access patient/medecin fields outside the EJB transaction
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Column(name = "date_rendez_vous", nullable = false)
    private LocalDateTime dateRendezVous;

    @Column(name = "motif")
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut = Statut.PLANIFIE;

    public RendezVous() {}

    public RendezVous(Patient patient, Medecin medecin, LocalDateTime dateRendezVous, String motif) {
        this.patient = patient;
        this.medecin = medecin;
        this.dateRendezVous = dateRendezVous;
        this.motif = motif;
        this.statut = Statut.PLANIFIE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }
    public LocalDateTime getDateRendezVous() { return dateRendezVous; }
    public void setDateRendezVous(LocalDateTime dateRendezVous) { this.dateRendezVous = dateRendezVous; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    // Convenience helpers for JSP — avoids EL date formatting issues
    public String getDateFormatted() {
        if (dateRendezVous == null) return "";
        return String.format("%02d/%02d/%04d %02d:%02d",
            dateRendezVous.getDayOfMonth(), dateRendezVous.getMonthValue(),
            dateRendezVous.getYear(), dateRendezVous.getHour(), dateRendezVous.getMinute());
    }

    public String getDateForInput() {
        if (dateRendezVous == null) return "";
        return dateRendezVous.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    @Transient
    public String getMotifForJson() {
        if (motif == null) return "";
        return motif.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    public String getStatutLower() {
        return statut != null ? statut.name().toLowerCase() : "planifie";
    }

    @Override
    public String toString() {
        return "RendezVous{id=" + id + ", statut=" + statut + '}';
    }
}
