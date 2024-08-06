set dependency=..\..\java-advanced-2024\modules\info.kgeorgiy.java.advanced.implementor

javadoc -d ..\javadoc -private -author -version ^
..\java-solutions\info\kgeorgiy\ja\novitskii\implementor\Implementor.java ^
%dependency%\info\kgeorgiy\java\advanced\implementor\JarImpler.java ^
%dependency%\info\kgeorgiy\java\advanced\implementor\Impler.java ^
%dependency%\info\kgeorgiy\java\advanced\implementor\ImplerException.java

pause