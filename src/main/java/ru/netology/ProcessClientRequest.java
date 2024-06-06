package ru.netology;

import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.*;

public class ProcessClientRequest implements Runnable {
    private final BufferedInputStream in;
    private final BufferedOutputStream out;
    final int limit = 4096;
    public static final String GET = "GET";
    public static final String POST = "POST";

    public ProcessClientRequest(BufferedInputStream in, BufferedOutputStream out) {
        this.in = in;
        this.in.mark(limit); // лимит на request line + заголовки
        this.out = out;
    }

    @SneakyThrows
    @Override
    public void run() {
        final List<String> allowedMethods = List.of(GET, POST);
        // ищем request line
        byte[] buffer = new byte[limit];
        int read = in.read(buffer);
        byte[] requestLineDelimiter = new byte[]{'\r', '\n'};
        int requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);

        if (requestLineEnd == -1) {
            new HttpHandlerNotFound().handle(null, out);
            return; // just close socket
        }

        // читаем request line
        String[] requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }

        String httpMethod = requestLine[0];
        if (!allowedMethods.contains(httpMethod)) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }
        System.out.println(httpMethod);

        String httpPath = requestLine[1];
        if (!httpPath.startsWith("/")) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }
        System.out.println(httpPath);

        // ищем заголовки
        byte[] headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        int headersStart = requestLineEnd + requestLineDelimiter.length;
        int headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }

        in.reset(); // отматываем на начало буфера
        long skipped = in.skip(headersStart);  // пропускаем requestLine
        System.out.println("HTTP requestLine ended at position: " + skipped);

        byte[] headersBytes = in.readNBytes(headersEnd - headersStart);
        List<String> headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        System.out.println(headers);

        // для GET тела нет
        String body = "";
        if (!httpMethod.equals(GET)) {
            skipped = in.skip(headersDelimiter.length);
            System.out.println("HTTP headers ended at position: " + skipped);
            System.out.println("HTTP header Content-Type is: " + extractHeader(headers, "Content-Type"));
            // вычитываем Content-Length, чтобы прочитать body
            Optional<String> contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                int length = Integer.parseInt(contentLength.get());
                byte [] bodyBytes = in.readNBytes(length);

                body = new String(bodyBytes);
                System.out.println(body);
            }
        }
        System.out.println(Arrays.toString(requestLine));

        URI uriPath = new URI(httpPath);
        List<NameValuePair> params = URLEncodedUtils.parse(uriPath, StandardCharsets.UTF_8);
        String mimeType = Files.probeContentType(Path.of("./public" + uriPath.getPath()));
        final Request request = new Request(
                httpMethod,             // method
                uriPath.getPath(),      // path
                uriPath.getQuery(),     // query string
                headers,                // headers
                params,                 // request parameters (http://urt?inline=)
                mimeType,               // content type
                body                    // request body if present
        );

        // Поиск Обработчика
        Optional<Handler> handler = Server.handlers
                .entrySet()
                .stream()
                .filter(
                        method -> method
                                .getValue()
                                .containsKey(request.getMethod()))
                .filter(
                        path -> path
                                .getValue()
                                .values()
                                .stream()
                                .anyMatch(set -> set.contains(request.getPath())))
                .map(Map.Entry::getKey)
                .findAny();

        if (handler.isEmpty()) {
            new HttpHandlerNotFound().handle(null, out);
            return;
        }
        handler.get().handle(request, out);
    }

    // from Google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }
}
