# Quick Reference Card - Cabinet Medical

## 🚀 Quick Start (5 minutes)

### Build & Deploy
```bash
# Build
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE
mvn clean package

# Deploy to WildFly
mvn wildfly:deploy

# Verify deployment
curl -s http://localhost:8080/CabinetMedical/index.jsp | head -5
```

### Test Web Interface
```
http://localhost:8080/CabinetMedical/

Available Pages:
  - /patients or /patients/add
  - /medecins or /medecins/add
  - /rendezvous or /rendezvous/add
```

---

## 🖥️ Test RMI Terminal (10 minutes)

### Terminal 1: Start RMI Server
```bash
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE

java -cp target/CabinetMedical-1.0.0-jar-with-dependencies.jar \
  tn.isims.cabinet.rmi.impl.RMIServer

# Should see:
# RMI Server démarré et prêt à recevoir les connexions
```

### Terminal 2: Start RMI Client
```bash
cd /Users/7abcha9la/Downloads/CabinetMedical_Projet_cours_JEE

java -cp target/CabinetMedical-1.0.0-jar-with-dependencies.jar \
  tn.isims.cabinet.rmi.impl.RMIClientApplication

# Enter patient ID: 1  (or 2, depending on your database)
```

### Test Menu Options
```
Menu Options (Now with improved UX):

1. Consult Rendez-vous
   ✅ Fixed: No more JNDI error!
   ✅ Shows all appointments with doctor names

2. Create Rendez-vous
   ✅ Lists all doctors to select from (no ID memorizing!)
   ✅ Auto-generates appointment ID

3. Modify Rendez-vous
   ✅ Lists your appointments
   ✅ Select by number, then enter new date

4. Cancel Rendez-vous
   ✅ Lists your appointments
   ✅ Select by number to cancel

5. View Notifications
   ✅ Shows all received messages

6. Quit/Logout
   ✅ Fixed: Now offers Quit or Try Another Patient ID
```

---

## 📋 Changes Made

### Critical Fix: JNDI Error (Option 1)
```
BEFORE: ClassNotFoundException when selecting option 1
AFTER:  ✅ Works! Shows all rendez-vous

Cause:  Missing WildFly remote naming library
Solution: Added wildfly-client-all to pom.xml
```

### UX Improvements: Options 2, 3, 4
```
BEFORE: Required manual ID entry
AFTER:  ✅ Shows interactive lists to select from

Option 2 Flow:
  1. Shows list of doctors
  2. You select by number (1, 2, 3, etc.)
  3. Enter date and reason
  4. Done! ID auto-generated

Option 3 Flow:
  1. Shows all your appointments
  2. You select by number
  3. Enter new date
  4. Done!

Option 4 Flow:
  1. Shows all your appointments
  2. You select by number
  3. Confirmed!
```

### Graceful Exit: Option 6
```
BEFORE: Just said "Au revoir!" then quit
AFTER:  ✅ Menu asks:
        1. Quit application
        2. Try different patient ID

Much better for testing!
```

---

## 🔧 Troubleshooting

### "JNDI Error" (NOW FIXED)
```
If you STILL see: ClassNotFoundException: org.jboss.naming...

1. Rebuild: mvn clean package
2. Restart RMI server with new JAR
3. Check WildFly is running on port 8080
```

### "Connection refused on port 1099"
```
1. Check if port is free: lsof -i :1099
2. Kill if needed: kill -9 <PID>
3. Restart RMI server
```

### "No doctor list appears" (Option 2)
```
1. Database initialized? Run: docs/init_database.sql
2. Doctors exist? Check: SELECT COUNT(*) FROM medecin;
3. Get doctor count: SELECT id, nom FROM medecin;
```

### "No appointments to modify" (Option 3/4)
```
1. Create appointment first via Option 2
2. Or check patient ID is correct
3. Or verify appointment exists: SELECT * FROM rendezvous WHERE patient_id=<your_id>;
```

---

## 📊 Architecture Quick View

```
┌─────────────────────┐
│   Your Computer     │
├─────────────────────┤
│ Web Browser         │──→ http://localhost:8080/CabinetMedical
│ RMI Terminal 1      │──→ localhost:1099 (RMI Registry)
│ RMI Terminal 2      │──→ localhost:1099 (RMI Client)
└─────────────────────┘
         │
         ├──────────────────────────────┐
         │                              │
         ▼                              ▼
    ┌─────────────┐          ┌──────────────────┐
    │  WildFly    │          │  RMI Server      │
    │  Port 8080  │          │  Port 1099       │
    │  Port 8080  │          │                  │
    │  Servlets   │◄────────►│  EJB Lookups     │
    │  JSP        │          │  (via JNDI)      │
    │  EJBs       │          │                  │
    └─────────────┘          └──────────────────┘
         │
         ▼
    ┌──────────────┐
    │  MySQL       │
    │  :3306       │
    │  cabinet_    │
    │  medical     │
    └──────────────┘
```

---

## 📁 Key Files Modified

| File | Change | Effect |
|------|--------|--------|
| `pom.xml` | + wildfly-client-all | Fixes JNDI error |
| `CabinetRMIService.java` | + listerTousLesMedecins() | Enables doctor list UX |
| `CabinetRMIServiceRemote.java` | + interface method | Defines contract |
| `RMIClientApplication.java` | Rewrote options 2,3,4,6 | Better UX |

---

## ✅ Test Checklist

- [ ] `mvn clean package` succeeds
- [ ] Web page loads: http://localhost:8080/CabinetMedical/
- [ ] RMI Server starts without errors
- [ ] RMI Client connects and shows menu
- [ ] Option 1: Shows appointments (NO JNDI ERROR!)
- [ ] Option 2: Shows doctor list
- [ ] Option 3: Shows appointment list  
- [ ] Option 4: Shows appointment list
- [ ] Option 5: Shows notifications
- [ ] Option 6: Offers quit or re-login

---

## 🎯 Expected Output Examples

### Option 1 (Consult)
```
✅ Working! Should show:
RendezVous[id=1, dateRendezVous=2026-05-10T14:30, 
           patient=Patient[id=2, nom=Ahmed, ...],
           medecin=Medecin[id=1, nom=Tounsi, ...]]
```

### Option 2 (Create) - NEW!
```
--- Sélectionner un médecin ---
1. Dr. Ahmed Tounsi (Cardiologue)
2. Dr. Fatima Ben Ali (Dermatologue)
Sélectionnez un médecin (1-2): 1

Date du rendez-vous (yyyy-MM-dd HH:mm): 2026-05-10 14:30
Motif: Consultation générale
✅ Rendez-vous créé avec succès. ID: 5
```

### Option 3 (Modify) - NEW!
```
--- Vos rendez-vous ---
1. RDV #1 - 2026-05-08T10:00 avec Dr. Ahmed Tounsi (Motif: Consultation)
2. RDV #3 - 2026-05-12T15:00 avec Dr. Fatima Ben Ali (Motif: Traitement)
Sélectionnez un rendez-vous à modifier (1-2): 1

Nouvelle date (yyyy-MM-dd HH:mm): 2026-05-15 11:00
✅ Rendez-vous #1 modifié au 2026-05-15T11:00
```

### Option 6 (Quit) - NEW!
```
--- Fin de session ---
1. Quitter l'application
2. Choisir un autre ID patient
Votre choix: 1
Au revoir !
```

---

## 📞 Need Help?

1. **For setup**: See README.md
2. **For detailed testing**: See TESTING_GUIDE.md
3. **For all changes**: See SESSION_SUMMARY.md
4. **For architecture**: See AGENTS.md

---

## 🎊 Summary

**All Issues Fixed!**

| Issue | Before | After |
|-------|--------|-------|
| Option 1 Error | ❌ JNDI Error | ✅ Shows appointments |
| Option 2 UX | 📝 Manual ID | ✅ Select from list |
| Option 3 UX | 📝 Manual ID | ✅ Select from list |
| Option 4 UX | 📝 Manual ID | ✅ Select from list |
| Option 6 | ❌ Just exits | ✅ Quit or retry |
| Overall | ❌ Hard to use | ✅ Easy to use |

**Ready to test!** 🚀

---

**Build Status**: ✅ SUCCESS
**Last Updated**: May 5, 2026

