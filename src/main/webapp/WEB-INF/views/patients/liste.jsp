<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Patients" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>👤 Gestion des Patients</h2>
    <a href="${pageContext.request.contextPath}/patients/add" class="btn btn-primary">+ Nouveau Patient</a>
  </div>

  <form method="get" action="${pageContext.request.contextPath}/patients/search" class="search-form">
    <div class="search-group">
      <input type="text" name="recherche" placeholder="🔍  Rechercher par nom ou email..."
             value="${recherche}" class="search-input">
      <button type="submit" class="btn btn-primary">Rechercher</button>
      <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">Réinitialiser</a>
    </div>
  </form>

  <table class="data-table">
    <thead>
      <tr>
        <th>#</th><th>Patient</th><th>Email</th><th>Téléphone</th><th>Date de Naissance</th><th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty patients}">
          <tr><td colspan="6" class="text-center">Aucun patient trouvé</td></tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="patient" items="${patients}">
            <tr>
              <td style="color:var(--steel);font-size:0.8rem;">${patient.id}</td>
              <td>
                <div class="user-cell">
                  <div class="avatar">${patient.prenom.charAt(0)}${patient.nom.charAt(0)}</div>
                  <div>
                    <div class="name">${patient.prenom} ${patient.nom}</div>
                  </div>
                </div>
              </td>
              <td>${patient.email}</td>
              <td>${patient.telephone}</td>
              <td>${patient.dateNaissance}</td>
              <td class="actions">
                <a href="${pageContext.request.contextPath}/patients/edit?id=${patient.id}" class="btn btn-small btn-secondary">✏️ Modifier</a>
                <form method="post" action="${pageContext.request.contextPath}/patients/delete" style="display:inline;"
                      onsubmit="return confirm('Supprimer ce patient ?');">
                  <input type="hidden" name="id" value="${patient.id}">
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
