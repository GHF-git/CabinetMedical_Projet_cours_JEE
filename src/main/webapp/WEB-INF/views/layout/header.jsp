<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr" class="h-full bg-slate-50">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:if test="${not empty titre}">${titre} — </c:if>Cabinet Médical</title>
  
  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  
  <!-- Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      theme: {
        extend: {
          fontFamily: {
            sans: ['Inter', 'sans-serif'],
          },
          colors: {
            brand: {
              50: '#eef2ff',
              100: '#e0e7ff',
              500: '#6366f1',
              600: '#4f46e5',
              700: '#4338ca',
            }
          }
        }
      }
    }
  </script>

  <!-- SweetAlert2 -->
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

  <!-- Phosphor Icons (Sleek icons) -->
  <script src="https://unpkg.com/@phosphor-icons/web"></script>

  <style>
    /* Custom simple overrides */
    body { -webkit-font-smoothing: antialiased; }
    /* Hide default scrollbar for sleekness */
    ::-webkit-scrollbar { width: 8px; height: 8px; }
    ::-webkit-scrollbar-track { background: transparent; }
    ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
    ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
  </style>
</head>
<body class="h-full flex flex-col">

<!-- Navigation -->
<nav class="bg-white border-b border-slate-200 sticky top-0 z-50">
  <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
    <div class="flex h-16 justify-between">
      <div class="flex">
        <div class="flex flex-shrink-0 items-center gap-2">
          <div class="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-sm">
            <i class="ph ph-heartbeat text-xl"></i>
          </div>
          <a href="${pageContext.request.contextPath}/" class="text-lg font-bold tracking-tight text-slate-900">Cabinet Médical</a>
        </div>
        <div class="hidden sm:ml-8 sm:flex sm:space-x-2">
          <c:choose>
            <c:when test="${sessionScope.role == 'ROLE_PATIENT'}">
              <a href="${pageContext.request.contextPath}/" class="inline-flex items-center px-3 py-2 mt-3 mb-3 rounded-md text-sm font-medium transition-colors bg-brand-50 text-brand-700">
                <i class="ph ph-house mr-2 text-lg"></i> Mon Espace
              </a>
            </c:when>
            <c:otherwise>
              <a href="${pageContext.request.contextPath}/patients" class="inline-flex items-center px-3 py-2 mt-3 mb-3 rounded-md text-sm font-medium transition-colors <c:choose><c:when test="${pageContext.request.requestURI.contains('/patients')}">bg-brand-50 text-brand-700</c:when><c:otherwise>text-slate-600 hover:bg-slate-50 hover:text-slate-900</c:otherwise></c:choose>">
                <i class="ph ph-users mr-2 text-lg"></i> Patients
              </a>
              <a href="${pageContext.request.contextPath}/medecins" class="inline-flex items-center px-3 py-2 mt-3 mb-3 rounded-md text-sm font-medium transition-colors <c:choose><c:when test="${pageContext.request.requestURI.contains('/medecins')}">bg-brand-50 text-brand-700</c:when><c:otherwise>text-slate-600 hover:bg-slate-50 hover:text-slate-900</c:otherwise></c:choose>">
                <i class="ph ph-stethoscope mr-2 text-lg"></i> Médecins
              </a>
              <a href="${pageContext.request.contextPath}/rendezvous" class="inline-flex items-center px-3 py-2 mt-3 mb-3 rounded-md text-sm font-medium transition-colors <c:choose><c:when test="${pageContext.request.requestURI.contains('/rendezvous')}">bg-brand-50 text-brand-700</c:when><c:otherwise>text-slate-600 hover:bg-slate-50 hover:text-slate-900</c:otherwise></c:choose>">
                <i class="ph ph-calendar-blank mr-2 text-lg"></i> Rendez-vous
              </a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
      <div class="hidden sm:ml-6 sm:flex sm:items-center">
        <c:if test="${not empty sessionScope.authenticated and sessionScope.authenticated}">
          <div class="flex items-center gap-4 pl-6 border-l border-slate-200">
            <div class="flex items-center gap-2">
              <div class="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-600 font-medium text-xs">
                <i class="ph ph-user"></i>
              </div>
              <span class="text-sm font-medium text-slate-700">${sessionScope.username}</span>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="rounded-md bg-white px-3 py-2 text-sm font-semibold text-rose-600 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-rose-50 transition-colors">
              Déconnexion
            </a>
          </div>
        </c:if>
      </div>
    </div>
  </div>
</nav>

<!-- Main Content Wrapper -->
<main class="flex-grow">
  <div class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
    
    <!-- Flash Messages Handler via SweetAlert2 -->
    <c:if test="${not empty message}">
      <script>
        document.addEventListener("DOMContentLoaded", () => {
          const Toast = Swal.mixin({
            toast: true, position: 'bottom-end', showConfirmButton: false, timer: 3500, timerProgressBar: true,
            didOpen: (toast) => { toast.addEventListener('mouseenter', Swal.stopTimer); toast.addEventListener('mouseleave', Swal.resumeTimer); }
          });
          Toast.fire({ icon: 'success', title: '${message}' });
        });
      </script>
    </c:if>
    <c:if test="${not empty erreur}">
      <script>
        document.addEventListener("DOMContentLoaded", () => {
          const Toast = Swal.mixin({
            toast: true, position: 'bottom-end', showConfirmButton: false, timer: 4000, timerProgressBar: true,
          });
          Toast.fire({ icon: 'error', title: '${erreur}' });
        });
      </script>
    </c:if>
