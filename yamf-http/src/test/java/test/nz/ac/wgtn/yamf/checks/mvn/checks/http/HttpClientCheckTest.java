package test.nz.ac.wgtn.yamf.checks.mvn.checks.http;

import nz.ac.wgtn.yamf.Attachments;
import nz.ac.wgtn.yamf.checks.http.HttpClientChecks;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpClientCheckTest {

    @BeforeAll
    public static void init() {
        Attachments.setTestTestMode();
    }

    private HttpResponse response (int statusCode, String contentType,String content) throws IOException {

        HttpEntity entity = Mockito.mock(HttpEntity.class);
        Mockito.when(entity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));

        StatusLine statusline = Mockito.mock(StatusLine.class);
        Mockito.when(statusline.getStatusCode()).thenReturn(statusCode);

        Header header = Mockito.mock(Header.class);
        Mockito.when(header.getValue()).thenReturn(contentType);

        HttpResponse response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.getEntity()).thenReturn(entity);
        Mockito.when(response.getStatusLine()).thenReturn(statusline);
        Mockito.when(response.getFirstHeader("Content-Type")).thenReturn(header);

        return response;
    }

    @Test
    public void testStatus200Succeeds () throws IOException {
        HttpClientChecks.assert200OK(response(200,"text/html",""));
    }

    @Test
    public void testStatus200Fails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assert200OK(response(400, "text/html", ""))
        );
    }

    @Test
    public void testStatus201Succeeds () throws IOException {
        HttpClientChecks.assert201Created(response(201,"text/html",""));
    }

    @Test
    public void testStatus201Fails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assert201Created(response(400, "text/html", ""))
        );
    }

    @Test
    public void testStatus400Succeeds () throws IOException {
        HttpClientChecks.assert400BadRequest(response(400,"text/html",""));
    }

    @Test
    public void testStatus400Fails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assert400BadRequest(response(200, "text/html", ""))
        );
    }

    @Test
    public void testStatus404Succeeds () throws IOException {
        HttpClientChecks.assert404NotFound(response(404,"text/html",""));
    }

    @Test
    public void testStatus404Fails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assert404NotFound(response(200, "text/html", ""))
        );
    }

    @Test
    public void testStatus123Succeeds () throws IOException {
        HttpClientChecks.assertStatusCodeEquals(response(123,"text/html",""),123);
    }

    @Test
    public void testStatus123Fails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertStatusCodeEquals(response(200, "text/html", ""),123)
        );
    }

    @Test
    public void testContentTypeHTMLSucceeds1 () throws IOException {
        HttpClientChecks.assertContentTypeIsHTML(response(123,"text/html",""));
    }

    @Test
    public void testContentTypeHTMLSucceeds2 () throws IOException {
        HttpClientChecks.assertContentTypeIsHTML(response(123,"text/html; charset=utf-8",""));
    }

    @Test
    public void testContentTypeHTMLFails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentTypeIsHTML(response(200, "application/json", ""))
        );
    }

    @Test
    public void testContentTypeJSONSucceeds () throws IOException {
        HttpClientChecks.assertContentTypeIsJSON(response(123,"application/json",""));
    }

    @Test
    public void testContentTypeJSONFails () throws IOException {
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentTypeIsJSON(response(200, "text/html", ""))
        );
    }

    @Test
    public void testContentTypePlainTextSucceeds1 () throws IOException {
        HttpClientChecks.assertContentTypeEquals(response(123,"text/plain",""),"text/plain");
    }

    @Test
    public void testContentTypePlainTextSucceeds2 () throws IOException {
        HttpClientChecks.assertContentTypeEquals(response(123,"text/plain; charset=utf-8",""),"text/plain");
    }

    @Test
    public void testContentTypePlainTextFails () throws IOException {
        assertThrows(
                AssertionError.class,
                () -> HttpClientChecks.assertContentTypeEquals(response(200, "text/html", ""),"text/csv")
        );
    }

    @Test
    public void testContentIsJSONObjectSucceeds1 () throws IOException {
        String data = "{}";
        HttpClientChecks.assertContentIsJSONObject(response(123,"application/json",data));
    }

    @Test
    public void testContentIsJSONObjectSucceeds2 () throws IOException {
        String data = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        HttpClientChecks.assertContentIsJSONObject(response(123,"application/json",data));
    }

    @Test
    public void testContentIsJSONObjectFails1 () throws IOException {
        String data = "{\"key1\":\"value1\",\"key2\":\"value2";
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentIsJSONObject(response(200, "application/json", data))
        );
    }

    @Test
    public void testContentIsJSONObjectFails2 () throws IOException {
        String data = "[]";
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentIsJSONObject(response(200, "application/json", data))
        );
    }


    @Test
    public void testContentIsJSONArraySucceeds1 () throws IOException {
        String data = "[]";
        HttpClientChecks.assertContentIsJSONArray(response(123,"application/json",data));
    }

    @Test
    public void testContentIsJSONArraySucceeds2 () throws IOException {
        String data = "[{\"key1\":\"value1\",\"key2\":\"value2\"},{\"key3\":\"value3\",\"key4\":\"value4\"}]";
        HttpClientChecks.assertContentIsJSONArray(response(123,"application/json",data));
    }

    @Test
    public void testContentIsJSONArrayFails1 () throws IOException {
        String data = "[{\"key1\":\"value1\",\"key2\":\"value2";
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentIsJSONArray(response(200, "application/json", data))
        );
    }

    @Test
    public void testContentIsJSONArrayFails2 () throws IOException {
        String data = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        assertThrows(
            AssertionError.class,
            () -> HttpClientChecks.assertContentIsJSONArray(response(200, "application/json", data))
        );
    }

    @Test
    public void testContentIsHTMLSucceeds () throws IOException {
        String data = "<html><body><h1>Hello World</h1></body></html>";
        HttpClientChecks.assertContentIsValidHTML(response(123,"text/html",data));
    }

    // tricky to make jsoup fail !
//    @Test
//    public void testContentIsHTMLFails () throws IOException {
//        // jsoup will (like parsers) parse dirty HTML , such as: "<html><body><h1>Hello World</h1>"
//        String data = "<h1>>foo";
//        assertThrows(
//            AssertionError.class,
//            () -> HttpClientChecks.assertContentIsValidHTML(response(200, "text/html", data))
//        );
//    }

}
