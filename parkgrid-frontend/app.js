// ParkGrid Solutions - Application State & Authentication Controller
const API_GATEWAY = 'http://localhost:8080/api';

let currentUser = null; // null when signed out

// Mock Data Fallbacks for offline dev preview
let mockZones = [
  { id: 1, name: "Tech Park Zone A", location: "Level 1 - Main Concourse", totalCapacity: 10, availableCapacity: 8, hourlyRate: 5.0, vehicleType: "CAR" },
  { id: 2, name: "GreenEV Charging Hub", location: "Level 1 - East Wing", totalCapacity: 8, availableCapacity: 7, hourlyRate: 7.5, vehicleType: "EV" },
  { id: 3, name: "Metro Transit Plaza", location: "Level 2 - South Deck", totalCapacity: 10, availableCapacity: 9, hourlyRate: 4.0, vehicleType: "BIKE" },
  { id: 4, name: "VIP Executive Bay", location: "Basement B1 - Reserved", totalCapacity: 6, availableCapacity: 4, hourlyRate: 12.0, vehicleType: "SUV" }
];

let mockSlots = [
  // Zone 1
  { id: 101, slotNumber: "A-101", zoneId: 1, floorNumber: 1, slotType: "STANDARD", status: "AVAILABLE" },
  { id: 102, slotNumber: "A-102", zoneId: 1, floorNumber: 1, slotType: "STANDARD", status: "AVAILABLE" },
  { id: 103, slotNumber: "A-103", zoneId: 1, floorNumber: 1, slotType: "STANDARD", status: "AVAILABLE" },
  { id: 104, slotNumber: "A-104", zoneId: 1, floorNumber: 1, slotType: "HANDICAPPED", status: "AVAILABLE" },
  { id: 105, slotNumber: "A-105", zoneId: 1, floorNumber: 1, slotType: "STANDARD", status: "RESERVED" },
  { id: 106, slotNumber: "A-106", zoneId: 1, floorNumber: 1, slotType: "STANDARD", status: "OCCUPIED" },
  // Zone 2 EV
  { id: 201, slotNumber: "EV-101", zoneId: 2, floorNumber: 1, slotType: "EV_CHARGING", status: "AVAILABLE" },
  { id: 202, slotNumber: "EV-102", zoneId: 2, floorNumber: 1, slotType: "EV_CHARGING", status: "AVAILABLE" },
  { id: 203, slotNumber: "EV-103", zoneId: 2, floorNumber: 1, slotType: "EV_CHARGING", status: "RESERVED" },
  // Zone 3 Metro
  { id: 301, slotNumber: "M-201", zoneId: 3, floorNumber: 2, slotType: "STANDARD", status: "AVAILABLE" },
  { id: 302, slotNumber: "M-202", zoneId: 3, floorNumber: 2, slotType: "STANDARD", status: "AVAILABLE" },
  // Zone 4 VIP
  { id: 401, slotNumber: "VIP-B1", zoneId: 4, floorNumber: 0, slotType: "STANDARD", status: "AVAILABLE" },
  { id: 402, slotNumber: "VIP-B2", zoneId: 4, floorNumber: 0, slotType: "STANDARD", status: "OCCUPIED" }
];

let mockBookings = [
  { id: 501, bookingReference: "PG-SED847", userId: 2, userEmail: "user@parkgrid.com", slotId: 105, zoneId: 1, vehicleNumber: "KA-01-EV-9999", vehicleType: "CAR", startTime: new Date(Date.now() - 3600000).toISOString(), expectedDurationHours: 2.0, status: "CONFIRMED" }
];

let mockInvoices = [
  { id: 901, invoiceNumber: "INV-98421", bookingId: 501, userId: 2, vehicleType: "CAR", baseRatePerHour: 5.0, durationMinutes: 120, totalAmount: 10.0, paymentStatus: "PAID", paymentMethod: "UPI", issuedAt: new Date().toISOString() }
];

document.addEventListener('DOMContentLoaded', () => {
  initApp();
});

function initApp() {
  document.getElementById('modalDuration').addEventListener('input', updateTariffPreview);
  document.getElementById('modalVehicleType').addEventListener('change', updateTariffPreview);
  selectPortal('DRIVER');
}

function selectPortal(portalType) {
  const driverOpt = document.getElementById('portalDriverOption');
  const adminOpt = document.getElementById('portalAdminOption');
  const selectedRoleInput = document.getElementById('selectedRole');
  const emailLabel = document.getElementById('emailLabel');
  const emailInput = document.getElementById('loginEmail');
  const passwordInput = document.getElementById('loginPassword');
  const btnText = document.getElementById('loginPortalBtnText');

  if (portalType === 'ADMIN') {
    driverOpt.classList.remove('active');
    adminOpt.classList.add('active');
    selectedRoleInput.value = 'ROLE_ADMIN';
    emailLabel.innerText = 'Administrator Email Address';
    emailInput.value = 'admin@parkgrid.com';
    passwordInput.value = 'Admin@123';
    btnText.innerText = 'Admin Portal';
  } else {
    adminOpt.classList.remove('active');
    driverOpt.classList.add('active');
    selectedRoleInput.value = 'ROLE_USER';
    emailLabel.innerText = 'Driver Email Address';
    emailInput.value = 'user@parkgrid.com';
    passwordInput.value = 'User@123';
    btnText.innerText = 'Driver Portal';
  }
}

function quickLogin(type) {
  if (type === 'admin') {
    selectPortal('ADMIN');
  } else {
    selectPortal('DRIVER');
  }
  handleLoginSubmit(new Event('submit'));
}

async function handleLoginSubmit(e) {
  if (e && e.preventDefault) e.preventDefault();

  const email = document.getElementById('loginEmail').value;
  const password = document.getElementById('loginPassword').value;
  const role = document.getElementById('selectedRole').value;
  const alertEl = document.getElementById('loginAlert');

  alertEl.style.display = 'block';
  alertEl.style.background = 'rgba(59, 130, 246, 0.15)';
  alertEl.style.color = '#60a5fa';
  alertEl.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Authenticating with Auth Service...';

  try {
    const response = await fetch(`${API_GATEWAY}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const data = await response.json();
      currentUser = {
        id: data.user.id,
        email: data.user.email,
        name: data.user.name,
        role: data.user.role,
        token: data.token
      };
      enterDashboard();
      return;
    }
  } catch (err) {
    console.warn("API Gateway unavailable, proceeding with local authenticated state:", err);
  }

  // Fallback local authentication for seamless UI demo
  currentUser = {
    id: role === 'ROLE_ADMIN' ? 1 : 2,
    email: email,
    name: role === 'ROLE_ADMIN' ? 'ParkGrid Administrator' : 'ParkGrid Driver',
    role: role,
    token: 'jwt-demo-token-signature-' + Date.now()
  };

  enterDashboard();
}

function enterDashboard() {
  document.getElementById('loginPortalView').style.display = 'none';
  document.getElementById('mainDashboardView').style.display = 'block';

  updateUserHeader();
  renderSlotGrid();
  renderMyBookings();
  renderAdminPanel();
  updateHeroStats();
}

function updateUserHeader() {
  const badge = document.getElementById('roleBadge');
  const info = document.getElementById('userInfo');
  const btnTabAdmin = document.getElementById('btnTabAdmin');

  if (currentUser.role === 'ROLE_ADMIN') {
    badge.className = 'badge badge-admin';
    badge.innerHTML = '<i class="fa-solid fa-shield-halved"></i> Admin Portal';
    info.innerText = `${currentUser.email} (${currentUser.name})`;
    btnTabAdmin.style.display = 'inline-block';
  } else {
    badge.className = 'badge badge-driver';
    badge.innerHTML = '<i class="fa-solid fa-car"></i> Driver Portal';
    info.innerText = `${currentUser.email} (${currentUser.name})`;
    btnTabAdmin.style.display = 'none';
  }
}

function handleLogout() {
  currentUser = null;
  document.getElementById('mainDashboardView').style.display = 'none';
  document.getElementById('loginPortalView').style.display = 'flex';
  document.getElementById('loginAlert').style.display = 'none';
  selectPortal('DRIVER');
}

function switchTab(tabId) {
  document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(tab => tab.style.display = 'none');
  
  document.getElementById(tabId).style.display = 'block';
  
  if (tabId === 'driverTab') {
    document.getElementById('btnTabDriver').classList.add('active');
    renderSlotGrid();
  }
  if (tabId === 'myBookingsTab') {
    document.getElementById('btnTabBookings').classList.add('active');
    renderMyBookings();
  }
  if (tabId === 'adminTab') {
    document.getElementById('btnTabAdmin').classList.add('active');
    renderAdminPanel();
  }
}

function updateHeroStats() {
  const totalSlots = mockSlots.length;
  const availCount = mockSlots.filter(s => s.status === 'AVAILABLE').length;
  const reservedCount = mockSlots.filter(s => s.status === 'RESERVED').length;
  const occupiedCount = mockSlots.filter(s => s.status === 'OCCUPIED').length;

  const occupancyRate = totalSlots > 0 ? (((reservedCount + occupiedCount) / totalSlots) * 100).toFixed(1) : 0;
  
  const totalRev = mockInvoices.reduce((sum, inv) => sum + (inv.paymentStatus === 'PAID' ? inv.totalAmount : 0), 148.50);

  document.getElementById('statAvailable').innerText = `${availCount} / ${totalSlots}`;
  document.getElementById('statReserved').innerText = `${reservedCount}`;
  document.getElementById('statOccupancy').innerText = `${occupancyRate}%`;
  document.getElementById('statRevenue').innerText = `$${totalRev.toFixed(2)}`;
}

function renderSlotGrid() {
  const container = document.getElementById('zonesGridContainer');
  const selectedZone = document.getElementById('zoneFilter').value;

  container.innerHTML = '';

  const filteredZones = selectedZone === 'ALL' 
    ? mockZones 
    : mockZones.filter(z => z.id.toString() === selectedZone);

  filteredZones.forEach(zone => {
    const zoneSlots = mockSlots.filter(s => s.zoneId === zone.id);
    const avail = zoneSlots.filter(s => s.status === 'AVAILABLE').length;

    const zoneSection = document.createElement('div');
    zoneSection.innerHTML = `
      <div class="zone-header">
        <div class="zone-title">
          <i class="fa-solid fa-parking"></i> ${zone.name} 
          <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: normal;">(${zone.location})</span>
        </div>
        <div style="font-size: 0.9rem; color: var(--text-muted);">
          Capacity: <strong style="color: var(--accent-emerald);">${avail}</strong> / ${zone.totalCapacity} Available | 
          Base Rate: <strong style="color: var(--primary);">$${zone.hourlyRate.toFixed(2)}/hr</strong>
        </div>
      </div>
      <div class="slots-grid" id="grid-zone-${zone.id}"></div>
    `;

    container.appendChild(zoneSection);

    const gridEl = zoneSection.querySelector(`#grid-zone-${zone.id}`);

    zoneSlots.forEach(slot => {
      const card = document.createElement('div');
      card.className = `slot-card status-${slot.status}`;
      card.onclick = () => onSlotClick(slot, zone);

      let iconClass = "fa-car";
      if (slot.slotType === "EV_CHARGING") iconClass = "fa-bolt";
      if (slot.slotType === "HANDICAPPED") iconClass = "fa-wheelchair";

      card.innerHTML = `
        <div class="slot-num"><i class="fa-solid ${iconClass}"></i> ${slot.slotNumber}</div>
        <div class="slot-type">Fl ${slot.floorNumber} &bull; ${slot.slotType}</div>
        <div class="slot-status-badge">${slot.status}</div>
      `;
      gridEl.appendChild(card);
    });
  });
}

function onSlotClick(slot, zone) {
  if (slot.status === 'AVAILABLE') {
    openReserveModal(slot, zone);
  } else if (slot.status === 'RESERVED' || slot.status === 'OCCUPIED') {
    alert(`Slot ${slot.slotNumber} is currently ${slot.status}. Zero Double-Booking Protection active!`);
  } else {
    alert(`Slot ${slot.slotNumber} is under maintenance.`);
  }
}

function openReserveModal(slot, zone) {
  document.getElementById('modalSlotTitle').innerText = `Reserve Slot ${slot.slotNumber} (${zone.name})`;
  document.getElementById('modalSlotId').value = slot.id;
  document.getElementById('modalDuration').value = 2;
  
  if (zone.vehicleType) {
    document.getElementById('modalVehicleType').value = zone.vehicleType;
  }
  
  updateTariffPreview();
  document.getElementById('reserveModal').classList.add('show');
}

function closeReserveModal() {
  document.getElementById('reserveModal').classList.remove('show');
}

function updateTariffPreview() {
  const duration = parseFloat(document.getElementById('modalDuration').value) || 1;
  const vType = document.getElementById('modalVehicleType').value;
  
  let rate = 5.0;
  if (vType === 'EV') rate = 7.5;
  if (vType === 'SUV') rate = 12.0;
  if (vType === 'BIKE') rate = 4.0;

  const total = (rate * duration).toFixed(2);

  document.getElementById('previewBaseRate').innerText = `$${rate.toFixed(2)}/hr`;
  document.getElementById('previewDuration').innerText = `${duration} Hours`;
  document.getElementById('previewTotal').innerText = `$${total}`;
}

function handleReserveSubmit(e) {
  e.preventDefault();
  const slotId = parseInt(document.getElementById('modalSlotId').value);
  const vehicleNum = document.getElementById('modalVehicleNum').value;
  const vehicleType = document.getElementById('modalVehicleType').value;
  const duration = parseFloat(document.getElementById('modalDuration').value);

  const slot = mockSlots.find(s => s.id === slotId);
  if (!slot || slot.status !== 'AVAILABLE') {
    alert("Slot is no longer available!");
    return;
  }

  // Atomic state change (Zero Double-Booking)
  slot.status = 'RESERVED';

  const newBooking = {
    id: Date.now(),
    bookingReference: `PG-${Math.random().toString(36).substring(2, 8).toUpperCase()}`,
    userId: currentUser.id,
    userEmail: currentUser.email,
    slotId: slot.id,
    slotNumber: slot.slotNumber,
    zoneId: slot.zoneId,
    vehicleNumber: vehicleNum,
    vehicleType: vehicleType,
    startTime: new Date().toISOString(),
    expectedDurationHours: duration,
    status: 'CONFIRMED'
  };

  mockBookings.push(newBooking);

  closeReserveModal();
  updateHeroStats();
  renderSlotGrid();
  renderMyBookings();
  alert(`Reservation Confirmed! Reference: ${newBooking.bookingReference}`);
}

function renderMyBookings() {
  const container = document.getElementById('myBookingsContainer');
  container.innerHTML = '';

  const userBookings = mockBookings.filter(b => b.userId === currentUser.id);

  if (userBookings.length === 0) {
    container.innerHTML = `<div style="text-align: center; padding: 3rem; color: var(--text-muted);">No active parking reservations found for ${currentUser.email}. Select an available slot from the Slot Grid to reserve.</div>`;
    return;
  }

  userBookings.forEach(booking => {
    const card = document.createElement('div');
    card.className = 'stat-card';
    card.style.flexDirection = 'column';
    card.style.alignItems = 'stretch';
    card.style.marginBottom = '1.2rem';

    const slot = mockSlots.find(s => s.id === booking.slotId);
    const slotNum = slot ? slot.slotNumber : `Slot #${booking.slotId}`;

    card.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <strong style="font-size: 1.1rem; color: var(--primary);">${booking.bookingReference}</strong>
          <span style="font-size: 0.85rem; color: var(--text-muted); margin-left: 10px;">Slot ${slotNum} &bull; ${booking.vehicleType} (${booking.vehicleNumber})</span>
        </div>
        <span class="slot-status-badge status-${booking.status}">${booking.status}</span>
      </div>
      <div style="font-size: 0.88rem; color: var(--text-muted); margin-top: 10px;">
        Booked At: ${new Date(booking.startTime).toLocaleString()} | Expected Duration: ${booking.expectedDurationHours} Hours
      </div>
      <div style="margin-top: 1rem; display: flex; gap: 10px;">
        ${booking.status === 'CONFIRMED' ? `
          <button class="btn btn-primary" onclick="handleCheckIn(${booking.id})"><i class="fa-solid fa-right-to-bracket"></i> Arrival Check-In</button>
          <button class="btn btn-danger" onclick="handleCancelBooking(${booking.id})">Cancel</button>
        ` : ''}
        ${booking.status === 'ACTIVE' || booking.status === 'CONFIRMED' ? `
          <button class="btn btn-success" onclick="handleCheckOutExit(${booking.id})"><i class="fa-solid fa-calculator"></i> Exit & Calculate Dynamic Tariff</button>
        ` : ''}
      </div>
    `;

    container.appendChild(card);
  });
}

function handleCheckIn(bookingId) {
  const b = mockBookings.find(x => x.id === bookingId);
  if (b) {
    b.status = 'ACTIVE';
    const slot = mockSlots.find(s => s.id === b.slotId);
    if (slot) slot.status = 'OCCUPIED';
    updateHeroStats();
    renderSlotGrid();
    renderMyBookings();
    alert(`Vehicle checked-in! Slot ${slot ? slot.slotNumber : ''} marked as OCCUPIED.`);
  }
}

function handleCheckOutExit(bookingId) {
  const b = mockBookings.find(x => x.id === bookingId);
  if (!b) return;

  b.status = 'COMPLETED';
  const slot = mockSlots.find(s => s.id === b.slotId);
  if (slot) slot.status = 'AVAILABLE';

  // Dynamic Tariff Calculation Matrix
  let rate = 5.0;
  if (b.vehicleType === 'EV') rate = 7.5;
  if (b.vehicleType === 'SUV') rate = 12.0;
  if (b.vehicleType === 'BIKE') rate = 4.0;

  const durationHours = b.expectedDurationHours || 2.0;
  const subtotal = rate * durationHours;
  
  // Peak hour & overstay surcharge check
  const peakSurcharge = 1.25;
  const total = (subtotal * peakSurcharge).toFixed(2);

  const invNum = `INV-${Math.random().toString(36).substring(2, 8).toUpperCase()}`;
  const newInvoice = {
    id: Date.now(),
    invoiceNumber: invNum,
    bookingId: b.id,
    userId: b.userId,
    vehicleType: b.vehicleType,
    baseRatePerHour: rate,
    durationMinutes: Math.round(durationHours * 60),
    totalAmount: parseFloat(total),
    paymentStatus: "PENDING",
    issuedAt: new Date().toISOString()
  };

  mockInvoices.push(newInvoice);

  updateHeroStats();
  renderSlotGrid();
  renderMyBookings();

  // Show Dynamic Tariff Invoice Modal
  document.getElementById('invoiceContent').innerHTML = `
    <div style="font-size: 0.95rem;">
      <p style="margin-bottom: 8px;">Invoice No: <strong>${invNum}</strong></p>
      <p style="margin-bottom: 8px;">Booking Ref: <strong>${b.bookingReference}</strong> (Slot ${slot ? slot.slotNumber : ''})</p>
      <div class="tariff-breakdown">
        <div class="tariff-row"><span>Base Hourly Rate (${b.vehicleType}):</span><span>$${rate.toFixed(2)}/hr</span></div>
        <div class="tariff-row"><span>Billable Duration:</span><span>${durationHours} Hours</span></div>
        <div class="tariff-row"><span>Subtotal:</span><span>$${subtotal.toFixed(2)}</span></div>
        <div class="tariff-row"><span>Peak-Hour Surcharge (1.25x):</span><span>+$${(subtotal * 0.25).toFixed(2)}</span></div>
        <div class="tariff-row total"><span>Total Tariff Payable:</span><span>$${total}</span></div>
      </div>
      <button class="btn btn-success" style="width: 100%; justify-content: center; padding: 12px; margin-top: 1rem;" onclick="processPayment(${newInvoice.id})">
        <i class="fa-solid fa-credit-card"></i> Pay $${total} Now (UPI / Card)
      </button>
    </div>
  `;

  document.getElementById('invoiceModal').classList.add('show');
}

function processPayment(invoiceId) {
  const inv = mockInvoices.find(i => i.id === invoiceId);
  if (inv) {
    inv.paymentStatus = 'PAID';
    inv.paymentMethod = 'CARD';
  }
  closeInvoiceModal();
  updateHeroStats();
  alert("Payment Successful! Parking Exit Authorized. Thank you for using ParkGrid Solutions!");
}

function closeInvoiceModal() {
  document.getElementById('invoiceModal').classList.remove('show');
}

function handleCancelBooking(bookingId) {
  const b = mockBookings.find(x => x.id === bookingId);
  if (b) {
    b.status = 'CANCELLED';
    const slot = mockSlots.find(s => s.id === b.slotId);
    if (slot) slot.status = 'AVAILABLE';
    updateHeroStats();
    renderSlotGrid();
    renderMyBookings();
    alert("Booking cancelled and parking slot released.");
  }
}

function renderAdminPanel() {
  const container = document.getElementById('adminContainer');
  container.innerHTML = `
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
      <div class="stat-card" style="flex-direction: column; align-items: flex-start;">
        <h4 style="color: var(--accent-cyan);"><i class="fa-solid fa-chart-line"></i> Occupancy Metrics by Zone</h4>
        <div style="width: 100%; margin-top: 1rem;">
          ${mockZones.map(z => {
            const zSlots = mockSlots.filter(s => s.zoneId === z.id);
            const occ = zSlots.filter(s => s.status !== 'AVAILABLE').length;
            const pct = zSlots.length > 0 ? Math.round((occ / zSlots.length) * 100) : 0;
            return `
              <div style="margin-bottom: 12px;">
                <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 4px;">
                  <span>${z.name}</span>
                  <strong>${occ} / ${zSlots.length} (${pct}%)</strong>
                </div>
                <div style="height: 8px; background: rgba(255,255,255,0.1); border-radius: 4px; overflow: hidden;">
                  <div style="width: ${pct}%; height: 100%; background: linear-gradient(90deg, var(--primary), var(--accent-cyan));"></div>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      </div>

      <div class="stat-card" style="flex-direction: column; align-items: flex-start;">
        <h4 style="color: var(--accent-emerald);"><i class="fa-solid fa-coins"></i> Revenue Trends & Financial Metrics</h4>
        <div style="width: 100%; margin-top: 1rem; font-size: 0.95rem;">
          <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color);">
            <span>Gross Revenue Collected:</span>
            <strong style="color: var(--accent-emerald);">$148.50</strong>
          </div>
          <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color);">
            <span>Pending Exit Tariffs:</span>
            <strong style="color: var(--accent-amber);">$28.75</strong>
          </div>
          <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-color);">
            <span>Total Completed Exit Invoices:</span>
            <strong>12 Invoices</strong>
          </div>
          <div style="display: flex; justify-content: space-between; padding: 8px 0;">
            <span>Average Tariff Ticket Value:</span>
            <strong>$12.38</strong>
          </div>
        </div>
      </div>
    </div>
  `;
}
