/* ═══════════════════════════════════════════════
   FarmaPlus — Nueva Venta · Script
   Lógica visual: totales, filas, filtros, cambio
═══════════════════════════════════════════════ */

'use strict';

// ── Estado ────────────────────────────────────
const NV = {
  rowCount: 1,
  total: 0,
  itemCount: 0,
};

// ── Helpers ───────────────────────────────────
function fmt(n) {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(n);
}

function getProductData(selectEl) {
  const opt = selectEl.options[selectEl.selectedIndex];
  return {
    precio: parseFloat(opt?.dataset.precio || '0'),
    stock:  parseInt(opt?.dataset.stock  || '0', 10),
    nombre: opt?.text || '',
  };
}

// ── Recalcular total ──────────────────────────
function recalcularTotal() {
  let total = 0;
  let items = 0;

  document.querySelectorAll('.nv-product-row').forEach(row => {
    const sel = row.querySelector('select[name="productoId"]');
    const qty = parseInt(row.querySelector('input[name="cantidad"]')?.value || '0', 10);
    const { precio } = getProductData(sel);
    const sub = precio * qty;

    row.querySelector('.nv-unit-price').textContent = precio > 0 ? `$${fmt(precio)}` : '—';
    row.querySelector('.nv-row-subtotal').textContent = sub > 0 ? `$${fmt(sub)}` : '—';

    total += sub;
    if (sel.value) items += qty;
  });

  NV.total = total;
  NV.itemCount = items;

  // Actualizar panel
  document.getElementById('nv-total-amount').textContent = `$${fmt(total)}`;
  document.getElementById('nv-total-items').textContent =
    items === 0 ? 'Sin productos' : `${items} unidad${items !== 1 ? 'es' : ''}`;

  // Breakdown IVA (19 %)
  const iva = total * 0.19 / 1.19;
  const subtotal = total - iva;
  document.getElementById('nv-breakdown-sub').textContent = `$${fmt(subtotal)}`;
  document.getElementById('nv-breakdown-iva').textContent = `$${fmt(iva)}`;
  document.getElementById('nv-breakdown-total').textContent = `$${fmt(total)}`;

  // Actualizar cambio si aplica
  actualizarCambio();
}

// ── Calcular cambio ───────────────────────────
function actualizarCambio() {
  const monto  = parseFloat(document.getElementById('nv-monto-pagado')?.value || '0');
  const box    = document.getElementById('nv-cambio-box');
  if (!box) return;

  const metodoPago = document.querySelector('input[name="metodoPago"]:checked')?.value;
  if (metodoPago !== 'EFECTIVO') { box.className = 'nv-cambio-box'; return; }
  if (!monto || isNaN(monto)) { box.className = 'nv-cambio-box'; return; }

  const cambio = monto - NV.total;
  box.classList.add('show');

  if (cambio >= 0) {
    box.className = 'nv-cambio-box show ok';
    box.innerHTML = `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
      </svg>
      Cambio: <strong>$${fmt(cambio)}</strong>`;
  } else {
    box.className = 'nv-cambio-box show err';
    box.innerHTML = `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/>
      </svg>
      Faltan: <strong>$${fmt(Math.abs(cambio))}</strong>`;
  }
}

// ── Método de pago → mostrar/ocultar campo ────
function togglePago() {
  const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value;
  const efDiv  = document.getElementById('nv-efectivo-fields');
  if (!efDiv) return;

  if (metodo === 'EFECTIVO') {
    efDiv.style.display = 'block';
    efDiv.style.animation = 'fadeIn .25s ease both';
  } else {
    efDiv.style.display = 'none';
    document.getElementById('nv-cambio-box').className = 'nv-cambio-box';
  }
}

// ── Agregar fila producto ─────────────────────
function agregarProducto() {
  const tbody    = document.getElementById('nv-products-tbody');
  const template = document.getElementById('nv-product-row-template');
  if (!template || !tbody) return;

  NV.rowCount++;
  const clone = template.content.cloneNode(true);
  const row   = clone.querySelector('.nv-product-row');
  row.dataset.row = NV.rowCount;

  // Clonar opciones del primer select
  const firstSelect = document.querySelector('.nv-product-row select[name="productoId"]');
  const newSelect   = row.querySelector('select[name="productoId"]');
  if (firstSelect && newSelect) {
    newSelect.innerHTML = firstSelect.innerHTML;
    newSelect.selectedIndex = 0;
  }

  // Eventos de la nueva fila
  newSelect?.addEventListener('change', recalcularTotal);
  row.querySelector('input[name="cantidad"]')?.addEventListener('input', recalcularTotal);

  tbody.appendChild(clone);
  actualizarBtnRemove();
  recalcularTotal();

  // Scroll suave a la fila nueva
  row.scrollIntoView?.({ behavior: 'smooth', block: 'nearest' });
}

// ── Quitar fila ───────────────────────────────
function quitarProducto(btn) {
  const row = btn.closest('.nv-product-row');
  if (!row) return;
  if (document.querySelectorAll('.nv-product-row').length <= 1) return; // mínimo 1

  row.style.animation = 'fadeOut .2s ease forwards';
  setTimeout(() => { row.remove(); actualizarBtnRemove(); recalcularTotal(); }, 200);
}

function actualizarBtnRemove() {
  const filas = document.querySelectorAll('.nv-product-row');
  filas.forEach(r => {
    const btn = r.querySelector('.nv-btn-remove');
    if (btn) btn.disabled = filas.length <= 1;
  });
}

// ── Filtrar clientes ──────────────────────────
function filtrarClientes() {
  const q   = document.getElementById('nv-cliente-search')?.value.toLowerCase().trim() || '';
  const sel = document.getElementById('nv-cliente-select');
  if (!sel) return;

  Array.from(sel.options).forEach(opt => {
    if (!opt.value) { opt.style.display = ''; return; }
    const hay = (opt.dataset.search || '').includes(q);
    opt.style.display = hay ? '' : 'none';
  });
}

// ── Filtrar productos en selects ──────────────
function filtrarProductos() {
  const q = document.getElementById('nv-producto-search')?.value.toLowerCase().trim() || '';
  document.querySelectorAll('.nv-product-row select[name="productoId"]').forEach(sel => {
    Array.from(sel.options).forEach(opt => {
      if (!opt.value) { opt.style.display = ''; return; }
      const hay = (opt.dataset.search || '').includes(q);
      opt.style.display = hay ? '' : 'none';
    });
  });
}

// ── Step progress ─────────────────────────────
function actualizarSteps() {
  const clienteOk = !!document.getElementById('nv-cliente-select')?.value;
  const productosOk = Array.from(document.querySelectorAll('.nv-product-row select[name="productoId"]'))
    .some(s => !!s.value);

  setStep('step-1', clienteOk);
  setStep('step-2', productosOk && clienteOk);
}

function setStep(id, done) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.toggle('done', done);
  if (done) el.classList.remove('active');
  else el.classList.add('active');
}

// ── Validar antes de submit ───────────────────
function validarFormulario() {
  const clienteId = document.getElementById('nv-cliente-select')?.value;
  if (!clienteId) {
    mostrarToast('Seleccione un cliente antes de continuar', 'error');
    document.getElementById('nv-cliente-select')?.focus();
    return false;
  }

  const alguno = Array.from(document.querySelectorAll('.nv-product-row select[name="productoId"]'))
    .some(s => !!s.value);
  if (!alguno) {
    mostrarToast('Agregue al menos un producto', 'error');
    return false;
  }

  if (NV.total <= 0) {
    mostrarToast('El total debe ser mayor a cero', 'error');
    return false;
  }

  const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value;
  if (metodo === 'EFECTIVO') {
    const monto = parseFloat(document.getElementById('nv-monto-pagado')?.value || '0');
    if (monto < NV.total) {
      mostrarToast('El monto pagado es insuficiente', 'error');
      return false;
    }
  }

  // Loading state
  const btn = document.getElementById('nv-btn-submit');
  if (btn) { btn.classList.add('loading'); btn.disabled = true; }
  return true;
}

// ── Toast simple ──────────────────────────────
function mostrarToast(msg, tipo = 'info') {
  let c = document.getElementById('nv-toast-container');
  if (!c) {
    c = document.createElement('div');
    c.id = 'nv-toast-container';
    Object.assign(c.style, {
      position: 'fixed', top: '20px', right: '20px', zIndex: '9999',
      display: 'flex', flexDirection: 'column', gap: '8px',
    });
    document.body.appendChild(c);
  }

  const t = document.createElement('div');
  const color = tipo === 'error' ? '#ef4444' : tipo === 'success' ? '#22a04d' : '#6b706b';
  Object.assign(t.style, {
    background: 'white', border: `1.5px solid ${color}`, borderLeft: `4px solid ${color}`,
    borderRadius: '10px', padding: '12px 18px', fontSize: '.9rem', fontWeight: '500',
    color: '#181a18', maxWidth: '320px', boxShadow: '0 4px 20px rgba(0,0,0,.12)',
    animation: 'fadeIn .25s ease both', fontFamily: "'DM Sans', sans-serif",
  });
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; t.style.transition = 'opacity .3s'; setTimeout(() => t.remove(), 300); }, 3000);
}

// ── Fade out keyframe (inyectar) ──────────────
(function injectFadeOut() {
  if (document.getElementById('nv-extra-kf')) return;
  const s = document.createElement('style');
  s.id = 'nv-extra-kf';
  s.textContent = `
    @keyframes fadeOut {
      to { opacity: 0; transform: translateX(10px); }
    }
  `;
  document.head.appendChild(s);
})();

// ── Init ──────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  // Eventos primer select/qty
  document.querySelector('.nv-product-row select[name="productoId"]')
    ?.addEventListener('change', () => { recalcularTotal(); actualizarSteps(); });
  document.querySelector('.nv-product-row input[name="cantidad"]')
    ?.addEventListener('input', recalcularTotal);

  // Monto pagado
  document.getElementById('nv-monto-pagado')
    ?.addEventListener('input', actualizarCambio);

  // Cliente
  document.getElementById('nv-cliente-select')
    ?.addEventListener('change', actualizarSteps);

  // Pagos radio
  document.querySelectorAll('input[name="metodoPago"]')
    .forEach(r => r.addEventListener('change', () => { togglePago(); actualizarCambio(); }));

  // Init estado
  togglePago();
  recalcularTotal();
  actualizarBtnRemove();
});