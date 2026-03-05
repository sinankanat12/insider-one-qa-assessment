from sqlalchemy import Column, Integer, String, Float, DateTime
from datetime import datetime
from app.database import Base

class TestHistory(Base):
    __tablename__ = "test_history"

    id = Column(Integer, primary_key=True, index=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    base_url = Column(String, index=True)
    endpoints = Column(Integer)
    duration_seconds = Column(Integer)
    
    # Metrics
    num_requests = Column(Integer, default=0)
    num_failures = Column(Integer, default=0)
    avg_rps = Column(Float, default=0.0)
    avg_response_time_ms = Column(Float, default=0.0)
    p95_response_time_ms = Column(Float, default=0.0)
    failure_rate_pct = Column(Float, default=0.0)
