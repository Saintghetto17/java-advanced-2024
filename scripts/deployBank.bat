@echo off
rem Compile the Java files
javac ../java-solutions/info/kgeorgiy/ja/novitskii/bank/*.java

rem Start the Server in a new command window
start cmd /k "java -classpath ../java-solutions info.kgeorgiy.ja.novitskii.bank.Server"
timeout /t 5 /nobreak

rem Start the Client in another new command window
start cmd /k "java -classpath ../java-solutions info.kgeorgiy.ja.novitskii.bank.Client Ilya Novitskiy 12345 12 1000"

echo All commands executed. Main CMD window will stay open.
pause
