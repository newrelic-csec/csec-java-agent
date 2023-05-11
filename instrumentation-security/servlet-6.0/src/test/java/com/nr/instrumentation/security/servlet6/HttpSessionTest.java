package com.nr.instrumentation.security.servlet6;

import com.newrelic.agent.security.introspec.InstrumentationTestConfig;
import com.newrelic.agent.security.introspec.SecurityInstrumentationTestRunner;
import com.newrelic.agent.security.introspec.SecurityIntrospector;
import com.newrelic.api.agent.security.schema.AbstractOperation;
import com.newrelic.api.agent.security.schema.VulnerabilityCaseType;
import com.newrelic.api.agent.security.schema.operation.SecureCookieOperation;
import com.newrelic.api.agent.security.schema.operation.TrustBoundaryOperation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@RunWith(SecurityInstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = { "jakarta.servlet" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpSessionTest {
    @ClassRule
    public static HttpServletServer server = new HttpServletServer();

    @Test
    public void testSessionSetAttribute() throws IOException, URISyntaxException {
        String method = "GET";
        String POST_PARAMS = "hook=readLine";
        makeRequest(method, POST_PARAMS, "session");

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        TrustBoundaryOperation targetOperation = (TrustBoundaryOperation) operations.get(0);
        Assert.assertNotNull("No target operation detected", targetOperation);
        Assert.assertEquals("Wrong case-type detected", VulnerabilityCaseType.TRUSTBOUNDARY, targetOperation.getCaseType());
        Assert.assertEquals("Wrong key detected", "key", targetOperation.getKey());
        Assert.assertEquals("Wrong value detected", "value", targetOperation.getValue());

        targetOperation = (TrustBoundaryOperation) operations.get(1);
        Assert.assertNotNull("No target operation detected", targetOperation);
        Assert.assertEquals("Wrong case-type detected", VulnerabilityCaseType.TRUSTBOUNDARY, targetOperation.getCaseType());
        Assert.assertEquals("Wrong key detected", "key", targetOperation.getKey());
        Assert.assertEquals("Wrong value detected", "value", targetOperation.getValue());
    }

    @Test
    public void testAddCookie() throws IOException, URISyntaxException {
        String method = "GET";
        String POST_PARAMS = "hook=readLine";
        makeRequest(method, POST_PARAMS, "cookie");

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        SecureCookieOperation targetOperation = (SecureCookieOperation) operations.get(0);
        Assert.assertNotNull("No target operation detected", targetOperation);
        Assert.assertEquals("Wrong case-type detected", VulnerabilityCaseType.SECURE_COOKIE, targetOperation.getCaseType());
        Assert.assertEquals("Wrong key detected", "false", targetOperation.getValue());
    }

    @Test
    public void testAddCookie1() throws IOException, URISyntaxException {
        String method = "GET";
        String POST_PARAMS = "hook=readLine";
        makeRequest(method, POST_PARAMS, "securecookie");

        SecurityIntrospector introspector = SecurityInstrumentationTestRunner.getIntrospector();
        List<AbstractOperation> operations = introspector.getOperations();
        Assert.assertTrue("No operations detected", operations.size() > 0);

        SecureCookieOperation targetOperation = (SecureCookieOperation) operations.get(0);
        Assert.assertNotNull("No target operation detected", targetOperation);
        Assert.assertEquals("Wrong case-type detected", VulnerabilityCaseType.SECURE_COOKIE, targetOperation.getCaseType());
        Assert.assertEquals("Wrong key detected", "true", targetOperation.getValue());
    }

    private void makeRequest( String Method, final String POST_PARAMS, String path) throws URISyntaxException, IOException{

        URL u = server.getEndPoint(path).toURL();
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod(Method);
        conn.setDoOutput(true);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", "multipart/form-data");

        conn.connect();
        conn.getResponseCode();

    }
}
