/* ============================================================
   FarmaPlux — Catálogo JS
   Base: script original funcional
   Mejoras: SVG en botones +/-, toast en vez de alert
   ============================================================ */

let carrito = JSON.parse(localStorage.getItem("carrito") || "{}");

function toggleCart() {
  document.getElementById("cart-sidebar").classList.toggle("open");
  document.getElementById("cart-overlay").classList.toggle("open");
}

function agregarAlCarrito(tarjeta) {
  const id = tarjeta.dataset.id;
  const nombre = tarjeta.dataset.nombre;
  const precio = parseFloat(tarjeta.dataset.precio);
  const stock = parseInt(tarjeta.dataset.stock);

  if (!carrito[id]) {
    carrito[id] = { nombre, precio, cantidad: 0, stock };
  }

  if (carrito[id].cantidad < stock) {
    carrito[id].cantidad++;
  } else {
    mostrarToast("Stock máximo alcanzado para: " + nombre);
    return;
  }

  localStorage.setItem("carrito", JSON.stringify(carrito));
  actualizarCarrito();
  toggleCart();
}

function cambiarCantidad(id, delta) {
  if (!carrito[id]) return;
  carrito[id].cantidad += delta;
  if (carrito[id].cantidad <= 0) {
    delete carrito[id];
  }
  localStorage.setItem("carrito", JSON.stringify(carrito));
  actualizarCarrito();
}

function actualizarCarrito() {
  const container = document.getElementById("cart-items");
  const totalSpan = document.getElementById("cart-total-amount");
  const countSpan = document.getElementById("cart-count");
  const headerCount = document.getElementById("cart-count-header");
  let total = 0,
    count = 0,
    html = "";

  for (const id in carrito) {
    const item = carrito[id];
    const subtotal = item.cantidad * item.precio;
    total += subtotal;
    count += item.cantidad;
    html +=
      '<div class="cart-item">' +
      '<div class="cart-item-info">' +
      "<strong>" +
      item.nombre +
      "</strong><br/>" +
      "<span>$" +
      item.precio.toFixed(2) +
      "</span>" +
      "</div>" +
      '<div class="cart-item-actions">' +
      "<button onclick=\"cambiarCantidad('" +
      id +
      '\', -1)" class="cart-qty-btn minus">' +
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" style="width:12px;height:12px;"><line x1="5" y1="12" x2="19" y2="12"/></svg>' +
      "</button>" +
      '<span class="cart-qty">' +
      item.cantidad +
      "</span>" +
      "<button onclick=\"cambiarCantidad('" +
      id +
      '\', 1)" class="cart-qty-btn plus">' +
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" style="width:12px;height:12px;"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>' +
      "</button>" +
      "</div>" +
      "</div>";
  }

  container.innerHTML = html || '<p class="cart-empty">Carrito vacío</p>';
  totalSpan.textContent = total.toFixed(2);
  countSpan.textContent = count;
  if (headerCount) headerCount.textContent = count;
  document.getElementById("whatsapp-btn").disabled = count === 0;
}

function vaciarCarrito() {
  carrito = {};
  localStorage.setItem("carrito", JSON.stringify(carrito));
  actualizarCarrito();
}

function enviarWhatsApp() {
  const items = Object.values(carrito);
  if (items.length === 0) return;
  let mensaje = "Hola, quisiera comprar los siguientes productos:%0A";
  let total = 0;
  items.forEach((item) => {
    const subtotal = item.cantidad * item.precio;
    total += subtotal;
    mensaje +=
      "- " +
      item.nombre +
      " (x" +
      item.cantidad +
      ") - $" +
      subtotal.toFixed(2) +
      "%0A";
  });
  mensaje += "%0ATotal: $" + total.toFixed(2);
  window.open("https://wa.me/573052621244?text=" + mensaje, "_blank");
}

/* ── Toast en vez de alert ───────────────────────────────── */
let toastTimeout;
function mostrarToast(msg) {
  let toast = document.getElementById("fp-toast");
  if (!toast) {
    toast = document.createElement("div");
    toast.id = "fp-toast";
    toast.style.cssText =
      "position:fixed;bottom:28px;left:50%;transform:translateX(-50%) translateY(80px);" +
      "background:#D94F3D;color:#fff;padding:10px 20px;border-radius:8px;" +
      "font-family:inherit;font-size:.85rem;font-weight:500;z-index:9999;" +
      "opacity:0;transition:opacity .2s,transform .2s;pointer-events:none;" +
      "max-width:320px;text-align:center;box-shadow:0 4px 16px rgba(0,0,0,.25);";
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.style.opacity = "1";
  toast.style.transform = "translateX(-50%) translateY(0)";
  clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateX(-50%) translateY(80px)";
  }, 2800);
}

/* ── Init ────────────────────────────────────────────────── */
actualizarCarrito();
