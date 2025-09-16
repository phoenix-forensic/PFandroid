"""Minimal Flask-SocketIO server used to exercise the realtime dashboard locally."""

from __future__ import annotations

import time
from typing import Dict

from flask import Flask, Response, jsonify
from flask_socketio import SocketIO

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*", async_mode="eventlet")

_PIXEL_BYTES = (
    b"\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06"
    b"\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\x0bIDATx\x9cc``\x00\x00\x00\x02\x00\x01"
    b"\xe2!\xbc3\x00\x00\x00\x00IEND\xaeB`\x82"
)


@app.route("/pixel/<tracker_id>.png")
def pixel(tracker_id: str) -> Response:
    """Return a transparent 1x1 PNG while emitting a realtime event."""
    payload: Dict[str, object] = {
        "lat": -23.5505,
        "lon": -46.6333,
        "risk": "spoof" if int(time.time()) % 2 == 0 else "low",
        "tracker_id": tracker_id,
    }
    socketio.emit("novo_evento", payload)
    return Response(
        _PIXEL_BYTES,
        mimetype="image/png",
        headers={"Cache-Control": "no-store, max-age=0"},
    )


@app.route("/health")
def health() -> Response:
    """Simple health endpoint used in CI."""
    return jsonify(status="ok")


if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5000)
