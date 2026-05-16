// ===================== CampusBite – Home Script =====================
const API_BASE = 'http://localhost:8080/api';

// Fallback static outlets data (used if backend is unavailable)
const FALLBACK_OUTLETS = [
  {
    name: 'Smoothie Zone',
    category: 'Drinks & Shakes',
    emoji: '🥤',
    bg: 'linear-gradient(135deg, #1a0a00, #2d1500)',
    menuImage: ['../assets/images/SmoothieZone.png'],
    items: [
      { name: 'Mango Smoothie', price: 90, veg: true },
      { name: 'Chocolate Shake', price: 120, veg: true },
      { name: 'Cold Coffee', price: 80, veg: true },
      { name: 'Banana Shake', price: 70, veg: true },
    ]
  },
  {
    name: 'Silver Spoon',
    category: 'Restaurant',
    emoji: '🍽️',
    bg: 'linear-gradient(135deg, #0a1200, #162200)',
    menuImage: ['../assets/images/SilverSpoon1.png', '../assets/images/SilverSpoon2.png'],
    items: [
      { name: 'Kadhai Paneer', price: 170, veg: true },
      { name: 'Butter Naan', price: 40, veg: true },
      { name: 'Chicken Curry', price: 220, veg: false },
      { name: 'Veg Biryani', price: 140, veg: true },
    ]
  },
  {
    name: 'Apna Gaon',
    category: 'Indian Food',
    emoji: '🥘',
    bg: 'linear-gradient(135deg, #120a00, #201500)',
    menuImage: ['../assets/images/AG1.png', '../assets/images/AG2.png'],
    items: [
      { name: 'Dal Tadka', price: 120, veg: true },
      { name: 'Paneer Butter Masala', price: 180, veg: true },
      { name: 'Tandoori Roti', price: 20, veg: true },
      { name: 'Chicken Handi', price: 260, veg: false },
    ]
  }
];

/**
 * Renders outlet cards into the grid.
 * First tries backend API; falls back to static data.
 */
async function loadOutlets() {
  const grid = document.getElementById('outlets-grid');
  const token = localStorage.getItem('campusbite_token');

  let outlets = null;

  try {
    const res = await fetch(`${API_BASE}/outlets`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
    if (res.ok) {
      const data = await res.json();
      outlets = transformApiOutlets(data);
    }
  } catch (e) {
    console.warn('Backend unavailable, using fallback data:', e.message);
  }

  // Use fallback if API failed
  if (!outlets || outlets.length === 0) outlets = FALLBACK_OUTLETS;

  // Clear skeletons
  grid.innerHTML = '';

  outlets.forEach((outlet, i) => {
    grid.innerHTML += buildOutletCard(outlet, i);
  });
}


function getOutletMenuImage(outletName) {
  const name = outletName.toLowerCase();

  if (name.includes('smoothie')) {
    return ['../assets/images/SmoothieZone.png'];
  }

  if (name.includes('silver')) {
  return [
    '../assets/images/SilverSpoon1.png',
    '../assets/images/SilverSpoon2.png'
  ];
}

  if (name.includes('apna') || name.includes('gaon')) {
    return [
      '../assets/images/AG1.png',
      '../assets/images/AG2.png'
    ];
  }

  return ['../assets/images/AG1.png'];
}

/**
 * Transforms API response to the expected format.
 */
function transformApiOutlets(apiData) {
  const emojis = ['🍽️', '☕', '🥘', '🌯'];
  const bgs = [
    'linear-gradient(135deg, #1a0a00, #2d1500)',
    'linear-gradient(135deg, #0a1200, #162200)',
    'linear-gradient(135deg, #120a00, #201500)',
    'linear-gradient(135deg, #0a0012, #150020)',
  ];
  const groups = {};
  apiData.forEach(item => {
    if (!groups[item.outletName]) groups[item.outletName] = [];
    groups[item.outletName].push(item);
  });
  return Object.entries(groups).map(([name, items], i) => ({
    name,
    category: items[0]?.foodType || 'All Day',
    emoji: emojis[i % emojis.length],
    bg: bgs[i % bgs.length],
    menuImage: getOutletMenuImage(name),
    items: items.slice(0, 4).map(fi => ({
      name: fi.foodName,
      price: fi.price,
      veg: fi.isVeg
    }))
  }));
}

/**
 * Builds HTML for a single outlet card.
 */
function buildOutletCard(outlet, index) {
  return `
    <div class="outlet-card" style="animation-delay:${index * 0.1}s">
      <div class="card-image" style="background:${outlet.bg}">
        <span style="position:relative;z-index:1">${outlet.emoji}</span>
      </div>
      <div class="card-body">
        <div class="outlet-name">${outlet.name}</div>
        <div class="outlet-category">${outlet.category}</div>
        <button class="menu-open-btn" onclick='openMenuModal(${JSON.stringify(outlet.menuImage)})'>
          View Menu
        </button>
      </div>
    </div>
  `;
}

function openMenuModal(imagePaths) {
  const modal = document.getElementById('menu-modal');
  const img1 = document.getElementById('menuImage');
  const img2 = document.getElementById('menuImage2');

  if (!Array.isArray(imagePaths)) {
    imagePaths = [imagePaths];
  }

  img1.src = imagePaths[0];

  if (imagePaths[1]) {
    img2.src = imagePaths[1];
    img2.style.display = 'block';
  } else {
    img2.src = '';
    img2.style.display = 'none';
  }

  modal.classList.remove('hidden');
}

function closeMenuModal() {
  const modal = document.getElementById('menu-modal');
  const img1 = document.getElementById('menuImage');
  const img2 = document.getElementById('menuImage2');

  modal.classList.add('hidden');

  img1.src = '';
  img2.src = '';
  img2.style.display = 'none';
}

window.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeMenuModal();
});

function logout() {
  localStorage.removeItem('campusbite_token');
  localStorage.removeItem('campusbite_user');
  window.location.href = 'login.html';
}

function toggleMenu() {
  document.getElementById('nav-mobile').classList.toggle('hidden');
}

// Guard: redirect to login if not authenticated
if (!localStorage.getItem('campusbite_token')) {
  window.location.href = 'login.html';
}
// Load on page ready
loadOutlets();