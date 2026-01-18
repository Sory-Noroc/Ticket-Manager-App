from pydantic import BaseModel
from typing import List, Optional


# This model is for events listed inside a package
class EventInfo(BaseModel):
    id: Optional[int] = None # Added for querying
    name: str
    location: str


class TicketModel(BaseModel):
    code: str
    eventName: str
    eventLocation: str
    isPackage: bool = False
    eventID: Optional[int] = None # Added for querying
    groupID: Optional[int] = None # Added for querying
    includedEvents: Optional[List[EventInfo]] = None  # For packages
