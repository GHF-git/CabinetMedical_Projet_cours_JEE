<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Ajouter Patient" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>👤 Nouveau Patient</h2>
    <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">← Retour</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/patients/add" class="form">
    <div class="form-row">
      <div class="form-group">
        <label>Nom *</label>
        <input type="text" name="nom" class="form-control" placeholder="Ex: Ben Ali" required autofocus>
      </div>
      <div class="form-group">
        <label>Prénom *</label>
        <input type="text" name="prenom" class="form-control" placeholder="Ex: Mohamed" required>
      </div>
    </div>
    <div class="form-group">
      <label>Email *</label>
      <input type="email" name="email" class="form-control" placeholder="exemple@email.com" required>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Téléphone</label>
        <input type="tel" name="telephone" class="form-control" placeholder="+216 XX XXX XXX">
      </div>
      <div class="form-group">
        <label>Date de Naissance</label>
        <input type="date" name="dateNaissance" class="form-control">
      </div>
    </div>
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">✓ Enregistrer</button>
      <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">Annuler</a>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
