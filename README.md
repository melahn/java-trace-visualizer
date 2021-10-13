# java-trace-visualizer

[![GitHub Build Status](https://github.com/melahn/java-trace-visualizer/actions/workflows/build.yml/badge.svg)](https://github.com/melahn/java-trace-visualizer/actions/workflows/build.yml)
[![Sonar Cloud Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=melahn_java-trace-visualizer&metric=alert_status)](https://sonarcloud.io/dashboard?id=melahn_java-trace-visualizer)

## Overview

A  project to make jdb trace output easier to understand.

Note: At the moment, this is a skeleton to setup automated build and test.

## Prerequisites

Java 8 or later.  

[Graphviz](https://www.graphviz.org/) is also a prerequisite only if you are generating images from the PlantUML file using the '-g' option described below.

## Goals

* Make jdb trace output easier to understand

## Using Java Trace Visualizer

### Command Line Syntax

``` java
java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar

Flags:
  -i  <filename>  A location in the file system for the input file 
  -o  <filename>  A location in the file system for the visualization file to be produced
  -g      Generate image from the PlantUML file
  -h      Help               
```

#### Flags

* **Required**
  * **-i** \<filename\>
    * The name of the input file. This is the output of the jdb command captured in a file.
  * **-o** \<filename\>
    * The name of the output file to be created. If the name has the extension 'puml', the file will be written in *PlantUML* format. Otherwse it will be written as a text file.
* **Optional**
  * **-g**
    * Generate image. Whenever specified, an image file is generated from the PlantUML file.  This is only applicable if
      the filename of the generated output file has the extension 'puml'.
  * **-h**
    * Help. Whenever specified, any other parameters are ignored.  When no parameters are specified, **-h** is assumed.

#### Example Commands

##### Generating a Trace Visualization as a PlantUML file with image generated from the PlantUML file

``` java
java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar -i foo.jdb.out -o foo.puml  -g
```
