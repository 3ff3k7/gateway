import os
import requests


class USCISClient:
    def __init__(self, base_url: str = "https://sandbox.api.uscis.gov", token: str | None = None):
        self.base_url = base_url.rstrip("/")
        # Token may be supplied via env var USCIS_API_TOKEN
        self.token = token or os.getenv("USCIS_API_TOKEN")
        self.session = requests.Session()

    def check_case_status(self, receipt: str) -> dict:
        """Retrieve case status for a USCIS receipt number.

        If no token is configured, returns a stubbed response. Otherwise
        performs an authenticated GET request to the official API.
        """
        if not receipt:
            raise ValueError("receipt number required")

        if not self.token:
            return {
                "receipt_number": receipt,
                "status": "PENDING",
                "message": "This is a stub. Provide USCIS_API_TOKEN to query the real API.",
            }

        headers = {"Authorization": f"Bearer {self.token}"}
        url = f"{self.base_url}/v1/case-status/{receipt}"
        resp = self.session.get(url, headers=headers, timeout=10)
        resp.raise_for_status()
        return resp.json()
