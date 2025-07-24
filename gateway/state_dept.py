import requests
from bs4 import BeautifulSoup

CEAC_STATUS_URL = "https://ceac.state.gov/CEACStatTracker/Status.aspx?App=NIV"


def check_ceac_status(case_number: str, visa_type: str = "IV") -> dict:
    """Retrieve case status from the State Department CEAC page.

    This implementation attempts a form POST and parses the resulting
    HTML page. Many requests require solving a CAPTCHA, so if scraping
    fails a stubbed response is returned.
    """
    if not case_number:
        raise ValueError("case_number required")

    form = {"CaseNumber": case_number, "VisaType": visa_type}
    try:
        resp = requests.post(CEAC_STATUS_URL, data=form, timeout=10)
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "html.parser")
        elem = soup.select_one("#ctl00_ContentPlaceHolder1_ucApplicationStatus_lblStatus")
        status = elem.get_text(strip=True) if elem else "UNKNOWN"
        return {"case_number": case_number, "status": status}
    except Exception:
        return {
            "case_number": case_number,
            "status": "UNKNOWN",
            "message": "CEAC scraping not yet implemented",
        }
