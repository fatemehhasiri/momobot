momobot uses a unique momobot.xml file to store profiles of your bots. In order to create your own bot with its own nickname and features, you have to edit this file and add a `<profil>` block.

The default profile block is this one (you can adapt it to your needs) :

```
	<profil name="default" nick="momobot3" login="mmb" fullName="MMB v3">
		<autoconnect server="Quakenet" fullName="MMB v3 - Qnet Special">
			<join>#tsi.fr</join>
			<join>#cos_squad</join>
		</autoconnect>
		<loadtriggerpack prefix="$" bundle="admin" />
		<loadtriggerpack prefix="$" bundle="base" />
		<loadtriggerpack prefix="$" bundle="event" />
		<loadtriggerpack prefix="$" bundle="gather" />
		<loadtriggerpack prefix="$" bundle="pendu" />
	</profil>
```

You may want to load just a few [triggerpacks](triggerpack.md) so just delete the corresponding lines.