<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
  <div>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
      <i class="ph ph-calendar-blank text-brand-600"></i> Gestion des Rendez-vous
    </h1>
    <p class="mt-1 text-sm text-slate-500">Gérez les consultations et plannings du cabinet.</p>
  </div>
  <div class="flex items-center gap-3">
    <a href="${pageContext.request.contextPath}/rendezvous/calendar" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
      <i class="ph ph-calendar-check mr-2 text-lg"></i> Vue Calendrier
    </a>
    <a href="${pageContext.request.contextPath}/rendezvous/add" class="inline-flex items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all">
      <i class="ph ph-plus mr-2 text-lg"></i> Nouveau Rendez-vous
    </a>
  </div>
</div>

<div class="mb-6 border-b border-slate-200">
  <nav class="-mb-px flex space-x-6" aria-label="Tabs">
    <a href="${pageContext.request.contextPath}/rendezvous" class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors <c:choose><c:when test="${activeTab == 'tous' or empty activeTab}">border-brand-500 text-brand-600</c:when><c:otherwise>border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700</c:otherwise></c:choose>">
      <i class="ph ph-folders mr-1"></i> Tous
    </a>
    <a href="${pageContext.request.contextPath}/rendezvous/du-jour" class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors <c:choose><c:when test="${activeTab == 'jour'}">border-brand-500 text-brand-600</c:when><c:otherwise>border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700</c:otherwise></c:choose>">
      <i class="ph ph-calendar-star mr-1"></i> Aujourd'hui
    </a>
    <a href="${pageContext.request.contextPath}/rendezvous/passes" class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors <c:choose><c:when test="${activeTab == 'passes'}">border-brand-500 text-brand-600</c:when><c:otherwise>border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700</c:otherwise></c:choose>">
      <i class="ph ph-clock-counter-clockwise mr-1"></i> Passés
    </a>
  </nav>
</div>

<div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-slate-200">
      <thead class="bg-slate-50">
        <tr>
          <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider sm:pl-6">#</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Date & Heure</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Patient</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Médecin</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Statut</th>
          <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6 text-right"><span class="sr-only">Actions</span></th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 bg-white">
        <c:choose>
          <c:when test="${empty rendezVous}">
            <tr>
              <td colspan="6" class="py-12 text-center">
                <div class="flex flex-col items-center justify-center text-slate-400">
                  <i class="ph ph-calendar-x text-5xl mb-3 opacity-50"></i>
                  <p class="text-base font-medium text-slate-600">Aucun rendez-vous trouvé</p>
                </div>
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="rdv" items="${rendezVous}">
              <tr class="hover:bg-slate-50 transition-colors group">
                <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-slate-500 sm:pl-6">${rdv.id}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm font-semibold text-slate-900">${rdv.dateFormatted}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <div class="flex items-center gap-3">
                    <div class="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-brand-100 text-brand-700 font-bold text-xs ring-2 ring-white">
                      ${rdv.patient.prenom.substring(0,1)}${rdv.patient.nom.substring(0,1)}
                    </div>
                    <div>
                      <div class="font-medium text-slate-900">${rdv.patient.prenom} ${rdv.patient.nom}</div>
                      <div class="text-xs text-slate-500">${rdv.patient.telephone}</div>
                    </div>
                  </div>
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <div class="flex flex-col">
                    <span class="font-medium text-slate-900">Dr. ${rdv.medecin.prenom} ${rdv.medecin.nom}</span>
                    <span class="text-xs text-slate-500">${rdv.medecin.specialite}</span>
                  </div>
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <c:choose>
                    <c:when test="${rdv.statut.name() == 'PLANIFIE'}">
                      <span class="inline-flex items-center rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-medium text-blue-700 ring-1 ring-inset ring-blue-700/10">Planifié</span>
                    </c:when>
                    <c:when test="${rdv.statut.name() == 'TERMINE'}">
                      <span class="inline-flex items-center rounded-full bg-emerald-50 px-2.5 py-0.5 text-xs font-medium text-emerald-700 ring-1 ring-inset ring-emerald-700/10">Terminé</span>
                    </c:when>
                    <c:otherwise>
                      <span class="inline-flex items-center rounded-full bg-rose-50 px-2.5 py-0.5 text-xs font-medium text-rose-700 ring-1 ring-inset ring-rose-700/10">Annulé</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                  <c:choose>
                    <c:when test="${rdv.statut.name() == 'PLANIFIE'}">
                      <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        <a href="${pageContext.request.contextPath}/rendezvous/edit?id=${rdv.id}" class="inline-flex items-center justify-center h-8 w-8 rounded-lg bg-white text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 hover:text-brand-600 transition-colors" title="Modifier">
                          <i class="ph ph-pencil-simple text-lg"></i>
                        </a>
                        <form method="post" action="${pageContext.request.contextPath}/rendezvous/terminate" class="inline-block" onsubmit="return confirmDelete(this, 'Marquer ce rendez-vous comme terminé ?');">
                          <input type="hidden" name="id" value="${rdv.id}">
                          <button type="submit" class="inline-flex items-center justify-center h-8 w-8 rounded-lg bg-white text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-emerald-50 hover:text-emerald-600 transition-colors" title="Terminer">
                            <i class="ph ph-check text-lg"></i>
                          </button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/rendezvous/cancel" class="inline-block" onsubmit="return confirmDelete(this, 'Voulez-vous vraiment annuler ce rendez-vous ?');">
                          <input type="hidden" name="id" value="${rdv.id}">
                          <button type="submit" class="inline-flex items-center justify-center h-8 w-8 rounded-lg bg-white text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-rose-50 hover:text-rose-600 transition-colors" title="Annuler">
                            <i class="ph ph-x text-lg"></i>
                          </button>
                        </form>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <span class="text-slate-300 text-sm">—</span>
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
</div>

<jsp:include page="../layout/footer.jsp"/>
