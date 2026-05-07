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

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final String PATIENT_JNDI =
        "ejb:/CabinetMedical/PatientService!tn.isims.cabinet.ejb.patient.PatientServiceRemote";
    private static final String MEDECIN_JNDI =
        "ejb:/CabinetMedical/MedecinService!tn.isims.cabinet.ejb.medecin.MedecinServiceRemote";
    private static final String RDV_JNDI =
        "ejb:/CabinetMedical/RendezVousService!tn.isims.cabinet.ejb.rendezvous.RendezVousServiceRemote";

    public CabinetRMIService() throws RemoteException { super(); }

    // ── Logging ───────────────────────────────────────────────────────────────
    private static void log(String action, Long patientId, String detail) {
        String nom = PatientNotificationRegistry.getNom(patientId);
        System.out.printf("  [%s] 👤 %s (ID:%d) ▶ %s%s%n",
            LocalDateTime.now().format(FMT),
            nom, patientId, action,
            detail != null && !detail.isEmpty() ? " — " + detail : "");
    }

    private static void logOk(String action, Long patientId, String detail) {
        String nom = PatientNotificationRegistry.getNom(patientId);
        System.out.printf("  [%s] ✅ %s (ID:%d) ▶ %s%s%n",
            LocalDateTime.now().format(FMT),
            nom, patientId, action,
            detail != null && !detail.isEmpty() ? " — " + detail : "");
    }

    private static void logErr(String action, Long patientId, String err) {
        String nom = PatientNotificationRegistry.getNom(patientId);
        System.out.printf("  [%s] ❌ %s (ID:%d) ▶ %s ÉCHOUÉ: %s%n",
            LocalDateTime.now().format(FMT),
            nom, patientId, action, err);
    }

    // ── JNDI helpers ──────────────────────────────────────────────────────────
    private InitialContext buildContext() throws Exception {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY,
              "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(Context.PROVIDER_URL,         "remote+http://localhost:8080");
        p.put(Context.SECURITY_PRINCIPAL,   "rmiuser");
        p.put(Context.SECURITY_CREDENTIALS, "Rmi@12345");
        p.put("jboss.naming.client.ejb.context", "true");
        return new InitialContext(p);
    }

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

    // ── Enregistrement ────────────────────────────────────────────────────────
    @Override
    public boolean sEnregistrerPourNotifications(Long patientId, PatientCallback callback)
            throws RemoteException {
        PatientNotificationRegistry.enregistrer(patientId, callback);

        // Try to fetch patient name for nicer logs
        try {
            Patient p = getPatientService().trouverPatientParId(patientId);
            if (p != null) {
                PatientNotificationRegistry.enregistrer(patientId, callback,
                    p.getPrenom() + " " + p.getNom());
            }
        } catch (Exception ignored) {}

        log("CONNEXION", patientId, "Patient connecté au système de notifications");
        System.out.printf("  [INFO] Patients en ligne: %d%n",
            PatientNotificationRegistry.getNombreConnectes());

        callback.recevoirNotification(
            "CONFIRMATION: Vous êtes maintenant inscrit aux notifications du cabinet médical.");
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
        log("CONSULTER RDV", patientId, "");
        try {
            List<RendezVous> liste = getRdvService().listerRendezVousParPatient(patientId);
            logOk("CONSULTER RDV", patientId, liste.size() + " rendez-vous trouvé(s)");
            return liste;
        } catch (Exception e) {
            logErr("CONSULTER RDV", patientId, e.getMessage());
            throw new RemoteException("Erreur consultation: " + e.getMessage(), e);
        }
    }

    // ── Liste Médecins ────────────────────────────────────────────────────────
    @Override
    public List<Medecin> listerMedecins() throws RemoteException {
        System.out.printf("  [%s] 📋 Récupération liste médecins%n",
            LocalDateTime.now().format(FMT));
        try {
            List<Medecin> liste = getMedecinService().listerTousLesMedecins();
            System.out.printf("  [%s] ✅ %d médecin(s) retourné(s)%n",
                LocalDateTime.now().format(FMT), liste.size());
            return liste;
        } catch (Exception e) {
            System.out.printf("  [%s] ❌ listerMedecins ÉCHOUÉ: %s%n",
                LocalDateTime.now().format(FMT), e.getMessage());
            throw new RemoteException("Erreur liste médecins: " + e.getMessage(), e);
        }
    }

    // ── Liste Patients ────────────────────────────────────────────────────────
    @Override
    public List<Patient> listerPatients() throws RemoteException {
        System.out.printf("  [%s] 📋 Récupération liste patients%n",
            LocalDateTime.now().format(FMT));
        try {
            List<Patient> liste = getPatientService().listerTousLesPatients();
            System.out.printf("  [%s] ✅ %d patient(s) retourné(s)%n",
                LocalDateTime.now().format(FMT), liste.size());
            return liste;
        } catch (Exception e) {
            System.out.printf("  [%s] ❌ listerPatients ÉCHOUÉ: %s%n",
                LocalDateTime.now().format(FMT), e.getMessage());
            throw new RemoteException("Erreur liste patients: " + e.getMessage(), e);
        }
    }

    // ── Créer RDV ─────────────────────────────────────────────────────────────
    @Override
    public String creerRendezVousRMI(Long patientId, Long medecinId,
                                      LocalDateTime date, String motif)
            throws RemoteException {
        log("CRÉER RDV", patientId,
            "Médecin #" + medecinId + " | " + date + " | motif: " + motif);
        try {
            RendezVous rdv = getRdvService()
                .creerRendezVous(patientId, medecinId, date, motif);

            String msg = "CRÉATION RDV #" + rdv.getId() + " : confirmé pour le "
                + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            logOk("CRÉER RDV", patientId, "RDV #" + rdv.getId() + " créé avec succès");

            // Notify patient
            PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
            if (cb != null) cb.recevoirNotification(msg);

            return "✅ Rendez-vous créé avec succès. ID: " + rdv.getId();
        } catch (Exception e) {
            logErr("CRÉER RDV", patientId, e.getMessage());
            throw new RemoteException("Erreur création RDV: " + e.getMessage(), e);
        }
    }

    // ── Modifier RDV ──────────────────────────────────────────────────────────
    @Override
    public String modifierRendezVousRMI(Long rdvId, LocalDateTime nouvelleDate)
            throws RemoteException {
        System.out.printf("  [%s] ✏️  MODIFIER RDV #%d → %s%n",
            LocalDateTime.now().format(FMT), rdvId, nouvelleDate);
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ RDV #" + rdvId + " introuvable.";

            Long patientId = rdv.getPatient().getId();
            RendezVous modifie = svc.modifierHoraire(rdvId, nouvelleDate);
            if (modifie == null) return "❌ Modification impossible (RDV terminé/annulé).";

            String msg = "MODIFICATION RDV #" + rdvId + " : déplacé au "
                + nouvelleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            logOk("MODIFIER RDV", patientId, "RDV #" + rdvId + " → " + nouvelleDate);

            PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
            if (cb != null) cb.recevoirNotification(msg);

            return "✅ RDV #" + rdvId + " modifié au "
                + nouvelleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            System.out.printf("  [%s] ❌ MODIFIER RDV #%d ÉCHOUÉ: %s%n",
                LocalDateTime.now().format(FMT), rdvId, e.getMessage());
            throw new RemoteException("Erreur modification: " + e.getMessage(), e);
        }
    }

    // ── Annuler RDV ───────────────────────────────────────────────────────────
    @Override
    public String annulerRendezVousRMI(Long rdvId) throws RemoteException {
        System.out.printf("  [%s] ❌ ANNULER RDV #%d%n",
            LocalDateTime.now().format(FMT), rdvId);
        try {
            RendezVousServiceRemote svc = getRdvService();
            RendezVous rdv = svc.trouverRendezVousParId(rdvId);
            if (rdv == null) return "❌ RDV #" + rdvId + " introuvable.";

            Long patientId = rdv.getPatient().getId();
            boolean ok = svc.annulerRendezVous(rdvId);
            if (!ok) return "❌ Annulation impossible (RDV déjà terminé/annulé).";

            String msg = "ANNULATION RDV #" + rdvId + " : votre rendez-vous du "
                + rdv.getDateRendezVous()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + " avec Dr. " + rdv.getMedecin().getNom() + " a été annulé.";
            logOk("ANNULER RDV", patientId, "RDV #" + rdvId + " annulé");

            PatientCallback cb = PatientNotificationRegistry.getCallback(patientId);
            if (cb != null) cb.recevoirNotification(msg);

            return "✅ RDV #" + rdvId + " annulé avec succès.";
        } catch (Exception e) {
            System.out.printf("  [%s] ❌ ANNULER RDV #%d ÉCHOUÉ: %s%n",
                LocalDateTime.now().format(FMT), rdvId, e.getMessage());
            throw new RemoteException("Erreur annulation: " + e.getMessage(), e);
        }
    }
}
