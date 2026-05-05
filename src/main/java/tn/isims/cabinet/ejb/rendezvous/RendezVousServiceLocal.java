package tn.isims.cabinet.ejb.rendezvous;

import jakarta.ejb.Local;
import tn.isims.cabinet.entity.RendezVous;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface Local pour le service Rendez-Vous (intra-JVM, pas de sérialisation)
 */
@Local
public interface RendezVousServiceLocal {

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
