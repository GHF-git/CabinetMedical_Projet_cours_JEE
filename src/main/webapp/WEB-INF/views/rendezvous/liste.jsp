<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<c:set var="titre" value="Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="content">
  <div class="page-header">
    <h2>📅 Gestion des Rendez-vous</h2>
    <a href="${pageContext.request.contextPath}/rendezvous/add" class="btn btn-primary">＋ Nouveau Rendez-vous</a>
  </div>

  <div class="filter-tabs">
    <a href="${pageContext.request.contextPath}/rendezvous"
       class="tab <c:if test="${activeTab == 'tous' or empty activeTab}">active</c:if>">🗂 Tous</a>
    <a href="${pageContext.request.contextPath}/rendezvous/du-jour"
       class="tab <c:if test="${activeTab == 'jour'}">active</c:if>">📅 Aujourd'hui</a>
    <a href="${pageContext.request.contextPath}/rendezvous/passes"
       class="tab <c:if test="${activeTab == 'passes'}">active</c:if>">🕐 Passés</a>
  </div>

  <table class="data-table">
    <thead>
      <tr>
        <th>#</th>
        <th>Date &amp; Heure</th>
        <th>Patient</th>
        <th>Médecin</th>
        <th>Motif</th>
        <th>Statut</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty rendezVous}">
          <tr><td colspan="7" class="text-center">
            <span style="font-size:2rem;">📭</span><br>Aucun rendez-vous trouvé
          </td></tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="rdv" items="${rendezVous}">
            <tr>
              <td class="col-id">${rdv.id}</td>
              <td class="col-date"><strong>${rdv.dateFormatted}</strong></td>
              <td>
                <div class="user-cell">
                  <div class="avatar">${rdv.patient.prenom.charAt(0)}${rdv.patient.nom.charAt(0)}</div>
                  <div>
                    <div class="name">${rdv.patient.prenom} ${rdv.patient.nom}</div>
                    <div class="sub">${rdv.patient.telephone}</div>
                  </div>
                </div>
              </td>
              <td>
                <div class="user-cell">
                  <div class="avatar avatar-dr">Dr</div>
                  <div>
                    <div class="name">Dr. ${rdv.medecin.prenom} ${rdv.medecin.nom}</div>
                    <div class="sub">${rdv.medecin.specialite}</div>
                  </div>
                </div>
              </td>
              <td class="col-motif" title="${rdv.motif}">${rdv.motif}</td>
              <td>
                <span class="badge badge-${rdv.statutLower}">${rdv.statut.name()}</span>
              </td>
              <td class="actions">
                <c:choose>
                  <c:when test="${rdv.statut.name() == 'PLANIFIE'}">
                    <a href="${pageContext.request.contextPath}/rendezvous/edit?id=${rdv.id}"
                       class="btn btn-small btn-secondary" title="Modifier">✏️</a>
                    <form method="post"
                          action="${pageContext.request.contextPath}/rendezvous/terminate"
                          style="display:inline;"
                          onsubmit="return confirm('Marquer comme terminé ?');">
                      <input type="hidden" name="id" value="${rdv.id}">
                      <button type="submit" class="btn btn-small btn-success" title="Terminer">✓</button>
                    </form>
                    <form method="post"
                          action="${pageContext.request.contextPath}/rendezvous/cancel"
                          style="display:inline;"
                          onsubmit="return confirm('Annuler ce rendez-vous ?');">
                      <input type="hidden" name="id" value="${rdv.id}">
                      <button type="submit" class="btn btn-small btn-danger" title="Annuler">✗</button>
                    </form>
                  </c:when>
                  <c:otherwise>
                    <span class="text-muted" style="font-size:0.8rem;">—</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
