import os
import requests

from .models import FoiaResult


class FOIAClient:
    def __init__(self, base_url: str = "https://sandbox.api.uscis.gov", token: str | None = None):
        self.base_url = base_url.rstrip("/")
        self.token = token or os.getenv("USCIS_API_TOKEN")
        self.session = requests.Session()

    def create_request(self, data: dict) -> FoiaResult:
        """Create a FOIA request via the USCIS API.

        Returns a stub if authentication is not configured.
        """
        if not isinstance(data, dict):
            raise ValueError("data must be a dict")

        if not self.token:
            return FoiaResult(
                request_id="DUMMY-REQUEST-ID",
                message="FOIA request creation not yet implemented",
            )

        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json",
        }
        url = f"{self.base_url}/v1/foia-requests"
        try:
            resp = self.session.post(url, json=data, headers=headers, timeout=10)
            resp.raise_for_status()
            payload = resp.json()
            return FoiaResult(
                request_id=payload.get("request_id", "UNKNOWN"),
                status=payload.get("status"),
                message=payload.get("statusMessage"),
                raw=payload,
            )
        except requests.RequestException as exc:
            return FoiaResult(request_id="UNKNOWN", status="ERROR", message=str(exc))

    def check_status(self, request_id: str) -> FoiaResult:
        """Retrieve status for a FOIA request."""
        if not request_id:
            raise ValueError("request_id required")

        if not self.token:
            return FoiaResult(
                request_id=request_id,
                status="PENDING",
                message="Connect to the USCIS FOIA API to get real status",
            )

        headers = {"Authorization": f"Bearer {self.token}"}
        url = f"{self.base_url}/v1/foia-requests/{request_id}"
        try:
            resp = self.session.get(url, headers=headers, timeout=10)
            if resp.status_code == 404:
                return FoiaResult(
                    request_id=request_id,
                    status="NOT_FOUND",
                    message="Request ID not found",
                )
            resp.raise_for_status()
            payload = resp.json()
            return FoiaResult(
                request_id=request_id,
                status=payload.get("status"),
                message=payload.get("statusMessage"),
                raw=payload,
            )
        except requests.RequestException as exc:
            return FoiaResult(request_id=request_id, status="ERROR", message=str(exc))
