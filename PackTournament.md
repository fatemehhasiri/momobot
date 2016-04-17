# About this package #

This package contains advanced function to organize your own tournament via IRC.

# List of triggers #
(sorted in alphabetical order)
  * RegisterTrigger: register a new team into the tournament. Arguments provide informations about this team
    * **$tn-register** <arg team-id> <arg country-letter> <arg team-tag> <arg player1...player5>
  * ResultTournamentTrigger: attribute the specified result to the match
    * **$tn-result** <arg winner-team-id> <arg score1> <arg score2>
  * TounamentTrigger: starts a new tournament based on specified maps.
    * **$tn** <arg map1...mapN>



note: tournament is an [event](event.md), so you may want to load the [package event](PackEvent.md)