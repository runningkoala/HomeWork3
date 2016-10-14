package Server;

import javax.servlet.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class HttpServer {

    class ServletProcessor {

        public void process(Request request, Response response) {

            String uri = request.getUri();
            String servletName="";
            if(uri.indexOf("?")!=-1)
            {
             servletName = uri.substring(uri.lastIndexOf("/") + 1,uri.indexOf("?"));
           System.out.println(servletName);
            }
            else
            {
            	servletName = uri.substring(uri.lastIndexOf("/") + 1);
                 System.out.println(servletName);
            }
           
            URLClassLoader loader = null;
            try {
            	
                URLStreamHandler streamHandler = null;
                //加载类
               loader = new URLClassLoader(
                        new URL[] { new URL(null, "file:" + System.getProperty("user.dir")
                                + File.separator +"targets"+ File.separator + "classes", streamHandler) });
            } catch (IOException e) {
                System.out.println(e.toString());
            }

            Class<?> myClass = null;
            try {
                //通过解析的uri进行类加载
                myClass = loader.loadClass("Servlet."+servletName);
            } catch (ClassNotFoundException e) {
                System.out.println(e.toString());
            }

            Servlet servlet = null;

            try {
                //创建类的实例
                servlet = (Servlet) myClass.newInstance();
                //调用servlet方法
                servlet.service((ServletRequest) request, (ServletResponse) response);
            } catch (Exception e) {
                System.out.println(e.toString());
            } catch (Throwable e) {
                System.out.println(e.toString());
            }

        }
    }

    class Request implements ServletRequest {

        private InputStream input;
        private String uri;

        public Request(InputStream input) {
            this.input = input;
        }

        public String getUri() {
            return uri;
        }
     
        /**
         * requestString褰㈠紡濡備笅锛�
         * GET /index.html HTTP/1.1
         * Host: localhost:8080
         * Connection: keep-alive
         * Cache-Control: max-age=0
         * ...
         *
         */
        private String parseUri(String requestString) {
            int index1, index2;
            index1 = requestString.indexOf(' ');
            if (index1 != -1) {
                index2 = requestString.indexOf(' ', index1 + 1);
                if (index2 > index1)
                    return requestString.substring(index1 + 1, index2);
            }
            return null;
        }

        
        public void parse() {
            // Read a set of characters from the socket
            StringBuffer request = new StringBuffer(2048);
            int i;
            byte[] buffer = new byte[2048];
            try {
                i = input.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                i = -1;
            }
            for (int j = 0; j < i; j++) {
                request.append((char) buffer[j]);
            }
            System.out.print(request.toString());
            uri = parseUri(request.toString());
        }

        /* implementation of the ServletRequest */
        public Object getAttribute(String attribute) {
            return null;
        }

        public Enumeration<String> getAttributeNames() {
            return null;
        }

        public String getRealPath(String path) {
            return null;
        }

        
        public int getRemotePort() {
            return 0;
        }

        
        public String getLocalName() {
            return null;
        }

        
        public String getLocalAddr() {
            return null;
        }

        
        public int getLocalPort() {
            return 0;
        }

        
        public ServletContext getServletContext() {
            return null;
        }

        
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        
        public boolean isAsyncStarted() {
            return false;
        }

        
        public boolean isAsyncSupported() {
            return false;
        }

        
        public AsyncContext getAsyncContext() {
            return null;
        }

        
        public DispatcherType getDispatcherType() {
            return null;
        }

        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        public boolean isSecure() {
            return false;
        }

        public String getCharacterEncoding() {
            return null;
        }

        public int getContentLength() {
            return 0;
        }

        public long getContentLengthLong() {
            return 0;
        }

        public String getContentType() {
            return null;
        }

        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        public Locale getLocale() {
            return null;
        }

        public Enumeration<Locale> getLocales() {
            return null;
        }

        public String getParameter(String name) {
        	return name;
        }

        public Map<String, String[]> getParameterMap() {
            return null;
        }

        public Enumeration<String> getParameterNames() {
            return null;
        }

        public String[] getParameterValues(String parameter) {
            return null;
        }

        public String getProtocol() {
            return null;
        }

        public BufferedReader getReader() throws IOException {
            return null;
        }

        public String getRemoteAddr() {
            return null;
        }

        public String getRemoteHost() {
            return null;
        }

        public String getScheme() {
            return null;
        }

        public String getServerName() {
            return null;
        }

        public int getServerPort() {
            return 0;
        }

        public void removeAttribute(String attribute) {
        }

        public void setAttribute(String key, Object value) {
        }

        public void setCharacterEncoding(String encoding)
                throws UnsupportedEncodingException {
        }

    }

    class Response implements ServletResponse {

        private static final int BUFFER_SIZE = 1024;
        Request request;
        OutputStream output;
        PrintWriter writer;

        public Response(OutputStream output) {
            this.output = output;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        //灏唚eb鏂囦欢鍐欏叆鍒癘utputStream瀛楄妭娴佷腑
        public void sendStaticResource() throws IOException {
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            try {
            	String uri=request.getUri();
            	String contentType;
				if(uri.indexOf("html")!=-1||uri.indexOf("htm")!=-1)
					contentType="text/html";
				else if(uri.indexOf("jpg")!=-1||uri.indexOf("jpeg")!=-1)
					contentType="image/jpeg";
				else if(uri.indexOf("gif")!=-1)
					contentType="image/gif";
				else
					contentType="application/octet-stream";  //字节流类型
            	InputStream in=null;
				in = HttpServer.class.getResourceAsStream("/Server/"+uri);
				if(in!=null)
				{
				//创建HTTP响应的结果
				//创建第一行
				String responseFirstLine="HTTP/1.1 200 OK\r\n";
				//HTTP响应头
				String responseHeader="Content-Type:"+contentType+"\r\n\r\n";
				//发送HTTP响应结果

			    //发送http响应的第一行
			   output.write(responseFirstLine.getBytes());
			    //发送http响应的头
			   
			   output.write(responseHeader.getBytes());
			    //发送http响应正文
			    int len=0;

			    buffer=new byte[1280];
			    while((len=in.read(buffer))!=-1)
			    {
			    	output.write(buffer,0,len);
			    }
				}
            } catch (FileNotFoundException e) {
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                        + "Content-Type: text/html\r\n" + "Content-Length: 23\r\n"
                        + "\r\n" + "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            } finally {
                if (fis != null)
                    fis.close();
            }
        }

        /**
         * implementation of ServletResponse
         */
        public void flushBuffer() throws IOException {
        }

        public int getBufferSize() {
            return 0;
        }

        public String getCharacterEncoding() {
            return null;
        }

        
        public String getContentType() {
            return null;
        }

        public Locale getLocale() {
            return null;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        public PrintWriter getWriter() throws IOException {
            // autoflush is true, println() will flush,
            // but print() will not.
            writer = new PrintWriter(output, true);
            return writer;
        }

        
        public void setCharacterEncoding(String s) {

        }

        public boolean isCommitted() {
            return false;
        }

        public void reset() {
        }

        public void resetBuffer() {
        }

        public void setBufferSize(int size) {
        }

        public void setContentLength(int length) {
        }

        public void setContentLengthLong(long l) {

        }

        public void setContentType(String type) {
        }

        public void setLocale(Locale locale) {
        }
    }

    // 鍏抽棴鏈嶅姟鍛戒护
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
       
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8888;
        try {
           
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
               
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                
                Request request = new Request(input);
                request.parse();
               
                if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }

                
                Response response = new Response(output);
                response.setRequest(request);
                if (request.getUri().startsWith("/Servlet/")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                }
                
                else
                {
                	XmlParse xml= new XmlParse();
         	        String ok=xml.getClassByUrl(request.getUri());
         	       // System.out.println(ok);
         	        
         	        if(ok!=null)
         	        {
         	       URLClassLoader loader = null;
                   try {
                   	
                       URLStreamHandler streamHandler = null;
                       //加载类
                      loader = new URLClassLoader(
                               new URL[] { new URL(null, "file:" + System.getProperty("user.dir")
                                       + File.separator +"targets"+ File.separator + "classes", streamHandler) });
                   } catch (IOException e) {
                       System.out.println(e.toString());
                   }

                   Class<?> myClass = null;
                   try {
                       //通过解析的uri进行类加载
                       myClass = loader.loadClass(ok);
                   } catch (ClassNotFoundException e) {
                       System.out.println(e.toString());
                   }

                   Servlet servlet = null;

                   try {
                       //创建类的实例
                       servlet = (Servlet) myClass.newInstance();
                       //调用servlet方法
                       servlet.service((ServletRequest) request, (ServletResponse) response);
                   } catch (Exception e) {
                       System.out.println(e.toString());
                   } catch (Throwable e) {
                       System.out.println(e.toString());
                   }
         	        }
         	        
         	        else
         	        {
         	        	response.sendStaticResource();
         	        }
         	    
                }

                // 鍏抽棴 socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    
    
   

}