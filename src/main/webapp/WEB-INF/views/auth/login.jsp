<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr" class="h-full bg-slate-50">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Connexion — Cabinet Médical</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  <script>
    tailwind.config = { theme: { extend: { fontFamily: { sans: ['Inter', 'sans-serif'], }, colors: { brand: { 50: '#eef2ff', 500: '#6366f1', 600: '#4f46e5', 700: '#4338ca', } } } } }
  </script>
</head>
<body class="h-full flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">

<div class="w-full max-w-md space-y-8 bg-white p-10 rounded-3xl shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-slate-100">
  <div>
    <div class="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 shadow-md">
      <i class="ph ph-heartbeat text-3xl text-white"></i>
    </div>
    <h2 class="mt-6 text-center text-3xl font-bold tracking-tight text-slate-900">Bienvenue</h2>
    <p class="mt-2 text-center text-sm text-slate-500">
      Espace sécurisé du cabinet médical
    </p>
  </div>

  <c:if test="${not empty erreur}">
    <div class="rounded-md bg-rose-50 p-4 border border-rose-200">
      <div class="flex">
        <div class="flex-shrink-0"><i class="ph ph-warning-circle text-rose-500 text-lg"></i></div>
        <div class="ml-3"><p class="text-sm font-medium text-rose-800">${erreur}</p></div>
      </div>
    </div>
  </c:if>
  
  <c:if test="${param.logout == 'success'}">
    <div class="rounded-md bg-emerald-50 p-4 border border-emerald-200">
      <div class="flex">
        <div class="flex-shrink-0"><i class="ph ph-check-circle text-emerald-500 text-lg"></i></div>
        <div class="ml-3"><p class="text-sm font-medium text-emerald-800">Vous avez été déconnecté avec succès.</p></div>
      </div>
    </div>
  </c:if>

  <form class="mt-8 space-y-6" action="${pageContext.request.contextPath}/login" method="post">
    <div class="space-y-4 rounded-md shadow-sm">
      <div>
        <label for="username" class="block text-sm font-medium leading-6 text-slate-900">Identifiant</label>
        <div class="mt-2">
          <input id="username" name="username" type="text" required autofocus class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-slate-50 hover:bg-white focus:bg-white outline-none">
        </div>
      </div>
      <div>
        <label for="password" class="block text-sm font-medium leading-6 text-slate-900">Mot de passe</label>
        <div class="mt-2">
          <input id="password" name="password" type="password" required class="block w-full rounded-xl border-0 py-2.5 px-3.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-brand-600 sm:text-sm sm:leading-6 transition-all bg-slate-50 hover:bg-white focus:bg-white outline-none">
        </div>
      </div>
    </div>

    <div>
      <button type="submit" class="flex w-full justify-center rounded-xl bg-brand-600 py-3 px-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 transition-all">
        Se connecter
      </button>
    </div>
  </form>
</div>

</body>
</html>
