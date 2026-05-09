// ===================== CampusBite – Register Script =====================
const API_BASE = 'http://localhost:8080/api';

/**
 * Handles registration form submission.
 * Validates inputs and sends credentials to backend.
 */
async function handleRegister() {
  const username  = document.getElementById('username').value.trim();
  const password  = document.getElementById('password').value;
  const confirm   = document.getElementById('confirm-password').value;
  const btn       = document.getElementById('register-btn');
  const errMsg    = document.getElementById('error-msg');
  const succMsg   = document.getElementById('success-msg');
  const btnText   = btn.querySelector('.btn-text');
  const loader    = btn.querySelector('.btn-loader');

  // Validations
  if (!username || !password || !confirm) {
    showError('Please fill in all fields.'); return;
  }
  if (username.length < 3) {
    showError('Username must be at least 3 characters.'); return;
  }
  if (password.length < 6) {
    showError('Password must be at least 6 characters.'); return;
  }
  if (password !== confirm) {
    showError('Passwords do not match.'); return;
  }

  setLoading(true, btn, btnText, loader);
  hideMessages();

  try {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (response.ok) {
      showSuccess('Account created! Redirecting to login...');
      setTimeout(() => { window.location.href = 'login.html'; }, 1500);
    } else {
      showError(data.message || 'Registration failed. Try a different username.');
    }
  } catch (err) {
    showError('Cannot connect to server. Make sure the backend is running on port 8080.');
    console.error('Register error:', err);
  } finally {
    setLoading(false, btn, btnText, loader);
  }
}

function setLoading(loading, btn, btnText, loader) {
  btn.disabled = loading;
  btnText.classList.toggle('hidden', loading);
  loader.classList.toggle('hidden', !loading);
}

function showError(msg) {
  const el = document.getElementById('error-msg');
  el.textContent = msg;
  el.classList.remove('hidden');
  document.getElementById('success-msg').classList.add('hidden');
}

function showSuccess(msg) {
  const el = document.getElementById('success-msg');
  el.textContent = msg;
  el.classList.remove('hidden');
  document.getElementById('error-msg').classList.add('hidden');
}

function hideMessages() {
  document.getElementById('error-msg').classList.add('hidden');
  document.getElementById('success-msg').classList.add('hidden');
}

// Allow pressing Enter to submit
document.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') handleRegister();
});