# Encore — Event Ticket Booking

## Overview

Encore lets a guest browse live events across a range of categories, pick one
or more seats, optionally apply a discount code, and complete a booking. There
is no account system: the app opens directly to the event catalog, and a
guest's bookings persist for the duration of their session so they can review
what they've booked without re-entering any details.

## Goals

- Let a guest find an event and understand what's available (date, venue, price
  range, remaining capacity) before committing to seats.
- Offer enough variety within each category that filtering by type is
  meaningful rather than cosmetic — a guest narrowing to one category should
  still have real choices in front of them.
- Make seat selection visual and unambiguous — tier, price, and availability
  should be obvious at a glance.
- Keep checkout short: seat → discount code → payment → confirmation, with no
  unnecessary steps in between.
- Give guests a simple way to look back at what they've booked in the current
  session.

## User Flow

1. **Guest Entry** — a single "Continue as Guest" action; no fields, no sign-in.
2. **Event List** — a scrollable catalog of upcoming events, each showing title,
   category, venue, date/time, and a starting price. A row of category filter
   chips ("All" plus each event type present in the catalog) sits above the
   list; selecting one narrows the catalog to that category, and "All" resets
   it. Events with no remaining seats are marked sold out and cannot be
   selected.
3. **Event Details** — full description, venue, date/time, and a breakdown of
   seating (how many Standard vs. VIP seats, and how many remain). From here the
   guest proceeds to seat selection.
4. **Seat Selection** — a seat map grouped by row. Each seat shows its tier
   (Standard or VIP) and reflects whether it's available, already booked, or
   currently selected by the guest. The guest may select up to 10 seats to
   include in a single booking.
5. **Discount Code** — optional. The guest may enter a code to reduce the price
   of eligible seats in their selection; entering a code is never required to
   proceed to payment. An invalid code shows an inline error and does not block
   checkout — the guest can simply continue without it.
6. **Payment** — a review of every selected seat and the order total (subtotal,
   any discount, total). Payment is mocked for this release: no card details are
   collected and no external processor is called. Confirming payment finalizes
   the booking.
7. **Booking Confirmation** — shown immediately after payment, displaying a
   confirmation number, the event, each seat booked, and the final price paid.
8. **My Bookings** — a running list of everything the guest has booked this
   session, most recent first, each entry showing the event, its seats, total
   price, and confirmation number.

## Business Rules

- A seat can only be held by one booking at a time; once a seat is booked it no
  longer appears as available to other guests browsing the same event.
- Pricing is per seat and varies by tier — VIP seats are priced higher than
  Standard seats for the same event.
- Discount codes are entered as free text and matched case-insensitively against
  a small set of active codes. Each code defines a flat percentage taken off a
  seat's base price; within a multi-seat booking, a code is evaluated per seat,
  so it can discount some seats in the order and not others depending on
  tier.[^1][^2][^3]
- A booking is only finalized once payment completes; no seat is held
  indefinitely on the guest's behalf before that point.
- Each venue has one fixed seat map, shared by every event hosted there — the
  room's shape doesn't change from one show to the next, only which seats are
  already booked and what the venue charges for that particular event.

[^1]: The standard 10%-off welcome code (`SAVE10`) applies to Standard-tier
  seats only and does not discount VIP-tier seats.

[^2]: A second code, `FIRST20`, gives 20% off any seat regardless of tier, but
  only on a guest's first completed booking in a session — it's no longer
  valid on any booking after that, whether or not it was actually used on
  the first one.

[^3]: A third code, `WELCOME15`, gives 15% off any seat regardless of tier,
  but only from a guest's second booking onward in a session — it isn't
  valid on the first booking, and once it's been used successfully one
  time, it becomes invalid again for the rest of the session.

## Screens Out of Scope for This Release

- Account creation, login, or any persisted identity across sessions.
- Real payment processing or stored payment methods.
- Editing or cancelling an existing booking.
- Free-text search of the event list (only category filtering is supported).
- Bookings spanning more than one event at a time.

## Data

All event, seat, and discount code data is bundled with the app for this
release — there is no backend and no network dependency. A future release may
move this to a live catalog service; the screens and flow described above should
not need to change when that happens.

The current catalog spans 30 events across 5 categories (Music, Theater,
Comedy, Classical, Sports), hosted across a dozen recurring venues so that
filtering by category, or browsing a single venue's calendar, both feel like
real choices rather than a single obvious option. Event dates are generated
relative to today rather than hardcoded, so the catalog always reads as
"coming up over the next few months" regardless of when the app is installed
or opened — no manual data refresh is needed between sessions.
