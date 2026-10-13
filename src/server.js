import { createApp } from "./app.js";
import { config } from "./config.js";

const server = createApp().listen(config.port, () => {
  console.log(`${config.name} listening on http://localhost:${config.port}`);
  console.log(`Status page: http://localhost:${config.port}/`);
});
server.on("error", (err) => {
  console.error(`Unable to start server: ${err.message}`);
  process.exitCode = 1;
});
for (const signal of ["SIGINT", "SIGTERM"]) {
  process.once(signal, () => {
    const timeout = setTimeout(() => process.exit(1), 10000);
    timeout.unref();
    server.close(() => clearTimeout(timeout));
  });
}
