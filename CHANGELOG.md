<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# BiroPlugin Changelog

## [Unreleased]
### Funkciók
- Be tudsz jelentkezni a Biro 3 rendszerbe
- Meg tudod nézni a bíróban elérhető kurzusaidat
- Megtekintheted a kurzusokhoz tartozó számonkéréseket
  - A feladatsorokat megnyitva megnézheted a feladatokat
  - Az egyes feladatokhoz biztosított fájlokat le tudod tölteni egy gombnyomásra
    - A gomb akkor is ott van, ha nincs letölthető fájl, de akkor nem csinál semmit, ez később javítva lesz
- A megoldásokat be lehet adni és kiértékeli a Bíró3
- A riportokat meg lehet nézni a ```Riportok megtekintése``` gombra kattintva, a Korábbi Feltöltések ablakban
- - -
### Javítások
- 32 bites képek megjelenítése
- SVG képek kezelése (UML diagramok)
- GIF képek kezelése
- A fájlválasztó ablakban a fájlok szűrése a ```*.java``` és ```*.zip``` fájlokra
- Ha nincs letölthető fájl, akkor Notification-ben tájékoztat erről
- Biztosított fájl letöltésekor (ha van), akkor minden megnyitott fájlt bezár és a frissen letöltött fájlokat megnyitja
- - -
### Ismert hibák
- A tárgyaknál a feladatsorok nem (jól) jelennek meg, ha a tárgy többször van a listában (elvileg javítva)
- ```IDE Error Occured```: jellemzően a feltöltés fájlválasztó ```cancel``` gombjának megnyomásakor
- A ```Korábbi Feltöltések``` ablakból ki lehet kattintani, így a háttérben másik feladat lesz nyitva, de nem azok a riportok lesznek betöltve a megnyitott ablakban, hanem annak a feladatnak a feltöltései, ahol a Korábbi Feltöltések ablakot megnyitottad
- - -
### Tervezett funkciók
- Riportoknak külön panel készítése az ablakos megoldás helyett
- Beállítások panel, ahol a jövőben több dolgot is be lehet állítani:
  - LOG-ok küldése a fejlesztőnek és az ehhez kapcsolódó anonimitás beállítása
  - Aktuálisan megnyitott feladatsor fájljainak automatikus pipálása feltöltéskor
    - Automatikus feltöltés, ha az adott feladat fájljai pipálva vannak
  - Riport azonnali megnyitása, amikor a kiértékelés megtörtént
  - Ugrás a következő feladatra, ha a jelenlegi kész és max pontos lett
  - __Ha lesz rá igény:__ UI beállítások
- - -