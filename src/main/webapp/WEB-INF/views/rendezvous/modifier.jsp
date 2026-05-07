<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Modifier Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="max-w-3xl mx-auto">
  <div class="mb-6 flex items-center justify-between">
    <div>
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
        <i class="ph ph-pencil-simple text-brand-600"></i> Modifier le Rendez-vous
      </h1>
      <p class="mt-1 text-sm text-slate-500">Mettez à jour la date ou le motif.</p>
    </div>
    <a href="${pageContext.request.contextPath}/rendezvous" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
      <i class="ph ph-arrow-left mr-2 text-lg"></i> Retour
    </a>
  </div>

  <c:choose>
    <c:when test="${not empty rendezVous}">
      
      <!-- Info Card -->
      <div class="bg-brand-50 border border-brand-100 rounded-2xl p-6 mb-6">
        <h3 class="text-sm font-semibold text-brand-900 uppercase tracking-wider mb-4">Informations Actuelles</h3>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <p class="text-xs text-brand-700 mb-1">Patient</p>
            <p class="font-medium text-slate-900">${rendezVous.patient.prenom} ${rendezVous.patient.nom}</p>
            <p class="text-sm text-slate-500">${rendezVous.patient.email}</p>
          </div>
          <div>
            <p class="text-xs text-brand-700 mb-1">Médecin</p>
            <p class="font-medium text-slate-900">Dr. ${rendezVous.medecin.prenom} ${rendezVous.medecin.nom}</p>
            <p class="text-sm text-slate-500">${rendezVous.medecin.specialite}</p>
          </div>
          <div>
            <p class="text-xs text-brand-700 mb-1">Date</p>
            <p class="font-medium text-slate-900">${rendezVous.dateFormatted}</p>
          </div>
          <div>
            <p class="text-xs text-brand-700 mb-1">Statut</p>
            <span class="inline-flex items-center rounded-full bg-white px-2.5 py-0.5 text-xs font-medium text-slate-700 shadow-sm ring-1 ring-inset ring-slate-200">
              ${rendezVous.statut.name()}
            </span>
          </div>
        </div>
      </div>

      <form method="post" action="${pageContext.request.contextPath}/rendezvous/edit" class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <input type="hidden" name="id" value="${rendezVous.id}">
        
        <div class="p-6 sm:p-8 space-y-6">
          
          <div>
            <label for="dateRendezVous" class="block text-sm font-medium leading-6 text-slate-900">Nouvelle Date et Heure <span class="text-rose-500">*</span></label>
            <div class="mt-2 relative">
              <input type="datetime-local" step="1800" name="dateRendezVous" id="dateRendezVous" value="${rendezVous.dateForInput}" required class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
            </div>
          </div>

          <div>
            <label for="motif" class="block text-sm font-medium leading-6 text-slate-900">Motif <span class="text-rose-500">*</span></label>
            <div class="mt-2">
              <textarea name="motif" id="motif" rows="3" required class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">${rendezVous.motif}</textarea>
            </div>
          </div>

        </div>
        
        <div class="bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-end gap-3">
          <a href="${pageContext.request.contextPath}/rendezvous" class="text-sm font-semibold leading-6 text-slate-900 hover:text-slate-700 px-3 py-2">Annuler</a>
          <button type="submit" class="rounded-xl bg-brand-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
            <i class="ph ph-check-circle text-lg"></i> Mettre à jour
          </button>
        </div>
      </form>

    </c:when>
    <c:otherwise>
      <div class="rounded-md bg-rose-50 p-4 border border-rose-200 mb-6">
        <div class="flex">
          <div class="flex-shrink-0"><i class="ph ph-warning-circle text-rose-500 text-lg"></i></div>
          <div class="ml-3"><p class="text-sm font-medium text-rose-800">Rendez-vous introuvable.</p></div>
        </div>
      </div>
      <a href="${pageContext.request.contextPath}/rendezvous" class="btn btn-secondary">← Retour à la liste</a>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="../layout/footer.jsp"/>
