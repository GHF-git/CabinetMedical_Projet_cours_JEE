<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Modifier Patient" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>✏️ Modifier Patient</h2>
    <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">← Retour</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/patients/edit" class="form">
    <input type="hidden" name="id" value="${patient.id}">
    <div class="form-row">
      <div class="form-group">
        <label>Nom *</label>
        <input type="text" name="nom" class="form-control" value="${patient.nom}" required>
      </div>
      <div class="form-group">
        <label>Prénom *</label>
        <input type="text" name="prenom" class="form-control" value="${patient.prenom}" required>
      </div>
    </div>
    <div class="form-group">
      <label>Email *</label>
      <input type="email" name="email" class="form-control" value="${patient.email}" required>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Téléphone</label>
        <input type="tel" name="telephone" class="form-control" value="${patient.telephone}">
      </div>
      <div class="form-group">
        <label>Date de Naissance</label>
        <input type="date" name="dateNaissance" class="form-control" value="${patient.dateNaissance}">
      </div>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">✓ Mettre à jour</button>
      <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">Annuler</a>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
