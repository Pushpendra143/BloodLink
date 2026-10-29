const G = ['A+','A-','B+','B-','AB+','AB-','O+','O-'];
const $ = s => document.querySelector(s);
const main = $('#main');
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const opts = (a, v = x => x, l = x => x) => a.map(x => `<option value="${esc(v(x))}">${esc(l(x))}</option>`).join('');
const today = () => new Date(Date.now() - new Date().getTimezoneOffset() * 6e4).toISOString().slice(0, 10);
const table = (head, rows) => `<div class="scroll"><table><thead><tr>${head.map(h => `<th>${h}</th>`).join('')}</tr></thead><tbody>${rows.join('')}</tbody></table></div>`;
const btn = (act, id, label, extra = '') => `<button class="btn ghost" data-act="${act}" data-id="${id}" ${extra}>${label}</button>`;
let view = 'dashboard', matchFor = null, F = { bloodGroup: '', city: '' };
let tt;

function toast(msg, bad) {
  const t = $('#toast');
  t.textContent = msg; t.className = 'show' + (bad ? ' bad' : '');
  clearTimeout(tt); tt = setTimeout(() => t.className = '', 4000);
}

async function api(path, method = 'GET', body) {
  const r = await fetch('/api' + path, {
    method, headers: body ? { 'Content-Type': 'application/json' } : {},
    body: body ? JSON.stringify(body) : undefined
  });
  if (r.status === 204) return null;
  const d = await r.json().catch(() => ({}));
  if (!r.ok) throw new Error(d.message || 'Request failed (' + r.status + ')');
  return d;
}

const views = {
  async dashboard() {
    const [s, stock] = await Promise.all([api('/reports/summary'), api('/stock')]);
    const stat = (n, l) => `<div class="stat"><b>${n}</b><span>${l}</span></div>`;
    const bags = stock.map(b => {
      const u = b.unitsAvailable, c = u === 0 ? 'out' : u < 5 ? 'low' : 'ok';
      return `<div class="bag ${c}"><div class="fill" style="height:${Math.min(u, 12) / 12 * 100}%"></div><strong>${esc(b.bloodGroup)}</strong><span>${u} unit${u === 1 ? '' : 's'}</span></div>`;
    }).join('');
    return `<h2>Overview</h2>
      <div class="stats">${stat(s.openRequests, 'open requests')}${stat(s.criticalOpenRequests, 'critical')}${stat(s.eligibleToday, 'donors ready today')}${stat(s.activeDonors + ' of ' + s.totalDonors, 'donors active')}${stat(s.totalDonations, 'donations so far')}</div>
      <h3>Blood in stock</h3><div class="bags">${bags}</div>
      <form data-form="stock"><label>Blood group<select name="group">${opts(G)}</select></label>
        <label>Units received<input name="units" type="number" min="1" value="1" required></label><button>Add to stock</button></form>`;
  },

  async donors() {
    const q = new URLSearchParams();
    if (F.bloodGroup) q.set('bloodGroup', F.bloodGroup);
    if (F.city) q.set('city', F.city);
    const list = await api('/donors?' + q);
    const rows = list.map(d => `<tr><td>${d.id}</td><td>${esc(d.fullName)}</td><td>${esc(d.bloodGroup)}</td><td>${esc(d.city)}</td><td>${esc(d.phone)}</td>
      <td>${d.lastDonationDate || 'never'}</td><td><span class="badge">${d.active ? 'Active' : 'Inactive'}</span></td>
      <td>${btn('elig', d.id, 'Can donate?')} ${d.active ? btn('deact', d.id, 'Deactivate') : ''}</td></tr>`);
    return `<h2>Donors</h2>
      <form data-form="filter"><label>Blood group<select name="bloodGroup"><option value="">All</option>${opts(G).replace(`value="${esc(F.bloodGroup)}"`, `value="${esc(F.bloodGroup)}" selected`)}</select></label>
        <label>City<input name="city" value="${esc(F.city)}" placeholder="Any city"></label><button>Filter</button></form>
      ${rows.length ? table(['ID', 'Name', 'Group', 'City', 'Phone', 'Last donation', 'Status', ''], rows) : '<p class="muted">No donors match. Clear the filter or register a donor below.</p>'}
      <details><summary>Register a donor</summary><form data-form="donor">
        <label>Full name<input name="fullName" required></label><label>Email<input name="email" type="email" required></label>
        <label>Mobile (10 digits)<input name="phone" pattern="[6-9][0-9]{9}" required></label>
        <label>Blood group<select name="bloodGroup">${opts(G)}</select></label><label>City<input name="city" required></label>
        <label>Date of birth<input name="dateOfBirth" type="date" required></label>
        <label>Last donation (optional)<input name="lastDonationDate" type="date" max="${today()}"></label><button>Register donor</button></form></details>`;
  },

  async requests() {
    const [list, hospitals] = await Promise.all([api('/requests'), api('/hospitals')]);
    const rows = list.map(r => `<tr><td>${r.id}</td><td>${esc(r.hospital.name)}<br><small class="muted">${esc(r.hospital.city)}</small></td><td>${esc(r.patientName)}</td>
      <td>${esc(r.bloodGroup)}</td><td>${r.unitsFulfilled} of ${r.unitsNeeded}</td><td><span class="badge ${r.urgency.toLowerCase()}">${r.urgency.toLowerCase()}</span></td>
      <td>${r.status.toLowerCase()}</td><td>${r.requiredBy}</td>
      <td>${r.status === 'OPEN' ? btn('matches', r.id, 'Find donors') + ' ' + btn('stockfill', r.id, 'Use stock') + ' ' + btn('cancel', r.id, 'Cancel') : ''}</td></tr>`);
    let panel = '';
    if (matchFor) {
      try {
        const m = await api(`/requests/${matchFor}/matches`);
        panel = `<section class="panel"><h3>Donors who can help request #${matchFor}</h3>` + (m.length ? table(['Name', 'Group', 'Match', 'Phone', ''],
          m.map(x => `<tr><td>${esc(x.name)}</td><td>${esc(x.bloodGroup)}</td><td>${x.exactMatch ? 'Same group' : 'Compatible'}</td><td>${esc(x.phone)}</td>
          <td>${btn('give', x.donorId, 'Record donation', `data-req="${matchFor}"`)}</td></tr>`)) : '<p class="muted">No eligible donors in this city right now. Try the stock.</p>') + '</section>';
      } catch (e) { panel = `<p class="err">${esc(e.message)}</p>`; }
    }
    return `<h2>Blood requests</h2>
      ${rows.length ? table(['ID', 'Hospital', 'Patient', 'Group', 'Units', 'Urgency', 'Status', 'Needed by', ''], rows) : '<p class="muted">No requests yet. Create the first one below.</p>'}${panel}
      <details><summary>New blood request</summary><form data-form="request">
        <label>Hospital<select name="hospitalId">${opts(hospitals, h => h.id, h => h.name + ', ' + h.city)}</select></label>
        <label>Patient name<input name="patientName" required></label><label>Blood group<select name="bloodGroup">${opts(G)}</select></label>
        <label>Units (1 to 10)<input name="unitsNeeded" type="number" min="1" max="10" value="1" required></label>
        <label>Urgency<select name="urgency"><option>NORMAL</option><option>URGENT</option><option>CRITICAL</option></select></label>
        <label>Needed by<input name="requiredBy" type="date" min="${today()}" value="${today()}" required></label><button>Create request</button></form></details>`;
  },

  async donations() {
    const [list, donors, reqs] = await Promise.all([api('/donations'), api('/donors'), api('/requests?status=OPEN')]);
    const rows = list.map(d => `<tr><td>${d.id}</td><td>${esc(d.donor.fullName)} (${esc(d.donor.bloodGroup)})</td>
      <td>${d.request ? '#' + d.request.id + ' ' + esc(d.request.patientName) : 'Blood bank stock'}</td><td>${d.donatedOn}</td></tr>`);
    return `<h2>Donations</h2>
      <form data-form="donation"><label>Donor<select name="donorId">${opts(donors.filter(d => d.active), d => d.id, d => d.fullName + ' (' + d.bloodGroup + ')')}</select></label>
        <label>Give to<select name="requestId"><option value="">Blood bank stock</option>${opts(reqs, r => r.id, r => '#' + r.id + ' ' + r.patientName + ' (' + r.bloodGroup + ')')}</select></label>
        <button>Record donation</button></form>
      ${rows.length ? table(['ID', 'Donor', 'Given to', 'Date'], rows.reverse()) : '<p class="muted">No donations recorded yet.</p>'}`;
  },

  async hospitals() {
    const list = await api('/hospitals');
    return `<h2>Hospitals</h2>${table(['ID', 'Name', 'City', 'Contact', 'Registration no.'],
      list.map(h => `<tr><td>${h.id}</td><td>${esc(h.name)}</td><td>${esc(h.city)}</td><td>${esc(h.contactNumber)}</td><td>${esc(h.registrationNo)}</td></tr>`))}
      <details><summary>Add a hospital</summary><form data-form="hospital"><label>Name<input name="name" required></label><label>City<input name="city" required></label>
        <label>Contact number<input name="contactNumber" required></label><label>Registration no.<input name="registrationNo" required></label><button>Add hospital</button></form></details>`;
  }
};

const forms = {
  stock: d => api(`/stock/${encodeURIComponent(d.group)}/add?units=${d.units}`, 'POST'),
  filter: d => { F = d; },
  donor: d => { if (!d.lastDonationDate) delete d.lastDonationDate; return api('/donors', 'POST', d); },
  request: d => api('/requests', 'POST', { ...d, hospitalId: +d.hospitalId, unitsNeeded: +d.unitsNeeded }),
  donation: d => api('/donations', 'POST', { donorId: +d.donorId, requestId: d.requestId ? +d.requestId : null }),
  hospital: d => api('/hospitals', 'POST', d)
};
const okMsg = { stock: 'Stock updated', donor: 'Donor registered', request: 'Request created', donation: 'Donation recorded', hospital: 'Hospital added' };

const actions = {
  async elig(id) {
    const e = await api(`/donors/${id}/eligibility`);
    toast(e.eligible ? 'This donor can donate today' : e.reason + (e.nextEligibleDate ? ' Next possible date: ' + e.nextEligibleDate : ''), !e.eligible);
  },
  async deact(id) {
    if (!confirm('Deactivate this donor? Their donation history is kept.')) return;
    await api('/donors/' + id, 'DELETE'); toast('Donor deactivated'); await show();
  },
  async matches(id) { matchFor = matchFor == id ? null : id; await show(); },
  async stockfill(id) { await api(`/requests/${id}/fulfill-from-stock`, 'POST'); toast('Request served from stock'); await show(); },
  async cancel(id) {
    if (!confirm('Cancel this request?')) return;
    await api(`/requests/${id}/cancel`, 'POST'); matchFor = null; toast('Request cancelled'); await show();
  },
  async give(id, d) { await api('/donations', 'POST', { donorId: +id, requestId: +d.req }); toast('Donation recorded'); await show(); }
};

async function run(fn) { try { await fn(); } catch (e) { toast(e.message, true); } }

async function show() {
  document.querySelectorAll('#nav button').forEach(b => b.classList.toggle('on', b.dataset.view === view));
  try { main.innerHTML = await views[view](); }
  catch (e) { main.innerHTML = '<p class="err">Cannot load data. Check that BloodLinkD is running on this address.</p>'; }
}

main.onsubmit = e => {
  e.preventDefault();
  const f = e.target, k = f.dataset.form;
  run(async () => {
    await forms[k](Object.fromEntries(new FormData(f)));
    if (okMsg[k]) toast(okMsg[k]);
    await show();
  });
};
main.onclick = e => {
  const b = e.target.closest('[data-act]');
  if (b) run(() => actions[b.dataset.act](b.dataset.id, b.dataset));
};
$('#nav').onclick = e => {
  const b = e.target.closest('button');
  if (b) { view = b.dataset.view; matchFor = null; show(); }
};
show();
