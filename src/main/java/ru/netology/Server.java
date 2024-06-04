package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private boolean running;
    public static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void listen(int port) throws IOException {
        running = true;
        try (ServerSocket sc = new ServerSocket(port)) {
            while (running) {
                Socket cs = sc.accept();
                System.out.println("Client socket accepted: " + cs.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(cs.getOutputStream());
                threadPool.submit(new ProcessClientRequest(in, out));
            }
        }
        threadPool.shutdown();
    }

    public void stop() {
        running = false;
    }
}

