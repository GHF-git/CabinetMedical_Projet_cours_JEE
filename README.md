# Cabinet Medical - JEE Application

## Système de Gestion de Cabinet Médical

Application JEE complète pour la gestion d'un cabinet médical avec Servlets, EJB, JPA et RMI.

## Technologies

- **Java 21**
- **Jakarta EE 10**
- **WildFly** (Serveur d'application)
- **MySQL** (Base de données)
- **Hibernate** (ORM/JPA)
- **RMI** (Communication distribuée)

## Structure du Projet

```
CabinetMedical/
├── src/main/
│   ├── java/tn/isims/cabinet/
│   │   ├── entity/           # Entités JPA
│   │   ├── ejb/              # EJB (Stateless & Stateful)
│   │   ├── servlet/          # Servlets (Contrôleurs)
│   │   └── rmi/              # Module RMI + Callback
│   ├── resources/
│   │   └── META-INF/
│   │       └── persistence.xml
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   └── views/         # JSP
│       ├── css/
│       └── index.jsp
├── docs/
│   ├── Rapport_Technique.md
│   └── init_database.sql
└── pom.xml
```

## Prérequis

1. **JDK 21** installé
2. **IntelliJ IDEA** (ou autre IDE Java EE)
3. **WildFly** configuré
4. **MySQL** 8.0+
5. **Maven** 3.8+

## Installation et Configuration

### 1. Base de données MySQL

```bash
# Connexion à MySQL
mysql -u root -p

# Exécuter le script de création
SOURCE docs/init_database.sql;
```

### 2. Configuration WildFly

Démarrer WildFly et configurer la DataSource :

```bash
# Dans la console WildFly
connect
xa-data-source add \
  --name=CabinetMedicalDS \
  --jndi-name=java:jboss/datasources/CabinetMedicalDS \
  --driver-name=mysql \
  --connection-url=jdbc:mysql://localhost:3306/cabinet_medical \
  --user-name=root \
  --password=your_password
```

### 3. Déploiement avec Maven

```bash
# Compiler le projet
mvn clean package

# Déployer sur WildFly
mvn wildfly:deploy
```

### 4. Alternative : Déploiement manuel

1. Copier le fichier WAR dans le répertoire de déploiement de WildFly :
   ```bash
   cp target/CabinetMedical.war $WILDFLY_HOME/standalone/deployments/
   ```

## Accès à l'Application

- **URL** : http://localhost:8080/CabinetMedical
- **Page d'accueil** : http://localhost:8080/CabinetMedical/index.jsp

## Routes Principales

| Module | URL | Description |
|--------|-----|-------------|
| Patients | `/patients` | Liste des patients |
| | `/patients/add` | Ajouter un patient |
| | `/patients/edit?id=X` | Modifier un patient |
| Médecins | `/medecins` | Liste des médecins |
| | `/medecins/add` | Ajouter un médecin |
| | `/medecins/patients?id=X` | Patients d'un médecin |
| Rendez-vous | `/rendezvous` | Liste des RDV |
| | `/rendezvous/add` | Créer un RDV |
| | `/rendezvous/du-jour` | RDV du jour |

## Module RMI

### Démarrage du Serveur RMI

```bash
java -cp CabinetMedical.jar tn.isims.cabinet.rmi.impl.RMIServer
```

### Utilisation du Client

```bash
java -cp CabinetMedical.jar tn.isims.cabinet.rmi.impl.RMIClientApplication
```

Le client permet :
- Consultation des rendez-vous
- Création/modification/annulation de RDV
- Réception de notifications en temps réel

## Architecture

```
┌─────────────────────────────────────────────┐
│           Navigateur Web                    │
└─────────────────┬───────────────────────────┘
                  │ HTTP
                  ▼
┌─────────────────────────────────────────────┐
│        WildFly Application Server            │
│  ┌──────────────────────────────────────┐  │
│  │         Couche Présentation           │  │
│  │         (Servlets + JSP)             │  │
│  └──────────────┬───────────────────────┘  │
│                 │                            │
│  ┌──────────────▼───────────────────────┐  │
│  │           Couche Métier               │  │
│  │    (EJB Stateless / Stateful)         │  │
│  └──────────────┬───────────────────────┘  │
│                 │                            │
│  ┌──────────────▼───────────────────────┐  │
│  │         Couche Persistance            │  │
│  │          (JPA / Hibernate)           │  │
│  └──────────────┬───────────────────────┘  │
└─────────────────┼───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│              MySQL Database                  │
│    (PATIENT, MEDECIN, RENDEZVOUS)           │
└─────────────────────────────────────────────┘
```

## Captures d'écran

### Page d'Accueil
[AJOUTER CAPTURE]

### Liste des Patients
[AJOUTER CAPTURE]

### Gestion des Rendez-vous
[AJOUTER CAPTURE]

## Équipe

- Projet développé dans le cadre du cours de Technologies JEE 1
- Institut Supérieur d'Informatique et de Multimédia de Sfax

## Licence

Projet académique - Tous droits réservés
