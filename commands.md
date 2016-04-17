# Introduction #

momoBot commands are sended throught the channel chat (in a simple IRC MSG). They all start with a dollard "$" symbol, so that they are not mistaken with common chat, that would trigger undesirable actions. Most of them are were created in order to organize counter strike games on quakenet network.


# Details #

Here are the documented momobot's command :
  1. Gamers commands
    * $start : Initialize a gather. 4 others players have to add themselves to the party.
    * $pickup: Initialize a picked : 9 others players are required to start game (5 versus 5).
    * $add {a | b} : add the triggering user to the current game (into the team a or b), if there is one being organized.
    * $rmv : remove the triggering user from the game.
    * $status : Show informations about the current game.
    * $roll : Highlight a random player in the gathered team.
    * $reset : reset the current game
  1. General-purpose commands
    * $help : shows helpful informations for the user who don't know how to use the bot.
    * $whois
    * $fessee : highlight everyone on the channel (a bit like mIRC 'slap' command)
    * $memo
    * $listmemos
    * $google {keywords} : launch a google research about 'keywords', and return the first result. Useful when you need to make a quick search.
  1. Testing/debugging commands
  1. Useless commands
    * $kenny
    * $love
  1. Unknown commands
    * $fracont
    * $len
    * $q3