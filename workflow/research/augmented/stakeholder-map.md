# Stakeholder Map — Cricket Game
**Status:** published
**Date:** 2026-08-20

---

## Stakeholder types

### 1. The Cricket Fan / Player (Primary User)

**Who they are:** People who already understand cricket — they know what field placements mean, what a good length delivery is, and why you don't play a backfoot shot to a full ball. They own an Android phone (Android 12+). Initially UK-based, but the game targets a global audience.

**What they want:**
- A game that tests their real cricket knowledge, not just reflexes
- To feel like they're making genuine tactical decisions a real batsman would make
- A satisfying feedback loop: right decision → runs, wrong decision → out
- Replayability — different bowlers, grounds, conditions each time
- Fun, characterful players with personality (nicknames, quirks)
- A quick game session — T20 format means ~20 minutes per match, or just 1 over for free

**What they fear:**
- A game that's too easy or too random — decisions don't feel meaningful
- A game that requires too much time per session
- Being patronised with oversimplified cricket concepts
- Poor mobile UX — tiny buttons, laggy responses, hard to read on a small screen
- A paywall that feels predatory rather than generous

**How they are affected by the product:**
- This is the primary beneficiary. The product exists to entertain and challenge them.
- Their engagement (or lack of) determines whether the product succeeds.

---

### 2. The Game Developer / Creator (You)

**Who they are:** The person with the idea, providing the opportunity document. Likely a solo developer or small team.

**What they want:**
- To build a working game that cricket fans enjoy
- To get to a playable version as quickly as possible
- A clear technical path that doesn't overcomplicate things
- Feedback on whether the concept works before investing heavily
- A visual style that is appealing and modern but achievable with their resources

**What they fear:**
- Over-engineering the first version
- Building something nobody wants to play
- Getting stuck in planning without shipping
- Technical decisions that are hard to undo later
- Choosing a visual style that looks cheap or dated

**How they are affected by the product:**
- They are building it. Their time, skill, and resources are the constraint.
- Every architectural or scope decision directly affects their workload.

---

### 3. The Casual Observer / Recommender (Secondary User)

**Who they are:** Friends or family of cricket fans who might download the game based on a recommendation. May have less cricket knowledge.

**What they want:**
- A game they can pick up without deep cricket knowledge
- Clear enough UI that they can learn as they play
- Fun even if they don't fully understand the tactics
- Entertaining characters they can enjoy even without understanding cricket nuance

**What they fear:**
- Being completely lost because they don't know cricket terms
- Feeling excluded from a game designed only for experts

**How they are affected by the product:**
- They expand the user base beyond hardcore cricket fans.
- If the game is too niche, this audience is lost entirely.
- The character/nickname system may help bridge the gap — personality is universal.

---

### 4. The App Store / Platform (Distribution)

**Who they are:** Google Play Store (Android initially). The distribution channel.

**What they want:**
- Apps that follow their guidelines (content, performance, privacy)
- Apps that keep users engaged (drives store revenue if monetised)
- Good ratings and reviews

**What they fear:**
- Apps that crash, drain battery, or have security issues
- Violations of their policies (content, data handling, in-app purchases)

**How they are affected by the product:**
- The product must meet their technical and policy requirements to be listed.
- Their review process is a gate to reaching users.
- In-app purchase policies are relevant if freemium model is adopted.

---

### 5. Potential Advertisers / Monetisation Partners (If applicable)

**Who they are:** Companies that would pay to show ads in the game, or payment processors for in-app purchases.

**What they want:**
- Engaged users who see their ads or make purchases
- A game with enough retention to make advertising worthwhile

**What they fear:**
- Low user engagement making the investment worthless
- Brand safety issues

**How they are affected by the product:**
- Only relevant if the business model includes ads or IAP.
- The freemium model (1 over free, pay for more) is the likely direction.

---

### 6. Cricket Authorities / Content Partners (Potential)

**Who they are:** Cricket boards (e.g., ECB, BCCI, Cricket Australia), leagues (IPL, BBL, The Hundred), or grounds.

**What they want:**
- Control over how their brand, teams, or grounds are represented
- Revenue from licensing if their content is used

**What they fear:**
- Unauthorised use of team names or ground names
- Inaccurate or unflattering representation of the sport

**How they are affected by the product:**
- The game uses real teams and grounds (human confirmed). Licensing may be needed.
- Players are fictional, so player likeness rights are not an issue.

**Unknowns:**
- Whether real team names and ground names require licensing, or whether they can be used under fair use / nominative use.

---

## Stakeholder tension map

| Tension | Between | Nature |
|---|---|---|
| Depth vs accessibility | Cricket Fan ↔ Casual Observer | Making the game deep enough for experts while accessible to newcomers. The character/nickname system may help bridge this. |
| Speed to market vs features | Developer ↔ All users | Shipping early vs shipping complete. The "try some things" approach to visual style suggests prototyping before committing. |
| Free vs monetised | Developer ↔ Users | Users want generous free experience; developer needs revenue. The "1 over free" model is a starting point but may not convert well. |
| Real content vs licensing cost | Developer ↔ Cricket Authorities | Real teams/grounds are more engaging but may require licensing. |
| Simplicity vs realism | Developer ↔ Cricket Fan | Fans want realistic cricket (surface physics, bowler types, shot variety); simplicity ships faster. The surface coefficient model is already ambitious. |
| Character fun vs cricket authenticity | Developer ↔ Cricket Fan | Nicknames and quirks add fun but may feel gimmicky to serious cricket fans. Balance is key. |

---

## Summary

The primary stakeholder is the cricket-knowledgeable mobile game player, initially UK-based, globally targeted. The product's success depends on whether their cricket knowledge feels valued and challenged by the game mechanics, and whether the characterful players add entertainment without undermining authenticity.

**Key insight from human responses:** The game is more character-driven than initially apparent. Fictional players with nicknames, quirks, and personality are a first-class feature, not an afterthought. This aligns well with a comic book visual style and has implications for UX (card-based player info), domain modelling (player entity with personality attributes), and the overall tone of the game.
