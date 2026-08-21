# TEST-003 — Defect log established
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** low
**Dependencies:** none
**Created:** 2026-08-20

---
## Purpose
workflow/quality/defects.md is in place with a clear intake process. At least one defect (real or seeded example) is recorded end-to-end with notification to the responsible agent. This ensures defects are tracked from day one, not retroactively.

**Ubiquitous language terms involved:**
None directly — this is quality infrastructure.

---
## Acceptance criteria
- [ ] workflow/quality/defects.md exists with the standard defect entry format
- [ ] Defect entry format includes: what happened, severity, found in which increment, owner notified
- [ ] Notification path confirmed: Coder for implementation defects, Delivery Lead for unclear ownership
- [ ] At least one defect (real, or a deliberately seeded example if none exist yet) recorded end-to-end
- [ ] Regression definition documented: a previously-passing test now failing without an intentional, documented behaviour change

---
## Technical notes
**SE:** None — this is a documentation and process increment, not code.

**Cyber:** None.

**UX:** None.

**Ops:** None.

**DDD:** None.

---
## Deployment validation
1. Verify workflow/quality/defects.md exists
2. Verify the file contains the standard defect entry format (as defined in test-engineer.md)
3. Verify at least one entry exists (seeded example if no real defects found yet)
4. Verify the entry includes all required fields: description, severity, increment, owner, status
5. Verify the notification path is documented (who gets notified for what)
