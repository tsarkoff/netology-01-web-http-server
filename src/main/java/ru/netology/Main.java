package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        final Server server = new Server();

        // сбор списка путей-файлов (/path) из каталога ./public
        // и добавление всех GET обработчиков по умолчанию
        List<String> paths = Files
                .walk(Paths.get("./public"))
                .filter(Files::isRegularFile)
                .map(f -> "/" + f.getFileName())
                .collect(Collectors.toList());
        server.addDefaultHandlers("GET", paths, new HttpDefaultHandler());

        // Добавление кастомных обработчиков (в т.ч. для уже добавленных выше - т.е. их замена / обновление)
        server.addHandler("GET", "/classic.html", new HttpClassicHandler());
        server.addHandler("GET", "/forms.html", new HttpDefaultHandler());
        server.addHandler("POST", "/forms.html", new HttpPostHandler());
        server.addHandler("POST", "/default-get.html", new HttpPostHandler());

        // Старт СЕРВЕРА
        server.listen(9999);
        server.stop();
    }
}
