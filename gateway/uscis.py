import os

import requests

from .models import CaseStatus


class USCISClient:
    def __init__(self, base_url: str = "https://sandbox.api.uscis.gov", token: str | None = None):
        self.base_url = base_url.rstrip("/")
        # Token may be supplied via env var USCIS_API_TOKEN
        self.token = token or os.getenv("USCIS_API_TOKEN")
        self.session = requests.Session()

    def check_case_status(self, receipt: str) -> CaseStatus:
        """Retrieve case status for a USCIS receipt number.

        Returns a :class:`CaseStatus` object populated with the API
        response. Network errors and HTTP errors are caught and returned
        in the ``message`` field.
        """

        if not receipt:
            raise ValueError("receipt number required")

        if not self.token:
            return CaseStatus(
                receipt_number=receipt,
                status="PENDING",
                message="This is a stub. Provide USCIS_API_TOKEN to query the real API.",
            )

        headers = {"Authorization": f"Bearer {self.token}"}
        url = f"{self.base_url}/v1/case-status/{receipt}"

        try:
            resp = self.session.get(url, headers=headers, timeout=10)
            if resp.status_code == 404:
                return CaseStatus(
                    receipt_number=receipt,
                    status="NOT_FOUND",
                    message="Receipt number not found",
                )
            resp.raise_for_status()
            data = resp.json()
            return CaseStatus(
                receipt_number=receipt,
                status_code=data.get("statusCode"),
                status=data.get("status"),
                message=data.get("statusMessage"),
                raw=data,
            )
        except requests.RequestException as exc:
            return CaseStatus(
                receipt_number=receipt,
                status="ERROR",
                message=str(exc),
            )
