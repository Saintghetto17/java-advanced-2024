package info.kgeorgiy.ja.novitskii.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {

    private final Comparator<Student> comparator = Comparator.comparing(Student::getLastName).
            thenComparing(Student::getFirstName).
            thenComparing(Comparator.reverseOrder());

    private <R> Stream<R> mapGeneral(Function<Student, ? extends R> mapper, Collection<Student> students) {
        return students.
                stream().
                map(mapper);
    }

    private Stream<Student> filterGeneralSorted(Predicate<? super Student> predicate, Collection<Student> students) {
        return students.
                stream().
                filter(predicate).
                sorted(comparator);
    }

    private Stream<Student> sortGeneral(Comparator<? super Student> comparator, Collection<Student> students) {
        return students.
                stream().
                sorted(comparator);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mapGeneral(Student::getFirstName, students).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mapGeneral(Student::getLastName, students).
                collect(Collectors.toList());
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return mapGeneral(Student::getGroup, students).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mapGeneral(student -> student.getFirstName() +
                " " + student.getLastName(), students).
                collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return mapGeneral(Student::getFirstName, students).
                collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students
                .stream()
                .max(Comparator.naturalOrder()).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortGeneral(Comparator.comparing(Student::getId), students)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortGeneral(comparator, students)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return filterGeneralSorted(student -> student.getFirstName().equals(name), students).
                collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return filterGeneralSorted(student -> student.getLastName().equals(name), students).
                collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return filterGeneralSorted(student -> student.getGroup().equals(group), students).
                collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return filterGeneralSorted(student -> student.getGroup().equals(group), students)
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName,
                        BinaryOperator.minBy(Comparator.naturalOrder())));
    }
}
