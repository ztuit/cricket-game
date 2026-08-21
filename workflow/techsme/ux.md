# UX Guidelines — Cricket Game
**Status:** draft

---

## Full Technical Detail

### User types

| User type | Technical level | Context of use | Primary goal | Key frustration |
|---|---|---|---|---|
| **Cricket Fan (Primary)** | Medium — comfortable with Android, not a developer | Commute, sofa, short breaks (~20 min sessions) | Test cricket knowledge through tactical shot decisions; feel the tension of a real innings | Being punished by invisible mechanics; feeling the game is random rather than skill-based |
| **Casual Observer (Secondary)** | Low-medium — may not know cricket deeply | Recommended by a friend; picks up to try | Have fun with characters and the drama of batting; learn cricket concepts organically | Being overwhelmed by jargon or options they don't understand; feeling excluded |
| **Solo Developer (Creator)** | High — builds the game | Development and testing | Validate the core loop is fun before investing further | Scope creep from UX complexity; visual style decisions blocking development |

### Interaction technology decisions (binding)

| Concern | Decision | Rationale |
|---|---|---|
| UI framework | **Jetpack Compose** — declarative UI for all chrome, menus, overlays, and non-gameplay screens | SE decision. Android standard. StateFlow integration is natural. Compose handles async state updates from domain events natively. |
| Gameplay rendering | **Compose Canvas** for MVP 1 (placeholder); rendering interface supports swap to sprite-based or engine rendering if comic book style is chosen | SE decision (ADR-001). Game engine is decoupled from rendering via `GameRenderer` interface. Visual style spike happens early in MVP 1 — Canvas is the placeholder, not the commitment. |
| Gesture handling | **Compose gesture APIs** (pointerInput, detectDragGestures, detectTapGestures) for shot selection and wrist angle input | No custom touch handling needed. Compose gestures map cleanly to the interaction model defined below. The input layer feeds a `ShotSelection` value object — the game engine doesn't care how input was collected. |
| Responsive strategy | **Phone-first (portrait orientation)**. Design for 5.5"–6.7" screens at 1080p. Layout adapts via Compose's responsive layout system (BoxWithConstraints, weight-based sizing). Tablet-optimised layouts deferred. | Vision explicitly scopes to phones. Tablets are a future concern. Portrait orientation suits the vertical flow of the delivery loop (info at top, action at bottom). |
| Accessibility standard | **WCAG 2.1 Level AA** — specifically: contrast ratios ≥ 4.5:1 for body text, ≥ 3:1 for large text and UI components; touch targets ≥ 48dp; support for Android system font scaling up to 200%; all interactive elements have content descriptions for TalkBack | Android 12+ has strong accessibility tooling. WCAG AA is the standard Google Play recommends. Level AAA is aspirational but not blocking for MVP 1. The time-pressure mechanic inherently challenges some WCAG principles (timing) — mitigation below. |
| Browser / platform support | **Android 12+ (API 31+) only**. No iOS, no web, no tablet-optimised layouts. Minimum SDK 31. Target SDK 35 (latest stable). | Vision and SE alignment. Offline-first architecture. iOS is a future platform decision. |

### Information architecture

#### Key screens and views

**1. Home Screen**
- Match summary (last match result, if any)
- "Play" CTA — launches a new match
- Settings gear icon
- Future: team/progression entry point (MVP 3)

**2. Pre-Match Flow**
- Ground selection (visual cards with ground name, location, typical weather)
- Toss animation (coin flip — tap to stop)
- Toss decision (Bat / Field) if player wins
- Target announcement if player loses toss ("The opposition scored 165")
- Bowler roster preview (list of bowler names and types — NOT full character cards yet)

**3. Delivery Loop Screen (the core gameplay screen)**
This is where 90% of the player's time is spent. It must be information-dense but not cluttered. The layout has three zones:

```
┌─────────────────────────────────┐
│  SCOREBOARD ZONE (top bar)      │
│  Score / Overs / Wickets / Tgt  │
├─────────────────────────────────┤
│                                 │
│  FIELD PLACEMENT ZONE           │
│  (top-down ground view showing  │
│   fielder positions)            │
│                                 │
│  BOWLER INFO (overlay)          │
│  Nickname, type, visible trait  │
│                                 │
│  DELIVERY INFO (after ball)     │
│  Line, length, pace indicator   │
│                                 │
│  SURFACE CONDITION (subtle)     │
│  Pitch zone indicators          │
│                                 │
├─────────────────────────────────┤
│  ACTION ZONE (bottom)           │
│  Shot Type selection +          │
│  Wrist Angle input              │
└─────────────────────────────────┘
```

**4. Outcome Screen (between deliveries)**
- Animated result: runs scored, boundary highlight, or dismissal
- Brief delivery summary: "Short ball, outside off — Pull shot, top edge, Caught at fine leg"
- "Next delivery" tap to continue (auto-advances after 2 seconds if no tap)

**5. Over Break Screen**
- Over summary (runs, wickets)
- New bowler announcement (nickname, type, character card preview)
- Surface condition update (subtle — "The pitch is starting to wear on a good length")
- Tap to continue

**6. Innings End / Match Result Screen**
- Final score, target, result
- Innings scorecard (ball-by-ball: delivery → shot → outcome)
- "Play again" / "Home"

**7. Character Card (overlay, accessible at any time)**
- Bowler nickname, type, experience class, quirk
- Visual portrait (placeholder in MVP 1)
- Tactical notes derived from stats (not raw numbers)

#### Navigation model

- **Linear flow:** Home → Pre-Match → Delivery Loop → Result
- **No back navigation during a match.** Once a match starts, the only way out is to finish or forfeit (confirm dialog). This prevents accidental loss of match state.
- **Character Card:** accessible via a tap on the bowler's name/portrait during the delivery loop. Opens as a bottom sheet overlay. Dismissed by swiping down or tapping outside.
- **Settings:** accessible from Home only during MVP 1. Future: pause menu during match.

#### How domain objects are presented

| Domain object | UI presentation | Naming decision |
|---|---|---|
| **Match** | Scoreboard bar: "MI vs CSK" or "Your Innings" | Uses ground/team name where applicable |
| **Over** | Over counter in scoreboard: "Over 12 of 20" | "Over" — follows ubiquitous language |
| **Delivery** | Not named in UI. Shown as the action: ball animation + outcome | Deliberately hidden — "delivery" is the domain term, but the player experiences "a ball being bowled" |
| **Bowler** | Name + nickname prominently shown. Type shown as icon/label | Nickname is primary identifier: "The Magician" not "Leg-Spin Bowler" |
| **Batsman** | Not named in MVP 1 (single batsman). Score shown in aggregate | Deferred to MVP 2 when batsman has character card |
| **Field Placement** | Top-down diagram showing 11 fielder dots on a green oval | "Field" in casual context. Diagram is the primary communication — not text |
| **Fielder** | Dot on the field diagram. Coloured by zone (close catchers = red, boundary = blue) | Not individually named |
| **Shot Type** | Action button in the action zone. Icon + label (e.g., "Drive", "Pull") | Follows ubiquitous language: "Drive", "Pull", "Cut", etc. |
| **Wrist Angle** | Swipe gesture on a visual arc indicator. No degrees shown — directional zones (Straight, Off Side, Leg Side) | **Deliberately diverges from domain term.** "Wrist angle" is the domain concept; the UI presents it as "Where do you want to hit it?" via a directional swipe. The domain term would confuse non-cricket players. |
| **Ball Characteristics** | Brief text + visual indicator after delivery: "Short, outside off, fast" | Uses cricket terms but keeps them short. "Short" not "Short length". |
| **Surface Condition** | Colour-coded zones on the pitch diagram. Green = good, amber = worn, red = degraded. Subtle — not the focus. | **Deliberately diverges from "coefficient."** The Customer hates jargon. The UI shows "The pitch is wearing on a good length" as flavour text, with the colour-coded diagram as the primary communication. Cricket fans will read the diagram; casual players will read the text. |
| **Pitch** | Shown as part of the field diagram — the central strip | "Pitch" — follows ubiquitous language |
| **Weather** | Shown as an icon in the scoreboard zone: sun, cloud, rain | "Weather" — follows ubiquitous language |
| **Target** | Scoreboard: "Target: 165" or "You need 23 from 18 balls" | "Target" — follows ubiquitous language |
| **Runs** | Scoreboard: large number. Outcome animation shows runs gained | "Runs" — follows ubiquitous language |
| **Wicket** | Scoreboard: wicket counter (e.g., "3/10" or visual icons). Dismissal shown with animation | "Wickets" in scoreboard. Dismissal type shown in outcome: "Caught at slip" |
| **Dismissal** | Outcome screen: "OUT — Caught at Cover" with animation | "Out" in casual UI. Full dismissal type in scorecard review. |
| **Outcome** | Brief animation + text. "4 runs" / "SIX!" / "OUT — Bowled" | Not a single UI element — it's the delivery result moment |
| **Innings Progress** | Scoreboard bar — always visible during delivery loop | Composite display, not a named concept in UI |
| **Character Card** | Bottom sheet overlay with portrait, name, stats, quirk | "Character Card" — follows ubiquitous language. Accessible by tapping bowler name. |
| **Toss** | Coin flip animation during pre-match | "Toss" — follows ubiquitous language |
| **Nickname** | Primary bowler identifier on screen: "THE MAGICIAN" in bold | Nickname is the UI-facing name. Bowler type is secondary. |
| **Quirk** | Shown on character card only. Flavour text, not tactical | "Quirk" not shown in UI label — just the text itself |
| **Experience Class** | Shown on character card as a star rating or tier label: "Elite" | "Elite" / "Established" / "Rookie" — follows ubiquitous language |

### Design principles

1. **Cricket knowledge is the skill, not the UI.** Every design decision asks: "Does this reward understanding of cricket, or does it reward fast thumbs?" The answer must always be the former. If a UI element exists that a non-cricket-fan can exploit as easily as a cricket fan, it's wrong.

2. **Show enough to decide, not enough to calculate.** The player should see enough information to make an informed choice — field placement, surface conditions, bowler type — but not so much that they can mathematically optimise every decision. The game is about judgement under uncertainty, not spreadsheet cricket. Surface conditions are visible but imprecise. Spin information is deliberately partial.

3. **Time pressure creates tension, not frustration.** The delivery timer exists to prevent analysis paralysis, not to punish slow readers. The timer should feel like the bowler running in — inevitable but not instant. The player should have enough time to read the field, glance at conditions, and commit to a shot. If they're scrambling to find the button, the timer is too short or the layout is too cluttered.

4. **The character system is personality, not decoration.** The bowler's nickname, quirk, and character card are part of the decision-making, not just flavour text. "The Magician" implies spin and variation. "Golden Arm" implies accuracy. The UI should make characters feel like opponents, not stats.

5. **Outcomes teach.** Every dismissal should leave the player thinking "I know what I did wrong" or "I couldn't have known that — now I can." The outcome moment must include enough information to learn from: what the ball did, what shot was played, and why it failed. A dismissal that feels random is a design failure.

6. **The pitch is alive — let the player see it breathe.** The surface condition model is the game's deepest feature and its biggest risk. If it's invisible, it's unfair. If it's a spreadsheet, it's not cricket. The pitch should be shown as a living thing — colour-coded zones that shift, subtle text hints ("the pitch is breaking up on a good length"), and enough visual language that a cricket fan can read it like a real pitch.

7. **Phone-first, thumb-friendly.** All primary actions reachable with one thumb in the bottom third of the screen. No critical UI element in the top 100dp. No gesture that requires two hands during gameplay. The action zone is always at the bottom.

### Look and feel

#### Visual style recommendation: Comic book — with a phased approach

The Customer and vision both lean toward character-driven presentation. A comic book style has the highest ceiling for the character system (nicknames, quirks, personality cards). However, committing to full comic book rendering on day one blocks development.

**Phased approach:**

| Phase | Visual approach | When |
|---|---|---|
| **MVP 1 prototype** | Compose Canvas with placeholder shapes. Field = green oval + dots. Pitch = coloured strip. Action zone = buttons. Characters = text labels. | Increments 1–3 |
| **MVP 1 spike** | Prototype two styles with real cricket scenarios: (1) clean 2D top-down with colour-coded zones, (2) comic-book-inspired UI chrome with bold borders, halftone textures, character silhouettes. Test with the human. | Early MVP 1, before rendering is deep |
| **MVP 1 committed style** | Apply chosen style to all gameplay screens. Canvas rendering for field/pitch. Compose styling for UI chrome. | Mid MVP 1 |
| **MVP 2 polish** | Character portraits, delivery animations, outcome effects. If comic book: frame-by-frame shot animations. | MVP 2 |

**Tone:** Bold, confident, slightly irreverent. Not clinical. Not retro. Think a cricket-themed graphic novel — dramatic angles, strong colours, personality in every element. The scoreboard should feel like a stadium scoreboard, not a spreadsheet.

**Colour palette (draft — to be validated in spike):**
- Primary: Deep green (pitch/field), bold white (text on dark)
- Accent: Gold/amber (highlights, boundaries, achievements)
- Danger: Red (dismissals, wickets falling)
- Neutral: Dark charcoal (backgrounds, UI chrome)
- Surface condition spectrum: Green → Amber → Red (good → worn → degraded)

**Typography:**
- Headlines/nicknames: Bold, condensed, slightly custom — characterful. Think sports broadcast lower-thirds.
- Body/stats: Clean sans-serif (system default for Android: Roboto or Google Sans). Readable at small sizes on phone screens.
- Scoreboard: Monospaced or tabular figures for score alignment.

**Audio (out of scope for UX doc but noted for awareness):**
- The Customer asked "what happens when I hit a six?" — the reward moment needs satisfying audio/visual payoff. This is a Coder/UX collaboration for MVP 2, but the outcome screen should be designed to accommodate it.

### Interaction patterns

| Pattern | Apply when | Avoid when | Example |
|---|---|---|---|
| **Tap to select** | Choosing from a small set (≤4 options). Shot type groups, toss decision, ground selection. | When precision is needed beyond binary choice | Tapping "Drive" from the shot type bar |
| **Swipe to aim** | Setting the wrist angle / shot direction. The player swipes on a directional arc to choose where to hit. | When the player needs to set a precise numerical value | Swiping toward "Off Side" on the wrist angle arc |
| **Tap and hold for detail** | Accessing character card, viewing pitch conditions in detail | As a primary action during time-pressured gameplay | Holding on the bowler name to open character card |
| **Auto-advance with tap override** | Outcome display between deliveries. Shows result for 2 seconds, then auto-advances. Player can tap to skip immediately. | When the player needs time to read (e.g., scorecard review) | After "4 runs — Drive through covers" displays briefly |
| **Bottom sheet overlay** | Character card, settings, help. Modal overlays that slide up from bottom. | When the information is part of the main gameplay flow | Character card opening over the delivery loop |
| **Timer bar** | Delivery decision window. A horizontal bar that depletes from left to right. Visual, not numeric — the player sees time running out, not "3 seconds remaining." | During pre-match or result screens where there is no time pressure | The bowler running in = the timer depleting |
| **Colour-coded indicators** | Surface condition zones, fielder proximity zones. Green/amber/red spectrum. | When precise values are needed — this is a visual heuristic, not a data table | Pitch zones shifting from green to amber as they degrade |
| **Gesture conflict avoidance** | All interactive elements must have clear touch targets with ≥8dp spacing. No overlapping gesture zones. | — | Wrist angle arc does not overlap with shot type buttons |

### Wrist angle interaction model — detailed design

The Customer's core concern: "A visual dial under time pressure sounds fiddly on a phone screen."

**Decision: Swipe-to-aim, not a rotary dial.**

The wrist angle input is a **directional swipe on a semi-circular arc** at the bottom of the screen. It is NOT a precision dial. The player does not set 47 degrees — they choose a direction.

**How it works:**

1. After selecting a shot type, the wrist angle arc appears (or is always visible — to be validated in spike).
2. The arc shows 5 zones: **Fine Leg** (far left), **Leg Side** (left), **Straight** (centre), **Off Side** (right), **Third Man** (far right).
3. The player swipes from the centre of the arc toward their chosen direction.
4. The swipe doesn't need to be precise — the zones are wide (each ~36° of the arc). A swipe toward the general direction is enough.
5. The shot fires when the swipe completes OR when the timer expires (in which case the last-set direction is used, or Straight if none set).

**Why this works under time pressure:**
- A swipe is a single gesture — faster than rotating a dial.
- The zones are wide — precision is not required.
- The visual arc communicates "choose a direction" intuitively.
- If the timer expires, the default (Straight) is a reasonable cricket choice — not punishing.

**Why NOT a rotary dial:**
- Requires fine motor control on a small screen.
- Hard to see exact position under thumb.
- Slower than a directional swipe.
- The domain model's "wrist angle" is an implementation detail — the player thinks "I want to hit it through the off side," not "I want 135 degrees."

**Shot selection under time pressure — detailed design:**

The Customer flagged: "8-12 options on a phone screen under time pressure is a lot."

**Decision: Two-tier shot selection with grouped presentation.**

The 10 shot types are grouped into 3 categories, shown as a horizontal bar:

| Category | Shots | Visual style |
|---|---|---|
| **Front Foot** (vertical bat) | Drive, Defensive, Leave, Leg Glance | Blue tones |
| **Back Foot** (horizontal bat) | Pull, Cut, Sweep | Amber tones |
| **Unorthodox** | Slog, Reverse Sweep, Upper Cut | Red tones |

**Interaction:**
1. The player sees 3 category buttons (Front Foot / Back Foot / Unorthodox) — always visible in the action zone.
2. Tapping a category expands it to show the specific shots (e.g., Front Foot → Drive, Defensive, Leave, Leg Glance).
3. The expanded shots appear as a second row of buttons.
4. The player taps the specific shot.
5. The category collapses after selection (or after timer expires).

**Why this works:**
- First decision is 1 of 3 — fast, low cognitive load.
- Second decision is 1 of 3-4 — still manageable.
- Cricket fans will know which category before seeing the ball (e.g., "it's a short ball, I'm going back foot"). The category selection can be anticipatory.
- Casual players can learn the categories first, then the specific shots.
- Under extreme time pressure, the player can default to the category they know and pick the first shot — not optimal but not random.

**Timer design:**
- The timer is visualised as the bowler's run-up. A progress bar styled as a pitch view, with a ball icon moving toward the batsman.
- When the ball reaches the batsman, time's up.
- Duration: **5 seconds** from when the delivery info appears to when the shot must be committed. This is enough to read the field, glance at conditions, choose a category, and choose a shot. It's not enough to analyse every variable — that's the point.
- The timer pauses briefly (0.5s) when delivery info first appears, to let the player read line/length before acting. This is the "bowler releasing the ball" moment.

### Bowler character card — tactical intel model

The Customer asked: "Can I see the bowler's character card before the ball is bowled, or only after?"

**Decision: Visible before delivery, as tactical intel.**

- The bowler's nickname and type are shown prominently during the delivery loop — always visible at the top of the field zone.
- Tapping the nickname opens the full character card (bottom sheet).
- The character card is available at any time — before, during, and after deliveries.
- **Why tactical:** "The Magician" (Leg-Spin, Elite) tells the player to expect variation and spin. This is information a real batsman would have from watching the bowler. It should influence shot selection.
- The character card shows: Nickname, Bowler Type, Experience Class (as star rating), Quirk (flavour text), and **tactical hints** derived from stats (e.g., "Tends to bowl wide outside off" — not raw accuracy numbers).

### Pitch / surface condition presentation

The Customer's strongest feedback: "If I play a defensive shot and it shoots along the ground for four because of some pitch degradation I couldn't see, that's not fun."

**Decision: Always visible, always readable, never precise.**

**Visual presentation:**
- The pitch strip in the field zone is divided into zones (3 zones: near batsman, middle, far).
- Each zone has a colour overlay: Green (good) → Amber (wearing) → Red (degraded).
- The colours shift gradually as the match progresses — the player can see the pitch changing.
- A subtle text hint appears at over breaks: "The pitch is starting to crack on a good length" or "The surface is holding up well."

**What the player sees vs what the model knows:**

| Model data | Player sees | Rationale |
|---|---|---|
| Degradation (0–1 float) | Colour zone (green/amber/red) | Degraded = higher bounce variation, more turn. Player sees the risk level, not the exact number. |
| Moisture (0–1 float) | Subtle shimmer effect on zone (darker = wetter) | Moisture affects swing and seam. Cricket fans will read this; casual players can ignore it. |
| Roughness (0–1 float) | Texture on the zone (smooth → rough visual) | Roughness affects spin. Combined with degradation for overall "how risky is this zone." |

**Why this is enough:**
- A cricket fan looking at a red zone on a good length knows: "That's where the ball will misbehave." They don't need the exact coefficient.
- A casual player sees red = danger, green = safe. Simple heuristic.
- The player who ignores the pitch and plays the wrong shot to a degraded zone deserves to get out — but they can see it coming if they look.

### Accessibility requirements

**Standard: WCAG 2.1 Level AA** (Android). Specific criteria:

| Criterion | WCAG reference | How met |
|---|---|---|
| Colour contrast ≥ 4.5:1 (body text), ≥ 3:1 (large text, UI components) | 1.4.3, 1.4.11 | All text on dark backgrounds uses white or high-contrast colours. Surface condition colours (green/amber/red) must be distinguishable by luminance, not just hue (for colour-blind users). |
| Touch targets ≥ 48dp × 48dp | 2.5.5 (Android) | All buttons, tappable areas, and gesture zones meet 48dp minimum. The wrist angle arc zones are each ≥ 48dp wide at their narrowest. |
| System font scaling up to 200% | 1.4.4 | All text uses `sp` units. Layout must not break at 200% scaling. Scoreboard numbers remain readable. |
| TalkBack support | 4.1.2 (Android) | All interactive elements have `contentDescription`. Field diagram has a text alternative describing fielder positions. Shot type buttons are labelled. Wrist angle has a "swipe to choose direction" description. |
| No content flashing > 3 times/second | 2.3.1 | Outcome animations, boundary highlights, and wicket animations do not flash rapidly. |
| Time limit adjustable or extendable | 2.2.1 | The delivery timer is a gameplay mechanic, not an accessibility barrier. **Mitigation:** In accessibility settings, offer an extended timer option (8 seconds instead of 5). This is a gameplay concession, not a cheat — it affects score but ensures playability. TalkBack users get the extended timer automatically. |
| Motion/animation can be reduced | 2.3.3 (Level AAA — aspirational) | Respect Android's "Reduce motion" system setting. Outcome animations simplified. No ball trajectory animation — just result text. |

**Accessibility settings (MVP 1):**
- Extended delivery timer (5s → 8s)
- High contrast mode (increases surface condition colour saturation)
- Reduce motion (simplifies outcome animations)
- These are in the Settings screen, not the delivery loop.

### Naming decisions: ubiquitous language vs UI language

| Domain term | UI term | Diverges? | Why |
|---|---|---|---|
| Wrist Angle | "Where to hit" / directional swipe | **Yes** | "Wrist angle" is cricket jargon that would confuse casual players. The UI communicates the same concept through direction. Cricket fans will understand the mapping. |
| Surface Condition | Colour-coded pitch zones + "The pitch is wearing..." | **Yes** | "Surface condition" is technical. The UI uses visual metaphor (colour) and cricket language ("wearing", "breaking up"). The domain model uses `SurfaceCondition` internally. |
| Coefficient | (Not used anywhere in UI) | **Yes** | Customer explicitly flagged this as jargon. Eliminated entirely from UI. |
| Ball Characteristics | "Short, outside off, fast" | **No** — uses cricket terms | These are standard cricket terms the primary audience knows. They appear briefly after delivery, not during the decision window. |
| Shot Type | "Drive", "Pull", "Cut", etc. | **No** | Standard cricket terms. The grouping (Front Foot / Back Foot / Unorthodox) adds structure without replacing the names. |
| Dismissal | "OUT — Caught at Cover" | **No** | Standard cricket language. |
| Character Card | "Character Card" | **No** | Clear, descriptive, matches the domain term. |
| Nickname | Nickname (shown as primary name) | **No** | The nickname IS the name. "THE MAGICIAN" is the identifier. |
| Experience Class | Star rating on character card | **Partial** | "Elite" is shown, but the primary visual is stars (★★★). Both are present. |
| Delivery | (Not named — it's "the ball") | **Yes** | "Delivery" is the domain term. The player experiences "a ball being bowled." The UI never says "delivery." |

### UX requirements (blocking)

| Requirement | Priority | From MVP | Acceptance criteria |
|---|---|---|---|
| **UX-001: Shot type grouped selection** | Must | MVP 1 | 10 shot types presented in 3 categories. Two-tap selection (category → shot). All 10 accessible within 2 taps from the action zone. |
| **UX-002: Swipe-to-aim wrist angle** | Must | MVP 1 | Wrist angle set via directional swipe on a 5-zone arc. Zones ≥ 48dp wide. Default to Straight if timer expires without input. |
| **UX-003: Delivery timer visualisation** | Must | MVP 1 | Timer shown as bowler run-up animation. 5-second default. Pauses 0.5s on delivery info appearance. Extended timer (8s) available in accessibility settings. |
| **UX-004: Field placement diagram** | Must | MVP 1 | Top-down field view showing 11 fielder positions. Colour-coded by zone. Tappable for detail (optional — may defer to MVP 2). |
| **UX-005: Surface condition visibility** | Must | MVP 1 | Pitch zones colour-coded (green/amber/red). Visible during delivery decision window. Text hint at over breaks describing pitch state. |
| **UX-006: Scoreboard persistence** | Must | MVP 1 | Score, overs, wickets, target always visible during delivery loop. Not hidden by overlays or animations. |
| **UX-007: Outcome moment with learning** | Must | MVP 1 | After each delivery, show: ball info (line/length/pace), shot played, result. Brief enough to not slow play, informative enough to learn from. |
| **UX-008: Character card accessible during play** | Should | MVP 1 | Bowler character card opens via tap on nickname. Shows type, experience, quirk, tactical hint. Available before and during deliveries. |
| **UX-009: Bowler nickname as primary identifier** | Should | MVP 1 | Nickname shown prominently during delivery loop. Type and experience shown as secondary info. |
| **UX-010: Over break transition** | Should | MVP 1 | Over summary shown between overs. New bowler announced with nickname and type. Surface condition update shown as text. |
| **UX-011: Innings scorecard review** | Could | MVP 1 | Ball-by-ball scorecard accessible from match result screen. Shows delivery → shot → outcome for each ball. |
| **UX-012: Visual style spike** | Must | MVP 1 | Prototype two visual approaches (2D top-down, comic book) with real cricket scenarios. Human validates before committing. Target: within first 3 increments. |

### UX actions

| Action | Priority | Owner | Target MVP | Status |
|---|---|---|---|---|
| Run visual style spike with two approaches | Must | Coder + UX | MVP 1 (early) | open |
| Validate shot selection timing with real gameplay | Must | Human (playtest) | MVP 1 | open |
| Validate wrist angle swipe feels natural on phone | Must | Human (playtest) | MVP 1 | open |
| Test colour-blind accessibility for surface condition colours | Should | UX | MVP 1 | open |
| Design character card layout and tactical hint system | Should | UX | MVP 2 | open |
| Design outcome animation and audio reward moment | Could | UX | MVP 2 | open |
| Design tutorial / onboarding flow | Could | UX | MVP 2 | open |
| Design post-match scorecard review screen | Could | UX | MVP 2 | open |

---

## Human Feedback on UX Decisions (2026-08-20)

| Decision | Human Response | Action Required |
|---|---|---|
| Wrist angle = swipe-to-aim on 5-zone arc | "We will need to trial it" | Spike required — not binding until trialled |
| Shot selection = two-tier (3 categories → specific shots) | "Looks ok!" | **Confirmed** |
| Surface conditions = colour-coded pitch zones | "Perhaps, we will need to trial it" | Spike required — not binding until trialled |
| Bowler character card visible before delivery | **Changed:** User taps bowler's player icon to see card, but NOT at the point they are about to receive the ball | UX must revise: card accessible on-demand via tap, but not during the delivery countdown. Timing restriction needed. |

**Note:** Two of the UX Expert's "binding" decisions have been overridden or deferred by the human. The wrist angle and surface condition presentations require trialling before commitment. The bowler card visibility timing is a direct change from the UX Expert's proposal.

---

## Plain-Language Summary

### Risk
The biggest UX risk is the shot selection mechanic under time pressure. 10 shot types on a phone screen, with a wrist angle input, and a ticking clock — if this feels fiddly or overwhelming, the core loop fails. The two-tier grouping (3 categories → 3-4 shots) and the swipe-to-aim wrist angle are designed to keep it fast and intuitive, but this needs real playtesting on a phone before we commit. The second risk is the surface condition model: if the player can't see or read the pitch, the deepest feature in the game becomes the most frustrating one.

### Constraint
Phone-only, portrait-only, thumb-friendly. Everything in the bottom third of the screen. 5-second decision window. Android 12+ only. No tablets, no landscape, no iOS. These constraints are deliberate — they simplify the design and keep the scope tight for a solo developer.

### Vision impact
The UX guidelines support the full product vision. The delivery loop screen is designed to show all the tactical information a cricket fan needs (field, conditions, bowler intel) while remaining readable for a casual player (colour-coded indicators, character personality). The character system is integrated into gameplay as tactical intel, not just flavour — this aligns with the Customer's feedback that characters should matter in MVP 1, not just MVP 2. The visual style decision is deferred to a spike but the guidelines support both 2D top-down and comic book approaches.

### Recommendation
Start with the delivery loop screen as the first UX deliverable. Everything else is secondary. If the delivery loop works — if a player can face a ball, read the field, choose a shot, set a direction, and see the result, all within 5 seconds, on a phone — the game works. Prototype the shot selection grouping and the swipe-to-aim early. Playtest with the human before building the rest of the UI. The visual style spike should run in parallel with the first gameplay increments — not after them.

---

## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** UX guidelines strongly support the product vision. Three vision-critical decisions stand out: (1) The character system is integrated into gameplay as tactical intelligence — "The Magician" tells the player to expect spin, not just flavour text. This directly serves the "character-driven" nature of the product. (2) The surface condition presentation (colour-coded zones + flavour text) solves the vision's biggest UX risk — the pitch model being invisible or unfair. Cricket fans read the diagram; casual players read the text. Both audiences are served. (3) The two-tier shot selection (3 categories → 3-4 shots) and swipe-to-aim wrist angle address the vision's acknowledged risk of overwhelming time-pressured UI. The 5-second timer with 0.5s pause balances tension against frustration. The visual style spike being early in MVP 1 is the right call — the "character-driven" nature of the product leans toward comic book, but we need to validate before committing.
