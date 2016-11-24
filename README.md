Minecart Updater - Minecraft Mods Updater for minecart.cz
=========================================================

Mods updater for minecart.cz

Screenshot
----------

![Minecart Updater Screenshot](images/screenshot.png?raw=true)

Features
--------

 * Checking for new updates
 * TODO: Automatic update of updater
 * Detecting Minecraft profile by name
 * Updating files in mod folder
 * TODO: Update forge using its installer

Structure
---------

As the project is currently in alpha stage, repository contains complete resources for distribution package with following folders:

 * doc - Documentation
 * gradle - Gradle wrapper
 * src - Sources related to building distribution packages
 * images - Some images

Compiling
---------

Java Development Kit (JDK) version 7 or later is required to build this project.

For project compiling Gradle 2.0 build system is used. You can either download and install gradle and run "gradle distZip" command in project folder or gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

Build system website: http://gradle.org

Development
-----------

The Gradle build system provides support for various IDEs. See gradle website for more information.

 * Eclipse 3.7 or later

   Install Gradle integration plugin: http://marketplace.eclipse.org/content/gradle-integration-eclipse-0

 * NetBeans 8.0 or later

   Install Gradle support plugin: http://plugins.netbeans.org/plugin/44510/gradle-support

License
-------

Apache License Version 2.0
