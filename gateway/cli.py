import argparse
import json

from .uscis import USCISClient
from .foia import FOIAClient
from .processing_times import get_processing_time
from .state_dept import check_ceac_status
from .calendar import create_event
from .models import CaseStatus, FoiaResult, ProcessingTime, CeacStatus


def main():
    parser = argparse.ArgumentParser(description="Gateway CLI")
    sub = parser.add_subparsers(dest="command")

    cs = sub.add_parser("case-status", help="Check USCIS case status")
    cs.add_argument("receipt")

    foia_create = sub.add_parser("foia-create", help="Create FOIA request")
    foia_create.add_argument("data", help="JSON payload for FOIA request")

    foia_status = sub.add_parser("foia-status", help="Check FOIA status")
    foia_status.add_argument("request_id")

    proc = sub.add_parser("processing-time", help="Get processing time")
    proc.add_argument("form")
    proc.add_argument("office")

    ceac = sub.add_parser("ceac-status", help="Check CEAC status")
    ceac.add_argument("case_number")

    cal = sub.add_parser("create-ics", help="Create calendar event")
    cal.add_argument("summary")
    cal.add_argument("start")
    cal.add_argument("end")
    cal.add_argument("--location")
    cal.add_argument("--description")
    cal.add_argument("--output", default="event.ics")

    args = parser.parse_args()

    if args.command == "case-status":
        client = USCISClient()
        result = client.check_case_status(args.receipt)
    elif args.command == "foia-create":
        client = FOIAClient()
        payload = json.loads(args.data)
        result = client.create_request(payload)
    elif args.command == "foia-status":
        client = FOIAClient()
        result = client.check_status(args.request_id)
    elif args.command == "processing-time":
        result = get_processing_time(args.form, args.office)
    elif args.command == "ceac-status":
        result = check_ceac_status(args.case_number)
    elif args.command == "create-ics":
        ics_text = create_event(
            args.summary,
            args.start,
            args.end,
            location=args.location,
            description=args.description,
        )
        with open(args.output, "w", encoding="utf-8") as f:
            f.write(ics_text)
        result = {"message": f"ICS event written to {args.output}"}
    else:
        parser.print_help()
        return

    if isinstance(result, (CaseStatus, FoiaResult, ProcessingTime, CeacStatus)):
        result = result.to_dict()
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
