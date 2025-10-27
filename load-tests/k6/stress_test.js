import http from 'k6/http';
import { check, sleep } from 'k6';

/*****
Parámetros vía CLI (con valores por defecto):
- BASE_URL: base de la API (ej: http://localhost:8080)
- DO_AUTH: true/false para activar login previo
- LOGIN_EMAIL, LOGIN_PASSWORD: credenciales
- STAGE_1_VUS, STAGE_1_DURATION, STAGE_2_VUS, STAGE_2_DURATION, STAGE_3_VUS, STAGE_3_DURATION
*****/

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const DO_AUTH = (__ENV.DO_AUTH || 'false').toLowerCase() === 'true';
const LOGIN_EMAIL = __ENV.LOGIN_EMAIL || 'admin@example.com';
const LOGIN_PASSWORD = __ENV.LOGIN_PASSWORD || 'admin123';

export const options = {
  thresholds: {
    http_req_failed: ['rate<0.01'], // <1% errores
    http_req_duration: ['p(95)<800'], // 95% < 800ms
  },
  stages: [
    { duration: __ENV.STAGE_1_DURATION || '30s', target: Number(__ENV.STAGE_1_VUS || 20) },
    { duration: __ENV.STAGE_2_DURATION || '1m', target: Number(__ENV.STAGE_2_VUS || 50) },
    { duration: __ENV.STAGE_3_DURATION || '30s', target: Number(__ENV.STAGE_3_VUS || 0) },
  ],
};

let TOKEN = '';

function login() {
  const payload = JSON.stringify({ correo: LOGIN_EMAIL, contrasena: LOGIN_PASSWORD });
  const headers = { 'Content-Type': 'application/json', Accept: 'application/json' };
  const res = http.post(`${BASE_URL}/usuarios/login`, payload, { headers });
  check(res, {
    'login status is 200': (r) => r.status === 200,
  });
  try {
    const body = res.json();
    TOKEN = body?.token || '';
  } catch (e) {
    TOKEN = '';
  }
}

export default function () {
  if (DO_AUTH && !TOKEN) {
    login();
  }

  const authHeaders = TOKEN
    ? { Authorization: `Bearer ${TOKEN}`, Accept: 'application/json' }
    : { Accept: 'application/json' };

  // 1) GET /empleado
  const r1 = http.get(`${BASE_URL}/empleado`, { headers: authHeaders });
  check(r1, {
    'GET /empleado is 200': (r) => r.status === 200,
  });

  // 2) GET /usuarios
  const r2 = http.get(`${BASE_URL}/usuarios`, { headers: authHeaders });
  check(r2, {
    'GET /usuarios is 200': (r) => r.status === 200,
  });

  sleep(1);
}
