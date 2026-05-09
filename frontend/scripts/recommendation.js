// ===================== CampusBite – Recommendation Script =====================
const API_BASE = 'http://localhost:8080/api';

let selectedMood = '';
let selectedType = '';
let selectedVeg  = 'true';

/* ===================== Chip Selection ===================== */

/**
 * Initializes click handlers for mood and food-type chips.
 */
function initChips() {
  // Mood chips — single select
  document.querySelectorAll('#mood-chips .chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('#mood-chips .chip').forEach(c => c.classList.remove('selected'));
      chip.classList.add('selected');
      selectedMood = chip.dataset.val;
      document.getElementById('mood').value = selectedMood;
    });
  });

  // Meal type chips — single select
  document.querySelectorAll('#type-chips .chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('#type-chips .chip').forEach(c => c.classList.remove('selected'));
      chip.classList.add('selected');
      selectedType = chip.dataset.val;
      document.getElementById('foodType').value = selectedType;
    });
  });
}

/**
 * Sets veg/non-veg/both preference.
 */
function setVeg(val, btn) {
  document.querySelectorAll('.toggle-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  selectedVeg = val;
  document.getElementById('isVeg').value = val;
}

/* ===================== Recommendation Flow ===================== */

/**
 * Validates form inputs, calls backend recommendation API,
 * and renders the results.
 */
async function getRecommendations() {
  const budget   = document.getElementById('budget').value;
  const mood     = selectedMood;
  const foodType = selectedType;
  const isVeg    = selectedVeg;
  const token    = localStorage.getItem('campusbite_token');
  const btn      = document.getElementById('submit-btn');
  const btnText  = btn.querySelector('.btn-text');
  const loader   = btn.querySelector('.btn-loader');

  // Validate inputs
  if (!budget || parseInt(budget) < 10) {
    showFormError('Please enter a valid budget (min ₹10).'); return;
  }
  if (!mood) {
    showFormError('Please select a mood/craving.'); return;
  }
  if (!foodType) {
    showFormError('Please select a meal type.'); return;
  }

  hideFormError();
  setLoading(true, btn, btnText, loader);
  showState('loading');

  const payload = {
    budget: parseInt(budget),
    mood,
    foodType,
    isVeg: isVeg === 'both' ? null : isVeg === 'true'
  };
  console.log(payload);

  try {
    const response = await fetch(`${API_BASE}/recommendations`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    if (response.status === 401) {
      // Token expired
      localStorage.removeItem('campusbite_token');
      window.location.href = 'login.html';
      return;
    }

    const data = await response.json();
    console.log(data.recommendations);

    if (response.ok && data.recommendations && data.recommendations.length > 0) {
      renderResults(data.recommendations);
    } else if (response.ok && data.recommendations && data.recommendations.length === 0) {
      showNoResults();
    } else {
      showFormError(data.message || 'Could not fetch recommendations. Try different preferences.');
      showState('placeholder');
    }

  } catch (err) {
    console.error('Recommendation error:', err);
    showFormError('Cannot connect to server. Make sure the backend is running on port 8080.');
    showState('placeholder');
  } finally {
    setLoading(false, btn, btnText, loader);
  }
}

/**
 * Renders recommendation cards into the results panel.
 */
function renderResults(recommendations) {
  const container = document.getElementById('recommendations-container');
  const count     = document.getElementById('results-count');

  count.textContent = `${recommendations.length} match${recommendations.length > 1 ? 'es' : ''}`;
  container.innerHTML = '';

  recommendations.forEach((item, index) => {
    console.log("FULL ITEM =", item);
    const card = document.createElement('div');
    card.className = 'rec-card';
    card.style.animationDelay = `${index * 0.08}s`;

    const isVegItem = item.veg === true || item.veg === "true";

        const vegClass = isVegItem ? 'veg' : 'nonveg';
        const vegLabel = isVegItem ? 'VEG' : 'NON-VEG';
    card.innerHTML = `
      <div class="rec-top">
        <div style="display:flex;gap:12px;align-items:flex-start;flex:1">
          <span class="rec-rank">${index + 1}</span>
          <div class="rec-info">
            <div class="rec-food-name">${escHtml(item.foodName)}</div>
            <div class="rec-outlet">
              <span class="outlet-dot"></span>
              ${escHtml(item.outletName)}
            </div>
          </div>
        </div>
        <div class="rec-right">
          <span class="rec-price">₹${item.price}</span>
          <span class="rec-veg-tag ${vegClass}">${vegLabel}</span>
        </div>
      </div>
      <div class="rec-reason">
        <strong>Why this? </strong>${escHtml(item.reason || 'Great match for your preferences!')}
      </div>
    `;
    container.appendChild(card);
  });

  showState('results');
}

function showNoResults() {
  document.getElementById('results-list').innerHTML = `
    <div class="results-placeholder">
      <div class="placeholder-emoji">😕</div>
      <div class="placeholder-title">No matches found</div>
      <div class="placeholder-sub">Try increasing your budget or changing your mood/type preferences.</div>
    </div>
  `;
  showState('results');
}

/* ===================== UI State Helpers ===================== */

function showState(state) {
  document.getElementById('results-placeholder').classList.add('hidden');
  document.getElementById('results-loading').classList.add('hidden');
  document.getElementById('results-list').classList.add('hidden');

  if (state === 'placeholder') document.getElementById('results-placeholder').classList.remove('hidden');
  if (state === 'loading')     document.getElementById('results-loading').classList.remove('hidden');
  if (state === 'results')     document.getElementById('results-list').classList.remove('hidden');
}

function setLoading(loading, btn, btnText, loader) {
  btn.disabled = loading;
  btnText.classList.toggle('hidden', loading);
  loader.classList.toggle('hidden', !loading);
}

function showFormError(msg) {
  const el = document.getElementById('form-error');
  el.textContent = msg;
  el.classList.remove('hidden');
}
function hideFormError() {
  document.getElementById('form-error').classList.add('hidden');
}

function escHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function logout() {
  localStorage.removeItem('campusbite_token');
  localStorage.removeItem('campusbite_user');
  window.location.href = 'login.html';
}

function toggleMenu() {
  document.getElementById('nav-mobile').classList.toggle('hidden');
}

/* ===================== Init ===================== */

// Guard: redirect to login if not authenticated
if (!localStorage.getItem('campusbite_token')) {
  window.location.href = 'login.html';
}

initChips();