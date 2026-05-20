export const environment = {
  production: false,
  // Empty in dev: requests go to the Angular dev-server origin and are forwarded
  // by proxy.conf.json to the API-Gateway. Avoids CORS preflight rejection from
  // Spring Cloud Gateway. Override in environment.prod.ts when needed.
  apiBase: ''
};
