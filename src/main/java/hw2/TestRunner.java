package hw2;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class TestRunner {
    private static int beforeAllPos, befoeEachPos, afterAllPos, afterEachPos = -1;

    public static void run(Class<?> testClass) {
        final Object testObj = initTestObj(testClass);

        findAnnotations(testClass);

        ArrayList<Integer> order = createOrderArray(testClass);
        sortTestOrder(order, 0, order.size() - 1);

        try {
            testClass.getDeclaredMethods()[beforeAllPos].invoke(testObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        for (Integer num : order) {
            for (int i = 0; i < testClass.getDeclaredMethods().length; i++) {
                if (testClass.getDeclaredMethods()[i].accessFlags().contains(AccessFlag.PRIVATE))
                    continue;

                if (testClass.getDeclaredMethods()[i].getAnnotation(Test.class) != null &&
                        testClass.getDeclaredMethods()[i].getAnnotation(Test.class).order() == num) {
                    try {
                        testClass.getDeclaredMethods()[befoeEachPos].invoke(testObj);
                        testClass.getDeclaredMethods()[i].invoke(testObj);
                        testClass.getDeclaredMethods()[afterEachPos].invoke(testObj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        try {
            testClass.getDeclaredMethods()[afterAllPos].invoke(testObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Integer> createOrderArray(Class<?> testClass) {
        ArrayList<Integer> order = new ArrayList<>();

        for (int i = 0; i < testClass.getDeclaredMethods().length; i++) {
            if (testClass.getDeclaredMethods()[i].accessFlags().contains(AccessFlag.PRIVATE))
                continue;

            if (testClass.getDeclaredMethods()[i].getAnnotation(Test.class) != null)
                order.add(testClass.getDeclaredMethods()[i].getAnnotation(Test.class).order());
        }

        return order;
    }

    private static void sortTestOrder(ArrayList<Integer> order, int low, int high) {
        if (low < high) {
            int pi = partition(order, low, high);

            sortTestOrder(order, low, pi - 1);
            sortTestOrder(order, pi + 1, high);
        }
    }

    private static int partition(ArrayList<Integer> order, int low, int high) {
        int middle = low + (high - low) / 2;
        int pivot = order.get(middle);

        int temp = order.get(middle);
        order.set(middle, order.get(high));
        order.set(high, temp);

        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (order.get(j) < pivot) {
                i++;

                temp = order.get(i);
                order.set(i, order.get(j));
                order.set(j, temp);
            }
        }

        temp = order.get(i + 1);
        order.set(i + 1, order.get(high));
        order.set(high, temp);

        return i + 1;
    }

    private static void findAnnotations(Class<?> testClass) {
        for (int i = 0; i < testClass.getDeclaredMethods().length; i++) {
            if (testClass.getDeclaredMethods()[i].accessFlags().contains(AccessFlag.PRIVATE))
                continue;

            if (testClass.getDeclaredMethods()[i].getAnnotation(BeforeAll.class) != null)
                beforeAllPos = i;

            if (testClass.getDeclaredMethods()[i].getAnnotation(BeforeEach.class) != null)
                befoeEachPos = i;

            if (testClass.getDeclaredMethods()[i].getAnnotation(AfterAll.class) != null)
                afterAllPos = i;

            if (testClass.getDeclaredMethods()[i].getAnnotation(AfterEach.class) != null)
                afterEachPos = i;
        }
    }

    private static Object initTestObj(Class<?> testClass) {
        try {
            Constructor<?> noArgsConstructor = testClass.getConstructor();
            return noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Нет конструктора по умолчанию");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект тест класса");
        }
    }
}
