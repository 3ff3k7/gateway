from dataclasses import dataclass, asdict
from typing import Optional

@dataclass
class CaseStatus:
    receipt_number: str
    status_code: Optional[str] = None
    status: Optional[str] = None
    message: Optional[str] = None
    raw: Optional[dict] = None

    def to_dict(self) -> dict:
        return asdict(self)

@dataclass
class FoiaResult:
    request_id: str
    status: Optional[str] = None
    message: Optional[str] = None
    raw: Optional[dict] = None

    def to_dict(self) -> dict:
        return asdict(self)

@dataclass
class ProcessingTime:
    form_number: str
    office_code: str
    processing_time: Optional[str] = None
    message: Optional[str] = None
    raw: Optional[dict] = None

    def to_dict(self) -> dict:
        return asdict(self)

@dataclass
class CeacStatus:
    case_number: str
    status: str
    message: Optional[str] = None

    def to_dict(self) -> dict:
        return asdict(self)
