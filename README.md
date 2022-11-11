# Java 4 Nuclear Physics (j4np)

This repository is for nuclear physics tools in java, includes data I/O HiPO libraries,
Twig (data visualization) package, MAchine learning libraries (DeepNetts) and many more.

# Linking to the Packages

For Maven dependencies use:

```
  <repositories>
    <repository>
      <id>j4np-maven</id>
      <url>https://clasweb.jlab.org/clas12maven</url>
    </repository>
  </repositories>

<dependencies> 
    <dependency>
      <groupId>j4np</groupId>
      <artifactId>j4np-data</artifactId>
      <version>1.0.5-SNAPSHOT</version>
    </dependency>
</dependencies>
```

# Useful Packages

Graph Neural Networks
https://github.com/maniospas/JGNN

Etreamly Randomized Trees


# Examples

Examples forlder contains simple exmaples designed for each
package to test functionality. All examples are implemented
as scripts for JSHELL (cool stuff). In order to run them use
command:

 >jshell --class-path j4np-physics/target/j4np-physics-0.9-SNAPSHOT-jar-with-dependencies.jar -s examples/physics/vector_example.jshell

