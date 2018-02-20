# Java and CPLEX example project: Precendence Constrained Knapsack Problem (basic package)
This repository contains example code for a small Java Project that utilizes the [IBM ILOG CPLEX library](https://www.ibm.com/products/ilog-cplex-optimization-studio)
to solve instances of the Precendence Constrained Knapsack Problem. This code is available in the `basic` package and folder of this repository. 

The Precendence Constrained Knapsack Problem is a variant of the regular Knapsack Problems where we have items *I*, where for each item *i* we have a profit *p<sub>i</sub>* and a weight *w<sub>i</sub>*.
Furthermore, there is a capacity*b* and a directed graph *G = (I,A)* defined on the items. The objective is to select a set of items *S &sube; I*, such that the sum of*p<sub>i</sub>* over *i &isin; S* is maximized,
while the sum of *w<sub>i</sub>* over *i &isin; S* is smaller than or equal to *b*. Additionally, an item *i* can only be in *S* if all items *j* for which *A* contains an arc *(i,j)* are also in *S*.

The code for the basic example consists of the following files:
* `DirectedGraph.java`  contains a general purpose data structure for directed graphs. It provides methods to add nodes and arcs, and allows us to attach arbitrary data types to the nodes and arcs.
* `DirectedGraphArc.java` models the arcs that are created by the graph. These arcs contain the origin node of an arc, the destination node of an arc and the data associated with the arc.
* `Item.java` models a knapsack item, without its precendence constraints (these are modelled in the project using a DirectedGraph object).
* `Model.java` manages a CPLEX integer linear programming model, converting a directed graph with a precendence constrained knapsack problem and a capacity to a CPLEX model that can be solved.
* `Main.java` is used to read in an instance of the problem from a text file, convert it to a directed graph, and call the `Model` class to solve the instance.

More detailed documentation of these classes can be found in the [Javadocs of this project](https://pcbouman-eur.github.io/JavaCplexExample/javadoc/).

For a general explanation of using CPLEX from Java the repository contains some [lecture slides](https://pcbouman-eur.github.io/JavaCplexExample/cplex_lecture.pdf).
There is also a [series of four YouTube videos](https://www.youtube.com/watch?v=C4YDrVT3fcg&list=PLrX1UIgv0C_4V5Xx6IIWj0U8i-4JN6F1g) in which this project is implemented.
 
# Java and CPLEX column generation project: Cutting Stock (colgen package)
A second part of the example code covers column generation. This example is self contained, and implements a solution approach for the [Cutting Stock Problem](https://en.wikipedia.org/wiki/Cutting_stock_problem).

The cutting stock problem consists of a number of orders of items *i &isin; I*, each with a unique size *s<sub>i</sub>* and a demand *d<sub>i</sub>*, as well as the capacity *C* of a single unit of base stock.
The objective of the cutting stock problem is to select cutting patterns that specify how many items of the given sizes are cut from a unit of base stock, such that no more items are cut than the capacity of the base stock allowed.
The patterns must be selected such that the demand *d<sub>i</sub>* of each item is covered, while units of base stock that are required to cut the items is minimized. Note that a cutting pattern can be applied more than once.

A classic approach for this problem is column generation, where we have a master problem that has decision variables to decide how many times each cutting pattern must be applied. As there are exponentially many cutting patterns,
the master problem is initialized with a limited set of cutting patterns. Dual information from the master problem is then provided to a pricing problem, that is basically a knapsack problem that is used to generate new cutting patterns
with positive reduced costs. These cutting patterns are added to the master problem, and this is resolved. This process is repeated iteratively as long as new cutting patterns with positive reduced costs are found. Finally, using the generated columns, the master problem is convereted to an integer programming problem and solved to find a heuristic solution.   

The code for the column generation example consists of the following classes:
* `Instance.java` is used to model an instance of the cutting stock problem. It also contains a static method that can be used to generate a random instance.
* `Main.java` contains a main method that generates random instances and solves them using the column generation approach.
* `MasterModel.java` contains an implementation of the master problem and provides methods to solve the LP-relaxation and generate columns as long as new columns with negative reduced costs are found, as well as a method that solves an integer program with the columns generated while solving the LP-relaxation.
* `Pattern.java` is used to model a cutting pattern, i.e. how to cut a single piece of stock into multiple items.
* `PricingModel.java` implements an optimization model for the pricing problem. The objective can be updated based on the current duals of the master problem, and solving it gives a pattern with maximal reduced costs. Note that adding a pattern to the master problem only has effect if the reduced costs are in fact positive.
* `Solution.java` represents a solution to a particular instance of the problem. The solution consists of utilized patterns, and how often each pattern must be applied to a piece of stock.

Currently, all documentation of this example is only provided within the source code itself. In future updates to this repository, the Javadoc documentation may be added to the Github pages website.

# Using CPLEX

## Obtaining CPLEX

For this example to work, you need to have a working version of the CPLEX Java Library `cplex.jar` and the native library. The native libary is called `cplexYYYY.dll` on Windows, `libcplexYYYY.jnilib` on MacOS and `libcplexYYYY.so` on Linux, where `YYYY` is replaced by the version of your CPLEX library (e.g. 1263).

Students who do not have these files can obtain the full [IBM ILOG CPLEX Optimization Studio](https://ibm.onthehub.com/WebStore/ProductSearchOfferingList.aspx?srch=cplex) from IBM for free using a student account.

After installing the IBM ILOG CPLEX Optimization Studio, you can find the .jar file in `cplex/lib` relative to the installation folder of the software. The native library can be found in `cplex/bin/x64_win64` on Windows installations, and similar locations on MacOS or Linux.

## Adding CPLEX to an Eclipse Project

If you only need CPLEX, the easiest way to include it in an Eclipse project is to just add it is a library to the project. You can take the following steps to do this:

1. Create a directory `lib` in the root of your project and copy `cplex.jar` and the native library file there.
1. Go to the project properties, which can be access by right clicking on your project folder or via the Project menu in the menu bar.
3. Go to the Java Build Path option and select the `Libraries` tab.
4. Press `Add JARs` and select the `cplex.jar` file from the lib folder.
5. Click on the `>` symbol in front of `cplex.jar`, select `Native Library Location` and click `Edit`. Click `Workspace` and select the `lib` folder that contains the native library.

There is a [YouTube video](https://youtu.be/C4YDrVT3fcg) that shows these steps.

## Adding CPLEX via Maven

If you have the full IBM ILOG CPLEX Optimization Studio installed, you can also add CPLEX as a [Maven](https://maven.apache.org/) dependency. 

```
<dependency>
   <groupId>cplex</groupId>
   <artifactId>cplex</artifactId>
   <version>YY.Y.Y</version>
   <scope>system</scope>
   <systemPath>${env.CPLEX_STUDIO_DIRYYYY}/cplex/lib/cplex.jar</systemPath>
</dependency>
```

Note that you have to replace the Y's with the proper CPLEX version. For CPLEX 12.6.3, the snippet would be:

```
<dependency>
   <groupId>cplex</groupId>
   <artifactId>cplex</artifactId>
   <version>12.6.3</version>
   <scope>system</scope>
   <systemPath>${env.CPLEX_STUDIO_DIR1263}/cplex/lib/cplex.jar</systemPath>
</dependency>
```
This approach assumes that a system environment variable `CPLEX_STUDIO_DIRYYYY` exists and that the CPLEX
binaries are on the system path. For Windows installations of full ILOG CPLEX Optimization Studio this is
the case and this step is sufficient. I am currently unaware if this works out-of-the-box on MacOS and
Linux as well.

## Further Reading

It is highly recommended to have a look at the [Java CPLEX Reference Documentation](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/index.html) to discover what methods are available for a `IloCplex` modelling object. Be sure to have a look at [the documentation of the](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/ilog/cplex/IloCplexModeler.html) `IloCplexModeler` class, which is a superclass of the `IloCplex` class you typically use to build a model in Java. In particular, the `addEq()`, `addLe()` and `addGe()` are important for the modelling of constraints, the `addMaximize()` and `addMinimize()` methodes are important for the modelling of the objective, and `sum()` and `prod()` are important for building mathematical expressions in CPLEX. It is also recommended to review [the documentation of](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/ilog/cplex/IloCplex.html) the `IloCplex` class, in particular the `solve()`, `getValue()`, `getObjValue()`, `exportModel()` and `setOut()` methods are important. Furthermore, you can finetune the advanced settings of CPLEX the `setParam()` methods if you desire. IBM provides a [list of available parameters](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/CPLEX/Parameters/topics/introListAlpha.html).

