package io.github.twendelmuth.sonarqube.api;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.HttpContext;

import io.github.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import io.github.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class SonarQubeServerHttpClient implements SonarQubeServer {
	private static final String USER_AGENT = "io.github.twendelmuth.SonarQubeJavaClient";

	private final String serverUrl;

	private final String loginToken;

	public SonarQubeServerHttpClient(String serverUrl, String loginToken) {
		this.serverUrl = serverUrl;
		this.loginToken = loginToken;
	}

	public CloseableHttpClient getHttpClient() {
		return HttpClients.custom()
				.addRequestInterceptorFirst(headerDecorator())
				.build();
	}

	protected HttpRequestInterceptor headerDecorator() {
		return new HttpRequestInterceptor() {

			@Override
			public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
				request.addHeader("Content-Type", "application/x-www-form-urlencoded");
				request.addHeader("User-Agent", USER_AGENT);
				authorizationHeaders(request);
			}
		};
	}

	protected void authorizationHeaders(HttpRequest httpRequest) {
		httpRequest.addHeader("Authorization", basicAuth(loginToken, ""));
	}

	protected static String basicAuth(String username, String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	private SonarApiResponse execute(final ClassicHttpRequest request) throws SonarQubeServerError {
		try (CloseableHttpClient httpClient = getHttpClient()) {
			CloseableHttpResponse response = httpClient.execute(request);
			String content = "";
			if (response.getEntity() != null) {
				content = IOUtils.toString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			}

			return new SonarApiResponse(response.getCode(), content);
		} catch (IOException ioe) {
			throw new SonarQubeServerError("IOException while talking to SonarQube server", -1, ioe.getMessage());
		}
	}

	@Override
	public SonarApiResponse doPost(String apiEndPoint, List<NameValuePair> parameters) throws SonarQubeServerError {
		HttpPost httpPost = new HttpPost(serverUrl + apiEndPoint);

		List<BasicNameValuePair> params = parameters.stream()
				.map(nvp -> new BasicNameValuePair(nvp.getName(), nvp.getValue()))
				.collect(Collectors.toList());
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		return execute(httpPost);
	}

	@Override
	public SonarApiResponse doGet(String apiEndPoint) throws SonarQubeServerError {
		return execute(new HttpGet(serverUrl + apiEndPoint));
	}

}
