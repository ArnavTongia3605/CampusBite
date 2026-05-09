// ===================== CampusBite – Login Script =====================
const API_BASE = 'http://localhost:8080/api';

/**
 * Handles the login form submission.
 * Sends credentials to backend, stores JWT token on success.
 */
async function handleLogin() {
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;
  const btn      = document.getElementById('login-btn');
  const errMsg   = document.getElementById('error-msg');
  const btnText  = btn.querySelector('.btn-text');
  const loader   = btn.querySelector('.btn-loader');

  // Basic client-side validation
  if (!username || !password) {
    showError('Please fill in all fields.');
    return;
  }

  // Show loading state
  setLoading(true, btn, btnText, loader);
  hideError(errMsg);

  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (response.ok && data.token) {
      // Store JWT token and username in localStorage
      localStorage.setItem('campusbite_token', data.token);
      localStorage.setItem('campusbite_user', username);
      // Redirect to home page
      window.location.href = 'home.html';
    } else {
      showError(data.message || 'Invalid username or password.');
    }
  } catch (err) {
    // Network or server error
    showError('Cannot connect to server. Make sure the backend is running on port 8080.');
    console.error('Login error:', err);
  } finally {
    setLoading(false, btn, btnText, loader);
  }
}

function setLoading(loading, btn, btnText, loader) {
  btn.disabled = loading;
  btnText.classList.toggle('hidden', loading);
  loader.classList.toggle('hidden', !loading);
}

function showError(message) {
  const errMsg = document.getElementById('error-msg');
  errMsg.textContent = message;
  errMsg.classList.remove('hidden');
}

function hideError() {
  document.getElementById('error-msg').classList.add('hidden');
}

// Allow pressing Enter to submit
document.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') handleLogin();
});

// Redirect if already logged in
if (localStorage.getItem('campusbite_token')) {
  window.location.href = 'home.html';
}