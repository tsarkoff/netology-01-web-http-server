package ru.netology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private String queryString;
    private List<NameValuePair> queryParams;
    private String contentType;
    private String body;
    private List<NameValuePair> bodyParams;

    public String getQueryParam(String name) {
        for (NameValuePair param : queryParams)
            if (param.getName().equals(name))
                return param.getValue();
        return null;
    }

    public List<NameValuePair> getBodyParam(String name) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (NameValuePair param : bodyParams)
            if (param.getName().equals(name)) {
                pairs.add(param);
            }
        return pairs;
    }
}

