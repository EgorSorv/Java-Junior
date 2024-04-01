package hw5.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0. Разобрать код, написанный на уроке. Прийти к осознанию, что все написанное - понятно. <p>
 * 1. Сформулировать вопросы, которые остались без ответа, и прислать их в форму сдачи дз. <p>
 * 2. Досдать долги по курсу (т.е. работы, которые еще не сданы). <p>
 * 3.** Дореализовать проект: <p>
 * 3.1 Доработать "отключение клиента" - отсылать всем уведомление о том, что клиент отключился <p>
 * 3.2 Разобраться с префиксами сообщений: навести порядок в консоли
 * (чтобы все было аккуратно и понятно) - на усмотрение студент <p>
 * 4.**** Реализовать "системные" вызовы со стороны клиента: <p>
 * 4.1 /all - получить список всех текущих пользователей <p>
 * 4.2 /exit - отключиться (старый exit удалить) <p>
 */

public class Server {
    public static final int PORT = 8181;


    public static void main(String[] args) {
        final Map<String, ClientHandler> clients = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту: " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Подключился новый клиент: " + clientSocket);

                    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientOut.println("Подключение успешно. Пришлите свой идентификатор");

                    Scanner clientIn = new Scanner(clientSocket.getInputStream());
                    String clientId = clientIn.nextLine();
                    System.out.println("Идентификатор клиента " + clientSocket + ": " + clientId);

                    String allClients = clients.entrySet().stream()
                            .map(it -> "id = " + it.getKey() + ", client = " +
                                    it.getValue().getClientSocket())
                            .collect(Collectors.joining("\n"));
                    clientOut.println("Список доступных клиентов: \n" + allClients);

                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients, clientId);
                    new Thread(clientHandler).start();

                    for (ClientHandler client: clients.values())
                        client.send("Подключился новый клиент: " + clientSocket + ", id = " + clientId);

                    clients.put(clientId, clientHandler);
                } catch (IOException e) {
                    System.err.println("Произошла ошибка при взаимодействии с клиентом: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось начать прослушивать порт " + PORT, e);
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PrintWriter out;
    private final Map<String, ClientHandler> clients;
    private final String clientId;


    public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clients, String clientId)
            throws IOException {
        this.clientSocket = clientSocket;
        this.out =new PrintWriter(clientSocket.getOutputStream(), true);
        this.clients = clients;
        this.clientId = clientId;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        try (Scanner in = new Scanner(clientSocket.getInputStream())) {
            while (true) {
                String input = in.nextLine();
                System.out.println("Получено сообщение от клиента " + clientSocket + ": " + input);

                String toClientId = null;

                if (Objects.equals("/exit", input)) {
                    String logOutMsg = "Клиент " + clientSocket + " отключился";
                    clients.values().forEach(it -> it.send(logOutMsg));
                    System.out.println(logOutMsg);
                    break;
                } else {

                    if (input.startsWith("@")) {
                        String[] parts = input.split("\\s+");

                        if (parts.length > 0)
                            toClientId = parts[0].substring(1);
                    }

                    if (toClientId == null)
                        clients.values().forEach(it -> it.send(clientId + " написал: " + input));
                    else {
                        ClientHandler toClient = clients.get(toClientId);

                        if (toClient != null)
                            toClient.send(input.replace("@" + toClientId + " ", ""));
                        else
                            System.err.println("Не найден клиент с идентификатором: " + toClientId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при взаимодействии с клиентом" +
                    clientSocket + ": " + e.getMessage());
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении клиента " + clientSocket + ": " + e.getMessage());
        }
    }

    public void send(String msg) {
        out.println(msg);
    }
}
