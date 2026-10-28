(function () {
    "use strict";

    var root = document.documentElement;
    var statusLabel = document.getElementById("status-label");
    var uptimeSlot = document.querySelector('[data-slot="uptime"]');
    var uptimeSub = document.getElementById("uptime-sub");
    var startedAtMs = null;

    function setStatus(state, label) {
        root.setAttribute("data-status", state);
        if (statusLabel) statusLabel.textContent = label;
    }

    function checkHealth() {
        fetch("/api/health", { cache: "no-store" })
            .then(function (res) {
                setStatus(res.ok ? "healthy" : "down", res.ok ? "Healthy" : "Degraded");
            })
            .catch(function () {
                setStatus("down", "Unreachable");
            });
    }

    function formatDuration(ms) {
        var s = Math.max(0, Math.floor(ms / 1000));
        var d = Math.floor(s / 86400); s -= d * 86400;
        var h = Math.floor(s / 3600); s -= h * 3600;
        var m = Math.floor(s / 60); s -= m * 60;
        var parts = [];
        if (d) parts.push(d + "d");
        if (h || d) parts.push(h + "h");
        if (m || h || d) parts.push(m + "m");
        parts.push(s + "s");
        return parts.join(" ");
    }

    function tickUptime() {
        if (startedAtMs == null) return;
        var text = formatDuration(Date.now() - startedAtMs);
        if (uptimeSlot) uptimeSlot.textContent = "up " + text;
        if (uptimeSub) uptimeSub.textContent = "Uptime " + text;
    }

    function fill(slot, value) {
        var el = document.querySelector('[data-slot="' + slot + '"]');
        if (el && value) el.textContent = value;
    }

    function loadInfo() {
        fetch("/api/info", { cache: "no-store" })
            .then(function (res) { return res.ok ? res.json() : Promise.reject(res.status); })
            .then(function (body) {
                var d = (body && body.data) || {};
                if (d.version) fill("version", "v" + d.version);
                if (d.apiVersion) fill("api", "API " + d.apiVersion);
                fill("port", ":" + (window.location.port || "80"));

                var cors = document.getElementById("cors-value");
                if (cors && Array.isArray(d.corsAllowedOrigins) && d.corsAllowedOrigins.length) {
                    cors.textContent = d.corsAllowedOrigins.join(", ");
                }

                if (d.startedAt) {
                    var started = new Date(d.startedAt);
                    if (!isNaN(started.getTime())) {
                        startedAtMs = started.getTime();
                        var startedValue = document.getElementById("started-value");
                        if (startedValue) {
                            startedValue.textContent = started.toLocaleString(undefined, {
                                dateStyle: "medium",
                                timeStyle: "short"
                            });
                        }
                        tickUptime();
                    }
                }
            })
            .catch(function () {
                if (uptimeSub) uptimeSub.textContent = "Metadata unavailable";
            });
    }

    checkHealth();
    loadInfo();
    setInterval(checkHealth, 15000);
    setInterval(tickUptime, 1000);
})();
