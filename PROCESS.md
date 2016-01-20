#day2

- Het zou eigenlijk mogelijk moeten zijn om vanaf elk screen de locatie van een object te kunnen updaten. Ik moet nog even bedenken hoe ik dit het beste kan doen. Momenteel kan het namelijk niet vanaf het startscherm. 
- Scherm met lijst van objecten gemaakt
- Scherm met map gemaakt

#day3

- Mijn app zal nu gebruik gaan maken van beacons, zodat mijn app voldoet aan de eis dat het uitdagend genoeg moet zijn om te maken.
- Ik heb besloten om de objectenlijst op te slaan in een database (Parse). Een database is makkelijker om mee om te gaan en professioneleren dan opslaan in sharedpreferences. Ook is de lijst van objecten nu gebonden aan een username. Nu kan je op meerdere apparaten de app gebruiken.
- Map werkt en zoomt in op currentlocation
- Buttons toegevoegd op de map


#day4

- Login scherm toegevoegd
- Database connectie toegevoegd: mogelijkheid tot creëren van account, inloggen en updaten van informatie.
- Arrayadapter gemaakt zodat de listview wordt gevuld met een array. De array is momenteel nog hardcoded, uiteindelijk moet de array geladen worden uit Parse.
- Ik heb een floating button in mijn app, die moet ik op de een of andere manier gaan omzetten in een '+ button'.

#day5

- Begin maken design document.
- Design document afgemaakt.
- App geupload op Github.
- Presentatie voorbereiden.
- Objectlist wordt nu geladen uit Parse en geupdate in de listview in de oncreate van mainactivity.
- Het duurt een seconde of twee voordat de objectlist geladen is, dit is niet heel mooi.
- Eventuele uitbreidingen voor deze app kunnen zijn: toevoegen van vrienden en het zien van hun objecten en het toevoegen van places of interest.

#day6

- Beacons verkregen.
- Bezig met het schrijven van een functie om objecten toe te voegen aan de lijst.
- Gebruiker kan nu objecten toevoegen aan de objectenlijst, alleen update de listview niet direct omdat Parse in de achtergrond opslaat.
- De query die ik heb geschreven voor Parse werkt niet meer, geen idee waarom.
- Parse doet het weer en de listview werkt ook naar behoren.

#day7
- Locatie kan opgeslagen worden in Parse als geopoint. 
- Locatie kan nog niet geupdate worden.
- Locatie kan nu geupdate worden en er worden markers geplaats op de kaart waar objecten een locatie hebben. (markers hebben de naam van het object)

#day8
- Wanneer een object geselecteerd wordt, zou de map gecentreerd moeten worden op het object.
- De locatie van het object wordt opgehaald uit Parse. Gegevens uit Parse worden op de achtergrond opgehaald. Dat betekent dat de rest van de code uitgevoerd wordt terwijl Parse bezig is. Hierdoor kreeg ik erg veel nullpointers en ik heb het opgelost met een runnable.
- Locaties werken nu.
- Onverklaarbaar probleem met Parse: ik kan objecten toevoegen zonder enig probleem. Wanneer ik locaties ga toevoegen aan Parse daarentegen, laadt Parse helemaal niks meer... Het ziet er naar uit dat het te maken heeft met een limiet op Parse, ik weet het niet zeker.
- Het ligt aan Parse. (bevestigd door een assistent) Ik ga me hier niet druk over maken.
- Textview toegevoegd die laat zien welk object aangeklikt is.

#day9
- Geprobeerd de interface mooier te maken zonder succes.

#day10

- Ziek, niet kunnen werken aan het project.

#day11

- Objecten kunnen verwijderd worden uit de lijst.
- Ik houd de objectenlijst zowel bij in een String array als in Parse, zodat de objectenlijst direct geupdate kan worden. 
- Interface vernieuwd met nieuwe icoontjes en kleuren.
- Objecten mogen niet dezelde naam hebben, dit moet ik toevoegen.
- functionaliteiten toegevoegd om dubbele objecten te voorkomen en feedback toegevoegd voor de gebruiker.

#day12

- Bij het updaten van een object wordt nu ook de laatste marker verwijderd.
- Wanneer er geen object geslecteerd is wordt de update location button verwijderd van de kaart

#day13

- Beacons werken nu met de app en geven een melding wanneer de beacons uit range zijn (1.5m)
- Ik wil de locationlistener van google maps gebruiken in een andere class maar dit is niet mogelijk. Een oplossing is om de locationlistener een aparte class te geven maar dit kost te veel tijd om nu nog te veranderen.
- Bluetooth flikkert aan en uit elke 5 seconden, de afgelopen uren gebeurde dit niet. 


