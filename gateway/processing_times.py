import requests

API_URL = "https://egov.uscis.gov/processing-times/api/"

def get_processing_time(form_number, office_code):
    """Fetch processing time information for a form and office.

    This implementation uses the public USCIS Processing Times API
    which does not require authentication. In the MVP this function
    will likely need to issue multiple requests to list form types and
    field offices. For now it returns a dummy response.
    """
    if not form_number or not office_code:
        raise ValueError("form_number and office_code required")

    # Example request (commented out):
    # resp = requests.get(f"{API_URL}forms/{form_number}/offices/{office_code}")
    # resp.raise_for_status()
    # return resp.json()

    return {
        "form_number": form_number,
        "office_code": office_code,
        "processing_time": "N/A",
        "message": "Processing time API integration pending"
    }
