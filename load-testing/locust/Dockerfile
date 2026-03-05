FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY locustfile.py task_builder.py ./

# Shared volume mount point
RUN mkdir -p /app/results

CMD ["locust", "-f", "locustfile.py", "--web-port", "8089"]
