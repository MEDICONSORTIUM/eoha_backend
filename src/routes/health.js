import { Router } from "express";

const router = Router();

// Plain-text liveness probe. `GET /` is deliberately left to the static dashboard.
router.get("/", (req, res) => {
  res.type("text/plain").send("OK");
});

export default router;
