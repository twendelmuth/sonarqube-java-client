package com.twendelmuth.sonarqube.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.HttpContext;

import com.twendelmuth.sonarqube.api.exception.SonarQubeServerError;
import com.twendelmuth.sonarqube.api.response.SonarApiResponse;

public class SonarQubeServerHttpClient implements SonarQubeServer {
	private static final String USER_AGENT = "SonarQubeJavaClient";

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

	@Override
	public SonarApiResponse doPost(String apiEndPoint, Map<String, String> parameters) throws SonarQubeServerError {
		HttpPost httpPost = new HttpPost(serverUrl + apiEndPoint);

		List<NameValuePair> params = new ArrayList<>();
		parameters.entrySet().forEach(entry -> params.add(new BasicNameValuePair(entry.getKey(), entry.getValue())));
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		try (CloseableHttpClient httpClient = getHttpClient()) {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			String content = "";
			if (response.getEntity() != null) {
				content = IOUtils.toString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			}

			return new SonarApiResponse(response.getCode(), content);
		} catch (IOException ioe) {
			return new SonarApiResponse(-1, ioe.getMessage());
		}

	}

	@Override
	public SonarApiResponse doGet(String apiEndPoint) throws SonarQubeServerError {
		HttpGet httpGet = new HttpGet(serverUrl + apiEndPoint);

		try (CloseableHttpClient httpClient = getHttpClient()) {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			String content = "";
			if (response.getEntity() != null) {
				content = IOUtils.toString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			}

			return new SonarApiResponse(response.getCode(), content);
		} catch (IOException ioe) {
			return new SonarApiResponse(-1, ioe.getMessage());
		}

	}

}
