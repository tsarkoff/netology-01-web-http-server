package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class HttpClassicHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        String template = Files.readString(Path.of("./public" + request.getPath()));
        byte[] content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();

        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getContentType() + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(content);
        responseStream.flush();
    }
}
