# Suggested Reading — Cricket Game
**Date:** 2026-08-20

---

## Domain and background reading

| Source | URL / reference | Why relevant | Priority |
|---|---|---|---|
| Laws of Cricket (MCC) | https://www.lords.org/mcc/the-laws-of-cricket | The authoritative rules of cricket. Defines overs, dismissals, fielding positions, and T20 format rules. | High |
| Wikipedia: Batting (cricket) | https://en.wikipedia.org/wiki/Batting_(cricket) | Comprehensive list of cricket shots with descriptions. Already used to identify shot types. | Done |
| Wikipedia: Bowling (cricket) | https://en.wikipedia.org/wiki/Bowling_(cricket) | Bowler types (fast, medium, spin), bowling styles, and characteristics. Needed for OQ-18 (bowler types). | High |
| Wikipedia: Fielding positions | Search: "Cricket fielding positions" | Defines standard field placements. Needed to understand predefined field sets. | Medium |
| T20 cricket format | Search: "Twenty20 cricket rules" | Specific T20 rules: fielding restrictions, powerplay, bowler limits (max 4 overs per bowler). | High |

## Competitor or comparable products

| Product | URL | Relevance | What to look for |
|---|---|---|---|
| Real Cricket (Nautilus Mobile) | Google Play Store | Most popular mobile cricket game. Real-time batting simulation. | How they handle shot selection UI, difficulty progression, match structure, and monetisation. |
| World Cricket Championship (Nextwave Multimedia) | Google Play Store | Another major mobile cricket game. | Visual style, monetisation model, audience engagement patterns. |
| Stick Cricket (Stick Sports Ltd) | Google Play Store | Simplified cricket game with minimal controls. Good for understanding casual cricket game design. | How they simplify cricket for casual play. Shot selection UX. |
| Cricket Captain (Childish Things) | PC/Mobile | Management/simulation game. Closer to tactical depth this game aims for. | How they present cricket decision-making without real-time action. Player characterisation. |
| Retro Bowl (New Star Games) | Mobile | Not cricket, but a character-driven sports game with personality, nicknames, and a comic-book feel. Very relevant to the "character" aspect. | How they make fictional players entertaining and memorable. Card-based player info. Freemium model. |

## Regulatory or compliance references

| Reference | URL | Applies because |
|---|---|---|
| Google Play Store policies | https://play.google.com/about/developer-content-policy/ | Required for Android distribution. Covers content, privacy, ads, and monetisation rules. |
| Google Play data safety requirements | https://support.google.com/googleplay/android-developer/answer/10787469 | If the game collects any user data (even analytics), this must be declared. |
| Google Play in-app purchase policies | https://support.google.com/googleplay/android-developer/answer/10228932 | Relevant if freemium model with in-app purchases is adopted. |

## Technical references

| Reference | URL | Why relevant |
|---|---|---|
| Android game development overview | https://developer.android.com/games | Official Android game development guide. Covers game engines, performance, and distribution. |
| Unity for Android | https://docs.unity3d.com/Manual/android.html | If the visual mode uses 3D or rich 2D, Unity is a common choice for Android games. |
| LibGDX | https://libgdx.com/ | A Java/Kotlin game framework for Android. Lighter weight than Unity. Good for 2D games. |
| Jetpack Compose for Android | https://developer.android.com/jetpack/compose | If the game is more UI-driven (menus, cards, text) than animation-driven, Compose may be sufficient. |
| Godot Engine | https://godotengine.org/ | Open-source game engine. Good for 2D. Lighter than Unity. Android export support. |
| Android API level 31 (Android 12) | https://developer.android.com/about/versions/12 | The minimum API level. Needed to confirm available APIs and features. |

## Open questions this reading may help answer

| Question | Relevant sources |
|---|---|
| OQ-17: Which shot types to include | Wikipedia: Batting (cricket), competitor analysis (Real Cricket, Stick Cricket) |
| OQ-18: What bowler types exist | Wikipedia: Bowling (cricket), T20 cricket format rules |
| OQ-22: What visual style to prototype | Competitor products (Real Cricket, Stick Cricket, Retro Bowl), game engine comparisons |
| OQ-23: How does freemium lock work | Retro Bowl (freemium model), Google Play IAP policies |
| OQ-25: T20 bowling restrictions | T20 cricket format rules |
| OQ-06: Architecture for future server features | Android game development overview, backend service options |
