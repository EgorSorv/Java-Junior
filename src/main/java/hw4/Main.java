package hw4;


import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

/**
 * 1. Создать сущность Student с полями: <p>
 * 1.1 id - int <p>
 * 1.2 firstName - string <p>
 * 1.3 secondName - string <p>
 * 1.4 age - int <p>
 * 2. Подключить hibernate. Реализовать простые запросы: Find(by id), Persist, Merge, Remove <p>
 * 3. Попробовать написать запрос поиска всех студентов старше 20 лет (session.createQuery) <p>
 */

public class Main {
    public static void main(String[] args) throws SQLException {
        Configuration configuration = new Configuration().configure();

        try(SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3060/test",
                    "root", "password"); Statement statement = connection.createStatement()) {
                insertStudents(sessionFactory);
                statement.executeQuery("select * from students");
                System.out.println("\n");
            }

            printStudents(sessionFactory);
            System.out.println("\n");

            insertNewStudent(sessionFactory);
            findStudent(sessionFactory, 11);
            System.out.println("\n");

            updateStudentSecondName(sessionFactory, 11);
            findStudent(sessionFactory, 11);
            System.out.println("\n");

            deleteStudent(sessionFactory, 11);
            printStudents(sessionFactory);
            System.out.println("\n");

            findStudentsByAge(sessionFactory);
        }
    }

    private static void printStudents(SessionFactory sessionFactory) {
        for (int i = 1; i <= 10; i++) {
            findStudent(sessionFactory, i);
        }
    }

    private static void findStudent(SessionFactory sessionFactory, int id) {
        try (Session session = sessionFactory.openSession()) {
            Student student = session.find(Student.class, id);
            System.out.println(student);
        }
    }

    private static void updateStudentSecondName(SessionFactory sessionFactory, int id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("select s from Student s where id = :id", Student.class);
            query.setParameter("id", id);
            Student student = (Student) query.getSingleResult();

            Transaction transaction = session.beginTransaction();
            student.setSecondName("New Student");
            session.merge(student);

            transaction.commit();
        }
    }

    private static void deleteStudent(SessionFactory sessionFactory, int id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("select s from Student s where id = :id", Student.class);
            query.setParameter("id", id);
            Student student = (Student) query.getSingleResult();

            Transaction transaction = session.beginTransaction();
            session.remove(student);

            transaction.commit();
        }
    }

    private static void findStudentsByAge(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            // SQL -> Structure Query Language
            // JQL -> Java Query Language

            Query query = session.createQuery("select s from Student s where age > :age", Student.class);
            query.setParameter("age", 20);
            List<Student> resultList = query.getResultList();

            System.out.println(resultList);
        }
    }

    private static void insertNewStudent(SessionFactory sessionFactory) {
        Student student = new Student();
        student.setId(11);
        student.setFirstName("Ivan");
        student.setSecondName("Ivan");
        student.setAge(19);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(student);

            transaction.commit();
        }
    }

    private static void insertStudents(SessionFactory sessionFactory) {
        Random random = new Random();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (int i = 1; i <= 10; i++) {
                Student student = new Student();
                student.setId(i);
                student.setFirstName("Student №" + i);
                student.setSecondName("Person №" + i);
                student.setAge(random.nextInt(17, 27));

                session.persist(student);
            }

            transaction.commit();
        }
    }
}
