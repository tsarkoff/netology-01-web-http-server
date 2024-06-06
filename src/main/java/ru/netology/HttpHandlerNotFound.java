package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class HttpHandlerNotFound implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        responseStream.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.flush();
    }
}
