package me.qusic.twij;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private final OAuthService service;
	private final Token accessToken;

	private final List<String> logs;

	{
		Map<String, String> environment = System.getenv();
		String consumerKey = environment.get("ConsumerKey");
		String consumerSecret = environment.get("ConsumerSecret");
		String accessToken = environment.get("AccessToken");
		String accessTokenSecret = environment.get("AccessTokenSecret");
		this.service = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(consumerKey).apiSecret(consumerSecret).build();
		this.accessToken = new Token(accessToken, accessTokenSecret);
		this.logs = new ArrayList<String>();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (twij(request, response)) {
			return;
		} else {
			processRequest(generateRequest(request), response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (twij(request, response)) {
			return;
		} else {
			processRequest(generateRequest(request), response);
		}
	}

	private OAuthRequest generateRequest(HttpServletRequest request)
			throws IOException {
		String url;
		boolean streaming;
		String method = request.getMethod();
		String contentType = request.getHeader("Content-Type");
		boolean includesQuery = !(method.equals("POST") && (contentType == null || contentType
				.equals("application/x-www-form-urlencoded")));

		String host = "api.twitter.com";
		String path = request.getPathInfo();
		if (path.startsWith("/1.1/statuses/filter")
				|| path.startsWith("/1.1/statuses/sample")
				|| path.startsWith("/1.1/statuses/firehose")) {
			streaming = true;
			host = "stream.twitter.com";
		} else if (path.startsWith("/1.1/user")
				&& !path.startsWith("/1.1/users")) {
			streaming = true;
			host = "userstream.twitter.com";
		} else if (path.startsWith("/1.1/site")) {
			streaming = true;
			host = "sitestream.twitter.com";
		} else {
			streaming = false;
		}
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
				BufferedReader reader = request.getReader();
				StringBuilder sb = new StringBuilder();
				int c;
				while ((c = reader.read()) != -1) {
					sb.append((char) c);
				}
				reader.close();
				String body = sb.toString();
				oauthRequest.addPayload(body);
				oauthRequest.addHeader("Content-Type", contentType);
			} else {
				@SuppressWarnings("unchecked")
				Enumeration<String> enumeration = request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String name = enumeration.nextElement();
					oauthRequest.addBodyParameter(name,
							request.getParameter(name));
				}
			}
		}
		oauthRequest.setConnectionKeepAlive(streaming);

		return oauthRequest;
	}

	private void processRequest(OAuthRequest oauthRequest,
			HttpServletResponse response) throws IOException {
		service.signRequest(accessToken, oauthRequest);
		Response oauthResponse = oauthRequest.send();

		response.setContentType(oauthResponse.getHeader("Content-Type"));
		response.setStatus(oauthResponse.getCode());
		response.setBufferSize(0);

		InputStreamReader reader = new InputStreamReader(
				oauthResponse.getStream());
		PrintWriter writer = response.getWriter();
		char[] buffer = new char[10240];
		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1) {
			writer.print(new String(buffer, 0, charsRead));
		}
		reader.close();
		writer.close();
	}

	private boolean twij(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String path = request.getPathInfo();
		if (path.equals("/")) {
			response.sendRedirect("http://www.google.com/ncr");
			return true;
		} else if (path.equals("/logs")) {
			response.setContentType("text/plain");
			response.setStatus(200);
			PrintWriter writer = response.getWriter();
			for (String log : logs) {
				writer.println(log);
			}
			writer.close();
			return true;
		} else {
			return false;
		}
	}
}
