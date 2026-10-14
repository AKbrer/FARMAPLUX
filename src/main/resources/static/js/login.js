(function() {
  localStorage.removeItem('jwt');
  localStorage.removeItem('user');

  const form = document.getElementById('loginForm');
  const errorDiv = document.getElementById('error-msg');
  const emailInput = document.getElementById('correo');
  const passwordInput = document.getElementById('password');
  const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

  if (!form) return;

  function showError(msg) {
    errorDiv.textContent = msg;
    errorDiv.style.display = 'flex';
    errorDiv.style.animation = 'none';
    void errorDiv.offsetHeight;
    errorDiv.style.animation = 'shake 0.4s ease';
  }

  function setLoading(loading) {
    if (submitBtn) {
      submitBtn.disabled = loading;
      submitBtn.innerHTML = loading
        ? '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spin" style="width:18px;height:18px;animation:spin 1s linear infinite;"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg> Ingresando...'
        : 'Ingresar';
    }
  }

  function validateForm() {
    let valid = true;
    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    emailInput.classList.remove('error');
    passwordInput.classList.remove('error');

    if (!email) {
      emailInput.classList.add('error');
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      emailInput.classList.add('error');
      showError('Ingresa un correo electrónico válido');
      valid = false;
    }

    if (!password) {
      passwordInput.classList.add('error');
      valid = false;
    }

    return valid;
  }

  emailInput.addEventListener('input', function() { this.classList.remove('error'); });
  passwordInput.addEventListener('input', function() { this.classList.remove('error'); });

  form.addEventListener('submit', async function(e) {
    e.preventDefault();
    errorDiv.style.display = 'none';

    if (!validateForm()) return;

    setLoading(true);

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          correo: emailInput.value.trim(),
          password: passwordInput.value.trim()
        })
      });

      if (!res.ok) {
        showError('Usuario o contraseña incorrectos');
        setLoading(false);
        return;
      }

      const data = await res.json();
      localStorage.setItem('jwt', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      window.location.href = data.rol === 'ROLE_ADMIN' ? '/admin' : '/catalogo';
    } catch (err) {
      showError('Error de conexión. Verifica tu internet.');
      setLoading(false);
    }
  });
})();
