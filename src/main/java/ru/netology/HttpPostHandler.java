package ru.netology;

import org.apache.http.NameValuePair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpPostHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        String template = Files.readString(Path.of(Server.HTTP_ROOT + request.getPath()));

        if (!request.getQueryParams().isEmpty()) {
            StringBuilder params = new StringBuilder();
            for (NameValuePair param : request.getQueryParams()) {
                params
                        .append("<br>QueryString Param name: ")
                        .append(param.getName()).append(", Param value: ")
                        .append(param.getValue());
            }
            template += params;
        }

        if (!request.getBodyParams().isEmpty()) {
            StringBuilder params = new StringBuilder();
            for (NameValuePair param : request.getBodyParams()) {
                params
                        .append("<br>Body Param name: ")
                        .append(param.getName())
                        .append(", Param value: ")
                        .append(param.getValue());
            }
            template += params;
        }

        responseStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + request.getContentType() + "\r\n" +
                        "Content-Length: " + template.getBytes().length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        responseStream.write(template.getBytes());
        responseStream.flush();
    }
}
