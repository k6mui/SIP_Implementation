package sipServlet;

public interface SipServletRequestInterface {
	String getCallerURI();
    String getCalleeURI();
    SipServletResponseInterface createResponse(int statusCode);
    ProxyInterface getProxy();
}
