import test, { before, after } from "node:test";
import assert from "node:assert/strict";
import { createApp } from "../src/app.js";
import { loadConfig } from "../src/config.js";
let server, base;
before(async () => {
  server = createApp().listen(0, "127.0.0.1");
  await new Promise(resolve => server.once("listening", resolve));
  base = `http://127.0.0.1:${server.address().port}`;
});
after(() => new Promise(resolve => server.close(resolve)));
async function json(path, status = 200) {
  const response = await fetch(base + path);
  assert.equal(response.status, status, path);
  return response.json();
}
test("health, metadata and dashboard contracts", async () => {
  assert.equal(await (await fetch(base + "/api/health")).text(), "OK");
  const info = await json("/api/info");
  assert.equal(info.success, true);
  assert.equal(info.data.apiVersion, "v1");
  assert.ok(Number.isFinite(Date.parse(info.data.startedAt)));
  assert.ok(Array.isArray(info.data.corsAllowedOrigins));
  const response = await fetch(base + "/");
  assert.equal(response.status, 200);
  assert.match(await response.text(), /Express 5/);
  for (const asset of ["/app.js", "/styles.css"]) assert.equal((await fetch(base + asset)).status, 200);
});
test("zone fields, filters and pagination", async () => {
  const all = (await json("/api/v1/zones")).data;
  assert.equal(all.totalElements, 3);
  assert.equal(all.content[0].zoneName, "ETH_24");
  assert.equal(all.content[0].boundaryPolygon, null);
  assert.equal(all.content[0].latitude, -29.85264);
  assert.deepEqual((await json("/api/v1/zones/1")).data, all.content[0]);
  assert.equal((await json("/api/v1/zones?province=limpopo")).data.content[0].zoneId, 3);
  assert.equal((await json("/api/v1/zones?municipality=Polokwane%20Local%20Municipality")).data.totalElements, 1);
  const page = (await json("/api/v1/zones?page=1&size=1")).data;
  assert.equal(page.content[0].zoneId, 2);
  assert.equal(page.totalPages, 3);
  assert.equal(page.first, false);
  assert.equal(page.last, false);
  assert.equal((await json("/api/v1/zones?page=-1&size=0")).data.page, 0);
  assert.equal((await json("/api/v1/zones?page=&size=")).data.size, 20);
  assert.equal((await json("/api/v1/zones?page=2147483647&size=2147483647")).data.content.length, 0);
  const empty = (await json("/api/v1/zones?province=unknown")).data;
  assert.equal(empty.totalElements, 0);
  assert.equal(empty.last, true);
});
test("observations and combined filters", async () => {
  assert.equal((await json("/api/v1/malaria-observations")).data.totalElements, 4);
  const result = (await json("/api/v1/malaria-observations?zoneId=1&date=2026-03-08")).data;
  assert.equal(result.totalElements, 1);
  assert.equal(result.content[0].envRecordId, 2);
  assert.deepEqual((await json("/api/v1/malaria-observations/2")).data, result.content[0]);
  assert.equal((await json("/api/v1/malaria-observations?zoneId=999")).data.totalElements, 0);
});
test("invalid parameters and missing resources", async () => {
  for (const path of ["/api/v1/zones/abc", "/api/v1/zones/2147483648", "/api/v1/zones?page=999999999999999999999", "/api/v1/zones?size=1.5", "/api/v1/zones?page=1&page=2", "/api/v1/malaria-observations?date=2026-02-30", "/api/v1/malaria-observations?zoneId=abc"]) {
    const body = await json(path, 400);
    assert.equal(body.success, false);
    assert.equal(body.data, null);
    assert.match(body.message, /Invalid value/);
  }
  for (const path of ["/api/v1/zones/999", "/api/v1/malaria-observations/999", "/api/missing"]) {
    const body = await json(path, 404);
    assert.equal(body.success, false);
    assert.equal(body.data, null);
  }
});
test("CORS and HEAD requests", async () => {
  for (const origin of ["http://localhost:5173", "http://127.0.0.1:5500"]) {
    const response = await fetch(base + "/api/v1/zones", {headers: {Origin: origin}});
    assert.equal(response.headers.get("access-control-allow-origin"), origin);
  }
  const denied = await fetch(base + "/api/v1/zones", {headers: {Origin: "https://example.com"}});
  assert.equal(denied.headers.get("access-control-allow-origin"), null);
  const preflight = await fetch(base + "/api/v1/zones", {method: "OPTIONS", headers: {Origin: "http://localhost:5173", "Access-Control-Request-Method": "GET", "Access-Control-Request-Headers": "authorization,x-request-id"}});
  assert.equal(preflight.status, 204);
  assert.equal(preflight.headers.get("access-control-allow-headers"), "authorization,x-request-id");
  assert.equal((await fetch(base + "/api/v1/zones", {method: "HEAD"})).status, 200);
});
test("configuration validates ports and parses origins", () => {
  assert.equal(loadConfig({}).port, 8080);
  assert.equal(loadConfig({PORT: "9000"}).port, 9000);
  for (const PORT of ["", "abc", "8080abc", "0", "65536", "1.5", "-1"]) assert.throws(() => loadConfig({PORT}), /PORT/);
  assert.deepEqual(loadConfig({CORS_ALLOWED_ORIGINS: " https://example.com, "}).corsAllowedOrigins, ["https://example.com"]);
});
