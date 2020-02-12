// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class HttpClient {

  public static final String URL_PATH_SEPARATOR = "/";
  private final RestTemplate restTemplate;

  /** Create a new instance of the HttpClient using default settings. */
  public HttpClient() {
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new ByteArrayHttpMessageConverter());
    messageConverters.add(new StringHttpMessageConverter());
    restTemplate = new RestTemplate(messageConverters);
  }

  /**
   * Build a URL as String from the uri and path components.
   *
   * @param uri the URI string to initialize with
   * @param path the URI path
   * @return the url as a String
   */
  public static String buildUrl(String uri, String path) {
    return UriComponentsBuilder.fromUriString(uri).path(path).build().toString();
  }

  /**
   * Execute the post request at specified URI.
   *
   * @param url the URL
   * @param body to write to the request
   * @return the body of entity as array of bytes
   */
  public byte[] doPost(String url, byte[] body) {

    HttpEntity<byte[]> entity = new HttpEntity<>(body, getHeadersOctetStream());
    ResponseEntity<byte[]> response = execute(url, HttpMethod.POST, entity);

    return response.getBody();
  }

  /**
   * Execute the get request at specified URI.
   *
   * @param url the URL
   * @return the body of entity as array of bytes
   */
  public byte[] doGet(String url) {

    HttpEntity<byte[]> entity = new HttpEntity<>(getHeadersOctetStream());
    URI targetUri = UriComponentsBuilder.fromUriString(url).build().toUri();

    ResponseEntity<byte[]> response = execute(targetUri.toString(), HttpMethod.GET, entity);
    return response.getBody();
  }

  private ResponseEntity<byte[]> execute(
      String targetUrl, HttpMethod method, HttpEntity<byte[]> entity) {

    ResponseEntity<byte[]> response;
    try {
      response = restTemplate.exchange(targetUrl, method, entity, byte[].class);
    } catch (HttpClientErrorException httpClientEx) {
      log.debug(
          "Response for '" + targetUrl + "' received status code: " + httpClientEx.getStatusText());
      throw httpClientEx;
    } catch (HttpServerErrorException httpServerEx) {
      log.debug(
          "Response for '" + targetUrl + "' received status code: " + httpServerEx.getStatusText());
      throw httpServerEx;
    }

    return response;
  }

  /**
   * Retrieve a representation by doing a GET on the URL.
   *
   * @param url the URL
   * @return the converted object
   */
  public ResponseEntity<String> getForStringEntity(String url) {
    return restTemplate.getForEntity(url, String.class);
  }

  private static HttpHeaders getHeadersOctetStream() {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
    return headers;
  }
}
