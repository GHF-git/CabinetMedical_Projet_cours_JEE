<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Mon Espace" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<%-- ── Real-time notification bell in header ─────────────────────────────── --%>
<div id="notif-toast-container"
     style="position:fixed;top:80px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:10px;max-width:380px;"></div>

<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
  <div>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
      <i class="ph ph-hand-waving text-brand-600"></i> Bonjour, ${patient.prenom} !
    </h1>
    <p class="mt-1 text-sm text-slate-500">Bienvenue dans votre espace. Consultations et notifications en temps réel.</p>
  </div>
  <div class="flex items-center gap-3">
    <div id="notif-status" class="flex items-center gap-2 text-xs text-slate-500 bg-slate-100 rounded-full px-3 py-1.5">
      <span id="notif-dot" class="w-2 h-2 rounded-full bg-slate-400"></span>
      <span id="notif-text">Connexion...</span>
    </div>
    <button onclick="toggleNotifPanel()"
            class="relative flex items-center gap-2 bg-brand-600 hover:bg-brand-700 text-white text-sm font-medium px-4 py-2 rounded-xl transition">
      <i class="ph ph-bell text-base"></i>
      <span>Notifications</span>
      <span id="notif-badge"
            class="absolute -top-1.5 -right-1.5 hidden bg-rose-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">0</span>
    </button>
  </div>
</div>

<%-- ── Notification Panel ───────────────────────────────────────────────── --%>
<div id="notif-panel"
     class="hidden mb-6 bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
  <div class="px-6 py-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
    <div class="flex items-center gap-2">
      <i class="ph ph-bell-ringing text-brand-600 text-lg"></i>
      <h3 class="text-sm font-semibold text-slate-900">Notifications en temps réel</h3>
    </div>
    <button onclick="clearNotifications()" class="text-xs text-slate-400 hover:text-slate-600">Tout effacer</button>
  </div>
  <div id="notif-list" class="divide-y divide-slate-100 max-h-80 overflow-y-auto">
    <div class="px-6 py-8 text-center text-slate-400 text-sm" id="notif-empty">
      <i class="ph ph-bell-slash text-3xl mb-2 block opacity-40"></i>
      Aucune notification pour l'instant.
    </div>
  </div>
</div>

<%-- ── Rendez-vous Timeline ─────────────────────────────────────────────── --%>
<div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden mb-8">
  <div class="px-6 py-4 border-b border-slate-200 bg-slate-50 flex items-center gap-2">
    <i class="ph ph-calendar-check text-brand-600 text-lg"></i>
    <h3 class="text-sm font-semibold text-slate-900">Mes Rendez-vous</h3>
    <span class="ml-auto text-xs text-slate-400">${rendezVousList.size()} consultation(s)</span>
  </div>
  <div class="p-6">
    <c:choose>
      <c:when test="${empty rendezVousList}">
        <div class="py-12 text-center text-slate-400">
          <i class="ph ph-calendar-blank text-5xl mb-3 block opacity-50"></i>
          <p class="text-base font-medium text-slate-600">Vous n'avez aucun rendez-vous enregistré.</p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="space-y-4">
          <c:forEach var="rdv" items="${rendezVousList}">
            <div class="flex gap-4 items-start p-4 rounded-xl border
              <c:choose>
                <c:when test="${rdv.statut.name() == 'PLANIFIE'}">border-blue-200 bg-blue-50/40</c:when>
                <c:when test="${rdv.statut.name() == 'TERMINE'}">border-emerald-200 bg-emerald-50/40</c:when>
                <c:otherwise>border-rose-200 bg-rose-50/40</c:otherwise>
              </c:choose>">
              <div class="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center
                <c:choose>
                  <c:when test="${rdv.statut.name() == 'PLANIFIE'}">bg-blue-100 text-blue-600</c:when>
                  <c:when test="${rdv.statut.name() == 'TERMINE'}">bg-emerald-100 text-emerald-600</c:when>
                  <c:otherwise>bg-rose-100 text-rose-600</c:otherwise>
                </c:choose>">
                <c:choose>
                  <c:when test="${rdv.statut.name() == 'PLANIFIE'}"><i class="ph ph-calendar-plus"></i></c:when>
                  <c:when test="${rdv.statut.name() == 'TERMINE'}"><i class="ph ph-check-circle"></i></c:when>
                  <c:otherwise><i class="ph ph-x-circle"></i></c:otherwise>
                </c:choose>
              </div>
              <div class="flex-1">
                <div class="flex items-center justify-between flex-wrap gap-2">
                  <h4 class="font-semibold text-slate-900 text-sm">
                    Dr. ${rdv.medecin.nom} — ${rdv.medecin.specialite}
                  </h4>
                  <span class="text-xs text-slate-500"><i class="ph ph-clock mr-1"></i>${rdv.dateFormatted}</span>
                </div>
                <p class="text-sm text-slate-600 mt-1">Motif : ${rdv.motif}</p>
                <div class="mt-2">
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
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>

<%-- ── SSE JavaScript ──────────────────────────────────────────────────────── --%>
<script>
(function() {
  const patientId = ${patient.id};
  let unread = 0;
  let notifications = [];

  const dot      = document.getElementById('notif-dot');
  const statusTxt= document.getElementById('notif-text');
  const badge    = document.getElementById('notif-badge');
  const list     = document.getElementById('notif-list');
  const empty    = document.getElementById('notif-empty');
  const toast    = document.getElementById('notif-toast-container');

  // ── SSE Connection ─────────────────────────────────────────────
  function connect() {
    const es = new EventSource('${pageContext.request.contextPath}/notifications/stream');

    es.onopen = function() {
      dot.className = 'w-2 h-2 rounded-full bg-emerald-500';
      statusTxt.textContent = 'Connecté en temps réel';
    };

    es.onmessage = function(e) {
      try {
        const data = JSON.parse(e.data);
        if (data.type === 'connected') return; // skip confirmation
        if (data.type === 'notification') {
          addNotification(data.message, data.time);
        }
      } catch(err) {}
    };

    es.onerror = function() {
      dot.className = 'w-2 h-2 rounded-full bg-rose-500';
      statusTxt.textContent = 'Reconnexion...';
      es.close();
      setTimeout(connect, 3000); // auto-reconnect
    };
  }

  // ── Add notification to panel + show toast ─────────────────────
  function addNotification(message, time) {
    notifications.unshift({message, time});
    unread++;

    // Update badge
    badge.textContent = unread > 9 ? '9+' : unread;
    badge.classList.remove('hidden');
    badge.classList.add('flex');

    // Update panel list
    if (empty) empty.remove();
    const item = document.createElement('div');
    item.className = 'flex gap-3 items-start px-6 py-4 bg-brand-50 border-l-4 border-brand-500 animate-pulse-once';
    item.innerHTML = `
      <div class="flex-shrink-0 w-8 h-8 rounded-full bg-brand-100 flex items-center justify-center text-brand-600">
        <i class="ph ph-bell-ringing text-sm"></i>
      </div>
      <div class="flex-1 min-w-0">
        <p class="text-sm font-medium text-slate-800">${escapeHtml(message)}</p>
        <p class="text-xs text-slate-400 mt-0.5">${escapeHtml(time || new Date().toLocaleString('fr-FR'))}</p>
      </div>`;
    list.prepend(item);

    // Show toast popup
    showToast(message, time);
  }

  // ── Toast popup ────────────────────────────────────────────────
  function showToast(message, time) {
    const t = document.createElement('div');
    t.className = 'bg-white border border-slate-200 rounded-xl shadow-lg p-4 flex gap-3 items-start max-w-sm';
    t.style.cssText = 'animation: slideIn 0.3s ease; min-width:280px;';
    t.innerHTML = `
      <div class="flex-shrink-0 w-9 h-9 rounded-full bg-brand-100 flex items-center justify-center text-brand-600">
        <i class="ph ph-bell-ringing"></i>
      </div>
      <div class="flex-1">
        <p class="text-xs font-semibold text-brand-700 mb-0.5">🔔 Nouvelle notification</p>
        <p class="text-sm text-slate-700">${escapeHtml(message)}</p>
        <p class="text-xs text-slate-400 mt-1">${escapeHtml(time || '')}</p>
      </div>
      <button onclick="this.parentElement.remove()" class="text-slate-300 hover:text-slate-500 ml-1">✕</button>`;
    toast.appendChild(t);
    setTimeout(() => t.remove(), 6000);
  }

  // ── Toggle panel ───────────────────────────────────────────────
  window.toggleNotifPanel = function() {
    const panel = document.getElementById('notif-panel');
    panel.classList.toggle('hidden');
    if (!panel.classList.contains('hidden')) {
      unread = 0;
      badge.classList.add('hidden');
    }
  };

  window.clearNotifications = function() {
    list.innerHTML = '<div class="px-6 py-8 text-center text-slate-400 text-sm"><i class="ph ph-bell-slash text-3xl mb-2 block opacity-40"></i>Aucune notification.</div>';
    notifications = [];
    unread = 0;
    badge.classList.add('hidden');
  };

  function escapeHtml(s) {
    if (!s) return '';
    return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }

  // Inject slide-in animation
  const style = document.createElement('style');
  style.textContent = '@keyframes slideIn{from{opacity:0;transform:translateX(40px)}to{opacity:1;transform:translateX(0)}}';
  document.head.appendChild(style);

  connect();
})();
</script>
