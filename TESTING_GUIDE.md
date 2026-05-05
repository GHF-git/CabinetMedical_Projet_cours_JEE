# Testing Guide - Cabinet Medical Application

## Overview of Recent Fixes and Improvements

This guide documents all the fixes made to resolve terminal issues and simplify the RMI client UX.

### 1. **JNDI ClassNotFoundException - FIXED ✅**
   - **Problem**: When selecting option 1 (Consult rendez-vous), error: `ClassNotFoundException: org.jboss.naming.remote.client.InitialContextFactory`
   - **Root Cause**: WildFly remote naming client library was not in the classpath
   - **Solution**: Added `wildfly-client-all` v27.0.0.Final to `pom.xml`
   - **Result**: RMI server can now properly connect to WildFly via JNDI

### 2. **Graceful Exit / Re-login - FIXED ✅**
   - **Problem**: Option 6 (Quit) would immediately terminate without option to reconnect
   - **Solution**: Added menu asking "1. Quit" or "2. Choose another patient ID"
   - **Result**: Users can now gracefully exit or choose a different patient

### 3. **Simplified Create Rendez-vous - IMPROVED ✅**
   - **Before**: Required user to provide doctor ID (hard to remember)
   - **After**: Lists all available doctors with names and specialties for easy selection
   - **Auto-generated ID**: Server automatically generates RDV ID
   - **Result**: Much simpler UX - user selects doctor by number

### 4. **Simplified Modify Rendez-vous - IMPROVED ✅**
   - **Before**: Required user to provide RDV ID and new date
   - **After**: Lists all patient's existing rendez-vous for selection, only asks for new date
   - **Result**: User sees full appointment details before selecting

### 5. **Simplified Cancel Rendez-vous - IMPROVED ✅**
   - **Before**: Required RDV ID input
   - **After**: Lists all appointments, user selects by number
   - **Result**: Much more intuitive

### 6. **Thread-Safe Notifications - MAINTAINED ✅**
   - Notifications stored in `Collections.synchronizedList()` for thread safety
   - All access properly synchronized

---

## Prerequisites

### Installed Software
- **JDK 21** (verified working)
- **Maven 3.8+** (for compilation)
- **WildFly 27+** (JEE server)
- **MySQL 8.0+** (database)

### WildFly Configuration
- WildFly should be running at `localhost:8080`
- Admin credentials: `admin` / `admin` (for deployment)
- RMI/JNDI credentials: `admin` / `admin123` (for RMI connections)
- Datasource configured: `java:jboss/datasources/CabinetMedicalDS`

---

## Step-by-Step Testing

### Phase 1: Build and Deploy

#### 1.1 Build the Project
```bash
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE

# Clean and compile
mvn clean package -DskipTests

# Expected output:
# [INFO] Building war: .../target/CabinetMedical.war
# [INFO] BUILD SUCCESS
```

#### 1.2 Deploy to WildFly
```bash
# Deploy using Maven plugin
mvn wildfly:deploy

# OR manually copy
cp target/CabinetMedical.war $WILDFLY_HOME/standalone/deployments/

# Verify deployment
# Check WildFly console or logs for success message
```

#### 1.3 Test Web Interface
```
Open browser: http://localhost:8080/CabinetMedical/index.jsp

Expected: Homepage loads without blank pages
```

---

### Phase 2: Test Web Servlets

#### 2.1 Test Patient Management
```
URL: http://localhost:8080/CabinetMedical/patients

Expected:
- ✅ Patient list displays
- ✅ No blank pages after add/edit/delete
- ✅ Flash messages appear (if using session attributes)
```

#### 2.2 Test Doctor Management
```
URL: http://localhost:8080/CabinetMedical/medecins

Expected:
- ✅ Doctor list displays with name and specialty
- ✅ Search by specialty works
- ✅ Add/Edit/Delete operations complete without blank pages
```

#### 2.3 Test Rendez-vous Management
```
URL: http://localhost:8080/CabinetMedical/rendezvous

Expected:
- ✅ Rendez-vous list displays
- ✅ Doctor-Patient relationships visible (e.g., "RDV with Dr. Ahmed Tounsi")
- ✅ Can create/modify/cancel without blank pages
```

---

### Phase 3: Test RMI Terminal Interface (CRITICAL - All Options)

#### 3.1 Start WildFly RMI Registry
The RMI registry needs to be available. WildFly usually provides this, but verify:

```bash
# In WildFly standalone console, ensure RemoteNaming service is enabled
# This is typically enabled by default in WildFly 27+
```

#### 3.2 Start RMI Server (Terminal 1)
```bash
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE

# Build JAR with dependencies
mvn clean package assembly:single -DskipTests

# Start RMI server
java -cp target/CabinetMedical-1.0.0-jar-with-dependencies.jar \
  tn.isims.cabinet.rmi.impl.RMIServer

# Expected output:
# [DEBUG] RMI Registry created on port 1099
# [DEBUG] Service bound: CabinetRMIService
# RMI Server démarré et prêt à recevoir les connexions
```

**If you get** `java.rmi.registry.LocateRegistry` error:
- Ensure port 1099 is available: `lsof -i :1099`
- Kill conflicting process if needed: `kill -9 <PID>`

---

#### 3.3 Start RMI Client (Terminal 2)
```bash
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE

java -cp target/CabinetMedical-1.0.0-jar-with-dependencies.jar \
  tn.isims.cabinet.rmi.impl.RMIClientApplication

# Expected output:
# Connexion à: rmi://localhost:1099/CabinetRMIService
# === Application Patient - Cabinet Médical ===
# Entrez votre ID patient: 
```

---

#### 3.4 Test All Menu Options (as Patient ID 2 or 1)

**Patient ID to use**: Look in database: `SELECT id FROM patient;`

Assuming patient ID = 2:

---

##### **Option 1: Consult Rendez-vous (✅ NOW FIXED)**

```
Input: 2 (for patient ID)
       1 (for option 1)

Expected Output:
✅ FIXED: No more ClassNotFoundException error
✅ Lists all rendez-vous for patient 2 with doctor names and dates
✅ Example output:
   RendezVous[id=1, patient=Patient[...], medecin=Medecin[...], ...]

Troubleshooting:
- If still get JNDI error:
  - Check WildFly is running
  - Check credentials in CabinetRMIService (admin/admin123)
  - Check datasource exists: java:jboss/datasources/CabinetMedicalDS
```

---

##### **Option 2: Create New Rendez-vous (✅ IMPROVED)**

```
Input: 2 (patient ID)
       2 (option 2)

Expected Flow (NEW):
--- Sélectionner un médecin ---
1. Dr. Ahmed Tounsi (Cardiologue)
2. Dr. Fatima Ben Ali (Dermatologue)
3. Dr. Salim Haddad (Neurologue)
...
Sélectionnez un médecin (1-X): 1

Date du rendez-vous (yyyy-MM-dd HH:mm): 2026-05-10 14:30
Motif de la consultation: Consultation générale

Expected Output:
✅ IMPROVED: Now lists doctors to select from (no ID memorization needed)
✅ Prompts for date in correct format
✅ Prompts for reason/motif
✅ Auto-generates RDV ID on server
✅ Success message: "✅ Rendez-vous créé avec succès. ID: 5"

Troubleshooting:
- Invalid date format: Must be "yyyy-MM-dd HH:mm" (e.g., 2026-05-10 14:30)
- Empty input: System will re-prompt
```

---

##### **Option 3: Modify Rendez-vous (✅ IMPROVED)**

```
Input: 2 (patient ID)
       3 (option 3)

Expected Flow (NEW):
--- Vos rendez-vous ---
1. RDV #1 - 2026-05-08T10:00 avec Dr. Ahmed Tounsi (Motif: Consultation)
2. RDV #3 - 2026-05-12T15:00 avec Dr. Fatima Ben Ali (Motif: Traitement)
...
Sélectionnez un rendez-vous à modifier (1-X): 1

Nouvelle date (yyyy-MM-dd HH:mm): 2026-05-15 11:00

Expected Output:
✅ IMPROVED: Now lists all patient's RDVs with full details
✅ User selects by number (no need to remember IDs)
✅ Only asks for new date
✅ Success message: "✅ Rendez-vous #1 modifié au 2026-05-15T11:00"
```

---

##### **Option 4: Cancel Rendez-vous (✅ IMPROVED)**

```
Input: 2 (patient ID)
       4 (option 4)

Expected Flow (NEW):
--- Vos rendez-vous ---
1. RDV #1 - 2026-05-08T10:00 avec Dr. Ahmed Tounsi (Motif: Consultation)
2. RDV #3 - 2026-05-12T15:00 avec Dr. Fatima Ben Ali (Motif: Traitement)
...
Sélectionnez un rendez-vous à annuler (1-X): 2

Expected Output:
✅ IMPROVED: Lists appointments with full details
✅ User selects by number
✅ Success message: "✅ Rendez-vous #3 annulé avec succès."
✅ Notifications sent to patient
```

---

##### **Option 5: View Notifications**

```
Input: 5

Expected Output:
✅ Lists all notifications received from server
✅ Includes registration confirmation and any RDV operation notifications
✅ Example:
   === Mes Notifications ===
   - CONFIRMATION: ...
   - CRÉATION RDV: ...
   - etc.
```

---

##### **Option 6: Graceful Exit (✅ NOW FIXED)**

```
Input: 6

Expected Flow:
--- Fin de session ---
1. Quitter l'application
2. Choisir un autre ID patient
Votre choix: 

If user enters "1":
✅ FIXED: Prints "Au revoir !" and exits gracefully

If user enters "2":
✅ FIXED: Returns to patient ID prompt to allow login with different patient
```

---

## Expected Test Results Summary

| Option | Before Fix | After Fix | Status |
|--------|-----------|-----------|--------|
| 1 (Consult) | ❌ JNDI Error | ✅ Works, shows all RDVs | FIXED |
| 2 (Create) | 📝 Works but hard UX | ✅ List doctors, auto-ID | IMPROVED |
| 3 (Modify) | 📝 Works but hard UX | ✅ List RDVs, select by # | IMPROVED |
| 4 (Cancel) | 📝 Works but hard UX | ✅ List RDVs, select by # | IMPROVED |
| 5 (Notify) | ✅ Works | ✅ Still works | UNCHANGED |
| 6 (Quit) | ❌ Just exits | ✅ Ask quit/re-login | FIXED |

---

## Troubleshooting

### "Cannot instantiate class: org.jboss.naming.remote.client.InitialContextFactory"
```
✅ SHOULD BE FIXED NOW
If you still get this:
1. Verify wildfly-client-all is in pom.xml
2. Rebuild: mvn clean package
3. Restart RMI server with new JAR
```

### "Connection refused to RMI registry"
```
Solution:
1. Ensure WildFly is running on port 8080
2. Ensure port 1099 is available: lsof -i :1099
3. If port taken, kill: kill -9 <PID>
4. Restart RMI server
```

### "No rendez-vous displayed"
```
Causes & Solutions:
1. Patient has no appointments: Add one via web interface first
2. Database not initialized: Run docs/init_database.sql
3. Wrong patient ID: Select valid ID from: SELECT id FROM patient;
```

### Menu not pausing after action
```
This is now FIXED - menu will always pause with:
"Appuyez sur Entrée pour revenir au menu..."

Wait for this prompt before continuing
```

---

## Code Changes Summary

### Files Modified
1. **pom.xml**: Added wildfly-client-all dependency
2. **CabinetRMIService.java**: 
   - Fixed JNDI context building with proper error handling
   - Added getMedecinService() method
   - Added listerTousLesMedecins() implementation
3. **CabinetRMIServiceRemote.java**: 
   - Added listerTousLesMedecins() interface method
   - Enhanced javadoc
4. **RMIClientApplication.java**:
   - Rewrote crierRendezVous() to show doctor list
   - Rewrote modifierRendezVous() to show RDV list
   - Rewrote annulerRendezVous() to show RDV list
   - Added gererQuitter() for graceful exit
5. **PatientCallbackImpl.java**: Already using thread-safe Collections.synchronizedList()

### Dependency Changes
```xml
<!-- NEW DEPENDENCY ADDED -->
<dependency>
    <groupId>org.wildfly</groupId>
    <artifactId>wildfly-client-all</artifactId>
    <version>27.0.0.Final</version>
</dependency>
```

---

## Verification Checklist

Use this checklist to verify all fixes:

- [ ] Project compiles without errors: `mvn clean compile`
- [ ] WAR builds successfully: `mvn clean package`
- [ ] Web interface loads without blank pages
- [ ] RMI option 1 works (no JNDI error)
- [ ] RMI option 2 shows doctor list
- [ ] RMI option 3 shows RDV list
- [ ] RMI option 4 shows RDV list for cancellation
- [ ] RMI option 5 displays notifications
- [ ] RMI option 6 offers quit/re-login choice
- [ ] Menu pauses after each action

---

## Next Steps

After all tests pass:
1. Update AGENTS.md with latest architecture changes
2. Document any additional edge cases discovered
3. Consider adding unit tests for edge cases
4. Deploy to production environment with monitoring

---

## Support

For issues:
1. Check the troubleshooting section
2. Review WildFly logs: `$WILDFLY_HOME/standalone/log/server.log`
3. Check MySQL connection: `mysql -u root -p cabinet_medical`
4. Verify database schema: `SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='cabinet_medical';`

---

**Last Updated**: May 5, 2026
**Version**: 1.0.0

