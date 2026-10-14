(function () {
  'use strict';

  var form = document.querySelector('form[action*="actualizar"]');
  var nombreInput = form && form.querySelector('[name="nombre"]');
  var correoInput = form && form.querySelector('[name="correo"]');
  var submitBtn = form && form.querySelector('.btn-primary[type="submit"]');

  if (!form) return;

  function validEmail(v) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v); }

  form.addEventListener('submit', function (e) {
    var valid = true;

    if (nombreInput && !nombreInput.value.trim()) {
      e.preventDefault();
      shakeField(nombreInput);
      showError(nombreInput, 'El nombre del usuario es obligatorio.');
      valid = false;
    }

    if (correoInput && !correoInput.value.trim()) {
      e.preventDefault();
      shakeField(correoInput);
      showError(correoInput, 'El correo electrónico es obligatorio.');
      valid = false;
    } else if (correoInput && !validEmail(correoInput.value)) {
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

  [nombreInput, correoInput].forEach(function (inp) {
    if (!inp) return;
    inp.addEventListener('input', function () { clearError(inp); });
  });

  function shakeField(inp) {
    inp.classList.remove('eu-shake');
    void inp.offsetWidth;
    inp.classList.add('eu-shake');
    inp.addEventListener('animationend', function () { inp.classList.remove('eu-shake'); }, { once: true });
  }

  function showError(inp, msg) {
    clearError(inp);
    inp.classList.add('eu-input-error');
    var p = document.createElement('p');
    p.className = 'eu-field-msg eu-field-error';
    p.textContent = msg;
    inp.closest('.form-group').appendChild(p);
  }

  function clearError(inp) {
    inp.classList.remove('eu-input-error');
    var g = inp.closest('.form-group');
    if (g) { var e = g.querySelector('.eu-field-error'); if (e) e.remove(); }
  }

  var s = document.createElement('style');
  s.textContent =
    '@keyframes eu-shake { 0%,100% { transform: translateX(0); } 20% { transform: translateX(-6px); } 40% { transform: translateX(6px); } 60% { transform: translateX(-4px); } 80% { transform: translateX(4px); } } ' +
    '.eu-shake { animation: eu-shake .4s ease; } ' +
    '.eu-input-error { border-color: var(--danger) !important; box-shadow: 0 0 0 4px rgba(239,68,68,.1) !important; } ' +
    '.eu-field-msg { font-size: .75rem; margin-top: 5px; } ' +
    '.eu-field-error { color: var(--danger); } ' +
    '.eu-field-error::before { content: "\u26A0 "; }';
  document.head.appendChild(s);
})();
