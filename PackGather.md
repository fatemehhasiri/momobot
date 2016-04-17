# About this package #

Everything you need to set a up a gather is inside this package.


# List of triggers #
(sorted in alphabetical order)
  * AddTrigger : add the triggering player to the gather
    * **$add**
  * AdminaddTrigger : force the player into the gather
    * **$adminadd** <arg player>
  * AdminrmvTrigger : force the player outside the gather
    * **$adminrmv** <arg player>
  * GatherTrigger : starts a new gather
    * **$start**
    * **$gather**
  * PickupTrigger : start a new pickup
    * **$pickup**
    * **$pick**
  * RmvTrigger : remove the triggering player from the gather
    * **$rmv**
    * **$remove**
  * RollTrigger : pickup a random player in the gather
    * **$adminrmv**
    * **$cagoule**
  * SeekTrigger : ?
    * **$seek** ?
  * ShakeTrigger : ?
    * **$shake** ?
  * StopSeekTrigger : ?
    * **$stopseek** ?
  * TagTrigger : change the team tag
    * **$tag** <arg newtag>
  * TgSeekTrigger : ?
    * **$tg** ?


note: gather is an [event](event.md), so you may want to load the [package event](PackEvent.md)