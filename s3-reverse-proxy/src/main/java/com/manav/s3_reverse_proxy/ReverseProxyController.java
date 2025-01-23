package com.manav.s3_reverse_proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.IOException;

@Controller
public class ReverseProxyController {

    private static final String BASE_PATH = "https://dockapi-build-server.s3.ap-south-1.amazonaws.com/__outputs";

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public ResponseEntity<String> proxyRequest(HttpServletRequest request) throws IOException {
        String hostname = request.getServerName();
        String subdomain = hostname.split("\\.")[0];
        String resolvesTo = BASE_PATH + "/" + subdomain;

        String targetUrl = resolvesTo + request.getRequestURI();
        if ("/".equals(request.getRequestURI())) {
            targetUrl += "index.html";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(targetUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                // Create Spring HttpHeaders and copy headers from Apache response
                HttpHeaders headers = new HttpHeaders();
                for (Header header : response.getAllHeaders()) {
                    headers.add(header.getName(), header.getValue());
                }

                // Return response with status, headers, and body
                return ResponseEntity.status(response.getStatusLine().getStatusCode())
                        .headers(headers)
                        .body(responseBody);
            }
        }
    }
}
