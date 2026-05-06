package tn.isims.cabinet.rmi.impl;

import tn.isims.cabinet.ejb.medecin.MedecinServiceRemote;
import tn.isims.cabinet.ejb.patient.PatientServiceRemote;
import tn.isims.cabinet.ejb.rendezvous.RendezVousServiceRemote;
import tn.isims.cabinet.entity.Medecin;
import tn.isims.cabinet.entity.Patient;
import tn.isims.cabinet.entity.RendezVous;
import tn.isims.cabinet.rmi.callback.PatientCallback;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class CabinetRMIService extends UnicastRemoteObject implements CabinetRMIServiceRemote {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── WildFly 32 JNDI names ─────────────────────────────────────────────────
    private static final String PATIENT_JNDI =
        "ejb:/CabinetMedical/PatientService!tn.isims.cabinet.ejb.patient.PatientServiceRemote";
    private static final String MEDECIN_JNDI =
        "ejb:/CabinetMedical/MedecinService!tn.isims.cabinet.ejb.medecin.MedecinServiceRemote";
    private static final String RDV_JNDI =
        "ejb:/CabinetMedical/RendezVousService!tn.isims.cabinet.ejb.rendezvous.RendezVousServiceRemote?stateful";

    public CabinetRMIService() throws RemoteException { super(); }

    // ── JNDI Context ─────────────────────────────────────────────────────────
    private InitialContext buildContext() throws Exception {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY,
              "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(Context.PROVIDER_URL,           "remote+http://localhost:8080");
        p.put(Context.SECURITY_PRINCIPAL,     "rmiuser");
        p.put(Context.SECURITY_CREDENTIALS,   "Rmi@12345");
        p.put("jboss.naming.client.ejb.context", "true");
        return new InitialContext(p);
    }

    // ── EJB Lookups ───────────────────────────────────────────────────────────
    private PatientServiceRemote getPatientService() throws RemoteException {
        try { return (PatientServiceRemote) buildContext().lookup(PATIENT_JNDI); }
        catch (Exception e) { throw new RemoteException("PatientService: " + e.getMessage(), e); }
    }

    private MedecinServiceRemote getMedecinService() throws RemoteException {
        try { return (MedecinServiceRemote) buildContext().lookup(MEDECIN_JNDI); }
        catch (Exception e) { throw new RemoteException("MedecinService: " + e.getMessage(), e); }
    }

    private RendezVousServiceRemote getRdvService() throws RemoteException {
        try { return (RendezVousServiceRemote) buildContext().lookup(RDV_JNDI); }
        catch (Exception e) { throw new RemoteException("RendezVousService: " + e.getMessage(), e); }
    }

    // ── Enregistrement notifications ──────────────────────────────────────────
    @Override
    public boolean sEnregistrerPourNotifications(Long patientId, PatientCallback callback)
            throws RemoteException {
        System.out.println("[RMI] Patient " + patientId + " enregistré pour notifications.");
        PatientNotificationRegistry.enregistrer(patientId, callback);
        callback.recevoirNotification(
            "CONFIRMATION: Vous êtes maintenant inscrit aux notifications du cabinet médical."
        );
        return true;
    }

    // ── Consultation RDV ──────────────────────────────────────────────────────
    @Override
    public List<RendezVous> consulterRendezVous(Long patientId) throws RemoteException {
        return consulterRendezVousPassesEtFuturs(patientId);
    }

    @Override
    public List<RendezVous> consulterRendezVousPassesEtFuturs(Long patientId)
            throws RemoteException {
        System.out.println("[RMI] Consultation RDV patient " + patientId);
        try { return getRdvService().listerRendezVousParPatient(patientId); }
        catch (Exception e) { throw new RemoteException("Erreur consultation: " + e.getMessage(), e); }
    }

    // ── Liste médecins ────────────────────────────────────────────────────────
    @Override
    public List<Medecin> listerMedecins() throws RemoteException {
        try { return getMedecinService().listerTousLesMedecins(); }
        catch (Exception e) { throw new RemoteException("Erreur médecins: " + e.getMessage(), e); }
    }

    // ── Liste patients (implémentation manquante ajoutée) ─────────────────────
    @Override
    public List<Patient> listerPatients() throws RemoteException {
        try { return getPatientService().listerTousLesPatients(); }
        catch (Exception e) { throw new RemoteException("Erreur patients: " + e.getMessage(), e); }
    }

    // ── Créer RDV ─────────────────────────────────────────────────────────────
    @Override
    public String creerRendezVousRMI(Long patientId, Long medecinId,
                                      LocalDateTime date, String motif)
            throws RemoteException {
        try {
            RendezVous rdv = getRdvService().creerRendezVous(patientId, medecinId, date, motif);
            notifier(patientId,
                "CRÉATION RDV #" + rdv.getId() + " : confirmé pour le "
                + rdv.getDateRendezVous().format(DATE_FMT));
            return "✅ Rendez-vous créé avec succès. ID: " + rdv.getId();
        } catch (Exception e) {
            throw new RemoteException("Erreur création: " + e.getMessage(), e);
        }
    }

    // ── Modifier RDV ──────────────────────────────────────────────────────────
    @Override
    public String modifierRendezVousRMI(Long rdvId, LocalDateTime nouvelleDate)
            throws RemoteException {
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ Rendez-vous #" + rdvId + " introuvable.";

            RendezVous modifie = svc.modifierHoraire(rdvId, nouvelleDate);
            if (modifie == null) return "❌ Modification impossible (RDV terminé ou annulé).";

            notifier(rdv.getPatient().getId(),
                "MODIFICATION RDV #" + rdvId + " : déplacé au " + nouvelleDate.format(DATE_FMT));
            return "✅ Rendez-vous #" + rdvId + " modifié au " + nouvelleDate.format(DATE_FMT);
        } catch (Exception e) {
            throw new RemoteException("Erreur modification: " + e.getMessage(), e);
        }
    }

    // ── Annuler RDV ───────────────────────────────────────────────────────────
    @Override
    public String annulerRendezVousRMI(Long rdvId) throws RemoteException {
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ Rendez-vous #" + rdvId + " introuvable.";

            boolean ok = svc.annulerRendezVous(rdvId);
            if (!ok) return "❌ Annulation impossible (RDV déjà terminé ou annulé).";

            notifier(rdv.getPatient().getId(),
                "ANNULATION RDV #" + rdvId + " du "
                + rdv.getDateRendezVous().format(DATE_FMT)
                + " avec Dr. " + rdv.getMedecin().getNom() + " — annulé.");
            return "✅ Rendez-vous #" + rdvId + " annulé avec succès.";
        } catch (Exception e) {
            throw new RemoteException("Erreur annulation: " + e.getMessage(), e);
        }
    }

    // ── Helper notification ───────────────────────────────────────────────────
    private void notifier(Long patientId, String message) {
        PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
        if (cb != null) {
            try { cb.recevoirNotification(message); }
            catch (RemoteException e) {
                System.err.println("[RMI] Notification échouée patient " + patientId
                    + ": " + e.getMessage());
                PatientNotificationRegistry.desinscrire(patientId);
            }
        }
    }
}
