# Rapport Technique
## Système de Gestion de Cabinet Médical

---

**Matière:** Technologies de développement JEE 1
**Filière:** P-IINFO
**Enseignants:** Sofien Chtourou, Mme. Emma Taktak, Mme. Leila Haj Meftah
**Année:** 2025-2026

---

## Table des Matières

1. [Page de Garde](#page-de-garde)
2. [Introduction](#introduction)
3. [Architecture Globale](#architecture-globale)
4. [Organisation du Projet](#organisation-du-projet)
5. [Description des Modules](#description-des-modules)
6. [Configuration et Déploiement](#configuration-et-déploiement)
7. [Module RMI et Callback](#module-rmi-et-callback)
8. [Interfaces JSP](#interfaces-jsp)
9. [Conclusion](#conclusion)

---

## 1. Page de Garde

| | |
|---|---|
| **Université** | Institut Supérieur d'Informatique et de Multimédia de Sfax |
| **République Tunisienne** | Ministère de l'Enseignement Supérieur et de la Recherche Scientifique |
| **Matière** | Technologies de développement JEE 1 |
| **Filière** | P-IINFO |
| **Sujet** | Système de Gestion de Cabinet Médical |
| **Technologies** | Servlets, EJB, JPA, RMI |

---

## 2. Introduction

### 2.1 Contexte du Projet

Ce projet consiste à développer une application JEE complète pour la gestion d'un cabinet médical. L'application est conçue pour répondre aux besoins de deux catégories d'utilisateurs :

- **Le personnel du cabinet** (secrétaire/médecins) : gestion des patients, médecins et rendez-vous
- **Les patients** : consultation de leurs rendez-vous et réception de notifications

### 2.2 Objectifs

- Mettre en place une architecture JEE complète avec MVC
- Utiliser les EJB (Stateless et Stateful) pour la logique métier
- Implémenter la persistance avec JPA et MySQL
- Développer un module distribué avec RMI et Callback

---

## 3. Architecture Globale

### 3.1 Architecture MVC

L'application suit le modèle **MVC (Modèle – Vue – Contrôleur)** :

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Navigateur)                   │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE PRÉSENTATION                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Servlets  │  │     JSP     │  │  HTML/CSS   │        │
│  └──────┬──────┘  └─────────────┘  └─────────────┘        │
│         │                                                     │
│         ▼                                                     │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              CONTRÔLEURS (Servlets)                      │ │
│  │  PatientServlet | MedecinServlet | RendezVousServlet   │ │
│  └──────────────────────────┬──────────────────────────────┘ │
└───────────────────────────────┼───────────────────────────────┘
                                │
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                    COUCHE MÉTIER                             │
│  ┌──────────────────────┐  ┌──────────────────────┐         │
│  │   EJB Stateless      │  │   EJB Stateful       │         │
│  │  PatientService      │  │  RendezVousService   │         │
│  │  MedecinService      │  │                      │         │
│  └──────────┬───────────┘  └──────────┬───────────┘         │
│             │                          │                     │
│             └──────────┬───────────────┘                     │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         ▼
┌───────────────────────────────────────────────────────────────┐
│                    COUCHE PERSISTANCE                        │
│  ┌──────────────────────┐  ┌──────────────────────┐         │
│  │         JPA          │  │       MySQL          │         │
│  │   (Hibernate ORM)    │  │                     │         │
│  └──────────────────────┘  └──────────────────────┘         │
└───────────────────────────────────────────────────────────────┘
```

### 3.2 Technologies Utilisées

| Élément | Technologie |
|---------|-------------|
| Présentation | Servlets, JSP, HTML/CSS |
| Métier | EJB (Stateless + Stateful) |
| Persistence | JPA + MySQL |
| Distribué | Java RMI + Callback |
| IDE | IntelliJ |
| Serveur | WildFly |
| JDK | 21 |

---

## 4. Organisation du Projet

### 4.1 Structure des Packages

```
tn.isims.cabinet/
├── entity/                    # Couche Modèle (JPA)
│   ├── Patient.java
│   ├── Medecin.java
│   └── RendezVous.java
│
├── ejb/                       # Couche Métier (EJB)
│   ├── patient/
│   │   ├── PatientService.java
│   │   └── PatientServiceRemote.java
│   ├── medecin/
│   │   ├── MedecinService.java
│   │   └── MedecinServiceRemote.java
│   └── rendezvous/
│       ├── RendezVousService.java
│       └── RendezVousServiceRemote.java
│
├── servlet/                   # Couche Contrôleur
│   ├── PatientServlet.java
│   ├── MedecinServlet.java
│   └── RendezVousServlet.java
│
├── rmi/                       # Module Distribué
│   ├── callback/
│   │   └── PatientCallback.java
│   └── impl/
│       ├── CabinetRMIService.java
│       ├── CabinetRMIServiceRemote.java
│       ├── PatientCallbackImpl.java
│       ├── PatientNotificationRegistry.java
│       ├── RMIServer.java
│       └── RMIClientApplication.java
│
└── dao/                       # Data Access (intégré dans les EJB)
```

### 4.2 Structure Web

```
src/main/webapp/
├── index.jsp                  # Page d'accueil
├── css/
│   └── styles.css             # Feuille de styles
├── js/
│   └── script.js              # Scripts JavaScript
└── WEB-INF/
    ├── web.xml                # Configuration web
    ├── views/
    │   ├── layout/
    │   │   ├── header.jsp
    │   │   └── footer.jsp
    │   ├── patients/
    │   │   ├── liste.jsp
    │   │   ├── ajouter.jsp
    │   │   └── modifier.jsp
    │   ├── medecins/
    │   │   ├── liste.jsp
    │   │   ├── patients.jsp
    │   │   ├── ajouter.jsp
    │   │   └── modifier.jsp
    │   └── rendezvous/
    │       ├── liste.jsp
    │       ├── creer.jsp
    │       └── modifier.jsp
    └── error/
        ├── 404.jsp
        └── 500.jsp
```

---

## 5. Description des Modules

### 5.1 Entités JPA

#### 5.1.1 Patient

```java
@Entity
@Table(name = "PATIENT")
public class Patient implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String telephone;
    private LocalDate dateNaissance;

    @OneToMany(mappedBy = "patient")
    private List<RendezVous> rendezVous;
}
```

#### 5.1.2 Médecin

```java
@Entity
@Table(name = "MEDECIN")
public class Medecin implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private String email;

    @OneToMany(mappedBy = "medecin")
    private List<RendezVous> rendezVous;
}
```

#### 5.1.3 Rendez-Vous

```java
@Entity
@Table(name = "RENDEZVOUS")
public class RendezVous implements Serializable {
    public enum Statut { PLANIFIE, TERMINE, ANNULE }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;

    private LocalDateTime dateRendezVous;
    private String motif;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.PLANIFIE;
}
```

### 5.2 EJB Stateless

Les EJB Stateless sont utilisés pour les opérations CRUD simples :

#### 5.2.1 PatientService

```java
@Stateless(mappedName = "PatientService")
public class PatientService implements PatientServiceRemote {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    public List<Patient> listerTousLesPatients() { ... }
    public Patient trouverPatientParId(Long id) { ... }
    public Patient ajouterPatient(Patient patient) { ... }
    public Patient modifierPatient(Long id, Patient patientModifie) { ... }
    public boolean supprimerPatient(Long id) { ... }
    public List<Patient> rechercherParNomOuEmail(String recherche) { ... }
}
```

#### 5.2.2 MedecinService

```java
@Stateless(mappedName = "MedecinService")
public class MedecinService implements MedecinServiceRemote {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    public List<Medecin> listerTousLesMedecins() { ... }
    public Medecin ajouterMedecin(Medecin medecin) { ... }
    public boolean supprimerMedecin(Long id) { ... }
    public List<Medecin> rechercherParSpecialite(String specialite) { ... }
    public List<Patient> obtenirPatientsDuMedecin(Long medecinId) { ... }
}
```

### 5.3 EJB Stateful

L'EJB Stateful est utilisé pour la gestion des rendez-vous afin de maintenir l'état de la session :

```java
@Stateful(mappedName = "RendezVousService")
public class RendezVousService implements RendezVousServiceRemote {

    @PersistenceContext(unitName = "CabinetMedicalPU")
    private EntityManager em;

    private PatientCallback patientCallback;

    public RendezVous creerRendezVous(Long patientId, Long medecinId,
                                       LocalDateTime dateRendezVous, String motif) {
        // Logique de création avec notification
    }

    public RendezVous modifierHoraire(Long rdvId, LocalDateTime nouvelleDate) {
        // Modification avec notification
    }

    public boolean annulerRendezVous(Long rdvId) {
        // Annulation avec notification
    }

    public List<RendezVous> listerRendezVousDuJour() { ... }
    public List<RendezVous> listerRendezVousPasses() { ... }
}
```

### 5.4 Servlets (Contrôleurs)

#### 5.4.1 PatientServlet

```java
@WebServlet({"/patients", "/patients/*"})
public class PatientServlet extends HttpServlet {

    @EJB(beanName = "PatientService")
    private PatientServiceRemote patientService;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Routing des requêtes GET
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Traitement des formulaires
    }
}
```

Les routes gérées :
- `GET /patients` → Liste des patients
- `GET /patients/add` → Formulaire d'ajout
- `GET /patients/edit?id=X` → Formulaire de modification
- `POST /patients/save` → Sauvegarde (ajout/modification)
- `GET /patients/search?recherche=X` → Recherche

---

## 6. Configuration et Déploiement

### 6.1 Configuration de la Persistance (persistence.xml)

```xml
<persistence-unit name="CabinetMedicalPU" transaction-type="JTA">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <jta-data-source>java:jboss/datasources/CabinetMedicalDS</jta-data-source>

    <class>tn.isims.cabinet.entity.Patient</class>
    <class>tn.isims.cabinet.entity.Medecin</class>
    <class>tn.isims.cabinet.entity.RendezVous</class>

    <properties>
        <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
        <property name="hibernate.show_sql" value="true"/>
    </properties>
</persistence-unit>
```

### 6.2 Configuration WildFly

Créer une DataSource dans WildFly :

```xml
<datasource jndi-name="java:jboss/datasources/CabinetMedicalDS">
    <connection-url>jdbc:mysql://localhost:3306/cabinet_medical</connection-url>
    <driver>mysql-connector-java.jar</driver>
    <pool>
        <min-pool-size>5</min-pool-size>
        <max-pool-size>20</max-pool-size>
    </pool>
</datasource>
```

### 6.3 Script SQL de Création de la Base

```sql
CREATE DATABASE IF NOT EXISTS cabinet_medical;
USE cabinet_medical;

CREATE TABLE PATIENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    date_naissance DATE
);

CREATE TABLE MEDECIN (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    specialite VARCHAR(100) NOT NULL,
    email VARCHAR(150)
);

CREATE TABLE RENDEZVOUS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    medecin_id BIGINT NOT NULL,
    date_rendez_vous DATETIME NOT NULL,
    motif TEXT,
    statut ENUM('PLANIFIE', 'TERMINE', 'ANNULE') DEFAULT 'PLANIFIE',
    FOREIGN KEY (patient_id) REFERENCES PATIENT(id),
    FOREIGN KEY (medecin_id) REFERENCES MEDECIN(id)
);
```

---

## 7. Module RMI et Callback

### 7.1 Architecture RMI avec Callback

Le module RMI permet aux patients de se connecter à distance et de recevoir des notifications en temps réel :

```
┌─────────────────────┐         ┌─────────────────────────┐
│   Client Patient    │         │     Serveur WildFly     │
│                     │         │                         │
│  ┌───────────────┐  │         │  ┌─────────────────┐   │
│  │ RMIClientApp  │──┼─────────┼──│ CabinetRMIService│   │
│  └───────────────┘  │   RMI   │  └────────┬────────┘   │
│         │           │         │           │            │
│         │           │         │           ▼            │
│  ┌───────────────┐  │         │  ┌─────────────────┐   │
│  │PatientCallback│◄─┼─────────┼──│RendezVousService│   │
│  │   (Async)     │  │         │  │  (EJB Stateful) │   │
│  └───────────────┘  │         │  └─────────────────┘   │
└─────────────────────┘         └─────────────────────────┘
```

### 7.2 Interface PatientCallback

```java
public interface PatientCallback extends Remote {
    void recevoirNotification(String notification) throws RemoteException;
    Long getPatientId() throws RemoteException;
}
```

### 7.3 Implémentation du Callback

```java
public class PatientCallbackImpl extends UnicastRemoteObject
        implements PatientCallback {

    private final Long patientId;
    private final List<String> notifications = new ArrayList<>();

    public PatientCallbackImpl(Long patientId) throws RemoteException {
        this.patientId = patientId;
    }

    @Override
    public void recevoirNotification(String notification) throws RemoteException {
        System.out.println("NOTIFICATION: " + notification);
        notifications.add(notification);
    }
}
```

### 7.4 Service RMI Principal

```java
public class CabinetRMIService extends UnicastRemoteObject
        implements CabinetRMIServiceRemote {

    @Override
    public boolean sEnregistrerPourNotifications(Long patientId,
                                                  PatientCallback callback)
            throws RemoteException {
        PatientNotificationRegistry.enregistrer(patientId, callback);
        callback.recevoirNotification("Inscription réussie!");
        return true;
    }

    @Override
    public String creerRendezVousRMI(Long patientId, Long medecinId,
                                      LocalDateTime date, String motif) {
        // Créer le RDV et notifier le patient
        PatientCallback callback = PatientNotificationRegistry.getCallback(patientId);
        if (callback != null) {
            callback.recevoirNotification("CRÉATION: Nouveau RDV créé");
        }
    }
}
```

### 7.5 Diagramme de Séquence (Callback)

```
Client Patient                         Serveur
    │                                    │
    │── sEnregistrer() ─────────────────►│
    │    (PatientCallback)                │
    │◄── Confirmation ───────────────────│
    │                                    │
    │                              [CRUD Rendez-vous]
    │                                    │
    │◄── Notifications ◄─────────────────│
    │    (Callback)                      │
    │                                    │
    │── consulterRendezVous() ───────────►│
    │◄── Liste des RDV ──────────────────│
```

---

## 8. Interfaces JSP

### 8.1 Page d'Accueil

```
┌────────────────────────────────────────────────────────────┐
│  Cabinet Médical                                           │
│  [Patients] [Médecins] [Rendez-vous]                       │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ╔═══════════════════════════════════════════════════════╗ │
│  ║         Système de Gestion de Cabinet Médical         ║ │
│  ║     Application JEE - Servlets, EJB, JPA, RMI        ║ │
│  ╚═══════════════════════════════════════════════════════╝ │
│                                                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Gestion   │ │   Gestion   │ │  Rendez-    │           │
│  │   Patients  │ │  Médecins   │ │   vous      │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### 8.2 Liste des Patients

```
┌────────────────────────────────────────────────────────────┐
│  Liste des Patients                              [+ Nouveau]│
├────────────────────────────────────────────────────────────┤
│  [Rechercher par nom ou email...    ] [Rechercher]         │
├────────────────────────────────────────────────────────────┤
│  ID │ Nom     │ Prénom  │ Email         │ Téléphone │ Act │
│  ───┼─────────┼─────────┼───────────────┼───────────┼────│
│  1  │ Ben Ali │ Ahmed    │ ahmed@...     │ 98XXX     │[M][S]│
│  2  │ Trabelsi│ Fatma   │ fatma@...     │ 96XXX     │[M][S]│
└────────────────────────────────────────────────────────────┘
```

---

## 9. Conclusion

### 9.1 Fonctionnalités Réalisées

- Gestion complète des patients (CRUD + recherche)
- Gestion complète des médecins (CRUD + recherche par spécialité)
- Gestion des rendez-vous avec EJB Stateful
- Notifications en temps réel via RMI Callback
- Interface web responsive avec JSP/CSS

### 9.2 Technologies Maîtrisées

- **Servlets/JSP** : Couche présentation et contrôle
- **EJB Stateless** : Logique métier CRUD
- **EJB Stateful** : Gestion d'état pour les rendez-vous
- **JPA/Hibernate** : Persistance objet-relationnel
- **RMI + Callback** : Communication distribuée asynchrone
- **WildFly** : Serveur d'application Java EE

### 9.3 Perspectives d'Amélioration

- Ajout d'authentification et autorisation (JAAS)
- Interface patient avec Angular/React
- Notifications email/SMS
- Calendrier de visualisation des RDV
- Statistiques et rapports

---

**Fin du Rapport**
