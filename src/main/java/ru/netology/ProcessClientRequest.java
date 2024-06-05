package ru.netology;

import lombok.SneakyThrows;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.*;

public class ProcessClientRequest implements Runnable {
    private final BufferedReader in;
    private final BufferedOutputStream out;

    public ProcessClientRequest(BufferedReader in, BufferedOutputStream out) {
        this.in = in;
        this.out = out;
    }

    @SneakyThrows
    @Override
    public void run() {
        // read only request line for simplicity = must be in form GET /path HTTP/1.1
        final String requestLine = in.readLine();
        final String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            new HttpHandlerNotFound().handle(null, out);
            return; // just close socket
        }
        System.out.println(Arrays.toString(parts));

        Path content = Path.of(".", "public", parts[1]);    // path & filePath
        final Request request = new Request(
                parts[0],   // method
                parts[1],   // path
                List.of("Content-Type: " + Files.probeContentType(content) + "\r\n"),    // headers
                content     // Path to recognize type of content later on, while checking Content-type
        );

        // Поиск Обработчика
        Optional<Handler> handler = Server.handlers
                .entrySet()
                .stream()
                .filter(
                        method -> method
                                .getValue()
                                .containsKey(request.getMethod()))
                .filter(
                        path -> path
                                .getValue()
                                .values()
                                .stream()
                                .anyMatch(set -> set.contains(request.getPath())))
                .map(Map.Entry::getKey)
                .findAny();

        if (handler.isEmpty()) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }
        handler.get().handle(request, out);
    }
}
