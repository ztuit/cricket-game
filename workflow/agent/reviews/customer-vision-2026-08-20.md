# Customer Vision Review — Cricket Game
**Date:** 2026-08-20
**Mode:** vision_review
**Document reviewed:** workflow/product/bluesky/v1.md

## Overall reaction

This is the cricket game I've been waiting for someone to make. The core idea — that knowing cricket is the skill, not mashing buttons — is genuinely exciting. I've played cricket games on mobile and they're all either timing-based batting sims where you just tap at the right moment, or management games where you don't actually play. Neither respects what I actually know about the sport. This one does. I want to play it.

## What resonates

The shot selection under time pressure is the heart of it. When I'm watching a real match and a batsman gets out, I'm thinking "why did he play that shot to that ball?" This game puts me in that seat. That's brilliant.

The field placement reading mechanic is exactly right. In real cricket, if there are two slips and a gully, the bowler wants an edge. If the field is spread, they're bowling defensive. A game that tests whether I can read that — yes, that's what I want.

Fictional bowlers with nicknames and personality cards. This is smart. It avoids licensing headaches but more importantly it gives the game its own identity. "The Magician" sounds like someone I'd want to face. I want to know what makes each bowler tick and use that against them.

Real teams and grounds. When I'm batting at Lord's or the MCG, that matters. It grounds the game in something real even though the players are fictional.

T20 format for mobile. 20 minutes for a full match is perfect. I can play on my commute.

## What does not resonate

"Surface physics model" sounds like jargon. If you mean the pitch changes over the match — yes, that's great, that's real cricket. But the vision document talks about "varies across the pitch, degrades over the match, reacts to weather, changes based on how many balls have been bowled to each area." That's a lot of variables. As a player, I need to understand *why* the ball did something unexpected. If I play a defensive shot and it shoots along the ground for four because of some pitch degradation I couldn't see, that's not fun — that's random. The document says "player must be able to see or infer the relevant conditions" in the risks section, which is right, but the product description doesn't commit to that. It just says "limited spin information." I need to know that I'll be able to read the pitch, not just guess at it.

The "wrist angle dial" worries me. A visual dial under time pressure sounds fiddly on a phone screen. I'm thinking about the shot, not fiddling with a dial. If it's a quick swipe gesture, fine. If it's a precise dial I need to rotate to 47 degrees, that's going to frustrate me. The vision doesn't clarify what this actually feels like in my hand.

"Coefficients shift as the innings progresses" — I don't know what that means. If you mean the bowler gets harder to face as the innings goes on, just say that. If you mean something more nuanced, explain it in terms I can feel as a player. Right now it reads like maths, not cricket.

## Missing scenarios

**What happens when I'm in a partnership?** The vision says 10 wickets, but doesn't say if there are two batsmen. In real cricket, the partnership dynamic — running between wickets, the non-striker's influence — is a huge part of the game. If I'm just one batsman facing 20 overs alone, that's less interesting than having a partner.

**What about bowling?** I get that the player always bats in v1, and that's fine. But I want to know: am I ever going to get to bowl? The vision says "out of scope" for v1 but doesn't say if it's planned for later. Bowling is half of cricket. A game where I only ever bat will eventually feel incomplete.

**Chasing vs setting a target.** The toss mechanic is mentioned but the experience of chasing a score (knowing exactly what you need, ball by ball) is very different from setting one. Both should feel distinct. The vision doesn't talk about this tension.

**What does getting out feel like?** The success definition says "I knew it was the wrong choice the moment I committed." That's perfect. But the vision doesn't describe the wicket experience. Is there a replay? Do I see what the ball did? Can I learn from it? Getting out should sting but teach me something.

**Rain and weather interruptions.** The vision mentions weather affecting the pitch, but in real cricket, rain can stop play entirely. Is that in scope or not? If not, just say so.

## Priorities

The roadmap is right to validate the core loop first. If shot selection isn't fun, nothing else matters. MVP 1 focusing on "can I face a delivery and make a meaningful choice" is exactly right.

But I'd push back on MVP 2. The character system is the differentiator, and you've put it in MVP 2. I'd argue that even in MVP 1, the bowler having *some* personality — a nickname, a visible trait — matters for the first impression. If I face a bowler in MVP 1 and he's just "Fast Bowler Type A," I'm not hooked. If he's "The Magician" with a visible quirk, I'm already invested. The characters aren't decoration — they're part of the decision-making. I'd want at least a taste of that in MVP 1.

MVP 3 (career) and MVP 4 (online) make sense in that order. I need a reason to come back before I care about leaderboards.

The freemium model at 1 over free is too stingy. One over is 6 balls. That's not enough to feel the tension of a T20 match. I'd need at least 3-5 overs to get invested. If the free tier doesn't let me feel the game, I won't pay for more. I'll just uninstall.

## Questions for the Product Owner

1. **How many shot types will I actually choose from per delivery?** The vision says "curated to 8-12" in MVP 2, but MVP 1 doesn't specify. On a phone screen, even 8 options under time pressure feels like a lot. What's the MVP 1 number?

2. **Can I see the bowler's character card before the ball is bowled, or only after?** If I can see it before, it's tactical intel. If only after, it's just flavour. This matters.

3. **What does "wrist angle" actually mean as a player action?** Is it a swipe? A tap? A dial? How long does it take to set? I need to understand the physical interaction.

4. **Will there be a way to review my innings after the match?** A ball-by-ball scorecard showing what shot I played, what the ball did, and what happened? That's how I'd learn and improve.

5. **Is the bowler's skill hidden or visible?** In real cricket, you know a bowler's reputation. Do I know "The Magician" is a leg-spinner with a googly, or do I have to figure it out?

6. **What happens when I hit a six?** Is there a satisfying visual/audio payoff? The reward for a good decision needs to feel good, not just update a number on screen.

7. **How does the toss work?** Is it a coin flip animation? Can I choose to bat or bowl? In real cricket, the toss decision is tactical — depends on conditions. Will the game let me make that choice or just assign it randomly?

## Recommendation

Proceed. This is a strong concept with a real gap to fill. The core loop idea is sound, the character system is a genuine differentiator, and the roadmap prioritises the right things.

Two things to address before building: (1) the physical interaction model — shot selection, wrist angle, time pressure — needs to be crystal clear so the UX team can prototype it, and (2) the free tier needs to be generous enough to hook someone in one session. Six balls isn't it.

The surface physics model is the biggest risk. If it's invisible to the player and just adds randomness, it'll feel unfair. If it's readable and rewards cricket knowledge, it's the best feature in the game. The difference is entirely in how it's presented to the player, not in the model itself.

I want to play this game. Ship it.
