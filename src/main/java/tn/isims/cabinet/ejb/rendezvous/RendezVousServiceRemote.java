package tn.isims.cabinet.ejb.rendezvous;

import jakarta.ejb.Remote;
import tn.isims.cabinet.entity.RendezVous;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface Remote pour le service Rendez-Vous Stateful
 */
@Remote
public interface RendezVousServiceRemote {

    RendezVous creerRendezVous(Long patientId, Long medecinId,
                                LocalDateTime dateRendezVous, String motif);

    RendezVous modifierHoraire(Long rdvId, LocalDateTime nouvelleDate);

    boolean annulerRendezVous(Long rdvId);

    List<RendezVous> listerRendezVousDuJour();

    List<RendezVous> listerRendezVousPasses();

    List<RendezVous> listerTousLesRendezVous();

    List<RendezVous> listerRendezVousParPatient(Long patientId);

    List<RendezVous> listerRendezVousParMedecin(Long medecinId);

    RendezVous trouverRendezVousParId(Long id);

    boolean marquerCommeTermine(Long rdvId);
}
