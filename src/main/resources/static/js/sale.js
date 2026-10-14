(function() {
  function filtrarClientes() {
    var input = document.getElementById('clienteSearch');
    var filtro = input.value.toLowerCase();
    var select = document.getElementById('clienteSelect');
    for (var i = 0; i < select.options.length; i++) {
      var texto = select.options[i].getAttribute('data-search') || select.options[i].text.toLowerCase();
      select.options[i].style.display = texto.indexOf(filtro) > -1 ? '' : 'none';
    }
    select.value = '';
  }

  function filtrarProductos() {
    var input = document.getElementById('productoSearch');
    var filtro = input.value.toLowerCase();
    document.querySelectorAll('select[name="productoId"]').forEach(function(select) {
      for (var i = 0; i < select.options.length; i++) {
        var texto = select.options[i].getAttribute('data-search') || '';
        select.options[i].style.display = !filtro || texto.indexOf(filtro) > -1 ? '' : 'none';
      }
      select.value = '';
    });
  }

  function agregarProducto() {
    var container = document.getElementById('productos-container');
    var first = container.querySelector('.producto-item');
    var clone = first.cloneNode(true);
    clone.querySelector('select').value = '';
    clone.querySelector('input[type="number"]').value = 1;
    clone.querySelectorAll('option').forEach(function(opt) {
      var texto = opt.getAttribute('data-search') || '';
      var filtro = document.getElementById('productoSearch').value.toLowerCase();
      opt.style.display = !filtro || texto.indexOf(filtro) > -1 ? '' : 'none';
    });
    clone.querySelector('select').onchange = calcularTotal;
    clone.querySelector('input[type="number"]').oninput = calcularTotal;
    container.appendChild(clone);
    calcularTotal();
  }

  function togglePago() {
    var efectivo = document.querySelector('input[name="metodoPago"]:checked').value === 'EFECTIVO';
    document.getElementById('efectivo-fields').style.display = efectivo ? 'block' : 'none';
    document.getElementById('montoPagado').disabled = !efectivo;
    if (!efectivo) {
      document.getElementById('montoPagado').value = '';
      document.getElementById('cambio-info').textContent = '';
    }
  }

  function calcularTotal() {
    var selects = document.querySelectorAll('select[name="productoId"]');
    var cantidades = document.querySelectorAll('input[name="cantidad"]');
    var total = 0;
    for (var i = 0; i < selects.length; i++) {
      var opt = selects[i].options[selects[i].selectedIndex];
      var precio = parseFloat(opt.getAttribute('data-precio')) || 0;
      var cant = parseInt(cantidades[i].value) || 0;
      total += precio * cant;
    }
    document.getElementById('total-preview').textContent = total.toFixed(2);
    return total;
  }

  function calcularCambio() {
    var total = calcularTotal();
    var pagado = parseFloat(document.getElementById('montoPagado').value) || 0;
    var cambio = pagado - total;
    var info = document.getElementById('cambio-info');
    if (cambio >= 0) {
      info.textContent = 'Cambio: $' + cambio.toFixed(2);
      info.style.color = '#065f46';
    } else {
      info.textContent = 'Faltan: $' + Math.abs(cambio).toFixed(2);
      info.style.color = '#991b1b';
    }
  }

  function validarFormulario() {
    var total = calcularTotal();
    var metodo = document.querySelector('input[name="metodoPago"]:checked').value;
    if (metodo === 'EFECTIVO') {
      var pagado = parseFloat(document.getElementById('montoPagado').value) || 0;
      if (pagado < total) {
        mostrarToast('El monto pagado ($' + pagado.toFixed(2) + ') es menor al total ($' + total.toFixed(2) + ')');
        return false;
      }
    }
    return true;
  }

  function mostrarToast(msg) {
    var toast = document.getElementById('fp-toast');
    if (!toast) {
      toast = document.createElement('div');
      toast.id = 'fp-toast';
      toast.style.cssText =
        'position:fixed;bottom:28px;left:50%;transform:translateX(-50%) translateY(80px);' +
        'background:#ef4444;color:#fff;padding:12px 24px;border-radius:8px;' +
        'font-family:inherit;font-size:.875rem;font-weight:500;z-index:9999;' +
        'opacity:0;transition:opacity .2s,transform .2s;pointer-events:none;' +
        'max-width:360px;text-align:center;box-shadow:0 8px 24px rgba(0,0,0,.25);';
      document.body.appendChild(toast);
    }
    toast.textContent = msg;
    toast.style.opacity = '1';
    toast.style.transform = 'translateX(-50%) translateY(0)';
    clearTimeout(toast._timeout);
    toast._timeout = setTimeout(function() {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(-50%) translateY(80px)';
    }, 3000);
  }

  document.addEventListener('input', function(e) {
    if (e.target.name === 'montoPagado' || e.target.matches('select[name="productoId"]') || e.target.matches('input[name="cantidad"]')) {
      if (document.querySelector('input[name="metodoPago"]:checked').value === 'EFECTIVO') calcularCambio();
      calcularTotal();
    }
  });

  window.filtrarClientes = filtrarClientes;
  window.filtrarProductos = filtrarProductos;
  window.agregarProducto = agregarProducto;
  window.togglePago = togglePago;
  window.calcularTotal = calcularTotal;
  window.calcularCambio = calcularCambio;
  window.validarFormulario = validarFormulario;

  togglePago();
  calcularTotal();
})();
