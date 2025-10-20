// ===== CONFIG =====
const PACK_COST = 100;
const DEFAULT_IMAGE = "assets/default-card.jpg";

const CARD_POOL = [
  { id: "c1", name: "Common Card", rarity: "common" },
  { id: "c2", name: "Uncommon Card", rarity: "uncommon" },
  { id: "c3", name: "Rare Debuff Card", rarity: "rare", debuff: "Half Health" },
];

const PROB = { common: 0.65, uncommon: 0.25, rare: 0.1 };
const DEBUFFS = ["Half Health", "Low FOV", "Invert Controls", "One-Handed"];

// ===== UTILITIES =====
function pickRarity() {
  const r = Math.random();
  let acc = 0;
  for (const key of Object.keys(PROB)) {
    acc += PROB[key];
    if (r <= acc) return key;
  }
  return "common";
}

// ===== STATE HANDLERS =====
function getState() {
  const s = localStorage.getItem("debuff_state_v1");
  if (s) return JSON.parse(s);
  const initial = { coins: 0, packs: 0, cards: [] };
  localStorage.setItem("debuff_state_v1", JSON.stringify(initial));
  return initial;
}

function setState(state) {
  localStorage.setItem("debuff_state_v1", JSON.stringify(state));
}

// ===== UI =====
function updateUI() {
  const state = getState();

  // Coins
  const coinEl = document.getElementById("coinCount");
  if (coinEl) coinEl.innerText = state.coins;

  // Packs
  const packsArea = document.getElementById("packsArea");
  if (packsArea) {
    packsArea.innerHTML = `
      <p>Packs: <span id="packsCount">${state.packs}</span>
      <button id="openPackBtn" class="btn btn-warning btn-sm ms-2" ${
        state.packs <= 0 ? "disabled" : ""
      }>Open Pack</button></p>
    `;
    document
      .getElementById("openPackBtn")
      .addEventListener("click", openPack);
  }

  renderCollection();
}

// ===== COLLECTION RENDER =====
function renderCollection() {
  const state = getState();
  const col = document.getElementById("cardCollection");
  if (!col) return;

  if (state.cards.length === 0) {
    col.innerHTML = `<div class="text-muted">No cards yet.</div>`;
    return;
  }

  col.innerHTML = state.cards
    .map(
      (c) => `
      <div class="card card-card p-2 text-dark bg-light">
        <img src="${c.image}" alt="${c.name}" class="w-100 h-100" style="object-fit:cover; border-radius:12px;">
        <div class="card-overlay text-center mt-1 small">
          <strong>${c.name}</strong><br>
          <span class="text-warning">${c.rarity.toUpperCase()}</span><br>
          ${c.debuff ? `<span class="badge badge-debuff">${c.debuff}</span>` : ""}
        </div>
      </div>`
    )
    .join("");
}

// ===== BUY PACKS =====
function buyPacks(n) {
  const state = getState();
  const cost = PACK_COST * n;

  if (state.coins < cost) {
    alert("Not enough coins!");
    return;
  }

  state.coins -= cost;
  state.packs += n;
  setState(state);
  updateUI();
}

// ===== OPEN PACK =====
function openPack() {
  const state = getState();

  if (!state.packs || state.packs <= 0) {
    alert("No packs to open!");
    return;
  }

  state.packs -= 1;

  const opened = [];
  for (let i = 0; i < 5; i++) {
    const rarity = pickRarity();
    const card = {
      id: `${Date.now()}_${i}`,
      name: rarity === "rare" ? "Rare Debuff Card" : `${rarity} card`,
      rarity,
      debuff: rarity === "rare" ? DEBUFFS[Math.floor(Math.random() * DEBUFFS.length)] : null,
      image: DEFAULT_IMAGE,
    };
    opened.push(card);
    state.cards.push(card);
  }

  setState(state);
  updateUI();
  showOpenedCards(opened);
}

// ===== PACK VIEWER (Pokémon-Style Swipe) =====
function showOpenedCards(cards) {
  const modal = new bootstrap.Modal(document.getElementById("openPackModal"));
  const container = document.getElementById("openedCards");
  container.innerHTML = "";

  let currentIndex = 0;
  const total = cards.length;

  const viewer = document.createElement("div");
  viewer.classList.add("viewer-container");

  const display = document.createElement("div");
  display.classList.add("card-display");

  const controls = document.createElement("div");
  controls.classList.add("viewer-controls");
  controls.innerHTML = `
    <button id="prevCardBtn" class="btn btn-outline-light" disabled>◀</button>
    <button id="nextCardBtn" class="btn btn-outline-light">▶</button>
  `;

  viewer.appendChild(display);
  viewer.appendChild(controls);
  container.appendChild(viewer);

  function renderCard(index) {
    const c = cards[index];
    display.innerHTML = `
      <div class="swipe-card ${index === total - 1 && c.rarity === "rare" ? "flip-ready" : ""}">
        <div class="card-inner">
          <div class="card-front">
            <img src="${c.image}" alt="${c.name}">
          </div>
          <div class="card-back">
            <div class="card-name">${c.name}</div>
            <div class="card-rarity">${c.rarity.toUpperCase()}</div>
            ${c.debuff ? `<div class="card-debuff">${c.debuff}</div>` : ""}
          </div>
        </div>
      </div>
    `;

    // Rare card auto-flip
    if (index === total - 1 && c.rarity === "rare") {
      setTimeout(() => {
        const card = document.querySelector(".swipe-card");
        card.classList.add("flipped");
        showRareOverlay(c.debuff);
      }, 1200);
    }

    document.getElementById("prevCardBtn").disabled = index === 0;
    document.getElementById("nextCardBtn").disabled = index === total - 1;
  }

  // Navigation
  document.getElementById("prevCardBtn").addEventListener("click", () => {
    if (currentIndex > 0) {
      currentIndex--;
      renderCard(currentIndex);
    }
  });
  document.getElementById("nextCardBtn").addEventListener("click", () => {
    if (currentIndex < total - 1) {
      currentIndex++;
      renderCard(currentIndex);
    }
  });

  // Swipe gestures (mobile)
  let startX = 0;
  display.addEventListener("touchstart", (e) => {
    startX = e.touches[0].clientX;
  });
  display.addEventListener("touchend", (e) => {
    const endX = e.changedTouches[0].clientX;
    if (endX < startX - 50 && currentIndex < total - 1) {
      currentIndex++;
      renderCard(currentIndex);
    } else if (endX > startX + 50 && currentIndex > 0) {
      currentIndex--;
      renderCard(currentIndex);
    }
  });

  renderCard(0);
  modal.show();
}

// ===== RARE OVERLAY =====
function showRareOverlay(debuff) {
  let overlay = document.getElementById("rareOverlay");
  if (!overlay) {
    overlay = document.createElement("div");
    overlay.id = "rareOverlay";
    overlay.classList.add("rare-overlay");
    document.body.appendChild(overlay);
  }

  overlay.textContent = `✨ RARE CARD UNLOCKED: ${debuff}! ✨`;
  overlay.classList.add("visible");
  setTimeout(() => overlay.classList.remove("visible"), 3000);
}

// ===== TWITCH LIVE STATUS =====
function checkTwitchLiveDemo() {
  let live = false;
  setInterval(() => {
    live = !live;
    document.getElementById("statusText").innerText = live ? "Live" : "Offline";
    document.getElementById("liveBadge").style.display = live
      ? "inline-block"
      : "none";
  }, 30000);
}

// ===== STRIPE PLACEHOLDERS =====
function buyCoins(amount) {
  alert(`Stripe integration placeholder: Buying ${amount} coins`);
  const s = getState();
  s.coins += amount;
  setState(s);
  updateUI();
}

// ===== INIT =====
document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("buy1").addEventListener("click", () => buyPacks(1));
  document.getElementById("buy10").addEventListener("click", () => buyPacks(10));
  document.getElementById("demoAddCoins").addEventListener("click", () => {
    const s = getState();
    s.coins += 500;
    setState(s);
    updateUI();
  });

  document.getElementById("loginBtn").addEventListener("click", () => {
    const s = { coins: 500, packs: 0, cards: [] };
    setState(s);
    updateUI();
    alert("Demo account created with 500 coins.");
  });

  document.getElementById("buyCoinsBtn100").addEventListener("click", () =>
    buyCoins(100)
  );
  document.getElementById("buyCoinsBtn500").addEventListener("click", () =>
    buyCoins(500)
  );

  document.getElementById("twitchLink").href =
    "https://www.twitch.tv/michiamojennifer";

  checkTwitchLiveDemo();
  updateUI();
});