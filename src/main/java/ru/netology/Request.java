package ru.netology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private Path content;   // so far, "content" just simplified = Path of Resource
}

