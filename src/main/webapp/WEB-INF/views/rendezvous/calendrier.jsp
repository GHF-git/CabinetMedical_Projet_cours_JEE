<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="titre" value="Calendrier des Rendez-vous" scope="request"/>
<jsp:include page="../layout/header.jsp"/>

<!-- FullCalendar Core and Plugins -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>

<div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
  <div>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
      <i class="ph ph-calendar-check text-brand-600"></i> Calendrier des Consultations
    </h1>
    <p class="mt-1 text-sm text-slate-500">Vue interactive de tous vos rendez-vous.</p>
  </div>
  <div class="flex items-center gap-3">
    <a href="${pageContext.request.contextPath}/rendezvous" class="inline-flex items-center justify-center rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-all">
      <i class="ph ph-list mr-2 text-lg"></i> Vue Liste
    </a>
    <a href="${pageContext.request.contextPath}/rendezvous/add" class="inline-flex items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all">
      <i class="ph ph-plus mr-2 text-lg"></i> Nouveau Rendez-vous
    </a>
  </div>
</div>

<div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden p-6">
  <div id="calendar" class="w-full"></div>
</div>

<script id="events-data" type="application/json">
[
  <c:forEach var="rdv" items="${rendezVous}" varStatus="status">
  {
    "id": "${rdv.id}",
    "title": "${rdv.patient.prenom} ${rdv.patient.nom} (Dr. ${rdv.medecin.nom})",
    "start": "${rdv.dateRendezVous}",
    "url": "${pageContext.request.contextPath}/rendezvous/edit?id=${rdv.id}",
    "backgroundColor": <c:choose><c:when test="${rdv.statut.name() == 'PLANIFIE'}">"#6366f1"</c:when><c:when test="${rdv.statut.name() == 'TERMINE'}">"#10b981"</c:when><c:otherwise>"#f43f5e"</c:otherwise></c:choose>,
    "borderColor": "transparent",
    "extendedProps": {
      "motif": "${rdv.motifForJson}"
    }
  }<c:if test="${!status.last}">,</c:if>
  </c:forEach>
]
</script>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: 'timeGridWeek',
      locale: 'fr',
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay'
      },
      buttonText: {
        today: "Aujourd'hui",
        month: 'Mois',
        week: 'Semaine',
        day: 'Jour',
        list: 'Liste'
      },
      slotMinTime: "08:00:00",
      slotMaxTime: "19:00:00",
      hiddenDays: [0], // Cacher le dimanche
      allDaySlot: false,
      slotDuration: '00:30:00',
      defaultTimedEventDuration: '00:30:00',
      forceEventDuration: true,
      events: JSON.parse(document.getElementById('events-data').textContent),
      eventClick: function(info) {
        info.jsEvent.preventDefault(); // Don't let the browser navigate immediately
        
        Swal.fire({
          title: 'Détails du Rendez-vous',
          html: `
            <div class="text-left space-y-3 mt-4">
              <p><strong>Date:</strong> \${info.event.start.toLocaleString('fr-FR')}</p>
              <p><strong>Patient/Médecin:</strong> \${info.event.title}</p>
              <p><strong>Motif:</strong> \${info.event.extendedProps.motif}</p>
            </div>
          `,
          icon: 'info',
          showCancelButton: true,
          confirmButtonText: 'Modifier le RDV',
          cancelButtonText: 'Fermer',
          confirmButtonColor: '#6366f1'
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.href = info.event.url;
          }
        });
      }
    });
    calendar.render();
  });
</script>

<style>
  /* Customize FullCalendar to match Tailwind Theme */
  .fc-theme-standard td, .fc-theme-standard th {
    border-color: #e2e8f0;
  }
  .fc-col-header-cell {
    background-color: #f8fafc;
    padding: 8px 0 !important;
    font-weight: 600;
    color: #475569;
  }
  .fc .fc-button-primary {
    background-color: #ffffff;
    color: #475569;
    border-color: #cbd5e1;
    text-transform: capitalize;
  }
  .fc .fc-button-primary:hover {
    background-color: #f8fafc;
    color: #0f172a;
    border-color: #94a3b8;
  }
  .fc .fc-button-primary:not(:disabled).fc-button-active, .fc .fc-button-primary:not(:disabled):active {
    background-color: #6366f1;
    color: #ffffff;
    border-color: #6366f1;
  }
  .fc-event {
    border-radius: 4px;
    padding: 2px 4px;
    box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
    cursor: pointer;
  }
  .fc .fc-toolbar-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: #0f172a;
  }
</style>

<jsp:include page="../layout/footer.jsp"/>
