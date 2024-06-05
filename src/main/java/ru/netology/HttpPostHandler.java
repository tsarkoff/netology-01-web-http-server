package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpPostHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            new HttpHandlerBadRequest().handle(request, responseStream);
            return;
        }

        // Dummy replacement output POST form (so far w/o real POST Request Body Parsing)
        request.setContent(Path.of("./public/forms_response.html"));
        request.setPath("lknhlkhl");
        String template = Files.readString(request.getContent());
        byte[] content = template
                .replace("{login}","Tsarkov")
                .replace("{password}", "qwerty123").getBytes();

        long length = Files.size(request.getContent());
        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getHeaders() + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(content);
        responseStream.flush();
    }
}
