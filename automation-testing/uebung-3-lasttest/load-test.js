// Lasttest für das Student-Backend mit autocannon.
// Aufruf: npm run test:load   (Backend muss auf Port 8081 laufen)
const autocannon = require('autocannon');
const fs = require('fs');
const path = require('path');

const BASE_URL = process.env.BASE_URL || 'http://localhost:8081';
const DURATION = Number(process.env.DURATION || 20); // Sekunden pro Szenario

const scenarios = [
  {
    title: '1) GET /students – Ausgangslage',
    url: `${BASE_URL}/students`,
    connections: 100,
  },
  {
    title: '2) POST /students – viele neue Studenten',
    url: `${BASE_URL}/students`,
    connections: 50,
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    // jede Anfrage bekommt einen eigenen Namen
    requests: [{
      setupRequest: (req) => {
        const id = Math.random().toString(36).slice(2, 10);
        req.body = JSON.stringify({ name: `Load ${id}`, email: `load-${id}@tbz.ch` });
        return req;
      },
    }],
  },
  {
    title: '3) GET /students – nach dem POST-Test (viel mehr Daten)',
    url: `${BASE_URL}/students`,
    connections: 100,
  },
];

function run(scenario) {
  return new Promise((resolve, reject) => {
    const instance = autocannon({ duration: DURATION, ...scenario }, (err, result) => {
      if (err) reject(err); else resolve(result);
    });
    autocannon.track(instance, { renderProgressBar: true, renderResultsTable: false });
  });
}

async function countStudents() {
  const res = await fetch(`${BASE_URL}/students`);
  return (await res.json()).length;
}

(async () => {
  const summary = [];
  for (const scenario of scenarios) {
    console.log(`\n${scenario.title}  (${scenario.connections} gleichzeitige Verbindungen, ${DURATION}s)`);
    const r = await run(scenario);
    summary.push({
      Szenario: scenario.title,
      'Requests total': r.requests.total,
      'Requests/s (avg)': Math.round(r.requests.average),
      'Latenz avg (ms)': Math.round(r.latency.average),
      'Latenz p99 (ms)': r.latency.p99,
      'Latenz max (ms)': r.latency.max,
      'Fehler': r.errors,
      'Timeouts': r.timeouts,
      'Nicht-2xx': r.non2xx,
      'Studenten in DB': await countStudents(),
    });
  }

  console.log('\nZusammenfassung:');
  console.table(summary);
  const out = path.join(__dirname, 'results.json');
  fs.writeFileSync(out, JSON.stringify(summary, null, 2));
  console.log(`Resultate gespeichert in ${out}`);
})();
