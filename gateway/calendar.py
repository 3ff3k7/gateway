from datetime import datetime
from typing import Optional

from ics import Calendar, Event


def create_event(
    summary: str,
    start: str,
    end: str,
    *,
    location: Optional[str] = None,
    description: Optional[str] = None,
) -> str:
    """Generate an iCalendar event.

    Parameters `start` and `end` should be ISO 8601 datetime strings.
    Returns the calendar as a string in ICS format.
    """
    event = Event()
    event.name = summary
    event.begin = datetime.fromisoformat(start)
    event.end = datetime.fromisoformat(end)
    if location:
        event.location = location
    if description:
        event.description = description

    cal = Calendar(events=[event])
    return cal.serialize()
