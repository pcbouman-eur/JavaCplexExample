# Obtaining CPLEX

For this example to work, you need to have a working version of the CPLEX Java Library `cplex.jar` and the native library. The native libary is called `cplexYYYY.dll` on Windows, `libcplexYYYY.jnilib` on MacOS and `libcplexYYYY.so` on Linux, where `YYYY` is replaced by the version of your CPLEX library (e.g. 1263).

Students who do not have these files can obtain the full [IBM ILOG CPLEX Optimization Studio](https://ibm.onthehub.com/WebStore/ProductSearchOfferingList.aspx?srch=cplex) from IBM for free using a student account.

After installing the IBM ILOG CPLEX Optimization Studio, you can find the .jar file in `cplex/lib` relative to the installation folder of the software. The native library can be found in `cplex/bin/x64_win64` on Windows installations, and similar locations on MacOS or Linux.

