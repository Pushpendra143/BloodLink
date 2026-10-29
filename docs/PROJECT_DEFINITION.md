# Project definition (for the form)

**Project title:** BloodLinkD - Blood Donation and Emergency Request System

**Problem statement:**
When a patient urgently needs blood, families and hospitals usually depend on phone calls, WhatsApp
forwards and social media posts to find donors. This is slow, unreliable, and nobody checks whether the
donor is actually allowed to donate (minimum gap between donations, age limit, blood group
compatibility). BloodLinkD keeps one central record of donors, hospital requests and blood stock, and
automatically suggests donors who are in the same city, have a compatible blood group and are
medically eligible on that day.

**Users:**
1. Donor - registers, updates profile, checks eligibility, sees donation history
2. Hospital - registers, raises blood requests with urgency level
3. Blood bank admin - manages stock, records donations, views reports

**Main functionalities:**
- Donor and hospital registration with validation
- Emergency blood requests (NORMAL / URGENT / CRITICAL)
- Smart donor matching (same city + compatible group + eligible today)
- Donation recording with business rules (90 day gap, age 18-65, compatibility)
- Blood stock management and fulfilling requests from stock
- Dashboard report (donors, open requests, stock per blood group)

**Technologies:** Java 17, Spring Boot 3, Spring Data JPA, Spring Web Services (SOAP),
Bean Validation, H2 / MySQL, Maven, STS/Eclipse.

**RESTful services:** donors, hospitals, requests, donations, stock, reports (JSON, proper HTTP
status codes, global exception handling).

**SOAP service:** `CheckStock` (blood availability for a group) and `CheckEligibility` (can donor X
donate today) exposed with a WSDL at `/ws/bloodlink.wsdl`, so external hospital systems can integrate.

**Database:** SQL (H2 by default, MySQL supported by changing application.properties).
