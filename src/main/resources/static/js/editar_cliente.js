(function () {
  'use strict';

  var form = document.querySelector('form[action*="actualizar"]');
  var nombreInput = form && form.querySelector('[name="nombre"]');
  var docInput = form && form.querySelector('[name="documento"]');
  var telInput = form && form.querySelector('[name="telefono"]');
  var correoInput = form && form.querySelector('[name="correo"]');
  var submitBtn = form && form.querySelector('.btn-primary[type="submit"]');

  if (!form) return;

  function validEmail(v) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v); }

  form.addEventListener('submit', function (e) {
    var valid = true;

    if (nombreInput && !nombreInput.value.trim()) {
      e.preventDefault();
      shakeField(nombreInput);
      showError(nombreInput, 'El nombre del cliente es obligatorio.');
      valid = false;
    }

    if (correoInput && correoInput.value.trim() && !validEmail(correoInput.value)) {
      e.preventDefault();
      shakeField(correoInput);
      showError(correoInput, 'Ingresa un correo electrónico válido.');
      valid = false;
    }

    if (valid && submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<span style="opacity:0.7;">Guardando…</span>';
    }
  });

  [nombreInput, docInput, telInput, correoInput].forEach(function (inp) {
    if (!inp) return;
    inp.addEventListener('input', function () { clearError(inp); });
  });

  if (docInput) {
    docInput.addEventListener('blur', function () {
      var v = docInput.value.trim();
      if (v && !/^\d{6,12}$/.test(v)) {
        showHint(docInput, 'Ingresa solo dígitos (6–12 caracteres).');
      } else {
        clearHint(docInput);
      }
    });
    docInput.addEventListener('focus', function () { clearHint(docInput); });
  }

  if (telInput) {
    telInput.addEventListener('blur', function () {
      var v = telInput.value.trim();
      if (v && !/^\+?[\d\s\-().]{7,}$/.test(v)) {
        showHint(telInput, 'Ej: +57 310 000 0000');
      } else {
        clearHint(telInput);
      }
    });
    telInput.addEventListener('focus', function () { clearHint(telInput); });
  }

  function shakeField(inp) {
    inp.classList.remove('ed-shake');
    void inp.offsetWidth;
    inp.classList.add('ed-shake');
    inp.addEventListener('animationend', function () { inp.classList.remove('ed-shake'); }, { once: true });
  }

  function showError(inp, msg) {
    clearError(inp);
    inp.classList.add('ed-input-error');
    var p = document.createElement('p');
    p.className = 'ed-field-msg ed-field-error';
    p.textContent = msg;
    inp.closest('.form-group').appendChild(p);
  }

  function clearError(inp) {
    inp.classList.remove('ed-input-error');
    var g = inp.closest('.form-group');
    if (g) { var e = g.querySelector('.ed-field-error'); if (e) e.remove(); }
  }

  function showHint(inp, msg) {
    clearHint(inp);
    var p = document.createElement('p');
    p.className = 'ed-field-msg ed-field-hint';
    p.textContent = msg;
    inp.closest('.form-group').appendChild(p);
  }

  function clearHint(inp) {
    var g = inp.closest('.form-group');
    if (g) { var h = g.querySelector('.ed-field-hint'); if (h) h.remove(); }
  }

  var s = document.createElement('style');
  s.textContent =
    '@keyframes ed-shake { 0%,100% { transform: translateX(0); } 20% { transform: translateX(-6px); } 40% { transform: translateX(6px); } 60% { transform: translateX(-4px); } 80% { transform: translateX(4px); } } ' +
    '.ed-shake { animation: ed-shake .4s ease; } ' +
    '.ed-input-error { border-color: var(--danger) !important; box-shadow: 0 0 0 4px rgba(239,68,68,.1) !important; } ' +
    '.ed-field-msg { font-size: .75rem; margin-top: 5px; } ' +
    '.ed-field-error { color: var(--danger); } ' +
    '.ed-field-error::before { content: "\u26A0 "; } ' +
    '.ed-field-hint { color: var(--text-muted); }';
  document.head.appendChild(s);
})();
