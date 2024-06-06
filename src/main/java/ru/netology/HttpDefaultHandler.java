package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpDefaultHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        if (!request.getMethod().equalsIgnoreCase("GET")) {
            new HttpHandlerBadRequest().handle(request, responseStream);
            return;
        }

        Path path = Path.of("./public" + request.getPath());
        long length = Files.size(path);
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getContentType() + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());

        if (!request.getQueryParams().isEmpty()) {
            String template = Files.readString(Path.of("./public/forms_response.html"));
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
