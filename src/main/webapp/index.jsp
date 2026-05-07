<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="titre" value="Accueil" />
</jsp:include>

<div class="relative overflow-hidden bg-white rounded-2xl shadow-sm border border-slate-200 mb-8">
  <div class="absolute inset-0">
    <div class="absolute inset-y-0 left-0 w-1/2 bg-slate-50 rounded-r-full opacity-50 blur-3xl"></div>
  </div>
  <div class="relative px-6 py-16 sm:px-12 sm:py-24 lg:py-32 lg:px-16 text-center">
    <div class="mx-auto max-w-2xl">
      <div class="flex justify-center items-center gap-2 mb-6">
        <span class="inline-flex items-center rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700 ring-1 ring-inset ring-brand-700/10">JEE 10</span>
        <span class="inline-flex items-center rounded-full bg-slate-100 px-3 py-1 text-sm font-medium text-slate-700 ring-1 ring-inset ring-slate-500/10">RMI Callback</span>
      </div>
      <h1 class="text-4xl font-bold tracking-tight text-slate-900 sm:text-5xl lg:text-6xl mb-6">
        Gestion de Cabinet <span class="text-transparent bg-clip-text bg-gradient-to-r from-brand-600 to-brand-400">Médical</span>
      </h1>
      <p class="mt-4 text-lg leading-8 text-slate-600 mb-10">
        Une plateforme complète de gestion hospitalière combinant la puissance de WildFly, EJB, et Hibernate JPA avec une interface moderne et intuitive.
      </p>

      <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
        <c:choose>
          <c:when test="${sessionScope.role == 'ROLE_PATIENT'}">
            <a href="${pageContext.request.contextPath}/mon-espace" class="rounded-lg bg-brand-600 px-5 py-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
              <i class="ph ph-arrow-right text-lg"></i> Accéder à mon espace
            </a>
          </c:when>
          <c:when test="${sessionScope.role == 'ROLE_ADMIN'}">
            <a href="${pageContext.request.contextPath}/patients" class="rounded-lg bg-brand-600 px-5 py-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
              <i class="ph ph-arrow-right text-lg"></i> Accéder au tableau de bord
            </a>
          </c:when>
          <c:otherwise>
            <a href="${pageContext.request.contextPath}/login" class="rounded-lg bg-brand-600 px-5 py-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all flex items-center gap-2">
              <i class="ph ph-sign-in text-lg"></i> Se connecter
            </a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>

<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
  <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
    <div class="h-12 w-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center mb-4">
      <i class="ph ph-users text-2xl"></i>
    </div>
    <h3 class="text-lg font-semibold text-slate-900 mb-2">Dossiers Patients</h3>
    <p class="text-sm text-slate-500 leading-relaxed">Gérez l'ensemble des informations, antécédents et coordonnées de votre patientèle en un seul endroit.</p>
  </div>
  <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
    <div class="h-12 w-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center mb-4">
      <i class="ph ph-stethoscope text-2xl"></i>
    </div>
    <h3 class="text-lg font-semibold text-slate-900 mb-2">Corps Médical</h3>
    <p class="text-sm text-slate-500 leading-relaxed">Administration des praticiens, avec leurs spécialités respectives et les patients associés.</p>
  </div>
  <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
    <div class="h-12 w-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center mb-4">
      <i class="ph ph-calendar-check text-2xl"></i>
    </div>
    <h3 class="text-lg font-semibold text-slate-900 mb-2">Planification</h3>
    <p class="text-sm text-slate-500 leading-relaxed">Un calendrier interactif pour gérer et suivre l'état de chaque rendez-vous avec précision.</p>
  </div>
  <div class="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
    <div class="h-12 w-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center mb-4">
      <i class="ph ph-bell-ringing text-2xl"></i>
    </div>
    <h3 class="text-lg font-semibold text-slate-900 mb-2">Temps Réel RMI</h3>
    <p class="text-sm text-slate-500 leading-relaxed">Système de notifications instantanées distribuées via RMI Callback pour les clients distants.</p>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
