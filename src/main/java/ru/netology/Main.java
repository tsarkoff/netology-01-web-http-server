package ru.netology;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        final var server = new Server();
        server.listen(9999);
        server.stop();
    }
}
