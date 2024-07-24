package org.test;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;


/**
 * Example plugin for Rundeck Notifications that performs HTTP requests.
 */
@Plugin(service="Notification",name="my-example")
@PluginDescription(title="Example Plugin", description="An example Plugin for Rundeck Notifications.")
public class Main implements NotificationPlugin {


    @PluginProperty(name = "example",title = "Example String",description = "Example description")
    private String example;

    @PluginProperty(name = "url",title = "URL",description = "The URL to send the HTTP request to")
    private String url;

    @PluginProperty(name = "httpMethod",title = "HTTP Method",description = "The HTTP method to use (GET, POST, PUT, DELETE)")
    private String httpMethod;

    @PluginProperty(name = "body",title = "Body",description = "The body content for the HTTP request")
    private String body;

    @PluginProperty(name = "contentType",title = "Content Type",description = "The content type of the HTTP request body")
    private String contentType;

    private OkHttpClient client = new OkHttpClient();

    public void MyNotificationPlugin() {
    }

    /**
     * Sends a notification via HTTP request.
     *
     * @param trigger       the trigger that fired the notification
     * @param executionData data related to the execution
     * @param config        configuration for the notification
     * @return true if the notification was sent successfully, false otherwise
     */
     */
    public boolean postNotification(String trigger, Map executionData, Map config) {

        try {
            sendHttpRequest(url, httpMethod, body, contentType);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Sends an HTTP request.
     *
     * @param url         the URL to send the request to
     * @param method      the HTTP method to use
     * @param body        the body content of the request
     * @param contentType the content type of the request body
     * @throws IOException if an I/O error occurs
     */
    private void sendHttpRequest(String url, String method, String body, String contentType) throws IOException {
        RequestBody requestBody = null;
        if (body != null && !body.isEmpty() && contentType != null && !contentType.isEmpty()) {
            requestBody = RequestBody.create(MediaType.parse(contentType), body);
        }

        Request.Builder requestBuilder = new Request.Builder().url(url);

        switch (method.toUpperCase()) {
            case "POST":
                requestBuilder.post(requestBody);
                break;
            case "PUT":
                requestBuilder.put(requestBody);
                break;
            case "DELETE":
                requestBuilder.delete(requestBody);
                break;
            case "GET":
            default:
                requestBuilder.get();
                break;
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        System.out.println(response.body().string());
    }

    public static void main(String[] args) {

        Main pluginPOST = new Main();
        pluginPOST.example = "Test Example";
        pluginPOST.url = "https://jsonplaceholder.typicode.com/posts";
        pluginPOST.httpMethod = "POST";
        pluginPOST.body = "{\n" +
                "    title: 'foo',\n" +
                "    body: 'bar',\n" +
                "    userId: 1,\n" +
                "  }";

        Main pluginGET = new Main();
        pluginGET.example = "Test Example";
        pluginGET.url = "https://jsonplaceholder.typicode.com/posts/1";
        pluginGET.httpMethod = "GET";
        pluginGET.body = "";
        pluginGET.contentType = "application/json";


        pluginGET.postNotification("testTrigger", null, null);
    }
}