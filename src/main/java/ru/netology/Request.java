package ru.netology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Request {
    private String method;
    private String path;
    private String queryString;
    private List<String> headers;
    private List<NameValuePair> queryParams;
    private String contentType;
    private String body;   // so far, "content" just simplified = Path of Resource

    public String getQueryParam(String name) {
        for (NameValuePair param : queryParams)
            if (param.getName().equals(name))
                return param.getValue();
        return null;
    }
}

