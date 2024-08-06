set dependency=..\..\java-advanced-2024\modules\info.kgeorgiy.java.advanced.iterative

javadoc -d ..\javadoc -public -author -version ^
..\java-solutions\info\kgeorgiy\ja\novitskii\iterative\IterativeParallelism.java ^
%dependency%\info\kgeorgiy\java\advanced\iterative\NewScalarIP.java ^
%dependency%\info\kgeorgiy\java\advanced\iterative\ScalarIP.java

pause