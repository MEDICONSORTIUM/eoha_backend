const DEFAULT_CORS_ORIGINS = "http://localhost:[*],http://127.0.0.1:[*]";

export function loadConfig(env = process.env) {
  const rawPort = env.PORT ?? "8080";
  const port = Number(rawPort);
  if (!/^\d+$/.test(rawPort) || !Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error("PORT must be an integer between 1 and 65535");
  }
  return {
    port,
    name: env.EOHA_NAME ?? "EOHA Backend",
    version: env.EOHA_VERSION ?? "0.0.1-SNAPSHOT",
    apiVersion: "v1",
    corsAllowedOrigins: (env.CORS_ALLOWED_ORIGINS ?? DEFAULT_CORS_ORIGINS)
      .split(",").map(s => s.trim()).filter(Boolean),
    startedAt: new Date(),
  };
}
export const config = loadConfig();
