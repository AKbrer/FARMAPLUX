/* ============================================================
   FarmaPlus — nuevo-producto.js
   ============================================================ */
(function () {
  'use strict';

  /* ── Upload preview ──────────────────────────────────────── */
  const uploadZone    = document.getElementById('uploadZone');
  const imagenInput   = document.getElementById('imagenInput');
  const uploadPreview = document.getElementById('uploadPreview');

  if (uploadZone && imagenInput) {
    imagenInput.addEventListener('change', function () {
      if (this.files[0]) { showImagePreview(this.files[0]); updateChecklist(); }
    });
    uploadZone.addEventListener('dragover', function (e) {
      e.preventDefault(); uploadZone.classList.add('dragover');
    });
    uploadZone.addEventListener('dragleave', function () {
      uploadZone.classList.remove('dragover');
    });
    uploadZone.addEventListener('drop', function (e) {
      e.preventDefault(); uploadZone.classList.remove('dragover');
      const file = e.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        const dt = new DataTransfer(); dt.items.add(file); imagenInput.files = dt.files;
        showImagePreview(file); updateChecklist();
      }
    });
  }

  function showImagePreview(file) {
    if (file.size > 2 * 1024 * 1024) { showToast('La imagen supera el límite de 2MB', 'error'); return; }
    const reader = new FileReader();
    reader.onload = function (e) {
      uploadZone.querySelectorAll('.upload-img-preview, .upload-img-overlay').forEach(el => el.remove());
      if (uploadPreview) uploadPreview.style.display = 'none';
      const img = document.createElement('img');
      img.className = 'upload-img-preview'; img.src = e.target.result; img.alt = 'Preview';
      const overlay = document.createElement('div');
      overlay.className = 'upload-img-overlay';
      overlay.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg><span>Cambiar imagen</span>`;
      const inp = uploadZone.querySelector('.upload-input');
      uploadZone.insertBefore(img, inp); uploadZone.insertBefore(overlay, inp);
      uploadZone.classList.add('has-image');
    };
    reader.readAsDataURL(file);
  }

  /* ── Checklist de completitud ────────────────────────────── */
  const checkFields = {
    nombre:           () => !!val('nombre'),
    codigoBarras:     () => !!val('codigoBarras'),
    categoria:        () => !!selVal('categoria.id') || !!selVal('categoria'),
    proveedor:        () => !!selVal('proveedor.id')  || !!selVal('proveedor'),
    precioVenta:      () => parseFloat(val('precioVenta')) > 0,
    stock:            () => parseInt(val('stock'), 10) >= 0 && val('stock') !== '',
    precioCompra:     () => parseFloat(val('precioCompra')) > 0,
    fechaVencimiento: () => !!val('fechaVencimiento'),
    imagen:           () => !!(imagenInput && imagenInput.files && imagenInput.files.length > 0),
  };

  function val(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
  }
  function selVal(name) {
    const el = document.querySelector('[name="' + name + '"]') ||
               document.querySelector('[id="' + name.replace('.', '\\.') + '"]');
    return el ? el.value : '';
  }

  function updateChecklist() {
    const items = document.querySelectorAll('.check-item');
    let done = 0;
    items.forEach(function (item) {
      const field = item.dataset.field;
      const isDone = checkFields[field] ? checkFields[field]() : false;
      item.classList.toggle('done', isDone);
      if (isDone) done++;
    });
    const pct = Math.round((done / items.length) * 100);
    const fill = document.getElementById('completenessFill');
    const pctEl = document.getElementById('completenessPct');
    if (fill)  fill.style.width = pct + '%';
    if (pctEl) pctEl.textContent = pct + '%';
    checkLiveAlerts();
  }

  /* ── Alertas en vivo ─────────────────────────────────────── */
  function checkLiveAlerts() {
    // Alerta fecha vencimiento
    const fechaVal = val('fechaVencimiento');
    const alertVenc = document.getElementById('alertVencimiento');
    const alertVencMsg = document.getElementById('alertVencimientoMsg');
    if (alertVenc && fechaVal) {
      const fecha = new Date(fechaVal);
      const hoy   = new Date();
      hoy.setHours(0,0,0,0);
      const diff  = Math.ceil((fecha - hoy) / (1000 * 60 * 60 * 24));
      if (diff < 0) {
        alertVencMsg.textContent = 'La fecha ingresada ya venció. No registres productos vencidos.';
        alertVenc.style.display = 'flex';
      } else if (diff <= 30) {
        alertVencMsg.textContent = 'Este producto vence en ' + diff + ' día(s). Considera si es conveniente ingresarlo.';
        alertVenc.style.display = 'flex';
      } else {
        alertVenc.style.display = 'none';
      }
    } else if (alertVenc) {
      alertVenc.style.display = 'none';
    }

    // Alerta margen negativo
    const pc   = parseFloat(document.getElementById('precioCompra')  ? document.getElementById('precioCompra').value  : 0) || 0;
    const pv   = parseFloat(document.getElementById('precioVenta')   ? document.getElementById('precioVenta').value   : 0) || 0;
    const alertMargen = document.getElementById('alertMargen');
    if (alertMargen) {
      alertMargen.style.display = (pc > 0 && pv > 0 && pv < pc) ? 'flex' : 'none';
    }
  }

  /* ── Escuchar cambios en todos los campos ────────────────── */
  const form = document.getElementById('productoForm');
  if (form) {
    form.querySelectorAll('input, select').forEach(function (el) {
      el.addEventListener('input',  updateChecklist);
      el.addEventListener('change', updateChecklist);
    });
  }
  updateChecklist(); // estado inicial

  /* ── Animación de entrada escalonada ─────────────────────── */
  document.querySelectorAll('.form-group, .form-row').forEach(function (el, i) {
    el.style.opacity = '0';
    el.style.transform = 'translateY(8px)';
    el.style.transition = 'opacity .25s ease, transform .25s ease';
    el.style.transitionDelay = (i * 35) + 'ms';
    requestAnimationFrame(function () {
      requestAnimationFrame(function () {
        el.style.opacity = '1';
        el.style.transform = 'translateY(0)';
      });
    });
  });

  /* ── Validación submit ───────────────────────────────────── */
  if (form) {
    form.addEventListener('submit', function (e) {
      let hasError = false;
      form.querySelectorAll('.input-error').forEach(el => el.remove());
      form.querySelectorAll('.form-input--error').forEach(el => el.classList.remove('form-input--error'));

      form.querySelectorAll('[required]').forEach(function (input) {
        if (!input.value.trim()) { hasError = true; markError(input, 'Este campo es requerido'); }
      });

      if (hasError) {
        e.preventDefault();
        const first = form.querySelector('.form-input--error');
        if (first) { first.scrollIntoView({ behavior: 'smooth', block: 'center' }); first.focus(); }
        showToast('Completa los campos requeridos', 'error');
      } else {
        const btn = form.querySelector('.btn-primary');
        if (btn) {
          btn.disabled = true;
          btn.innerHTML = `<svg class="spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>Guardando...`;
        }
      }
    });
  }

  function markError(input, message) {
    input.classList.add('form-input--error');
    const wrap = input.closest('.form-group');
    if (wrap) {
      const msg = document.createElement('span');
      msg.className = 'input-error'; msg.textContent = message;
      wrap.appendChild(msg);
    }
  }

  /* ── Toast ───────────────────────────────────────────────── */
  function showToast(message, type) {
    const existing = document.querySelector('.fp-toast');
    if (existing) existing.remove();
    const toast = document.createElement('div');
    toast.className = 'fp-toast fp-toast--' + (type || 'info');
    toast.innerHTML = `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${type === 'error' ? '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>' : '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>'}
    </svg><span>${message}</span>`;
    Object.assign(toast.style, {
      position:'fixed', bottom:'24px', right:'24px',
      display:'flex', alignItems:'center', gap:'10px',
      padding:'11px 16px', borderRadius:'10px',
      fontSize:'13px', fontWeight:'600',
      fontFamily:'var(--font-sans, sans-serif)',
      zIndex:'9999',
      boxShadow:'0 8px 24px rgba(0,0,0,.12)',
      animation:'toastIn .25s ease',
      background: type === 'error' ? '#fef2f2' : '#f0fdf4',
      color:      type === 'error' ? '#dc2626'  : '#059669',
      border:     type === 'error' ? '1px solid #fecaca' : '1px solid #a7f3d0',
    });
    document.body.appendChild(toast);
    setTimeout(function () {
      toast.style.animation = 'toastOut .25s ease forwards';
      setTimeout(function () { toast.remove(); }, 260);
    }, 3500);
  }

  window.FarmaPlus = { showToast: showToast };
})();