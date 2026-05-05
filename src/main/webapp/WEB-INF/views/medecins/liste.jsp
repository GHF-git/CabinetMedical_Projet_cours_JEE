<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Médecins" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>🩺 Gestion des Médecins</h2>
    <a href="${pageContext.request.contextPath}/medecins/add" class="btn btn-primary">+ Nouveau Médecin</a>
  </div>

  <form method="get" action="${pageContext.request.contextPath}/medecins/search" class="search-form">
    <div class="search-group">
      <input type="text" name="specialite" placeholder="🔍  Rechercher par spécialité..."
             value="${not empty specialiteSelectionnee ? specialiteSelectionnee : specialite}" class="search-input">
      <button type="submit" class="btn btn-primary">Rechercher</button>
      <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">Réinitialiser</a>
    </div>
  </form>

  <table class="data-table">
    <thead>
      <tr>
        <th>#</th><th>Médecin</th><th>Spécialité</th><th>Email</th><th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty medecins}">
          <tr><td colspan="5" class="text-center">Aucun médecin trouvé</td></tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="medecin" items="${medecins}">
            <tr>
              <td style="color:var(--steel);font-size:0.8rem;">${medecin.id}</td>
              <td>
                <div class="user-cell">
                  <div class="avatar" style="background:#EDE9FE;color:#5B21B6;">Dr</div>
                  <div>
                    <div class="name">Dr. ${medecin.prenom} ${medecin.nom}</div>
                  </div>
                </div>
              </td>
              <td><span class="badge badge-planifie">${medecin.specialite}</span></td>
              <td>${medecin.email}</td>
              <td class="actions">
                <a href="${pageContext.request.contextPath}/medecins/patients?id=${medecin.id}" class="btn btn-small btn-secondary">👥 Patients</a>
                <a href="${pageContext.request.contextPath}/medecins/edit?id=${medecin.id}" class="btn btn-small btn-secondary">✏️ Modifier</a>
                <form method="post" action="${pageContext.request.contextPath}/medecins/delete" style="display:inline;"
                      onsubmit="return confirm('Supprimer ce médecin ?');">
                  <input type="hidden" name="id" value="${medecin.id}">
                  <button type="submit" class="btn btn-small btn-danger">🗑 Supprimer</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
