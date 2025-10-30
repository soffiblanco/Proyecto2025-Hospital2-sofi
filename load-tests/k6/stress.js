import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuration
export const options = {
  thresholds: {
    http_req_failed: ['rate<0.01'], // <1% errores
    http_req_duration: ['p(95)<1200'], // p95 < 1.2s
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

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Public endpoints to stress without auth
const endpoints = [
  '/api/servicios',
  '/api/page-content/home',
];

export default function () {
  for (const path of endpoints) {
    const res = http.get(`${BASE_URL}${path}`, { tags: { name: path } });
    check(res, {
      'status is 2xx': r => r.status >= 200 && r.status < 300,
      'has body': r => (r.body || '').length > 0,
    });
    // Pequeño tiempo entre peticiones para simular usuarios reales
    sleep(Math.random() * 0.1);
  }
  sleep(Math.random() * 0.5);
}


