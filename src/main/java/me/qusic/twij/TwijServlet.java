package me.qusic.twij;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class TwijServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private OAuthService service;
    private Token accessToken;

    private final List<String> logs;

    {
        this.logs = new ArrayList<String>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (twij(request, response)) {
            return;
        } else {
            processRequest(generateRequest(request), response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (twij(request, response)) {
            return;
        } else {
            processRequest(generateRequest(request), response);
        }
    }

    private OAuthRequest generateRequest(HttpServletRequest request) throws IOException {
        String url;
        String method = request.getMethod();
        String contentType = request.getContentType();
        boolean includesQuery = !(method.equals("POST") && (contentType == null || contentType.equals("application/x-www-form-urlencoded")));

        String host = "api.twitter.com";
        String path = request.getPathInfo();
        url = "https://" + host + path;
        String query = request.getQueryString();
        if (includesQuery && query != null) {
            url += "?" + query;
        }

        OAuthRequest oauthRequest = null;
        if (method.equals("GET")) {
            oauthRequest = new OAuthRequest(Verb.GET, url);
        } else if (method.equals("POST")) {
            oauthRequest = new OAuthRequest(Verb.POST, url);
            if (includesQuery) {
                InputStream in = request.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = in.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                byte[] body = out.toByteArray();
                in.close();
                out.close();
                oauthRequest.addPayload(body);
                oauthRequest.addHeader("Content-Type", contentType);
            } else {
                @SuppressWarnings("unchecked")
                Enumeration<String> enumeration = request.getParameterNames();
                while (enumeration.hasMoreElements()) {
                    String name = enumeration.nextElement();
                    oauthRequest.addBodyParameter(name, request.getParameter(name));
                }
            }
        }

        return oauthRequest;
    }

    private void processRequest(OAuthRequest oauthRequest, HttpServletResponse response) throws IOException {
        service.signRequest(accessToken, oauthRequest);
        Response oauthResponse = oauthRequest.send();

        response.setContentType(oauthResponse.getHeader("Content-Type"));
        response.setStatus(oauthResponse.getCode());
        response.setBufferSize(0);

        InputStream in = oauthResponse.getStream();
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        in.close();
        out.close();
    }

    private boolean twij(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        String path = request.getPathInfo();
        Map<String, String> environment = System.getenv();
        String consumerKey, consumerSecret, accessToken, accessTokenSecret;

        if (path.equals("/")) {
            response.sendRedirect("http://www.google.com/ncr");
            return true;
        }
        if (path.equals("/logs")) {
            response.setContentType("text/plain");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            for (String log : logs) {
                writer.println(log);
            }
            writer.close();
            return true;
        }
        if (path.startsWith("/1.1/statuses/filter.json") ||
                path.startsWith("/1.1/statuses/sample.json") ||
                path.startsWith("/1.1/statuses/firehose.json") ||
                path.startsWith("/1.1/user.json") ||
                path.startsWith("/1.1/site.json")) {
            response.setStatus(502);
            return true;
        }

        if (method.equals("POST")) {
            consumerKey = environment.get("ConsumerKey_VIA");
            consumerSecret = environment.get("ConsumerSecret_VIA");
            accessToken = environment.get("AccessToken_VIA");
            accessTokenSecret = environment.get("AccessTokenSecret_VIA");
        } else {
            consumerKey = environment.get("ConsumerKey");
            consumerSecret = environment.get("ConsumerSecret");
            accessToken = environment.get("AccessToken");
            accessTokenSecret = environment.get("AccessTokenSecret");
        }
        this.service = new ServiceBuilder().provider(TwitterApi.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(accessToken, accessTokenSecret);
        return false;
    }
}
