/* =========================================================
   nuevo_proveedor.js
   Lógica específica del formulario Nuevo Proveedor.
   No reemplaza app.js — se carga además de él.
   ========================================================= */

(function () {
  'use strict';

  const form     = document.querySelector('form[action*="guardar"]');
  const nombreInput = form && form.querySelector('input[name="nombre"]');
  const telefonoInput = form && form.querySelector('input[name="telefono"]');
  const emailInput  = form && form.querySelector('input[name="email"]');
  const submitBtn   = form && form.querySelector('.btn-primary[type="submit"]');

  if (!form) return;

  /* ── 1. Validación antes de enviar ── */
  form.addEventListener('submit', function (e) {
    let valid = true;

    if (nombreInput && !nombreInput.value.trim()) {
      e.preventDefault();
      shakeField(nombreInput);
      showFieldError(nombreInput, 'El nombre de la empresa es obligatorio.');
      valid = false;
    }

    if (emailInput && emailInput.value.trim() && !isValidEmail(emailInput.value)) {
      e.preventDefault();
      shakeField(emailInput);
      showFieldError(emailInput, 'Ingresa un correo electrónico válido.');
      valid = false;
    }

    if (valid && submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerHTML = `
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
             style="width:18px;height:18px;animation:np-spin .7s linear infinite;flex-shrink:0">
          <circle cx="12" cy="12" r="10" stroke-opacity=".25"/>
          <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round"/>
        </svg>
        Guardando…
      `;
    }
  });

  /* ── 2. Limpiar error al escribir ── */
  [nombreInput, telefonoInput, emailInput].forEach(function (input) {
    if (!input) return;
    input.addEventListener('input', function () {
      clearFieldError(input);
    });
  });

  /* ── 3. Contador de caracteres en Dirección ── */
  const textarea = form.querySelector('.form-textarea');
  if (textarea) {
    const counter = document.createElement('span');
    counter.className = 'np-char-counter';
    counter.textContent = '0 / 300';
    textarea.parentNode.appendChild(counter);

    textarea.addEventListener('input', function () {
      const len = textarea.value.length;
      counter.textContent = len + ' / 300';
      counter.style.color = len > 280 ? 'var(--danger)' : 'var(--text-muted)';
      if (len > 300) textarea.value = textarea.value.slice(0, 300);
    });
  }

  /* ── 4. Sugerencias de formato para Teléfono ── */
  if (telefonoInput) {
    telefonoInput.addEventListener('blur', function () {
      const val = telefonoInput.value.trim();
      if (val && !/^\+?[\d\s\-().]{7,}$/.test(val)) {
        showFieldHint(telefonoInput, 'Ej: +57 310 000 0000');
      } else {
        clearFieldHint(telefonoInput);
      }
    });
    telefonoInput.addEventListener('focus', function () {
      clearFieldHint(telefonoInput);
    });
  }

  /* ── 5. Highlight del campo activo ── */
  form.querySelectorAll('.form-input, .form-textarea').forEach(function (el) {
    el.addEventListener('focus', function () {
      el.closest('.form-group') && el.closest('.form-group').classList.add('np-focused');
    });
    el.addEventListener('blur', function () {
      el.closest('.form-group') && el.closest('.form-group').classList.remove('np-focused');
    });
  });

  /* ──────────────────────────────────────────
     Helpers
  ────────────────────────────────────────── */
  function isValidEmail(val) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val);
  }

  function shakeField(input) {
    const wrap = input.closest('.form-input-wrap') || input;
    wrap.classList.remove('np-shake');
    void wrap.offsetWidth;
    wrap.classList.add('np-shake');
    wrap.addEventListener('animationend', function () {
      wrap.classList.remove('np-shake');
    }, { once: true });
  }

  function showFieldError(input, msg) {
    clearFieldError(input);
    input.classList.add('np-input-error');
    const err = document.createElement('p');
    err.className = 'np-field-error';
    err.textContent = msg;
    input.closest('.form-group').appendChild(err);
  }

  function clearFieldError(input) {
    input.classList.remove('np-input-error');
    const group = input.closest('.form-group');
    if (group) {
      const err = group.querySelector('.np-field-error');
      if (err) err.remove();
    }
  }

  function showFieldHint(input, msg) {
    clearFieldHint(input);
    const hint = document.createElement('p');
    hint.className = 'np-field-hint';
    hint.textContent = msg;
    input.closest('.form-group').appendChild(hint);
  }

  function clearFieldHint(input) {
    const group = input.closest('.form-group');
    if (group) {
      const hint = group.querySelector('.np-field-hint');
      if (hint) hint.remove();
    }
  }

  /* ── Keyframes dinámicos ── */
  const style = document.createElement('style');
  style.textContent = `
    @keyframes np-spin {
      to { transform: rotate(360deg); }
    }
    @keyframes np-shake {
      0%,100% { transform: translateX(0); }
      20%      { transform: translateX(-6px); }
      40%      { transform: translateX(6px); }
      60%      { transform: translateX(-4px); }
      80%      { transform: translateX(4px); }
    }
    .np-shake { animation: np-shake .4s ease; }

    .np-input-error {
      border-color: var(--danger) !important;
      box-shadow: 0 0 0 4px rgba(239,68,68,.1) !important;
    }
    .np-field-error {
      font-size: .75rem;
      color: var(--danger);
      margin-top: 5px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
    .np-field-error::before {
      content: '⚠';
      font-size: .7rem;
    }
    .np-field-hint {
      font-size: .75rem;
      color: var(--text-muted);
      margin-top: 5px;
    }
    .np-char-counter {
      display: block;
      font-size: .72rem;
      color: var(--text-muted);
      text-align: right;
      margin-top: 4px;
    }
    .np-focused .form-label {
      color: var(--primary);
    }
  `;
  document.head.appendChild(style);

})();