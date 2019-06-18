# Dashboard System for codeBeamer 7.1

**DO NOT USE, EDIT THIS PROJECT** this project would work at only old version and doesn't grant any copyright, maintenance or guarantee.

[codeBeamer](https://codebeamer.com/cb/) is a commercial ALM(Application Lifecycle Management) software.

And this project is general purpose dashboard implementation for an old version of codeBeamer doesn't have any BI feature at that time.
(codeBeamer have its own BI features since version 8.0)

## Features

- Wiki plugin that makes to use a wiki page as an dashboard
- Widget system (`com.architectgroup.xbeamerchart.widget.base`)
  - Provide customizable, interactive configuration UI for plugin
  - Provide default widgets for general types (boolean, color, data, etc) and advanced types (codeBeamer internals and query)
- Plugin system (`com.architectgroup.xbeamerchart.plugin.base`)
  - An interface that makes to create a new plugin
  - Wrapper interface for exist plugins
- Many dashboard plugins (`com.intland.codebeamer.wiki.plugins`)

## Technologies

- Java 7
- Basic HTML/CSS/JavaScript
- Apache Velocity (Template engine)
- [codeBeamer's Wiki API](https://codebeamer.com/cb/wiki/566240) and internals

It was designed and implemented when I was not programming in professional, so it's a bit old-fashioned technology and shit codes but well reflects my API design skills at the time.

## Screenshots
