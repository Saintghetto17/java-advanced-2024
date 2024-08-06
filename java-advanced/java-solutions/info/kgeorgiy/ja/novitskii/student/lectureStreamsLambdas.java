package info.kgeorgiy.ja.novitskii.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class lectureStreamsLambdas {

    public static void main(String[] args) {
        BiFunction<String, Long, String> f = (String s, Long v) -> {
            return s + v;
        };
        BiFunction<String, Long, String> f1 = (String s, Long v) -> s + v;
        BiFunction<String, Long, String> f2 = (s, v) -> s + v;

        // Получение/производство объекта
        Supplier<String> s = () -> "!";
        // Потребление
        // объекта
        Consumer<String> c = s1 -> System.out.println(s1 + s1);

        // Композиция

        // Cсылка на МЕТОД КЛАССА + autoboxing
        Function<String, Integer> fParse = Integer::parseInt;

        // Cсылка на МЕТОД экземпляра: this неявный аргумент типа Integer
        // Function<Integer, String> fObj = Integer::toString;
        // x -> x.toString();

        // Метод экземпляра объекта, берем ссылку
        // на метод экземпляра объекта. Первый аргумент уже не this
        // А значит количество принимаемых аргументов 0, теперь это supplier.
        Integer i = 2;
        Supplier<String> f3 = i::toString; // () -> i.toString()
        f3.get();
        // Объект выше зафиксирован.
        i++;

        Supplier<Object> fConstructor = Object::new; // () -> new Object()

        // Замыкания.
        // Effectively final переменные:
        // 1) Ровно одно присваивание
        // 2) Может быть без модификатора final
        // 3) Могут использоваться в лямбдах

        // Замыкание -> объект в куче: разные вызовы->создание разных объектов замыкание

        // Замыкание ~ зависимость от внешних переменных. Жрет доп память.

        String hello = "Hello, ";
        Function<String, String> greeter = name -> hello + name;

        // На каждый вызов функции с замыканием мы будем
        // тратить память, так как
        // при каждом вызове функции на куче будет
        // создавать копия объекта замыкания


        // РАССМОТРИМ ДВА ПРИМЕРА: тут не работает, потому что переменная
        // должна быть effectively final.

        // При этом, менять СОСТОЯНИЕ изменяемого объекта мы можем, ГЛАВНОЕ,
        // ЧТОБ НЕ МЕНЯЛАСЬ ССЫЛКА НА ОБЪЕКТ.

//        Integer h = 1;
//        Supplier<String> convert = () -> h.toString();
//        convert.get();
//        h++;
//        convert.get();
//
//        String bad = "do not do this";
//        Function<String, String> badPractice = str -> bad;
//        bad = "a";


        Integer a = 1;
        Supplier<String> conv = () -> a.toString();

        String good = "a";
        Function<String, String> best = str -> good;


        // Ссылка на метод объекта допускает изменение значения самого объекта
        // Функция Supplier, которая возьмет ссылку на метод КОНКРЕТНОГО ОБЪЕКТА
        // зафиксирует объект ref и будет на нем вызывать метод toString()
        // Никакие внешние изменения ref не будут влиять на работу Supplier-a
        // toString() будет возвращать одно и то же значение всегда, которое он зафиксировал
        // изначально. ОНА ХРАНИТ ОБЪЕКТ изначальный который мы передали. Замыкание хранит объект

        Integer ref = 1; // 0 аргументов принимается
        Supplier<String> cong = ref::toString;
        ref++;

        String refStr = "a"; // 0 аргументов принимается
        Supplier<String> conf = refStr::toString;
        refStr = "aaaaaaaaaaaaaaa";

        // Не работает, так как write бросает IOException
        // А get() не бросает.
        // Consumer<String> c = writer::write

        // Переопределим метод get в своем Supplier-e

        // Предикаты
        Predicate<Integer> p1 = num -> num % 2 == 0;
        p1.test(2);

        Predicate<Integer> p2 = num -> ((num * num) % 7) % 2 == 0;
        Predicate<Integer> predOr = p1.or(p2);
        Predicate<Integer> predAnd = p1.and(p2);
        // Тут нужна явная ссылка на предикат
        Predicate<Integer> opposite = p2.negate();
        // Тут достаточно просто метода, возвращающего bool. Но можно и предикат положить
        Predicate<String> oppositeVersion2 = Predicate.not(String::isEmpty);
        Predicate<String> opp = String::isEmpty;
        opp.negate();

        // Операторы
        UnaryOperator<Integer> unary = i1 -> i1;
        BinaryOperator<Integer> bi = (c1, c2) -> c1 + c2;


        // hashcode и toString для примитивов при помощи оберток
        Integer.toString(1);
        Integer.hashCode(1);

        // Для передачи методов удобно использовать агрегацию
        Integer.sum(1, 2);
        Integer.max(1, -1);

        // Массивы глубокое сравнение, поэлементное
        Objects.deepEquals(new int[]{1, 2, 3}, new int[]{1, 2, 4});


        // СПОСОБ ИЗБЕЖАТЬ null-ов
        // Optional хранит либо null, либо значение само.
        // .of() принимает не null значение
        Optional<Integer> optional = Optional.of(1);
        // Делает null-optional
        Optional<String> optStr = Optional.empty();
        // Если полагаем, что может быть null
        String name = 1 + 2 * 3 > 1 + 8 ? null : "a";
        Optional<String> canBeNull = Optional.ofNullable(name);
        canBeNull.isPresent();
        canBeNull.isEmpty();
        // Получение значения из Optional-a
        String strStr = canBeNull.get();
        // Если в canBeNull ничего не лежит, то вернется аргумент orElse, если лежит, то содержимое Optional-a
        String strGet = canBeNull.orElse("");
        String lazy = canBeNull.orElseGet(() -> "Failed");
        optStr.ifPresent(String::toLowerCase);

        // Фильтруем - возвращаем optional с значением, если предикат корректен
        // Иначе возвращаем пустой Optional
        Optional<String> example = optStr.filter(oppositeVersion2);

        // Optional<T> -> Optional<R>
        Optional<Integer> o = optStr.map(sss -> sss.length());
        Optional<Optional<Integer>> inner = optStr.map(ssss -> Optional.of(ssss.length()));

        Comparator<Integer> comp = Comparator.naturalOrder();
        // Сравнить по comp компаратору, если 1, то сравнить компаратором
        // thenComparingDouble(), и т.д  comp.thenComparing();

        // null-ы первые в порядке
        Comparator.nullsFirst(comp);

        // Iterable<E>.forEach(consumer<-E>)
        // students.forEach(System.out::println);

        // Collection.removeIf(predicate<-E>)
        // Collection.toArray(intFunction<E[]>) -> Возвращает массив ТИПА E, а не OBJECT[]
        // List.replaceAll(unaryOperator<E>)


        // Stream - какой то набор значений типа T, который обрабатывается
        // оптом.
        Stream<Integer> stream;

        // Stream может не хранить элементы, может быть ленивым, может быть бесконечным
        // Получение : из коллекций и массивов, из генераторов, из файлов


        String streamStr = "asasdasdoaskdpaksd[askd[as asdasdaksdaskdoaksdoa [olas[dlasd";
        List<String> lst = new ArrayList<>(List.of("asdas", "asdasdasd", "asdasdasdasd"));


        // min() -> заверщающая операция тут
        lst.stream().filter(st -> st.endsWith("s")).mapToInt(String::length).min();

        // Операции над потоками:
        // 1. Промежуточные(никаких вычислений не происходит) O(1) амортизированно :
        // a) Порождают поток
        // б) Ленивые

        // 2. Завершающие(происходят все вычисления на этом этапе):
        // a) Порождают значения
        // б) Жадные

        // Получение элементов: iterator() - последовательная обработка

        // Создание потока
        Stream<Integer> stream2 = Stream.empty();
        Stream<Integer> strInt = Stream.ofNullable(1);

        // Из генераторов
        Stream<Integer> streamInteger = Stream.generate(() -> 2 * 2);
        Stream<Integer> iterStream = Stream.iterate(0, n -> n + 1);

        // Из коллекций
        Stream<String> coll = lst.stream();

        // Ввод-вывод
        /*
            bufferedReader.lines() - поток строк
            Files.lines(path, charset?) - строки файла
            Files.walk(path, depth?, fileVisitOption...) - поток путей
         */

        // Первые 10 элементов
        coll.limit(10);
        // Скипнуть первые 10
        coll.skip(10);
        // Берет пока удовлетворяет предикату и возвращает поток префикса
        coll.takeWhile(String::isEmpty);
        coll.dropWhile(String::isEmpty);

        Stream<String> sttr = lst.stream();
        Stream.concat(sttr, coll);

        // Из потока int-ов в поток оберток
        // Stream.boxed

        // Комбинаторы -> фильтруем
        lst.stream().filter(st -> st.endsWith("a"));
        lst.stream().mapToInt(l -> l.length());

        // Завершающие операции
        // Проверка предиката
        // stream.allMatch(predicate<-T>)
        // stream.anyMatch(predicate<-T>)
        // findFirst() -> первый попадающий, findAny()->какой то
        // count(), min(comparator<-T>)


        // КОЛЛЕКТОРЫ - способ получить элементы из потока

        // Collector<T, A, R> T - то, что получает из потока,
        // R-то, что генерирует на выходе, промежуточный тип A(не всегда интересен)
        // Завершающая операция R stream.collect(collector<T, A, R>)
        // Использовать коллекторы из класса Collectors
        // toList() - коллектор позволяет преобразовать stream в list

        // Stream API - интерфейс к источнику, а каким образом их будет получать источник
        // стрим скрывает.
        // Stream не изменяет источник и не может изменять источник.
        // все промежуточные операции над потоками — ленивые(LAZY).
        // Они не будут исполнены, пока не будет вызвана терминальная (конечная) операция


        // .sorted функция - это БАРЬЕР, требующий всех элементов потока.
        // Все что до нее выполниться, а все что после нее выполниться
        // только после сортировки и получения всего потока,
        // поэтому все операции до sorted
        // будут применены ко всем элементам.
        // .sorted - должен собрать все элементы.
        // в ХОРОШЕМ стриме не должно быть изменяемого состояния

        // Collector - рецепт терминальной операции, способ
        // комбинирования элементов в единое целое.

        List<Student> students = null;
        GroupName group = null;
        // Что выполняет этот блок кода? Мы делаем стрим из коллекции, фильтруем
        // поток по студентам, у которых группа совпадает с нашей, а потом вызываем
        // терминальную операцию. Каким образом? - группировкой (то есть как бы мапа)
        // ключ - фамилия, а значение -> лексикографически минимальная фамилия.
        // В коллектрах groupingBy, первый элемент - это ключ, а второй это значение
        // Чтоб получить значение зависящее от стрима, нам нужно использовать
        // коллектор!!! ключ - КОЛЛЕКТОР, потому что значение из стрима
        students.stream().filter(student -> student.getGroup().equals(group))
                .collect(Collectors.groupingBy(Student::getLastName, Collectors.collectingAndThen(Collectors.
                        minBy(Comparator.comparing(Student::getFirstName)), st -> st.map(Student::getFirstName)
                        .orElse(""))));

        // Из лекции CSC Тагира Валеева:
        // minBy возвратит Optional из стрима
        // Чтоб избавиться от Optional Напишем: Collectors.collectingAndThen
        // - это комбинирующий коллектор, который позволяет использовать какой-нибудь
        // коллектор, а потом к результату использования коллектора применить
        // дополнительную функцию. В нашем случае Optional::get :
        // mapping - отображение студентов на ИМЕНА
        students.stream().filter(student -> student.getGroup().equals(group))
                .collect(Collectors.groupingBy(Student::getLastName, Collectors.
                        collectingAndThen(Collectors.mapping(Student::getFirstName,
                                Collectors.minBy(Comparator.naturalOrder())), Optional::get)));


        // Но есть более красивое решение. Воспользуемся коллектором .toMap
        // ведь нам нужна МАПА!
        // Выбери минимальное значение из двух
        // 1-ое поле - это ключ, 2-ое поле это значение, а третье - это коллектор,
        // который говорит, что делать в случае коллизии фамилий - возьми минимальное имя
        students.stream().filter(student -> student.getGroup().equals(group))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(Comparator.naturalOrder())));

        List<Integer> lstExampleThen = new ArrayList<>(List.of(1, 2, 3));
        Comparator<Integer> compExample = (Comparator<Integer>) Comparator.naturalOrder().reversed().thenComparing(Comparator.comparing(Object::hashCode).reversed());
    }
}
