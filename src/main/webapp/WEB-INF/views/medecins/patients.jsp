<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Patients du Médecin" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
  <div>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
      <i class="ph ph-users-three text-brand-600"></i> Patients de Dr. ${medecin.prenom} ${medecin.nom}
    </h1>
    <div class="mt-2 flex items-center gap-2">
      <span class="inline-flex items-center rounded-full bg-brand-50 px-2.5 py-0.5 text-xs font-medium text-brand-700 ring-1 ring-inset ring-brand-700/10">
        ${medecin.specialite}
      </span>
    </div>
  </div>
  <a href="${pageContext.request.contextPath}/medecins" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
    <i class="ph ph-arrow-left mr-2 text-lg"></i> Retour
  </a>
</div>

<div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-slate-200">
      <thead class="bg-slate-50">
        <tr>
          <th scope="col" class="py-3.5 pl-4 pr-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider sm:pl-6">#</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Patient</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Email</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Téléphone</th>
          <th scope="col" class="px-3 py-3.5 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Date Naissance</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 bg-white">
        <c:choose>
          <c:when test="${empty patients}">
            <tr>
              <td colspan="5" class="py-12 text-center">
                <div class="flex flex-col items-center justify-center text-slate-400">
                  <i class="ph ph-users-three text-5xl mb-3 opacity-50"></i>
                  <p class="text-base font-medium text-slate-600">Aucun patient associé à ce médecin</p>
                </div>
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="patient" items="${patients}">
              <tr class="hover:bg-slate-50 transition-colors">
                <td class="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-slate-500 sm:pl-6">${patient.id}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm">
                  <div class="flex items-center gap-3">
                    <div class="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-brand-100 text-brand-700 font-bold text-xs ring-2 ring-white">
                      ${patient.prenom.substring(0,1)}${patient.nom.substring(0,1)}
                    </div>
                    <div class="font-medium text-slate-900">${patient.prenom} ${patient.nom}</div>
                  </div>
                </td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-slate-500">${patient.email}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-slate-500">${patient.telephone}</td>
                <td class="whitespace-nowrap px-3 py-4 text-sm text-slate-500">${patient.dateNaissance}</td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
