---
description: Push back on a Platform Engineer deployment sign-off before it is accepted
agent: platform-engineer
---

@platform-engineer before this sign-off is recorded, treat it as challenged, not automatic.

Re-check the release note against the pipeline run log and PVT results directly — do not accept the Coder's summary of them at face value. Confirm specifically:

1. The service actually started (PVT passed at startup) — not just that the deploy step reported success.
2. Every new PVT assertion listed in the release note is present in the pipeline run and actually failed the build when disabled (spot-check, don't assume).
3. Observability requirements from your own `platform-engineer.md` output are met for this increment, not deferred silently.

If any of this does not hold, record `pe-rejected` with the specific gap, not a soft caveat on an approval. A rejection here is cheaper than a `validated` increment that has to be unwound later — see `workflow/retrospective/outcome-current.md` for why this check exists.

Only once this holds up under your own re-verification should `pe-signed-off` be recorded on the release note, per the PE rejection loop in `.opencode/skills/workflow.md`.
