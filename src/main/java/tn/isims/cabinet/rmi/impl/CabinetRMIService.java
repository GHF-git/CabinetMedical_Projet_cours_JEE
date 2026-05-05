package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.ejb.patient.PatientServiceRemote;
import tn.isims.cabinet.ejb.rendezvous.RendezVousServiceRemote;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;
import tn.isims.cabinet.rmi.callback.PatientCallback;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class CabinetRMIService extends UnicastRemoteObject implements CabinetRMIServiceRemote {

    private static final long serialVersionUID = 1L;

    // JNDI names for WildFly remote lookup
    private static final String PATIENT_JNDI =
        "java:global/CabinetMedical/PatientService!tn.isims.cabinet.ejb.patient.PatientServiceRemote";
    private static final String RDV_JNDI =
        "java:global/CabinetMedical/RendezVousService!tn.isims.cabinet.ejb.rendezvous.RendezVousServiceRemote";

    public CabinetRMIService() throws RemoteException {
        super();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private InitialContext buildContext() throws NamingException {
        Properties p = new Properties();
        p.put("java.naming.factory.initial",
              "org.jboss.naming.remote.client.InitialContextFactory");
        p.put("java.naming.provider.url", "http-remoting://localhost:8080");
        p.put("java.naming.security.principal", "admin");
        p.put("java.naming.security.credentials", "admin123");
        return new InitialContext(p);
    }

    private PatientServiceRemote getPatientService() throws RemoteException {
        try {
            return (PatientServiceRemote) buildContext().lookup(PATIENT_JNDI);
        } catch (Exception e) {
            throw new RemoteException("PatientService introuvable: " + e.getMessage(), e);
        }
    }

    private RendezVousServiceRemote getRdvService() throws RemoteException {
        try {
            return (RendezVousServiceRemote) buildContext().lookup(RDV_JNDI);
        } catch (Exception e) {
            throw new RemoteException("RendezVousService introuvable: " + e.getMessage(), e);
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    @Override
    public boolean sEnregistrerPourNotifications(Long patientId, PatientCallback callback)
            throws RemoteException {
        System.out.println("RMI: Enregistrement du patient ID: " + patientId + " pour les notifications");
        PatientNotificationRegistry.enregistrer(patientId, callback);
        callback.recevoirNotification(
            "CONFIRMATION: Vous êtes maintenant inscrit aux notifications du cabinet médical."
        );
        return true;
    }

    // ── Consultation ─────────────────────────────────────────────────────────

    @Override
    public List<RendezVous> consulterRendezVous(Long patientId) throws RemoteException {
        return consulterRendezVousPassesEtFuturs(patientId);
    }

    @Override
    public List<RendezVous> consulterRendezVousPassesEtFuturs(Long patientId)
            throws RemoteException {
        System.out.println("RMI: Consultation de tous les rendez-vous pour patient ID: " + patientId);
        try {
            return getRdvService().listerRendezVousParPatient(patientId);
        } catch (Exception e) {
            System.err.println("[RMI] Erreur consultation RDV: " + e.getMessage());
            throw new RemoteException("Erreur consultation: " + e.getMessage(), e);
        }
    }

    // ── Créer RDV ─────────────────────────────────────────────────────────────

    @Override
    public String creerRendezVousRMI(Long patientId, Long medecinId,
                                      LocalDateTime dateRendezVous, String motif)
            throws RemoteException {
        try {
            RendezVous rdv = getRdvService().creerRendezVous(patientId, medecinId, dateRendezVous, motif);

            PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
            if (cb != null) {
                cb.recevoirNotification("CRÉATION RDV: Votre rendez-vous est confirmé pour le "
                    + dateRendezVous + " (ID: " + rdv.getId() + ")");
            }
            return "✅ Rendez-vous créé avec succès. ID: " + rdv.getId();
        } catch (Exception e) {
            throw new RemoteException("Erreur création RDV: " + e.getMessage(), e);
        }
    }

    // ── Modifier RDV ──────────────────────────────────────────────────────────

    @Override
    public String modifierRendezVousRMI(Long rdvId, LocalDateTime nouvelleDate)
            throws RemoteException {
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ Rendez-vous ID " + rdvId + " introuvable.";

            RendezVous modifie = svc.modifierHoraire(rdvId, nouvelleDate);
            if (modifie == null) return "❌ Modification impossible (RDV déjà terminé ou annulé).";

            PatientCallback cb = PatientNotificationRegistry.getCallback(rdv.getPatient().getId());
            if (cb != null) {
                cb.recevoirNotification("MODIFICATION RDV #" + rdvId
                    + ": Votre rendez-vous a été déplacé au " + nouvelleDate);
            }
            return "✅ Rendez-vous #" + rdvId + " modifié au " + nouvelleDate;
        } catch (Exception e) {
            throw new RemoteException("Erreur modification RDV: " + e.getMessage(), e);
        }
    }

    // ── Annuler RDV ───────────────────────────────────────────────────────────

    @Override
    public String annulerRendezVousRMI(Long rdvId) throws RemoteException {
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ Rendez-vous ID " + rdvId + " introuvable.";

            boolean ok = svc.annulerRendezVous(rdvId);
            if (!ok) return "❌ Annulation impossible (RDV déjà terminé ou annulé).";

            PatientCallback cb = PatientNotificationRegistry.getCallback(rdv.getPatient().getId());
            if (cb != null) {
                cb.recevoirNotification("ANNULATION RDV #" + rdvId
                    + ": Votre rendez-vous du " + rdv.getDateRendezVous()
                    + " avec Dr. " + rdv.getMedecin().getNom() + " a été annulé.");
            }
            return "✅ Rendez-vous #" + rdvId + " annulé avec succès.";
        } catch (Exception e) {
            throw new RemoteException("Erreur annulation RDV: " + e.getMessage(), e);
        }
    }
}
