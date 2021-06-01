package nz.ac.wgtn.yamf.checks.http;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Common http client actions.
 * @author jens dietrich
 */
public class HttpClientActions {

    public static CloseableHttpResponse get(URI uri) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        return client.execute(get);
    }

    public static CloseableHttpResponse get(String url) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        URI uri = new URI(url);
        HttpGet get = new HttpGet(uri);
        return client.execute(get);
    }

    public static CloseableHttpResponse get(String url, Map<String,String> requestParameters) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder(url);
        for (String propertyName:requestParameters.keySet()) {
            builder.addParameter(propertyName,requestParameters.get(propertyName));
        }
        URI uri = builder.build();
        HttpGet get = new HttpGet(uri);
        return client.execute(get);
    }

    public static CloseableHttpResponse get(String url,String key1,Object value1) throws Exception {
        return get(url, toParams(key1,value1));
    }

    public static CloseableHttpResponse get(String url,String key1,Object value1,String key2, Object value2) throws IOException, URISyntaxException {
        return get(url, toParams(key1,value1,key2,value2));
    }

    public static CloseableHttpResponse get(String url,String key1,Object value1,String key2, Object value2,String key3,Object value3) throws IOException, URISyntaxException {
        return get(url, toParams(key1,value1,key2,value2,key3,value3));
    }

    public static CloseableHttpResponse postCharData(URI uri, String contentType, String data) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        StringEntity entity = new StringEntity(data);
        post.setEntity(entity);
        post.setHeader("Accept", contentType);
        post.setHeader("Content-type", contentType);
        return client.execute(post);
    }

    public static CloseableHttpResponse postJSON(URI uri, String contentType, String data) throws IOException {
        return postCharData(uri,"application/json",data);
    }

    public static CloseableHttpResponse postJSON(String url, String contentType, String data) throws IOException, URISyntaxException {
        return postCharData(new URI(url),"application/json",data);
    }

    public static CloseableHttpResponse postForm(URI uri, Map<String,String> requestParameters) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String propertyName:requestParameters.keySet()) {
            params.add(new BasicNameValuePair(propertyName,requestParameters.get(propertyName)));
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        return client.execute(post);
    }

    public static CloseableHttpResponse postForm(String url, Map<String,String> requestParameters) throws IOException, URISyntaxException {
        return postForm(new URI(url),requestParameters);
    }

    public static CloseableHttpResponse postForm(URI uri, String key1,Object value1) throws IOException {
        return postForm(uri, toParams(key1,value1));
    }

    public static CloseableHttpResponse postForm(String url, String key1,Object value1) throws IOException, URISyntaxException {
        return postForm(new URI(url),key1,value1);
    }

    public static CloseableHttpResponse postForm(URI uri, String key1,Object value1,String key2,Object value2) throws IOException {
        return postForm(uri, toParams(key1,value1,key2,value2));
    }

    public static CloseableHttpResponse postForm(String url, String key1,Object value1,String key2,Object value2) throws IOException, URISyntaxException {
        return postForm(new URI(url), key1,value1,key2,value2);
    }

    public static CloseableHttpResponse postForm(URI uri, String key1,Object value1,String key2,Object value2,String key3,Object value3) throws IOException {
        return postForm(uri, toParams(key1,value1,key2,value2,key3,value3));
    }

    public static CloseableHttpResponse postForm(String url, String key1,Object value1,String key2,Object value2,String key3,Object value3) throws IOException, URISyntaxException {
        return postForm(new URI(url), key1,value1,key2,value2,key3,value3);
    }




    private static Map<String,String> toParams(Object... keysAndValues) {
        Map<Object,Object> map = ArrayUtils.toMap(keysAndValues);
        return map.keySet().stream()
                .collect(Collectors.toMap(k -> k.toString(), k -> String.valueOf(map.get(k))));
    }


    
}
