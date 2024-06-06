package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

@FunctionalInterface
public interface Handler {
    String httpResponse = "HTTP/1.1 %s\r\n Content-Type: %s\r\n Content-Length: %d\r\n Connection: close\r\n\r\n";

    void handle(Request request, BufferedOutputStream responseStream)
            throws IOException, URISyntaxException;
}
