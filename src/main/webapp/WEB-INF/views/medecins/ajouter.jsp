<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Ajouter Médecin" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>🩺 Nouveau Médecin</h2>
    <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">← Retour</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/medecins/add" class="form">
    <div class="form-group">
      <label>Nom *</label>
      <input type="text" name="nom" class="form-control" placeholder="Ex: Mansour" required>
    </div>
    <div class="form-group">
      <label>Prénom *</label>
      <input type="text" name="prenom" class="form-control" placeholder="Ex: Sami" required>
    </div>
    <div class="form-group">
      <label>Spécialité *</label>
      <input type="text" name="specialite" class="form-control" placeholder="Ex: Cardiologie" required>
    </div>
    <div class="form-group">
      <label>Email *</label>
      <input type="email" name="email" class="form-control" placeholder="dr.exemple@cabinet.tn" required>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">✓ Enregistrer</button>
      <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">Annuler</a>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
