  </div>
</main>

<footer class="mt-auto border-t border-slate-200 bg-white">
  <div class="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
    <div class="flex flex-col items-center justify-center gap-2">
      <div class="flex items-center gap-2 text-slate-500">
        <i class="ph ph-stethoscope text-xl"></i>
        <span class="text-sm font-semibold">Système de Gestion de Cabinet Médical</span>
      </div>
      <p class="text-center text-xs leading-5 text-slate-400">
        &copy; 2025-2026 Institut Supérieur d'Informatique et de Multimédia de Sfax (ISIMS) — Filière P-IINFO. Tous droits réservés.
      </p>
    </div>
  </div>
</footer>

<!-- Delete Confirmation Script -->
<script>
  function confirmDelete(formElement, textMessage) {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: textMessage || "Cette action est irréversible !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#94a3b8',
      confirmButtonText: 'Oui, supprimer !',
      cancelButtonText: 'Annuler',
      background: '#ffffff',
      borderRadius: '12px',
      customClass: {
        confirmButton: 'rounded-lg px-4 py-2 font-medium',
        cancelButton: 'rounded-lg px-4 py-2 font-medium'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        formElement.submit();
      }
    });
    return false; // Prevent default form submission
  }
</script>

</body>
</html>
