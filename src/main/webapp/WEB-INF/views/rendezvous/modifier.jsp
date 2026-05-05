<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Modifier Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>✏️ Modifier le Rendez-vous</h2>
    <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">← Retour</a>
  </div>

  <c:choose>
    <c:when test="${not empty rendezVous}">
      <div class="info-card">
        <p><strong>Patient :</strong>
          ${rendezVous.patient.prenom} ${rendezVous.patient.nom}
          <span style="color:var(--steel);font-size:0.85rem;">(${rendezVous.patient.email})</span>
        </p>
        <p><strong>Médecin :</strong>
          Dr. ${rendezVous.medecin.prenom} ${rendezVous.medecin.nom}
          — ${rendezVous.medecin.specialite}
        </p>
        <p><strong>Date actuelle :</strong> ${rendezVous.dateFormatted}</p>
        <p><strong>Statut :</strong>
          <span class="badge badge-${rendezVous.statutLower}">${rendezVous.statut.name()}</span>
        </p>
      </div>

      <form method="post" action="${pageContext.request.contextPath}/rendezvous/edit" class="form">
        <input type="hidden" name="id" value="${rendezVous.id}">
        <div class="form-group">
          <label>Nouvelle Date et Heure *</label>
          <input type="datetime-local" name="dateRendezVous" class="form-control"
                 value="${rendezVous.dateForInput}" required>
        </div>
        <div class="form-group">
          <label>Motif *</label>
          <textarea name="motif" class="form-control" rows="3" required>${rendezVous.motif}</textarea>
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary">✓ Mettre à jour</button>
          <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">Annuler</a>
        </div>
      </form>
    </c:when>
    <c:otherwise>
      <div class="alert alert-error">Rendez-vous introuvable.</div>
      <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">← Retour à la liste</a>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="../layout/footer.jsp"/>
