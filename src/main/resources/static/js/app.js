(function() {
  var sidebar = document.querySelector('.sidebar');
  var toggle = document.querySelector('.sidebar-toggle');

  if (toggle && sidebar) {
    toggle.addEventListener('click', function() {
      sidebar.classList.toggle('open');
      var svg = toggle.querySelector('svg');
      if (sidebar.classList.contains('open')) {
        svg.innerHTML = '<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>';
      } else {
        svg.innerHTML = '<line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/>';
      }
    });

    document.addEventListener('click', function(e) {
      if (window.innerWidth <= 768 && sidebar.classList.contains('open')) {
        if (!sidebar.contains(e.target) && !toggle.contains(e.target)) {
          sidebar.classList.remove('open');
        }
      }
    });
  }

  var alerts = document.querySelectorAll('.alert');
  alerts.forEach(function(alert) {
    setTimeout(function() {
      alert.style.opacity = '0';
      alert.style.transform = 'translateY(-10px)';
      alert.style.transition = 'all 0.3s ease';
      setTimeout(function() { alert.remove(); }, 300);
    }, 5000);
  });

  document.querySelectorAll('input, select, textarea').forEach(function(el) {
    el.addEventListener('focus', function() {
      this.closest('.form-group')?.querySelector('.form-label')?.classList.add('focused');
    });
    el.addEventListener('blur', function() {
      this.closest('.form-group')?.querySelector('.form-label')?.classList.remove('focused');
    });
  });

  document.querySelectorAll('a[href*="eliminar"]').forEach(function(link) {
    link.addEventListener('click', function(e) {
      if (!confirm('¿Estás seguro de eliminar este registro? Esta acción no se puede deshacer.')) {
        e.preventDefault();
      }
    });
  });
})();
