from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    target_base_url: str = "https://www.n11.com"
    default_user_count: int = 1
    default_spawn_rate: int = 1
    default_duration_seconds: int = 60

    locust_results_dir: str = "/app/results"
    locust_config_dir: str = "/app/results"

    backend_host: str = "0.0.0.0"
    backend_port: int = 8000
    app_env: str = "production"
    log_level: str = "info"

    cors_allowed_origins: str = "http://localhost:3000,http://load-frontend:80"

    @property
    def cors_origins_list(self) -> list[str]:
        return [o.strip() for o in self.cors_allowed_origins.split(",")]


settings = Settings()
