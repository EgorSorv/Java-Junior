package hw3;

import java.sql.*;

/**
 * 0. Разобрать код с семниара <p>
 * 1. Повторить код с семниара без подглядываний на таблице Student с полями: <p>
 * 1.1 id - int <p>
 * 1.2 firstName - string <p>
 * 1.3 secondName - string <p>
 * 1.4 age - int <p>
 * 2.* Попробовать подключиться к другой БД <p>
 * 3.** Придумать, как подружить запросы и reflection: <p>
 * 3.1 Создать аннотации Table, Id, Column <p>
 * 3.2 Создать класс, у которого есть методы: <p>
 * 3.2.1 save(Object obj) сохраняет объект в БД <p>
 * 3.2.2 update(Object obj) обновляет объект в БД <p>
 * 3.2.3 Попробовать объединить save и update (сначала select, потом update или insert) <p>
 */

public class Main {
    public static void main(String[] args) {
        String userName = "root";
        String password = "password";
        String url = "jdbc:mysql://127.0.0.1:8889/test";

        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            acceptConnection(connection);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    private static void acceptConnection(Connection connection) throws SQLException {
        createTable(connection);
        insertData(connection);
        deleteRow(connection, 4);
        updateRow(connection, "Bob", "Boris");

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select id, first_name, second_name, age " +
                    "from student");

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String secondName = resultSet.getString("second_name");
                int age = resultSet.getInt("age");

                System.out.println("id = " + id + ", first_name = " + firstName + ", second_name" +
                        secondName + ", age = " + age);
            }
        }
    }

    private static void deleteRow(Connection connection, int id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("delete from student where id = ?")) {
            stmt.setInt(1, id);
            System.out.println("DELETED ROWS: " + stmt.executeUpdate());
        }
    }

    private static void updateRow(Connection connection, String name, String secondName) throws SQLException {
        try (PreparedStatement stmt =
                     connection.prepareStatement("update student set second_name = ? where first_name = ?")) {
            stmt.setString(1, secondName);
            stmt.setString(2, name);

            System.out.println("AFFECTED ROWS: " + stmt.executeUpdate());
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate("""
                insert into student(id, first_name, second_name, age) values
                (1, 'Igor', 'Igor', 21),
                (2, 'Bob', 'Bob', 45),
                (3, 'Ivan', 'Ivan', 31),
                (4, 'Alex', 'Alex', 26),
                (5, 'Peter', 'Peter', 19)
                """);

            System.out.println("INSERT: affected rows: " + affectedRows);
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                create table student(
                    id bigint,
                    first_name varchar(256),
                    second_name varchar(256),
                    age int
                    )
                """);
        }
    }
}
