package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpDefaultHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        Path path = Path.of(Server.HTTP_ROOT + request.getPath());
        long length = Files.size(path);
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getContentType() + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());

        if (!request.getQueryParams().isEmpty()) {
            String template = Files.readString(Path.of(Server.HTTP_ROOT+ "forms_response.html"));
            byte[] content = template
                    .replace("{login}", request.getQueryParam("login"))
                    .replace("{password}", request.getQueryParam("password")).getBytes();
            responseStream.write(content);
        } else {
            Files.copy(path, responseStream);
        }

        responseStream.flush();
    }
}
