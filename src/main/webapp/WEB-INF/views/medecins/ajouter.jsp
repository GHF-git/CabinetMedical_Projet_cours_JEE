<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Ajouter Médecin" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<div class="max-w-3xl mx-auto">
  <div class="mb-6 flex items-center justify-between">
    <div>
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
        <i class="ph ph-stethoscope text-brand-600"></i> Nouveau Médecin
      </h1>
      <p class="mt-1 text-sm text-slate-500">Ajoutez un nouveau praticien à l'équipe.</p>
    </div>
    <a href="${pageContext.request.contextPath}/medecins" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
      <i class="ph ph-arrow-left mr-2 text-lg"></i> Retour
    </a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/medecins/add" class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
    <div class="p-6 sm:p-8 space-y-6">
      
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div>
          <label for="nom" class="block text-sm font-medium leading-6 text-slate-900">Nom <span class="text-rose-500">*</span></label>
          <div class="mt-2">
            <input type="text" name="nom" id="nom" required autofocus placeholder="Ex: Mansour" class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
          </div>
        </div>
        <div>
          <label for="prenom" class="block text-sm font-medium leading-6 text-slate-900">Prénom <span class="text-rose-500">*</span></label>
          <div class="mt-2">
            <input type="text" name="prenom" id="prenom" required placeholder="Ex: Sami" class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div>
          <label for="specialite" class="block text-sm font-medium leading-6 text-slate-900">Spécialité <span class="text-rose-500">*</span></label>
          <div class="mt-2 relative">
            <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
              <i class="ph ph-first-aid text-slate-400 text-lg"></i>
            </div>
            <input type="text" name="specialite" id="specialite" required placeholder="Ex: Cardiologie" class="block w-full rounded-xl border-0 py-2.5 pl-10 pr-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
          </div>
        </div>
        <div>
          <label for="email" class="block text-sm font-medium leading-6 text-slate-900">Email <span class="text-rose-500">*</span></label>
          <div class="mt-2 relative">
            <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
              <i class="ph ph-envelope-simple text-slate-400 text-lg"></i>
            </div>
            <input type="email" name="email" id="email" required placeholder="dr.exemple@cabinet.tn" class="block w-full rounded-xl border-0 py-2.5 pl-10 pr-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-white outline-none">
          </div>
        </div>
      </div>

    </div>
    
    <div class="bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-end gap-3">
      <a href="${pageContext.request.contextPath}/medecins" class="text-sm font-semibold leading-6 text-slate-900 hover:text-slate-700 px-3 py-2">Annuler</a>
      <button type="submit" class="rounded-xl bg-brand-600 px-6 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
        <i class="ph ph-check-circle text-lg"></i> Enregistrer
      </button>
    </div>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
