package hw1;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Department> departments = new ArrayList<>();
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 10; i++)
            departments.add(new Department("Department №" + i));

        for (int i = 0; i < 50; i++)
            persons.add(new Person(
                    "Person №" + i,
                    ThreadLocalRandom.current().nextInt(20, 61),
                    ThreadLocalRandom.current().nextInt(20_000, 100_000) * 1.0,
                    departments.get(ThreadLocalRandom.current().nextInt(departments.size()))
            ));

        printNamesOrdered(persons);
        System.out.println();

        Map<Department, Person> departmentOldestPerson = printDepartmentOldestPerson(persons);
        System.out.println(departmentOldestPerson + "\n");

        List<Person> firstPersons = findFirstPersons(persons);
        System.out.println(firstPersons + "\n");

        Optional<Department> topDepartment = findTopDepartment(persons);
        System.out.println(topDepartment + "\n");
    }

    /**
     * Вывести на консоль отсортированные (по алфавиту) имена персонов
     */
    public static void printNamesOrdered(List<Person> persons) {
        persons.stream()
                .map(Person::getName)
                .sorted()
                .forEach(System.out::println);
    }

    /**
     * В каждом департаменте найти самого взрослого сотрудника.
     * Вывести на консоль маппинг department -> personName
     * Map<Department, Person>
     */
    public static Map<Department, Person> printDepartmentOldestPerson(List<Person> persons) {
        Comparator<Person> ageComparator = Comparator.comparing(Person::getAge);
        return persons.stream()
                .collect(Collectors.toMap(Person::getDepartment,
                        Function.identity(), (first, second) -> {
                            if (ageComparator.compare(first, second) > 0)
                                return first;

                            return second;
                        }));
    }

    /**
     * Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
     */
    public static List<Person> findFirstPersons(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() < 30)
                .filter(person -> person.getSalary() > 50_000.0)
                .limit(10)
                .toList();
    }

    /**
     * Найти департамент, чья суммарная зарплата всех сотрудников максимальна
     */
    public static Optional<Department> findTopDepartment(List<Person> persons) {
        return persons.stream()
                .collect(Collectors.groupingBy(Person::getDepartment,
                        Collectors.summingDouble(Person::getSalary)))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static class Person {
        private String name;
        private int age;
        private double salary;
        private Department department;

        public Person(String name, int age, double salary, Department department) {
            this.name = name;
            this.age = age;
            this.salary = salary;
            this.department = department;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public double getSalary() {
            return salary;
        }

        public Department getDepartment() {
            return department;
        }

        @Override
        public String toString() {
            return "name - " + name +
                    ", age - " + age +
                    ", salary - " + salary +
                    ", department - " + department;
        }
    }

    public static class Department {
        private String name;

        public Department(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Department that = (Department) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
