@echo off

javac -d . -classpath ../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor ../java-solutions/info/kgeorgiy/ja/novitskii/implementor/Implementor.java

jar cfm Implementor.jar MANIFEST.MF info

rm -r info