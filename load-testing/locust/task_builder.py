"""
Reads current_test.json from shared volume and dynamically builds
a Locust TaskSet with weighted tasks per endpoint.
"""
import json
import os
from locust import TaskSet, task


CONFIG_PATH = os.environ.get("LOCUST_CONFIG_DIR", "/app/results") + "/current_test.json"


def build_task_set():
    """
    Reads current_test.json and returns a TaskSet class with dynamically
    generated @task(weight) methods for each endpoint.
    """
    try:
        with open(CONFIG_PATH, "r") as f:
            config = json.load(f)
    except FileNotFoundError:
        raise RuntimeError(
            f"current_test.json not found at {CONFIG_PATH}. "
            "Backend must write config before starting Locust."
        )

    endpoints = config.get("endpoints", [])
    if not endpoints:
        raise RuntimeError("No endpoints defined in current_test.json")

    methods = {}
    for i, ep in enumerate(endpoints):
        path = ep.get("path", "/")
        method = ep.get("method", "GET").upper()
        weight = ep.get("weight", 1)
        query_params = ep.get("query_params", {})

        def make_task(p, m, qp):
            def endpoint_task(self):
                if m == "GET":
                    self.client.get(p, params=qp, name=p)
                elif m == "POST":
                    self.client.post(p, params=qp, name=p)
                elif m == "PUT":
                    self.client.put(p, params=qp, name=p)
                elif m == "DELETE":
                    self.client.delete(p, params=qp, name=p)
                else:
                    self.client.get(p, params=qp, name=p)
            return endpoint_task

        task_func = task(weight)(make_task(path, method, query_params))
        task_func.__name__ = f"task_{i}_{path.strip('/').replace('/', '_') or 'root'}"
        methods[task_func.__name__] = task_func

    DynamicTaskSet = type("DynamicTaskSet", (TaskSet,), methods)
    return DynamicTaskSet
