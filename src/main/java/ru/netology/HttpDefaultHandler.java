package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class HttpDefaultHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        if (!request.getMethod().equalsIgnoreCase("GET")) {
            new HttpHandlerBadRequest().handle(request, responseStream);
            return;
        }

        long length = Files.size(request.getContent());
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getHeaders() + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(request.getContent(), responseStream);
        responseStream.flush();
    }
}
