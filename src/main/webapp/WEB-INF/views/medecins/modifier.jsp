<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Modifier Médecin" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>✏️ Modifier Médecin</h2>
    <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">← Retour</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/medecins/edit" class="form">
    <input type="hidden" name="id" value="${medecin.id}">
    <div class="form-group">
      <label>Nom *</label>
      <input type="text" name="nom" class="form-control" value="${medecin.nom}" required>
    </div>
    <div class="form-group">
      <label>Prénom *</label>
      <input type="text" name="prenom" class="form-control" value="${medecin.prenom}" required>
    </div>
    <div class="form-group">
      <label>Spécialité *</label>
      <input type="text" name="specialite" class="form-control" value="${medecin.specialite}" required>
    </div>
    <div class="form-group">
      <label>Email *</label>
      <input type="email" name="email" class="form-control" value="${medecin.email}" required>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">✓ Mettre à jour</button>
      <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">Annuler</a>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
