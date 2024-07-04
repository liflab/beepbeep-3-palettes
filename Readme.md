A set of ready-made palettes for BeepBeep 3
===========================================

This repository contains projects producing various independent
extensions to the [BeepBeep 3](https://liflab.github.io/beepbeep-3)
event stream query engine. Each project is also independent from the others,
and can be built separately. All projects require `beepbeep-3-xxx.jar` in
their classpath.

Available palettes
------------------

At the moment, the folder contains the following palettes:

- `Apache`: for parsing Apache log files
- `Complex`: for creating complex events out of lower-level events
- `Concurrency`: for using multi-threading in processors
- `Diagnostics`: to help debugging processor chains
- `Dsl`: for creating Domain Specific Languages with the
  [Bullwinkle](https://github.com/sylvainhalle/Bullwinkle) parser
- `Fol`: for manipulation of first-order logic statements
- `Fsm`: for manipulation of extended finite-state machines
- `Graphviz`: manipulate and draw directed graphs using Graphviz
- `Hibernation`: save/restore processor states to persistent storage
- `Http`: send and receive events over a network
- `Jdbc`: to interface with relational databases
- `Json`: to read and write events in the JSON format
- `Ltl`: to express properties in Linear Temporal Logic
- `Mtnp`: front-end to the [MTNP library](https://github.com/liflab/mtnp)
   to manipulate tables and plots
- `Provenance`: traceability in processor chains
- `Serialization`: serialize/deserialize events with the
   [Azrael](https://github.com/sylvainhalle/Azrael) library
- `Sets`: to manipulate streams of sets
- `Signal`: basic signal processing functions (peak detection, etc.)
- `Tuples`: for manipulating tuples and using BeepBeep as a JDBC driver
- `WebSocket`: to read/write events from/to a web socket
- `Widgets`: to manipulate AWT and Swing widgets as processors
- `Xml`: to read and write events in the XML format

Depending on the projects, additional dependencies may need to be
downloaded and installed. Please refer to the `Readme.md` file of each
particular project (if any) for more information on compiling and
building these extensions.

Using BeepBeep palettes in a project
------------------------------------

You can [download the latest JAR files](https://github.com/liflab/beepbeep-3-palettes/releases/latest)
and place them in your classpath. Otherwise, you can declare the palettes as a
dependency in your project. This will download a single JAR file that bundles
all the palettes together.

### Maven

```xml
<dependency>
  <groupId>io.github.liflab</groupId>
  <artifactId>beepbeep-3-palettes</artifactId>
  <version>0.8</version>
</dependency>
```

### Ivy

```xml
<dependency org="io.github.liflab" name="beepbeep-3-palettes" rev="0.8"/>
```

### Gradle

```
compileOnly group: 'io.github.liflab', name: 'beepbeep-3-palettes', version: '0.8'
```

### Groovy

```
@Grab('io.github.liflab:beepbeep-3-palettes:0.8')
```

Building the palettes
---------------------

### tl;dr

1. Run `ant` to build all palettes in succession.
   Since some palettes have dependencies on other palettes, the script
   must be run **twice**.
2. If everything goes well, all the resulting jars will be created in the
   `jars` repository. Move them around and enjoy.
3. You can also run the test scripts for each palette by running
   `ant test` script.

### More details

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. BeepBeep was developed and
  tested on version 8 of the JDK, but it is probably safe to use any later
  version.
- [Ant](http://ant.apache.org) to automate the compilation and build process

Each palette depends on BeepBeep's core library, which must be built or
downloaded beforehand. Please refer to the build instructions for [BeepBeep's main
engine](https://github.com/liflab/beepbeep-3).
The resulting JAR file, must be placed at the root of this
repository prior to building any of the palettes.

The repository is separated into multiple *projects*. Each of these
projects has the same Ant build script that allows you to compile them
(see below).

If the project you want to compile has dependencies,
you can automatically download any libraries missing from your
system by typing:

    ant download-deps

This will put the missing JAR files in the `deps` folder in the project's
root. These libraries should then be put somewhere in the classpath, such as
in Java's extension folder (don't leave them there, it won't work).

### Compiling

Compile the sources by simply typing:

    ant

This will produce a file called `xxx.jar` (depending on the palette you
are compiling) in the `jars` folder.

In addition, the script generates in the `doc` folder the Javadoc
documentation for using BeepBeep. To show documentation in Eclipse,
right-click on the jar, click "Properties", then fill the Javadoc location.

### Testing

Some of these palettes can can test themselves by running:

    ant test

Unit tests are run with [jUnit](http://junit.org); a detailed report of
these tests in HTML format is availble in the folder `tests`, which
is automatically created. Code coverage is also computed with
[JaCoCo](http://www.eclemma.org/jacoco/); a detailed report is available
in the folder `coverage`.

### Creating a fat JAR file

Once all the palettes have been built, a (fat) JAR file packaging all the
palettes can be creating by typing:

    ant fat-jar

in the root folder of this repository. The resulting file will be called
`beepbeep-3-palettes-x.x.zip`, where `x.x` is the current version.

### Creating a fat Zip file

Once all the palettes have been built, a (fat) zip file containing all the
generated JARs can be creating by typing:

    ant zip

in the root folder of this repository. The resulting file will be called
`beepbeep-3-palettes-vYYYYMMDD.zip`, where `YYYYMMDD` is the current date.
Some precompiled bundles are available in GitHub's *Releases* page, but they
may not contain the latest version of each palette. Use at your own risk!

Warning                                                          {#warning}
-------

The BeepBeep project is under heavy development. The repository may be
restructured, the API may change, and so on. This is R&D!

About the author                                                   {#about}
----------------

BeepBeep 3 was written by [Sylvain Hallé](https://leduotang.ca/sylvain),
full professor at Université du Québec à Chicoutimi, Canada. Part of
this work has been funded by the Canada Research Chair in Software
Specification, Testing and Verification and the
[Natural Sciences and Engineering Research Council
of Canada](http://nserc-crsng.gc.ca).
