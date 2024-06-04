package ru.netology;

import lombok.SneakyThrows;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;

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
        String requestLine = in.readLine();
        String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            return; // just close socket
        }
        System.out.println(Arrays.toString(parts));

        final String path = parts[1];
        if (!Server.validPaths.contains(path)) {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }

        Path filePath = Path.of(".", "public", path);
        String mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            String template = Files.readString(filePath);
            byte[] content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }

        long length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        //Files.copy(filePath, System.out);
        out.flush();
    }
}
