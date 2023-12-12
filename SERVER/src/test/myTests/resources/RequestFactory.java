package myTests.resources;

import spark.Request;
import spark.RequestResponseFactory;

public class RequestFactory {
    public static Request newRequest(String body, String... headers) {
        return RequestResponseFactory.create(new MockHttpRequest(body, headers));
    }

}
