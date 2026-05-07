<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Patients" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
  <div>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
      <i class="ph ph-users text-brand-600"></i> Gestion des Patients
    </h1>
    <p class="mt-1 text-sm text-slate-500">Consultez et gérez la liste de tous vos patients.</p>
  </div>
  <a href="${pageContext.request.contextPath}/patients/add" class="inline-flex items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all">
    <i class="ph ph-plus mr-2 text-lg"></i> Nouveau Patient
  </a>
</div>

<div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
  <div class="p-4 sm:p-6 border-b border-slate-200 bg-slate-50/50">
    <form method="get" action="${pageContext.request.contextPath}/patients/search" class="flex flex-col sm:flex-row gap-3">
      <div class="relative flex-grow max-w-md">
        <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
          <i class="ph ph-magnifying-glass text-slate-400 text-lg"></i>
        </div>
        <input type="text" name="recherche" value="${recherche}" placeholder="Rechercher par nom ou email..." class="block w-full rounded-xl border-0 py-2.5 pl-10 pr-3 text-slate-900 ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
      </div>
      <button type="submit" class="inline-flex items-center justify-center rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-slate-800 transition-all">
        Rechercher
      </button>
      <c:if test="${not empty recherche}">
        <a href="${pageContext.request.contextPath}/patients" class="inline-flex items-center justify-center rounded-xl bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
          Effacer
        </a>
      </c:if>
    </form>
  </div>

  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-slate-200">
      <thead class="bg-slate-50">
        <tr>
          <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider sm:pl-6">#</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Patient</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Contact</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Naissance</th>
          <th scope="col" class="relative py-3.5 pl-3 pr-4 sm:pr-6 text-right"><span class="sr-only">Actions</span></th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 bg-white">
        <c:choose>
          <c:when test="${empty patients}">
            <tr>
              <td colspan="5" class="py-12 text-center">
                <div class="flex flex-col items-center justify-center text-slate-400">
                  <i class="ph ph-users-three text-5xl mb-3 opacity-50"></i>
                  <p class="text-base font-medium text-slate-600">Aucun patient trouvé</p>
                  <p class="text-sm">Essayez de modifier vos critères de recherche.</p>
                </div>
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="patient" items="${patients}">
              <tr class="hover:bg-slate-50 transition-colors group">
                <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-slate-500 sm:pl-6">${patient.id}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <div class="flex items-center gap-3">
                    <div class="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-full bg-brand-100 text-brand-700 font-bold text-sm ring-2 ring-white">
                      ${patient.prenom.substring(0,1)}${patient.nom.substring(0,1)}
                    </div>
                    <div>
                      <div class="font-medium text-slate-900">${patient.prenom} ${patient.nom}</div>
                    </div>
                  </div>
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-slate-500">
                  <div class="flex flex-col gap-1">
                    <div class="flex items-center gap-1"><i class="ph ph-envelope-simple text-slate-400"></i> ${patient.email}</div>
                    <c:if test="${not empty patient.telephone}">
                      <div class="flex items-center gap-1"><i class="ph ph-phone text-slate-400"></i> ${patient.telephone}</div>
                    </c:if>
                  </div>
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-slate-500">
                  <div class="flex items-center gap-1">
                    <i class="ph ph-calendar-blank text-slate-400"></i> ${patient.dateNaissance}
                  </div>
                </td>
                <td class="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                  <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                    <a href="${pageContext.request.contextPath}/patients/edit?id=${patient.id}" class="inline-flex items-center justify-center h-8 w-8 rounded-lg bg-white text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-slate-50 hover:text-brand-600 transition-colors" title="Modifier">
                      <i class="ph ph-pencil-simple text-lg"></i>
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/patients/delete" class="inline-block" onsubmit="return confirmDelete(this, 'Voulez-vous vraiment supprimer le patient ${patient.prenom} ${patient.nom} ?');">
                      <input type="hidden" name="id" value="${patient.id}">
                      <button type="submit" class="inline-flex items-center justify-center h-8 w-8 rounded-lg bg-white text-slate-400 ring-1 ring-inset ring-slate-300 hover:bg-rose-50 hover:text-rose-600 transition-colors" title="Supprimer">
                        <i class="ph ph-trash text-lg"></i>
                      </button>
                    </form>
                  </div>
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
