<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Nouveau Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>📅 Nouveau Rendez-vous</h2>
    <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">← Retour</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/rendezvous/add" class="form">
    <div class="form-group">
      <label>Patient *</label>
      <select name="patientId" class="form-control" required>
        <option value="">— Sélectionner un patient —</option>
        <c:forEach var="patient" items="${patients}">
          <option value="${patient.id}">${patient.prenom} ${patient.nom} — ${patient.email}</option>
        </c:forEach>
      </select>
    </div>
    <div class="form-group">
      <label>Médecin *</label>
      <select name="medecinId" class="form-control" required>
        <option value="">— Sélectionner un médecin —</option>
        <c:forEach var="medecin" items="${medecins}">
          <option value="${medecin.id}">Dr. ${medecin.prenom} ${medecin.nom} — ${medecin.specialite}</option>
        </c:forEach>
      </select>
    </div>
    <div class="form-group">
      <label>Date et Heure *</label>
      <input type="datetime-local" name="dateRendezVous" class="form-control" required
             min="${pageContext.request.getAttribute('today')}">
    </div>
    <div class="form-group">
      <label>Motif de consultation *</label>
      <textarea name="motif" class="form-control" rows="3"
                placeholder="Ex: Consultation générale, suivi traitement..." required></textarea>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">✓ Créer le Rendez-vous</button>
      <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">Annuler</a>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
