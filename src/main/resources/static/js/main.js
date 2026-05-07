// ===== AUTH UTILS =====
const Auth = {
    getToken: () => localStorage.getItem('token'),
    getUser: () => { try { return JSON.parse(localStorage.getItem('user')); } catch { return null; } },
    isLoggedIn: () => !!localStorage.getItem('token'),
    isAdmin: () => { const u = Auth.getUser(); return u && u.role === 'ADMIN'; },
    logout: () => { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/'; },
    headers: () => ({
        'Content-Type': 'application/json',
        ...(Auth.getToken() ? { 'Authorization': 'Bearer ' + Auth.getToken() } : {})
    })
};

function logout() { Auth.logout(); }

// ===== UPDATE NAV =====
function updateNav() {
    const user = Auth.getUser();
    const guestLinks = document.getElementById('guestLinks');
    const userLinks = document.getElementById('userLinks');
    const navUsername = document.getElementById('navUsername');

    if (user && Auth.isLoggedIn()) {
        if (guestLinks) guestLinks.style.display = 'none';
        if (userLinks) userLinks.style.display = 'contents';
        if (navUsername) navUsername.textContent = user.firstName || user.username;
    } else {
        if (guestLinks) guestLinks.style.display = 'contents';
        if (userLinks) userLinks.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', updateNav);

// ===== HAMBURGER MENU =====
function toggleNav() {
    document.getElementById('navLinks').classList.toggle('open');
}

// ===== TOAST NOTIFICATIONS =====
function showToast(message, isError = false) {
    let toast = document.getElementById('globalToast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'globalToast';
        toast.className = 'toast';
        toast.innerHTML = '<span class="toast-icon"></span><span class="toast-msg"></span>';
        document.body.appendChild(toast);
    }
    toast.querySelector('.toast-icon').textContent = isError ? '❌' : '✅';
    toast.querySelector('.toast-msg').textContent = message;
    toast.classList.toggle('error', isError);
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3500);
}

// ===== HOTEL CARD BUILDER =====
function hotelCard(h) {
    const stars = '★'.repeat(h.stars) + '☆'.repeat(5 - h.stars);
    const rating = h.averageRating ? h.averageRating.toFixed(1) : null;
    const reviewLabel = rating
        ? (h.averageRating >= 9 ? 'Превосходно' : h.averageRating >= 8 ? 'Отлично' : h.averageRating >= 7 ? 'Хорошо' : 'Нормально')
        : 'Нет отзывов';

    const amenities = h.amenities
        ? h.amenities.split(',').slice(0, 3).map(a => `<span class="amenity-tag">${amenityIcon(a.trim())} ${a.trim()}</span>`).join('')
        : '';

    const img = h.imageUrl
        ? `<img class="hotel-card-img" src="${h.imageUrl}" alt="${h.name}" loading="lazy" onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">`
        : '';

    return `
    <div class="hotel-card" onclick="window.location.href='/hotel/${h.id}'">
      ${img}
      <div class="hotel-card-img-placeholder" style="${h.imageUrl ? 'display:none' : ''}"><i class="fas fa-hotel"></i></div>
      <div class="hotel-card-body">
        <div class="hotel-card-name">${h.name}</div>
        <div class="hotel-card-location"><i class="fas fa-map-marker-alt"></i>${h.city}, ${h.country}</div>
        <div class="hotel-stars">${stars}</div>
        ${amenities ? `<div class="amenity-tags">${amenities}</div>` : ''}
        <div class="hotel-rating-badge">
          ${rating ? `<span class="rating-score">${rating}</span><span class="rating-label">${reviewLabel} · ${h.reviewCount || 0} отзывов</span>` : '<span class="rating-label">Нет отзывов</span>'}
        </div>
        <div class="hotel-card-footer">
          <div class="hotel-price">
            <span class="price-label">от</span>
            <span class="price-amount">${formatPrice(h.pricePerNight)}</span>
            <span class="price-night">₸ / ночь</span>
          </div>
          <button class="btn-book" onclick="event.stopPropagation(); window.location.href='/hotel/${h.id}'">Выбрать</button>
        </div>
      </div>
    </div>`;
}

function amenityIcon(a) {
    const icons = { WiFi:'📶', Pool:'🏊', Spa:'💆', Gym:'💪', Restaurant:'🍽️', Bar:'🍸', Parking:'🅿️', Concierge:'🛎️', BusinessCenter:'💼', AllInclusive:'🌟', Waterpark:'🌊', Butler:'👔', Valet:'🚗', ArtGallery:'🎨', PrivateDining:'🥂' };
    return icons[a] || '✓';
}

function formatPrice(price) {
    return Number(price).toLocaleString('ru-KZ');
}

// ===== COUNTDOWN BADGE =====
function countdownBadge(daysUntilCheckIn, daysUntilFree) {
    if (daysUntilCheckIn > 0) {
        const cls = daysUntilCheckIn <= 3 ? 'soon' : '';
        return `<span class="countdown-badge ${cls}"><i class="fas fa-hourglass-half"></i> <span class="countdown-number">${daysUntilCheckIn}</span> дн. до заезда</span>`;
    } else if (daysUntilFree > 0) {
        return `<span class="countdown-badge"><i class="fas fa-bed"></i> Гость в отеле · освободится через <span class="countdown-number">${daysUntilFree}</span> дн.</span>`;
    } else {
        return `<span class="countdown-badge free"><i class="fas fa-check-circle"></i> Завершено</span>`;
    }
}

// ===== LIVE COUNTDOWN TIMER =====
function startLiveCountdowns() {
    document.querySelectorAll('[data-checkin]').forEach(el => {
        const checkIn = new Date(el.dataset.checkin);
        const checkOut = new Date(el.dataset.checkout);

        function update() {
            const now = new Date();
            const daysIn = Math.ceil((checkIn - now) / 86400000);
            const daysFree = Math.ceil((checkOut - now) / 86400000);
            el.innerHTML = countdownBadge(Math.max(0, daysIn), Math.max(0, daysFree));
        }
        update();
        setInterval(update, 60000);
    });
}

// ===== AI CHAT =====
function toggleChat() {
    document.getElementById('aiChat').classList.toggle('open');
}

async function sendAiMsg() {
    const input = document.getElementById('aiInput');
    const msg = input.value.trim();
    if (!msg) return;

    appendAiMsg(msg, 'user');
    input.value = '';

    // Typing indicator
    const typingId = 'typing-' + Date.now();
    document.getElementById('aiMessages').insertAdjacentHTML('beforeend',
        `<div id="${typingId}" class="ai-msg bot"><div class="ai-msg-bubble"><div class="ai-typing"><span></span><span></span><span></span></div></div></div>`
    );
    scrollAiChat();

    try {
        const res = await fetch('/api/ai/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: msg })
        });
        const data = await res.json();
        document.getElementById(typingId).remove();
        appendAiBotMsg(data.message, data.hotels || []);
    } catch {
        document.getElementById(typingId).remove();
        appendAiMsg('Произошла ошибка. Попробуйте снова.', 'bot');
    }
}

function sendQuick(msg) {
    document.getElementById('aiInput').value = msg;
    sendAiMsg();
}

function appendAiMsg(text, type) {
    document.getElementById('aiMessages').insertAdjacentHTML('beforeend',
        `<div class="ai-msg ${type}"><div class="ai-msg-bubble">${text}</div></div>`
    );
    scrollAiChat();
}

function appendAiBotMsg(text, hotels) {
    let html = `<div class="ai-msg bot"><div class="ai-msg-bubble">${text}`;
    if (hotels && hotels.length > 0) {
        html += `<div class="ai-hotel-results">`;
        hotels.slice(0, 4).forEach(h => {
            html += `<div class="ai-hotel-mini" onclick="window.location.href='/hotel/${h.id}'">
        <div>
          <div class="ai-hotel-mini-name">${h.name}</div>
          <div class="ai-hotel-mini-info">⭐ ${h.stars} · ${h.city}</div>
        </div>
        <div class="ai-hotel-mini-price">${formatPrice(h.pricePerNight)} ₸</div>
      </div>`;
        });
        html += `</div>`;
    }
    html += `</div></div>`;
    document.getElementById('aiMessages').insertAdjacentHTML('beforeend', html);
    scrollAiChat();
}

function scrollAiChat() {
    const msgs = document.getElementById('aiMessages');
    if (msgs) msgs.scrollTop = msgs.scrollHeight;
}

// ===== API HELPER =====
async function api(url, options = {}) {
    const res = await fetch(url, {
        ...options,
        headers: { ...Auth.headers(), ...(options.headers || {}) }
    });
    if (res.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return;
    }
    const data = res.ok ? await res.json().catch(() => ({})) : await res.json().catch(() => ({ message: 'Ошибка сервера' }));
    if (!res.ok) throw new Error(data.message || 'Ошибка сервера');
    return data;
}
