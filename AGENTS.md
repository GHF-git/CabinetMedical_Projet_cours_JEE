# AGENTS.md

## Quick orientation
- Stack: Java 21 + Jakarta EE 10, packaged as a WAR (`pom.xml`) and deployed to WildFly.
- Main app is MVC-style: Servlets (`src/main/java/tn/isims/cabinet/servlet`) -> EJB services (`.../ejb/...`) -> JPA entities (`.../entity/...`) -> MySQL.
- Secondary integration is Java RMI callback for patient notifications (`src/main/java/tn/isims/cabinet/rmi`).
- Existing AI guidance files discovered via required glob: only `README.md`.

## Architecture and boundaries
- HTTP entrypoints are servlet-mapped by module: `/patients`, `/medecins`, `/rendezvous` (see `PatientServlet`, `MedecinServlet`, `RendezVousServlet`).
- Business layer uses EJB remotes with explicit bean names (`@Stateless(mappedName = "PatientService")`, `@Stateful(mappedName = "RendezVousService")`).
- Persistence unit is `CabinetMedicalPU` with JTA datasource `java:jboss/datasources/CabinetMedicalDS` (`src/main/resources/META-INF/persistence.xml`).
- Entity relationships are bidirectional: `Patient` 1-N `RendezVous`, `Medecin` 1-N `RendezVous`; many views access nested properties like `${rdv.patient.nom}`.
- RMI layer bridges to EJB via JNDI lookups in `CabinetRMIService` (`java:global/CabinetMedical/...Service!...Remote`).

## High-value workflows
- Build WAR:
  - `mvn clean package`
- Deploy to WildFly management API:
  - `mvn wildfly:deploy`
- Initialize DB schema/data:
  - run `docs/init_database.sql` against MySQL database `cabinet_medical`.
- Manual smoke test routes after deploy:
  - `/patients`, `/medecins`, `/rendezvous`, `/rendezvous/du-jour`.
- RMI manual run path:
  - server: `tn.isims.cabinet.rmi.impl.RMIServer`
  - client: `tn.isims.cabinet.rmi.impl.RMIClientApplication`

## Project-specific coding patterns
- Servlets dispatch by `pathInfo` on GET and by hidden `action` field on POST (example: `patients/ajouter.jsp` posts to `/patients/save` with `action=save`).
- JSPs are under `WEB-INF/views/...` and use shared layout includes (`views/layout/header.jsp`, `footer.jsp`).
- User feedback uses request attributes `message` / `erreur` rendered in `header.jsp`; current servlets often `sendRedirect` after setting attributes, so those messages do not survive unless moved to session/flash.
- Date parsing is strict and format-coupled: `RendezVousServlet` expects `yyyy-MM-dd'T'HH:mm` to match `<input type="datetime-local">`.
- Business guards are enforced in EJBs (e.g., cannot delete patient with active planned RDV in `PatientService.supprimerPatient`).

## Integration notes and caveats
- `RendezVousService` is stateful and has callback support (`setPatientCallback`), but web flow does not inject callback; RMI notifications are mainly handled in `CabinetRMIService` + `PatientNotificationRegistry`.
- `CabinetRMIService.consulterRendezVous(...)` and `.consulterRendezVousPassesEtFuturs(...)` currently return `List.of()` placeholders; treat them as incomplete when extending RMI features.
- `hibernate.hbm2ddl.auto=update` and schema-generation update are enabled; preserve unless migration strategy is intentionally changed.
- There is no `src/test` test suite today; regression checks are primarily deploy + route-level/manual flows.

## If you add or change features
- Keep module symmetry: update remote interface, EJB implementation, servlet routing, and corresponding JSP form/view.
- Prefer extending existing URL/action conventions instead of introducing a new controller style.
- For RMI changes, update both remote interface (`CabinetRMIServiceRemote`) and console client menu flow.
- Validate JNDI names and WildFly datasource assumptions before debugging business logic.

