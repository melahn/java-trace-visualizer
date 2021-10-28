# java-trace-visualizer

[![GitHub Build Status](https://github.com/melahn/java-trace-visualizer/actions/workflows/build.yml/badge.svg)](https://github.com/melahn/java-trace-visualizer/actions/workflows/build.yml)
[![Sonar Cloud Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=melahn_java-trace-visualizer&metric=alert_status)](https://sonarcloud.io/dashboard?id=melahn_java-trace-visualizer)

## Overview

A  project to make jdb trace output easier to understand.

## Prerequisites

Java 8 or later.  

## Goals

* Make jdb trace output easier to understand

## Using Java Trace Visualizer

### Command Line Syntax

``` java
java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar

Flags:
  -i  <filename>  The name of the input file 
  -o  <filename>  The name of the output file
  -s  <filename>  The name of the stats file
  -g              Generate image from the PlantUML file
  -h              Help               
```

#### Flags

* **Required**
  * **-i** \<filename\>
    * The name of the input file. This is the output of the jdb command captured in a file.
  * **-o** \<filename\>
    * The name of the output file to be created. If the name has the extension 'puml', the file will be written in *PlantUML* format. Otherwse it will be written as a text file.
* **Optional**
  * **-s** \<filename\>
    * The name of the output file to be created to hold generated statistics. This is a csv file.
  * **-g**
    * Generate image. Whenever specified, an image file is generated from the PlantUML file.  This is only applicable if
      the filename of the generated output file has the extension 'puml'.
  * **-h**
    * Help. Whenever specified, any other parameters are ignored.  When no parameters are specified, **-h** is assumed.

#### Example Commands

##### Generating a Trace Visualization as an ascii text file

``` java
java -jar java-trace-visualizer-1.0.0-SNAPSHOT.jar -i foo.jdb.out -o foo.txt 
```

### Example Text File Output

``` text
 thread: main 
     
       start 
         |
         |____ com.melahn.util.java.trace.TestApp.A() (line 13) 
         |    |
         |    |____ com.melahn.util.java.trace.TestApp.B() (line 18) 
         |
         |____ com.melahn.util.java.trace.TestApp.C() (line 21) 
         |    |
         |    |____ com.melahn.util.java.trace.TestApp.D() (line 26) 
         |
         |____ com.melahn.util.java.trace.TestApp.E() (line 29) 
         |    |
         |    |____ com.melahn.util.java.trace.TestApp.F() (line 34) 
         |         |
         |         |____ com.melahn.util.java.trace.TestApp.G() (line 38) 
         |              |
         |              |____ com.melahn.util.java.trace.TestApp.H() (line 43) 
         |
         |____ com.melahn.util.java.trace.TestApp.I() (line 47) 
         |
         |____ com.melahn.util.java.trace.TestApp.B() (line 18) 

Generated on 2021/10/25 16:23:57 by com.melahn.util.java.trace.TraceVisualizerTextPrinter (https://github.com/melahn/java-trace-visualizer)
```

### Example Image Output
![Image generated from PlantUML](./resource/example/example-single-thread-trace-file.png)

### Example PlantUML File Output

``` text
@startuml Java Trace
scale 2
skinparam FooterFontColor Gray
skinparam FooterFontSize 6
salt
{
    {T
         Method Call |  Line Number
        +start | <color:Gray>0
        ++ com.melahn.util.java.trace.TestTraceApp.A() | <color:Gray>29
        +++ com.melahn.util.java.trace.TestTraceApp.B() | <color:Gray>37
        +++ com.melahn.util.java.trace.TestTraceApp.C() | <color:Gray>40
        ++++ com.melahn.util.java.trace.TestTraceApp.D() | <color:Gray>45
        +++ com.melahn.util.java.trace.TestTraceApp.E() | <color:Gray>48
        ++++ com.melahn.util.java.trace.TestTraceApp.F() | <color:Gray>52
        +++++ com.melahn.util.java.trace.TestTraceApp.G() | <color:Gray>56
        ++++++ com.melahn.util.java.trace.TestTraceApp.H() | <color:Gray>60
        +++++++ com.melahn.util.java.trace.TestTraceApp.I() | <color:Gray>65
        +++ com.melahn.util.java.trace.TestTraceApp.B() | <color:Gray>37
    }
}
center footer Generated on 2021/10/28 15:20:29 by com.melahn.util.java.trace.TraceVisualizerPlantUMLPrinter(https://github.com/melahn/java-trace-visualizer)
@enduml
```
