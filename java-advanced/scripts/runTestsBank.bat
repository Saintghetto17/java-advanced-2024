javac -cp "../lib/apiguardian-api-1.1.2.jar:../lib/junit-jupiter-api-5.10.2.jar:../lib/junit-jupiter-engine-5.10.2.jar:../lib/junit-platform-commons-1.10.2.jar:../lib/junit-platform-engine-1.10.2.jar:../lib/junit-platform-launcher-1.10.2.jar" ../java-solutions/info/kgeorgiy/ja/novitskii/bank/*.java ../java-solutions/info/kgeorgiy/ja/novitskii/bank/tests/*.java -d ./class_files
java -jar ../lib/junit-platform-console-standalone-1.10.2.jar -cp ./class_files --select-class info.kgeorgiy.ja.novitskii.bank.tests.BaseTests