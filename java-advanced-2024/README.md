# Тесты к курсу «Технологии Java»

[Условия домашних заданий](https://www.kgeorgiy.info/courses/java-advanced/homeworks.html)


## Домашнее задание 10. HelloUDP

Интерфейсы

 * `HelloUDPClient` должен реализовывать интерфейс
    [HelloClient](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPServer` должен реализовывать интерфейс
    [HelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * Базовый вариант (`client` и `server`)
 * Простой вариант (`new-client` и `new-server`)
    * `HelloUDPServer` должен реализовывать интерфейс
      [NewHelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloServer.java)
 * Сложный вариант (`new-client-i18n` и `new-server-i18n`)
    * `HelloUDPServer` должен реализовывать интерфейс
      [NewHelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloServer.java).
    * На противоположной стороне находится система, дающая ответы на различных языках.
 * Продвинутый вариант (`new-client-evil` и `new-server-evil`)
    * `HelloUDPServer` должен реализовывать интерфейс.
      [NewHelloServer](modules/info.kgeorgiy.java.advanced.hello/info/kgeorgiy/java/advanced/hello/NewHelloServer.java).
    * на противоположной стороне находится старая система,
      не полностью соответствующая последней версии спецификации.


## Домашнее задание 9. Web Crawler

Тесты используют только внутренние данные и ничего не скачивают из интернета.

Тестирование

 * простой вариант (`easy`):
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/EasyCrawlerTest.java)
 * сложный вариант (`hard`):
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/HardCrawlerTest.java)
 * простая модификация (`new-easy`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewEasyCrawlerTest.java)
 * сложная модификация (`new-hard`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/NewHardCrawlerTest.java)
 * продвинутый вариант (`advanced`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawler.java),
    [тесты](modules/info.kgeorgiy.java.advanced.crawler/info/kgeorgiy/java/advanced/crawler/AdvancedCrawlerTest.java)


## Домашнее задание 8. Параллельный запуск

Тестирование
 * простой вариант (`scalar`):
    [тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ScalarMapperTest.java)
 * сложный вариант (`list`):
    [тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/ListMapperTest.java)
 * простая модификация (`new-scalar`):
    * Класс `IterativeParallelism` должен реализовывать интерфейс
      [NewScalarIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewScalarIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/NewScalarMapperTest.java)
 * сложная модификация (`new-list`):
    * Класс `IterativeParallelism` должен реализовывать интерфейс
      [NewListIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewListIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/NewListMapperTest.java)
 * продвинутая модификация (`advanced`):
    * Класс `IterativeParallelism` должен реализовывать интерфейс
      [AdvancedIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIP.java).
    * [Тесты](modules/info.kgeorgiy.java.advanced.mapper/info/kgeorgiy/java/advanced/mapper/AdvancedMapperTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.mapper](artifacts/info.kgeorgiy.java.advanced.mapper.jar)


## Домашнее задание 7. Итеративный параллелизм

Тестирование

 * простой вариант (`scalar`):
    * Класс должен реализовывать интерфейс
      [ScalarIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ScalarIP.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ScalarIPTest.java)
 * сложный вариант (`list`):
    * Класс должен реализовывать интерфейс
      [ListIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ListIP.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/ListIPTest.java)
 * простая модификация (`new-scalar`):
    * Класс должен реализовывать интерфейс
      [NewScalarIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewScalarIP.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewScalarIPTest.java)
 * сложная модификация (`new-list`):
    * Класс должен реализовывать интерфейс
      [NewListIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewListIP.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/NewListIPTest.java)
 * продвинутая модификация (`advanced`):
    * Класс должен реализовывать интерфейс
      [AdvancedIP](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIP.java).
    * [тесты](modules/info.kgeorgiy.java.advanced.iterative/info/kgeorgiy/java/advanced/iterative/AdvancedIPTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.iterative](artifacts/info.kgeorgiy.java.advanced.iterative.jar)


## Домашние задания 5, 6. JarImplementor

Класс `Implementor` должен дополнительно реализовывать интерфейс
[JarImpler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java).

Скрипты, `MANIFEST.MF` и `.jar-файл` должны находиться в каталоге `scripts` 
в корне репозитория. 
Скомпилированный Javadoc должен находиться в каталоге `javadoc` 
в корне репозитория.

В скриптах вы можете рассчитывать на то, что репозиторий курса 
лежит рядом с вашим репозиторием в каталоге `java-advanced-2024`.

Исходный код

 * простой вариант (`jar-interface`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceJarImplementorTest.java)
 * сложный вариант (`jar-class`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassJarImplementorTest.java)
 * продвинутый вариант (`advanced`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/AdvancedJarImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)


## Домашнее задание 4. Implementor

Класс `Implementor` должен реализовывать интерфейс
[Impler](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java).

Исходный код

 * простой вариант (`interface`): 
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/InterfaceImplementorTest.java)
 * сложный вариант (`class`):
    [тесты](modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ClassImplementorTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.implementor](artifacts/info.kgeorgiy.java.advanced.implementor.jar)


## Домашнее задание 3. Студенты

Исходный код

 * простой вариант (`StudentQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/StudentQueryTest.java)
 * сложный вариант (`GroupQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/GroupQueryTest.java)
 * продвинутый вариант (`AdvancedQuery`):
    [интерфейс](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQuery.java),
    [тесты](modules/info.kgeorgiy.java.advanced.student/info/kgeorgiy/java/advanced/student/AdvancedQueryTest.java)


## Домашнее задание 2. ArraySortedSet

Исходный код

 * простой вариант (`SortedSet`):
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/SortedSetTest.java)
 * сложный вариант (`NavigableSet`):
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/NavigableSetTest.java)
 * продвинутый вариант (`AdvancedSet`):
    * `ArraySet` должен дополнительно реализовывать интерфейс `List`
    * Код должен собираться и работать под Java 21
    * [тесты](modules/info.kgeorgiy.java.advanced.arrayset/info/kgeorgiy/java/advanced/arrayset/AdvancedSetTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.arrayset](artifacts/info.kgeorgiy.java.advanced.arrayset.jar)


## Домашнее задание 1. Обход файлов

Исходный код

 * простой вариант (`Walk`):
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/WalkTest.java)
 * сложный вариант (`RecursiveWalk`):
    [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)
 * продвинутый вариант (`AdvancedWalk`):
    * Третьим аргументом командной строки может быть задан алгоритм хеширования: `jenkins` или `sha-1`.
    * [тесты](modules/info.kgeorgiy.java.advanced.walk/info/kgeorgiy/java/advanced/walk/AdvancedWalkTest.java)

Тестовый модуль: [info.kgeorgiy.java.advanced.walk](artifacts/info.kgeorgiy.java.advanced.walk.jar)

Для того, чтобы протестировать программу:

 * Скачайте
    * тесты
        * [базовый модуль](artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [тестовый модуль](artifacts/info.kgeorgiy.java.advanced.walk.jar) (свой для каждого ДЗ)
    * [библиотеки](lib)
 * Откомпилируйте решение домашнего задания
 * Протестируйте домашнее задание
    * Текущая директория должна:
       * содержать все скачанные `.jar` файлы;
       * содержать скомпилированное решение;
       * __не__ содержать скомпилированные самостоятельно тесты.
    * Запустите тесты:
        `java -cp . -p . -m <тестовый модуль> <вариант> <полное имя класса>`
    * Пример для простого варианта ДЗ-1:
        `java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>`
