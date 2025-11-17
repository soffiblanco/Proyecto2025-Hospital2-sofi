import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1200'],
  },
  scenarios: {
    stress_5000_users: {
      executor: 'ramping-vus',
      startVUs: 50,
      gracefulRampDown: '2m',
      stages: [
        { duration: '3m', target: 1000 },
        { duration: '3m', target: 3000 },
        { duration: '4m', target: 5000 },
        { duration: '5m', target: 0 },
      ],
    },
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:3003'; // <-- 3003 por defecto
const endpoints = ['/api/servicios', '/api/page-content/home'];

export default function () {
  for (const path of endpoints) {
    const res = http.get(`${BASE_URL}${path}`, { tags: { endpoint: path } });
    check(res, {
      'status < 400': r => r.status < 400,   // acepta 2xx/3xx si hay redirección
      'has body':     r => (r.body || '').length > 0,
    });
    sleep(Math.random() * 0.1);
  }
  sleep(Math.random() * 0.5);
}
