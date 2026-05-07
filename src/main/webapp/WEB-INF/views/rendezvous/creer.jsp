<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Nouveau Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="max-w-3xl mx-auto">
  <div class="mb-6 flex items-center justify-between">
    <div>
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
        <i class="ph ph-calendar-plus text-brand-600"></i> Nouveau Rendez-vous
      </h1>
      <p class="mt-1 text-sm text-slate-500">Planifiez une nouvelle consultation.</p>
    </div>
    <a href="${pageContext.request.contextPath}/rendezvous" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
      <i class="ph ph-arrow-left mr-2 text-lg"></i> Retour
    </a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/rendezvous/add" class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
    <div class="p-6 sm:p-8 space-y-6">
      
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div>
          <label for="patientId" class="block text-sm font-medium leading-6 text-slate-900">Patient <span class="text-rose-500">*</span></label>
          <div class="mt-2 relative">
            <select name="patientId" id="patientId" required class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none appearance-none">
              <option value="">— Sélectionner un patient —</option>
              <c:forEach var="patient" items="${patients}">
                <option value="${patient.id}">${patient.prenom} ${patient.nom} — ${patient.email}</option>
              </c:forEach>
            </select>
            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3">
              <i class="ph ph-caret-down text-slate-400"></i>
            </div>
          </div>
        </div>
        <div>
          <label for="medecinId" class="block text-sm font-medium leading-6 text-slate-900">Médecin <span class="text-rose-500">*</span></label>
          <div class="mt-2 relative">
            <select name="medecinId" id="medecinId" required class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none appearance-none">
              <option value="">— Sélectionner un médecin —</option>
              <c:forEach var="medecin" items="${medecins}">
                <option value="${medecin.id}">Dr. ${medecin.prenom} ${medecin.nom} — ${medecin.specialite}</option>
              </c:forEach>
            </select>
            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3">
              <i class="ph ph-caret-down text-slate-400"></i>
            </div>
          </div>
        </div>
      </div>

      <div>
        <label for="dateRendezVous" class="block text-sm font-medium leading-6 text-slate-900">Date et Heure <span class="text-rose-500">*</span></label>
        <div class="mt-2 relative">
          <input type="datetime-local" step="1800" name="dateRendezVous" id="dateRendezVous" required min="${pageContext.request.getAttribute('today')}" class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
        </div>
      </div>

      <div>
        <label for="motif" class="block text-sm font-medium leading-6 text-slate-900">Motif de consultation <span class="text-rose-500">*</span></label>
        <div class="mt-2">
          <textarea name="motif" id="motif" rows="3" required placeholder="Ex: Consultation générale, suivi traitement..." class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none"></textarea>
        </div>
      </div>

    </div>
    
    <div class="bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-end gap-3">
      <a href="${pageContext.request.contextPath}/rendezvous" class="text-sm font-semibold leading-6 text-slate-900 hover:text-slate-700 px-3 py-2">Annuler</a>
      <button type="submit" class="rounded-xl bg-brand-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
        <i class="ph ph-check-circle text-lg"></i> Enregistrer
      </button>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
