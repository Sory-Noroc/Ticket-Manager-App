from pydantic import BaseModel
from typing import List, Optional


# This model is for events listed inside a package
class EventInfo(BaseModel):
    name: str
    location: str


class TicketModel(BaseModel):
    code: str
    eventName: str
    eventLocation: str
    isPackage: bool = False
    includedEvents: Optional[List[EventInfo]] = None  # For packages
