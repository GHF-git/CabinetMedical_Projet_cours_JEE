<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cabinet Médical</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header>
  <nav class="navbar">
    <div class="logo">Cabinet Médical</div>
    <ul class="nav-menu">
      <li><a href="${pageContext.request.contextPath}/patients">Patients</a></li>
      <li><a href="${pageContext.request.contextPath}/medecins">Médecins</a></li>
      <li><a href="${pageContext.request.contextPath}/rendezvous">Rendez-vous</a></li>
    </ul>
  </nav>
</header>

<main class="container">
  <div class="hero">
    <h1>Système de Gestion de Cabinet Médical</h1>
    <p>Application JEE complète — Servlets · EJB · JPA · RMI</p>
    <div class="tech-chips">
      <span class="tech-chip">WildFly</span>
      <span class="tech-chip">Jakarta EE 10</span>
      <span class="tech-chip">Hibernate JPA</span>
      <span class="tech-chip">MySQL</span>
      <span class="tech-chip">Java RMI</span>
    </div>
  </div>

  <div class="features">
    <div class="feature-card">
      <div class="icon">👤</div>
      <h3>Gestion des Patients</h3>
      <p>Ajout, modification, suppression et recherche par nom ou email.</p>
    </div>
    <div class="feature-card">
      <div class="icon">🩺</div>
      <h3>Gestion des Médecins</h3>
      <p>Administration par spécialité avec liste des patients associés.</p>
    </div>
    <div class="feature-card">
      <div class="icon">📅</div>
      <h3>Rendez-vous</h3>
      <p>Planification, modification et annulation des consultations.</p>
    </div>
    <div class="feature-card">
      <div class="icon">🔔</div>
      <h3>Notifications RMI</h3>
      <p>Notifications temps réel pour les patients via RMI Callback.</p>
    </div>
  </div>

  <div class="quick-links">
    <a href="${pageContext.request.contextPath}/patients" class="btn btn-primary">👤 Gérer les Patients</a>
    <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">🩺 Gérer les Médecins</a>
    <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">📅 Rendez-vous</a>
  </div>
</main>

<footer>
  <div class="container">
    <p>Institut Supérieur d'Informatique et de Multimédia de Sfax — ISIMS</p>
    <p>2025-2026 · Technologies JEE · Filière P-IINFO</p>
  </div>
</footer>
</body>
</html>
