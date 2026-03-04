"""
Locust entry point.
Reads base_url and endpoint config from shared volume via task_builder.
"""
import json
import os

from locust import HttpUser, between
from task_builder import build_task_set


CONFIG_PATH = os.environ.get("LOCUST_CONFIG_DIR", "/app/results") + "/current_test.json"


def _read_base_url():
    try:
        with open(CONFIG_PATH, "r") as f:
            config = json.load(f)
        return config.get("base_url", "https://www.n11.com")
    except FileNotFoundError:
        return os.environ.get("TARGET_BASE_URL", "https://www.n11.com")


# Build dynamic TaskSet at import time (Locust requires class-level definitions)
DynamicTaskSet = build_task_set()


class DynamicUser(HttpUser):
    host = _read_base_url()
    tasks = [DynamicTaskSet]
    wait_time = between(1, 3)
