<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:if test="${not empty titre}">${titre} — </c:if>Cabinet Médical · ISIMS</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header>
  <nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="logo">Cabinet Médical</a>
    <ul class="nav-menu">
      <li>
        <a href="${pageContext.request.contextPath}/patients"
           class="${pageContext.request.requestURI.contains('/patients') ? 'active' : ''}">
          👤 Patients
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/medecins"
           class="${pageContext.request.requestURI.contains('/medecins') ? 'active' : ''}">
          🩺 Médecins
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/rendezvous"
           class="${pageContext.request.requestURI.contains('/rendezvous') ? 'active' : ''}">
          📅 Rendez-vous
        </a>
      </li>
    </ul>
  </nav>
</header>
<main class="container">
  <c:if test="${not empty message}">
    <div class="alert alert-success">${message}</div>
  </c:if>
  <c:if test="${not empty erreur}">
    <div class="alert alert-error">${erreur}</div>
  </c:if>
