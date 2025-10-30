import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuration
export const options = {
  thresholds: {
    http_req_failed: ["rate<0.01"], // <1% errors
    http_req_duration: ["p(95)<800"], // p95 < 800ms
  },
  scenarios: {
    stress: {
      executor: 'ramping-arrival-rate',
      startRate: 10,
      timeUnit: '1s',
      preAllocatedVUs: 50,
      maxVUs: 500,
      stages: [
        { duration: '2m', target: 50 },   // ramp to 50 rps
        { duration: '3m', target: 100 },  // ramp to 100 rps
        { duration: '5m', target: 150 },  // ramp to 150 rps
        { duration: '5m', target: 0 },    // ramp down
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
  }
  // Small think time to avoid pure hammering pattern
  sleep(Math.random() * 0.3);
}


