# Report
# Find My Stuff

Jochem van Dooren
10572929

###Beschrijving
De app Find My Stuff geeft de gebruiker de mogelijkheid om objecten toe te voegen aan een lijst. Wanneer een object is toegevoegd aan de lijst, kan de gebruiker de locatie opslaan van het object en een foto toevoegen aan het object. Vervolgens kan de gebruiker object selecteren die in de lijst staan om de locatie en de foto te kunnen zien op de kaart. Hiermee zal de gebruiker nooit meer objecten kwijtraken!

###Uitdagingen

- Beacons

Het was de bedoeling dat de app een functionaliteit zou hebben door middel van het gebruiken van beacons. De beacons maakten gebruik van de Estimote SDK. Om de uitdagingen te beschrijven die ik ben tegen gekomen zal ik de devices die ik heb gebruikt een naam geven als volgt: Iphone 5S = device1, Samsung Galaxy S3 = device2 en Motorola E = device3.
Het is gelukt om de beacons te connecten met device1 en device2 via de officiële Estimote app. Hierna is het gelukt om de beacons te laten connecten met device2 via de Find My Stuff app. Het was op een gegeven moment mogelijk voor de Find My Stuff app op device2 om te kunnen detecteren wanneer de beacons in of uit een bepaalde range (1,5m) waren. Een dag later was dit echter niet meer mogelijk. (zonder dat de Java file van de beacons veranderd was!) Device2 gaf problemen met de bluetooth die er voorheen niet waren. De bluetooth van het apparaat werd telkens in- en uitgeschakeld, wat het gebruik van bluetooth onmogelijk maakte. Ook de officiële Estimote app deed het niet meer op device2. Een belangrijk detail is dat de Estimote app het wél altijd heeft blijven doen op device1. Jaap heeft mij een leentelefoon (device3) gegeven om daarmee verder te gaan. Met device3 is hetzelfde gebeurd als device2, in de eerste instantie deed alles het naar behoren en op een gegeven kreeg ik ook op deze device het bluetooth probleem wat device2 ook had. Het zag er naar uit dat het een probleem was met de Estimote SDK. 

- InfoWindow Google Maps

Omdat de features van de beacons niet meer mogelijk waren, heb ik voor een nieuwe feature gekozen dat de gebruiker foto's kan toevoegen aan de objecten. Bij deze feature heb ik ervoor gekozen dat de gebruiker de foto's terug kan zien op de kaart en niet op de listview. Helaas was er niet genoeg tijd om de foto's ook te laten zien in de listview, omdat deze feature erg laat is toegevoegd aan het project. De uitdaging om de foto's te laten zien op de kaart lag in het verkrijgen van de foto's van Parse. Parse heeft namelijk alleen de optie om queries in de background te doen. 

In de eerste instantie wilde ik de foto van een object laden wanneer er op de marker geklikt werd, maar de infowindow werd al gecreëerd voordat de foto gedownload was van Parse. Dit was niet te voorkomen dus worden de foto's van alle objecten van de gebruiker geladen bij het openen van de map. 

- Parse

Parse voert alle queries asynchroon uit, hierdoor heb ik er voor gekozen om een handler te gebruiken om mijn listview te updaten. Hierdoor is er een kleine delay in het vullen van mijn listview. Dit is niet heel netjes maar het is niet iets waar de gebruiker zich aan zal storen. Hetzelfde geldt voor de markers: wanneer de map geopend wordt, moeten de markers eerst geladen worden voordat de map gecentreerd kan worden op de locatie van de marker. Hier is ook gebruik gemaakt van een handler om nullpointers te voorkomen.



