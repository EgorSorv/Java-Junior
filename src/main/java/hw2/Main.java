package hw2;

/**
 * 1. Создать аннотации BeforeEach, BeforeAll, AfterEach, AfterAll<p>
 * 2. Доработать класс TestRunner так, что:<p>
 * 2.1 Перед всеми тестами запускаеются методы, над которыми стоит BeforeAll<p>
 * 2.2 Перед каждым тестом запускаются методы, над которыми стоит BeforeEach<p>
 * 2.3 Запускаются все тест-методы (это уже реализовано)<p>
 * 2.4 После каждого теста запускаются методы, над которыми стоит AfterEach<p>
 * 2.5 После всех тестов запускаются методы, над которыми стоит AfterAll<p>
 * Другими словами, BeforeAll -> BeforeEach -> Test1 -> AfterEach -> BeforeEach -> Test2 -> AfterEach -> AfterAll<p>
 * 3.* Доработать аннотацию Test: добавить параметр int order,
 * по котрому нужно отсортировать тест-методы (от меньшего к большему) и запустить в нужном порядке.<p>
 * Значение order по умолчанию - 0<p>
 * 4.** Создать класс Asserter для проверки результатов внутри теста с методами:<p>
 * 4.1 assertEquals(int expected, int actual)<p>
 * Идеи реализации: внутри Asserter'а кидать исключения, которые перехвываются в тесте.<p>
 * Из TestRunner можно возвращать какой-то объект, описывающий результат тестирования.
 */

public class Main {
    public static void main(String[] args) {
        TestRunner.run(Main.class);
        System.out.println();
    }

    @BeforeAll
    void beforeAll() {
        System.out.println("before all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }

    @AfterAll
    void afterAll() {
        System.out.println("after all");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each");
    }

    @Test(order = 2)
    void test1() {
        System.out.println("test 1");
    }

    @Test(order = 1)
    void test2() {
        System.out.println("test 2");
    }
}
