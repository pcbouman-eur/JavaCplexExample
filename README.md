# Java and CPLEX example project: Precendence Constrained Knapsack Problem
This repository contains example code for a small Java Project that utilizes the [IBM ILOG CPLEX library](https://www.ibm.com/products/ilog-cplex-optimization-studio)
to solve instances of the Precendence Constrained Knapsack Problem. 

The Precendence Constrained Knapsack Problem is a variant of the regular Knapsack Problems where we have items *I*, where for each item *i* we have a profit *p<sub>i</sub>* and a weight *w<sub>i</sub>*.
Furthermore, there is a capacity*b* and a directed graph *G = (I,A)* defined on the items. The objective is to select a set of items *S &sube; I*, such that the sum of*p<sub>i</sub>* over *i &isin; S* is maximized,
while the sum of *w<sub>i</sub>* over *i &isin; S* is smaller than or equal to *b*. Additionally, an item *i* can only be in *S* if all items *j* for which *A* contains an arc *(i,j)* are also in *S*.

The code in this repository consists of the following files:
* `DirectedGraph.java`  contains a general purpose data structure for directed graphs. It provides methods to add nodes and arcs, and allows us to attach arbitrary data types to the nodes and arcs.
* `DirectedGraphArc.java` models the arcs that are created by the graph. These arcs contain the origin node of an arc, the destination node of an arc and the data associated with the arc.
* `Item.java` models a knapsack item, without its precendence constraints (these are modelled in the project using a DirectedGraph object).
* `Model.java` manages a CPLEX integer linear programming model, converting a directed graph with a precendence constrained knapsack problem and a capacity to a CPLEX model that can be solved.
* `Main.java` is used to read in an instance of the problem from a text file, convert it to a directed graph, and call the `Model` class to solve the instance.
 
# Using CPLEX

## Obtaining CPLEX

For this example to work, you need to have a working version of the CPLEX Java Library `cplex.jar` and the native library. The native libary is called `cplexYYYY.dll` on Windows, `libcplexYYYY.jnilib` on MacOS and `libcplexYYYY.so` on Linux, where `YYYY` is replaced by the version of your CPLEX library (e.g. 1263).

Students who do not have these files can obtain the full [IBM ILOG CPLEX Optimization Studio](https://ibm.onthehub.com/WebStore/ProductSearchOfferingList.aspx?srch=cplex) from IBM for free using a student account.

After installing the IBM ILOG CPLEX Optimization Studio, you can find the .jar file in `cplex/lib` relative to the installation folder of the software. The native library can be found in `cplex/bin/x64_win64` on Windows installations, and similar locations on MacOS or Linux.

## Further Reading

It is highly recommended to have a look at the [Java CPLEX Reference Documentation](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/index.html) to discover what methods are available for a `IloCplex` modelling object. Be sure to have a look at [the documentation of the](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/ilog/cplex/IloCplexModeler.html) `IloCplexModeler` class, which is a superclass of the `IloCplex` class you typically use to build a model in Java. In particular, the `addEq()`, `addLe()` and `addGe()` are important for the modelling of constraints, the `addMaximize()` and `addMinimize()` methodes are important for the modelling of the objective, and `sum()` and `prod()` are important for building mathematical expressions in CPLEX. It is also recommended to review [the documentation of](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/ilog/cplex/IloCplex.html) the `IloCplex` class, in particular the `solve()`, `getValue()`, `getObjValue()`, `exportModel()` and `setOut()` methods are important. Furthermore, you can finetune the advanced settings of CPLEX the `setParam()` methods if you desire. IBM provides a [list of available parameters](https://www.ibm.com/support/knowledgecenter/SSSA5P_12.8.0/ilog.odms.cplex.help/CPLEX/Parameters/topics/introListAlpha.html).

