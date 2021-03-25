package fr.martdel.rolecraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {

    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final StringBuilder response;

    public HttpRequest(String url, String method, Map<String, String> headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.response = new StringBuilder();
    }

    /**
     * Execute the HTTP request
     */
    public void execute(){
        try {
            URL URL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) URL.openConnection();
            con.setRequestMethod(method);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            // Set headers
            for(String key : headers.keySet()){
                con.setRequestProperty(key, headers.get(key));
            }

            // Set body
            if(body != null){
                con.setDoOutput(true);
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Get response
            int status = con.getResponseCode();
            Reader streamReader;
            if (status > 299 && con.getErrorStream() != null) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else streamReader = new InputStreamReader(con.getInputStream());
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the HTTP response
     * @return The http response string
     */
    public String getResponse() {
        return response.toString();
    }
}
