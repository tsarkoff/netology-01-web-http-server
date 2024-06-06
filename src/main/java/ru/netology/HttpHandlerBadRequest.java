package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class HttpHandlerBadRequest implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        responseStream.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.flush();

    }
}
