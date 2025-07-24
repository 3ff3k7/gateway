import requests

class FOIAClient:
    def __init__(self, base_url="https://sandbox.api.uscis.gov", token=None):
        self.base_url = base_url.rstrip("/")
        self.token = token

    def create_request(self, data):
        """Create a FOIA request.
        `data` should contain the fields required by the USCIS FOIA API.
        Currently returns a dummy request id.
        """
        if not isinstance(data, dict):
            raise ValueError("data must be a dict")
        return {
            "request_id": "DUMMY-REQUEST-ID",
            "message": "FOIA request creation not yet implemented"
        }

    def check_status(self, request_id):
        """Retrieve status for a FOIA request. Returns dummy data for now."""
        if not request_id:
            raise ValueError("request_id required")
        return {
            "request_id": request_id,
            "status": "PENDING",
            "message": "Connect to the USCIS FOIA API to get real status"
        }
