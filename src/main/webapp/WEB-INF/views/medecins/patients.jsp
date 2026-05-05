<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Patients du Médecin" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>👥 Patients de Dr. ${medecin.prenom} ${medecin.nom}</h2>
    <div style="display:flex;gap:8px;align-items:center;">
      <span class="badge badge-planifie">${medecin.specialite}</span>
      <a href="${pageContext.request.contextPath}/medecins" class="btn btn-secondary">← Retour</a>
    </div>
  </div>

  <table class="data-table">
    <thead>
      <tr><th>#</th><th>Patient</th><th>Email</th><th>Téléphone</th><th>Date Naissance</th></tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty patients}">
          <tr><td colspan="5" class="text-center">Aucun patient associé à ce médecin</td></tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="patient" items="${patients}">
            <tr>
              <td style="color:var(--steel);font-size:0.8rem;">${patient.id}</td>
              <td>
                <div class="user-cell">
                  <div class="avatar">${patient.prenom.charAt(0)}${patient.nom.charAt(0)}</div>
                  <div class="name">${patient.prenom} ${patient.nom}</div>
                </div>
              </td>
              <td>${patient.email}</td>
              <td>${patient.telephone}</td>
              <td>${patient.dateNaissance}</td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
