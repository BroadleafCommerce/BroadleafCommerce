import { createApp } from "./app.js";

const port = Number(process.env.PORT ?? 3000);
const host = process.env.HOST ?? "127.0.0.1";

const server = createApp();

server.listen(port, host, () => {
  console.log(`shipping-estimator listening on http://${host}:${port}`);
});
