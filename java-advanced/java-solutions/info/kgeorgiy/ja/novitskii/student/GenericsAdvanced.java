package info.kgeorgiy.ja.novitskii.student;

import java.util.ArrayList;
import java.util.List;

class Parent {
    void hello() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    int id = 1;

    @Override
    void hello() {
        System.out.println("Child");
    }

    void unique() {
        System.out.println("Unique");
    }
}

public class GenericsAdvanced {

    // В RunTime Generic класс стирает тип и мы ничего не знаем
    // о конкретном типе объекта внутри класса. Когда мы, например,
    // хотим вернуть объект типа T Generic-a в RunTime, то мы
    // возвращаем объект типа Object, прикастовывая его к типу
    // параметра Generic-a. return (T) obj

    // Точка входа -> момент, когда происходит добавление объекта в Generic
    // добавляется как бы объект типа Object, и происходит каст этого
    // Object-a к Generic типу.

    // Точка выхода у wildcard-a ? -> Object
    // Точка входа у wildcard-a ? -> null, в него ничего нельзя класть,
    // потому что непонятно, что класть, тип стирается и нет никакой информации.
    public Object example(List<?> arr) {
        return arr.get(0);
    }

    // Точка выхода у wildcard-a ? super A -> Object
    // Точка входа у wildcard-a ? super A -> A, потому что зная
    // настоящий тип Java кастанет объект типа A к произвольному предку.
    // (RealTypeAncestor) tObjTypeA
    public <T> Object exampleOld(List<? super T> arr) {
        T obj = null;
        arr.add(obj);
        return arr.get(0);
    }
    // Точка выхода у wildcard-a ? extends A -> A
    // Точка входа -> null. объект типа T положить нельзя,
    // так как DownCast из предка в потомка невозможен
    // (ChildType) tParentTypeObj

    public <T> T exampleNew(List<? extends T> arr) {
        return arr.get(0);
    }


    // Переменная типа List ссылается на объект типа ArrayList
    // lst.getClass() -> ArrayList
    List<Integer> lst = new ArrayList<>();

    // Переменная типа Object ссылается на объект типа ArrayList
    // lstBad.getClass() -> ArrayList
    // НО ФУНКЦИОНАЛ по данной переменной будет как у Object-a
    // ВСЕ МЕТОДЫ ТИПА ArrayList не будут доступны переменной
    // lstBad
    static Object lstBad = new ArrayList<>();

    // Кастовать можно потомка к предку CHILD -> PARENT
    // Как бы обрезая область значений.

    // Upcasting. После каста вверх по перемеменной p
    // будут доступны все общие мемберы Parent-a и Child-a
    // А ТАКЖЕ ВСЕ ПЕРЕОПРЕДЕЛЕННЫЕ МЕТОДЫ CHILD-a. p.hello() -> "Child"

    // При этом уникальные мемберы и уникальные методы Child-a
    // сквозь переменную типа Parent-a видны не будут

    static Parent p = new Child(); // Upcasting справа от равно
    // int a = p.id; // compile error, потому что id уникален для Child-a
    // p.unique(); // compile error -> этот метод только у Child-a
    static Parent pCast = (Parent) new Child();
    static Child child = new Child();

    public static void main(String[] args) {
        lstBad.toString();
        // p.hello -> Child, так как вызовется переопределенный метод
        p.hello();

        // DownCasting. КАСТ вниз из предка в потомка,
        // ТОЛЬКО ЕСЛИ МЫ ЗНАЕМ, что p изначально при создании был Child-ом
        child = (Child) p;
        child = (Child) pCast;
        child.unique();

        Child childBad = new Child();
        Parent parentBad = new Parent();
        // ClassCastException, так как parentBad изначально был создан
        // как Parent
        childBad = (Child) parentBad;
    }

    // ИТОГ:
    // Upcasting возможен всегда и потомок типа Child всегда скастится
    // к Parent-y с потерей уникальных мемберов, но с сохранением
    // переопределенных методов.

    // DownCasting просто из Parent-a в Child-a НЕВОЗМОЖЕН.
    // но ВОЗМОЖЕН, если переменная типа Parent изначально ссылалась
    // на объект типа Child.

    // Cast происходит справа от = по отношению к типу левого операнда
    // TypeA objA = objB -> кастуется objB к TypeA, а не objA к TypeB

}
