from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

# We store the database in the shared results directory so it persists across container restarts
SQLALCHEMY_DATABASE_URL = "sqlite:////app/results/history.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

# Dependency to get DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
