package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private boolean running;
    public static final Map<Handler, Map<String, Set<String>>> handlers = new HashMap<>(); // <Handler, <Methods, Paths>>
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void listen(int port) throws IOException {
        running = true;
        try (ServerSocket sc = new ServerSocket(port)) {
            while (running) {
                Socket cs = sc.accept();
                System.out.println("Client socket accepted: " + cs.toString());
                threadPool.submit(new ProcessClientRequest(
                        new BufferedInputStream(cs.getInputStream()),
                        new BufferedOutputStream(cs.getOutputStream()))
                );
            }
        }
        threadPool.shutdown();
    }

    public void addHandler(String method, String path, Handler handler) {
        addDefaultHandlers(method, List.of(path), handler);
    }

    // Добавление / обновление ОДНОГО ДЕФОЛТНОГО обработчика на ОДИН метод и МНОЖЕСТВО путей
    public void addDefaultHandlers(String method, List<String> paths, Handler handler) {
        Map<String, Set<String>> map = handlers.containsKey(handler)
                ? handlers.get(handler)
                : new HashMap<>();

        Set<String> set = map.containsKey(method)
                ? map.get(method)
                : new HashSet<>();

        set.addAll(paths);
        map.put(method, set);
        handlers.put(handler, map);
    }

    public void stop() {
        running = false;
    }
}

