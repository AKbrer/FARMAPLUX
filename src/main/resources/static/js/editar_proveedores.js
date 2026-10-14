/* =========================================================
   FarmaPlus – Editar Proveedor · app.js
   ========================================================= */

(function () {
  'use strict';

  /* ── Sidebar toggle (mobile) ── */
  const toggleBtn = document.querySelector('.sidebar-toggle');
  const sidebar   = document.querySelector('.sidebar');

  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });

    // Cerrar al hacer click fuera
    document.addEventListener('click', (e) => {
      if (
        sidebar.classList.contains('open') &&
        !sidebar.contains(e.target) &&
        !toggleBtn.contains(e.target)
      ) {
        sidebar.classList.remove('open');
      }
    });
  }

  /* ── Validación de formulario ── */
  const form = document.getElementById('editarProveedorForm');

  if (form) {
    form.addEventListener('submit', (e) => {
      const nombreInput = form.querySelector('[name="nombre"]') ||
                          form.querySelector('input[type="text"]');

      if (nombreInput && !nombreInput.value.trim()) {
        e.preventDefault();
        nombreInput.focus();
        shakeField(nombreInput);
        return;
      }

      // Feedback visual en botón submit
      const submitBtn = form.querySelector('.btn-primary[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = `
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
               style="width:15px;height:15px;animation:spin .7s linear infinite">
            <path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" opacity=".25"/>
            <path d="M21 12a9 9 0 00-9-9" stroke-linecap="round"/>
          </svg>
          Guardando…
        `;
      }
    });
  }

  /* ── Shake para campo requerido vacío ── */
  function shakeField(el) {
    const wrapper = el.closest('.input-wrapper') || el;
    wrapper.style.animation = 'none';
    wrapper.offsetHeight; // reflow
    wrapper.style.animation = 'shake .4s ease';
    el.style.borderColor = 'var(--danger)';
    el.style.boxShadow  = '0 0 0 3px rgba(239,68,68,.15)';

    el.addEventListener('input', () => {
      el.style.borderColor = '';
      el.style.boxShadow   = '';
    }, { once: true });
  }

  /* ── Spinner keyframe dinámico ── */
  const style = document.createElement('style');
  style.textContent = `
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      20%       { transform: translateX(-5px); }
      40%       { transform: translateX(5px); }
      60%       { transform: translateX(-4px); }
      80%       { transform: translateX(4px); }
    }
  `;
  document.head.appendChild(style);

})();