from pydantic import BaseModel
from typing import List, Optional, Dict

from .TicketModel import TicketModel


class ClientModel(BaseModel):
    email: str
    firstName: Optional[str] = None
    lastName: Optional[str] = None
    isNamePublic: bool = False
    socialMedia: Optional[Dict[str, str]] = None  # e.g., {"linkedin": "url", "twitter": "url"}
    tickets: List[TicketModel] = []