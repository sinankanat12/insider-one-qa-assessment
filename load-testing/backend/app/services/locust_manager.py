"""
Manages the Locust subprocess lifecycle.
State machine: idle → starting → running → stopped / error
"""
import asyncio
import json
import os
import signal
import subprocess
import time
from datetime import datetime
from typing import Literal

from app.core.config import settings
from app.models.schemas import StartTestRequest

TestState = Literal["idle", "starting", "running", "stopped", "error"]


class LocustManager:
    def __init__(self):
        self._process: subprocess.Popen | None = None
        self._state: TestState = "idle"
        self._start_time: float | None = None
        self._error_message: str | None = None
        self._monitor_task: asyncio.Task | None = None

    @property
    def state(self) -> TestState:
        return self._state

    @property
    def pid(self) -> int | None:
        return self._process.pid if self._process else None

    @property
    def uptime_seconds(self) -> float | None:
        if self._start_time is None:
            return None
        return time.monotonic() - self._start_time

    @property
    def error_message(self) -> str | None:
        return self._error_message

    def _write_config(self, request: StartTestRequest) -> None:
        os.makedirs(settings.locust_config_dir, exist_ok=True)
        config_path = os.path.join(settings.locust_config_dir, "current_test.json")
        config = {
            "base_url": request.base_url,
            "endpoints": [ep.model_dump() for ep in request.endpoints],
            "user_count": request.user_count,
            "spawn_rate": request.spawn_rate,
            "duration_seconds": request.duration_seconds,
            "started_at": datetime.utcnow().isoformat() + "Z",
        }
        with open(config_path, "w") as f:
            json.dump(config, f, indent=2)

    def _kill_existing(self) -> None:
        if self._process is None:
            return
        try:
            self._process.terminate()
            try:
                self._process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                self._process.kill()
                self._process.wait()
        except (ProcessLookupError, OSError):
            pass
        finally:
            self._process = None
        if self._monitor_task and not self._monitor_task.done():
            self._monitor_task.cancel()

    async def start(self, request: StartTestRequest) -> None:
        self._kill_existing()
        self._error_message = None

        self._write_config(request)
        self._state = "starting"

        locustfile = os.path.join(
            os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
            "locust",
            "locustfile.py",
        )
        # When running inside Docker, locustfile is at /locust/locustfile.py
        # Fall back to path inside container if dev path doesn't exist
        if not os.path.exists(locustfile):
            locustfile = "/locust/locustfile.py"

        stats_output = os.path.join(settings.locust_results_dir, "locust_stats.json")
        log_file = os.path.join(settings.locust_results_dir, "locust.log")

        cmd = [
            "locust",
            "-f", locustfile,
            "--headless",
            "--host", request.base_url,
            "-u", str(request.user_count),
            "-r", str(request.spawn_rate),
            "-t", f"{request.duration_seconds}s",
            "--json",
            "--logfile", log_file,
            "--loglevel", "INFO",
        ]

        env = os.environ.copy()
        env["LOCUST_CONFIG_DIR"] = settings.locust_config_dir
        env["LOCUST_RESULTS_DIR"] = settings.locust_results_dir

        os.makedirs(settings.locust_results_dir, exist_ok=True)

        with open(stats_output, "w") as stats_f:
            self._process = subprocess.Popen(
                cmd,
                stdout=stats_f,
                stderr=subprocess.PIPE,
                env=env,
            )

        self._start_time = time.monotonic()
        self._state = "running"

        loop = asyncio.get_event_loop()
        self._monitor_task = loop.create_task(self._monitor_process())

    async def _monitor_process(self) -> None:
        while True:
            await asyncio.sleep(5)
            if self._process is None:
                break
            retcode = self._process.poll()
            if retcode is not None:
                if self._state == "running":
                    if retcode == 0:
                        self._state = "stopped"
                    else:
                        self._state = "error"
                        stderr = b""
                        try:
                            stderr = self._process.stderr.read()
                        except Exception:
                            pass
                        self._error_message = (
                            f"Locust exited with code {retcode}: "
                            + stderr.decode(errors="replace")[:500]
                        )
                break

    async def stop(self) -> None:
        self._kill_existing()
        if self._state == "running":
            self._state = "stopped"
        self._start_time = None


locust_manager = LocustManager()
