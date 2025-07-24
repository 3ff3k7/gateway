"""Gateway API integration library."""

from .uscis import USCISClient
from .foia import FOIAClient
from .processing_times import get_processing_time
from .state_dept import check_ceac_status
from .calendar import create_event
from .models import (
    CaseStatus,
    FoiaResult,
    ProcessingTime,
    CeacStatus,
)


def check_case_status(receipt: str) -> CaseStatus:
    """Convenience wrapper around :class:`USCISClient`."""
    client = USCISClient()
    return client.check_case_status(receipt)


def create_foia_request(data: dict) -> FoiaResult:
    """Convenience wrapper to submit a FOIA request."""
    client = FOIAClient()
    return client.create_request(data)


def check_foia_status(request_id: str) -> FoiaResult:
    """Convenience wrapper to query a FOIA request."""
    client = FOIAClient()
    return client.check_status(request_id)

__all__ = [
    "USCISClient",
    "FOIAClient",
    "get_processing_time",
    "check_ceac_status",
    "create_event",
    "CaseStatus",
    "FoiaResult",
    "ProcessingTime",
    "CeacStatus",
    "check_case_status",
    "create_foia_request",
    "check_foia_status",
]
