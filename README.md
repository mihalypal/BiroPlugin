# BiroPlugin

![Build](https://github.com/mihalypal/BiroPlugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
Ez a plugin lehetővé teszi a Biro 3 rendszer használatát az IntelliJ IDEA-ban.

## Használat

- Jelentkezz be a h-s azonosítóddal és jelszavaddal
- Add meg, hogy szeretnél-e LOG-okat küldeni a fejlesztőnek, illetve, hogy ehhez szeretnéd-e, hogy beazonosítható legyél
- Válaszd ki a tárgyat, amin dolgozni szeretnél
- Válaszd ki a feladatsort, amit meg szeretnél oldani


- A felület, ami ezután megjelenik, talán már ismerős lehet, ha használtad a Bíró3 webes felületét korábban.
- A feladatsoron belüli egyes feladatokat az azokhoz tartozó gombokkal tudod megnyitni.
- Amennyiben vannak biztosított fájlok, azok letöltésére is van lehetőséged, a ```Biztoított fájl(ok) letöltése``` gomb megnyomásával. Melyet a panel alján találsz.
- A feladatok megoldása utána a ```Fájl(ok) feltöltése``` gomb megnyomásával felugrik egy fájlválasztó ablak, ahol kiválaszthatod a megoldásaidat. (Itt a package-ek lesznek listázva, ha abban dolgoztál, ha simán az ```src``` mappába dolgozol, akkor a ```<default>``` package alatt keresd a megoldásodat)
- Ha kiválasztottad a fájl(oka)t, akkor az ```OK``` gomb megnyomásával a plugin feltölti a fájlokat a Bíró3 rendszerébe.
- Ezután, ha megtörtént a kiértékelés, akkor egy értesítést kapsz a kiértékelés eredményéről, ahonnan rögtön megnyitható az aktuális riport. Egyéb esetben a ```Riportok megtekintése``` gombra kattintva érheted el a riportokat.


## Fejlesztés segítése

Tudod segíteni a fejlesztési folyamatot azzal, hogy a tárgy/feladatsor választása panelen fent található ```Visszajelzés küldése a pluginról``` gombra kattintva megosztod a tapasztalataidat, javaslataidat a plugin fejlesztőjével. \
Az esetleges hibákat is itt tudod jelezni, amennyiben a plugin nem működik megfelelően. \
Többféle visszajelzés kategória közül van lehetőség választani, így a visszajelzésedet a legjobban illeszkedő kategóriába tudod tenni.

__Fontos:__ A visszajelzések alapvetően *anonim* módon történnek, azonban ha szeretnéd, hogy beazonosítható legyen a visszajelzésed, akkor erre is van lehetőség a megfelelő checkbox kipipálásával.

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "BiroPlugin"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/mihalypal/BiroPlugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
