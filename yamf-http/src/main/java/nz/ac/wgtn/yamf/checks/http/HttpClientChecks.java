package nz.ac.wgtn.yamf.checks.http;

import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.Attachments;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Common http client checks
 * @author jens dietrich
 */
public class HttpClientChecks {

    public static void assertStatusCodeEquals(HttpResponse response,int expectedStatusCode) {
        assertNotNull(response);
        assertEquals(expectedStatusCode,response.getStatusLine().getStatusCode());
    }

    public static void assert200OK(HttpResponse response) {
        assertStatusCodeEquals(response,200);
    }

    public static void assert201Created(HttpResponse response) {
        assertStatusCodeEquals(response,201);
    }

    public static void assert400BadRequest(HttpResponse response) {
        assertStatusCodeEquals(response,400);
    }

    public static void assert404NotFound(HttpResponse response) {
        assertStatusCodeEquals(response,404);
    }

    public static void assertContentTypeEquals(HttpResponse response,String expectedContentType) {
        Header header = response.getFirstHeader("Content-Type");
        assertNotNull(header,"No Content-Type header found in response");
        String contentType = header.getValue();
        // compare with starts with to deal with char encoding that might be appended by server
        assertTrue(contentType.startsWith(expectedContentType),"Content type was " + contentType + " , but expected was " + expectedContentType);
    }

    public static void assertContentTypeIsJSON(HttpResponse response) {
        assertContentTypeEquals(response,"application/json");
    }

    public static void assertContentTypeIsHTML(HttpResponse response) {
        assertContentTypeEquals(response,"text/html");
    }

    public static void assertContentIsJSONObject(HttpResponse response) {
        try {
            String data = EntityUtils.toString(response.getEntity());
            JSONTokener tokener = new JSONTokener(data);
            JSONObject object = new JSONObject(tokener);
            assertNotNull(object);
        }
        catch (Exception x) {
            Attachment attachment = new Attachment("JSON Parsing Stack Trace",x);
            Attachments.add(attachment);
            assertTrue(false,"Error parsing response data as JSON Object");
        }
    }

    public static void assertContentIsJSONArray(HttpResponse response) {
        try {
            String data = EntityUtils.toString(response.getEntity());
            JSONTokener tokener = new JSONTokener(data);
            JSONArray array = new JSONArray(tokener);
            assertNotNull(array);
        }
        catch (Exception x) {
            Attachment attachment = new Attachment("JSON Parsing Stack Trace",x);
            Attachments.add(attachment);
            assertTrue(false,"Error parsing response data as JSON Object");
        }
    }

    /**
     * Valid HTML is interpreted as "parsable by JSoup".
     * An alternative would be based on sending a request to the http://validator.w3.org/check service.
     * @param response
     */
    public static void assertContentIsValidHTML(HttpResponse response) {
        try {
            String data = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(data);
            doc.parser().getErrors().size();
            assertTrue(true);

            Parser parser = Parser.htmlParser();
            parser.setTrackErrors(100);
            parser.parseInput(data, "");
            if (!parser.getErrors().isEmpty()) {
                List<String> errors = parser.getErrors().stream().map(err -> err.getErrorMessage()).collect(Collectors.toList());
                Attachments.add(new Attachment("Error parsing HTML in response",errors,"text/plain"));
            }

            assertTrue(parser.getErrors().isEmpty(),"The are errors parsing HTML");

        }
        catch (Exception x) {
            Attachments.add(new Attachment("HTML in response cannot be parsed",x));
            assertTrue(false,"HTML in response cannot be parsed");
        }
    }

}
