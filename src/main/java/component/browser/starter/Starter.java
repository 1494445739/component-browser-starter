package component.browser.starter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@WebServlet( loadOnStartup = 2, urlPatterns = { "/component-browser-starter" }, description = "服务器启动后开启浏览器模块" )
public final class Starter extends HttpServlet {

    private final static Logger logger = LoggerFactory.getLogger( Starter.class );

    private final static String URL = "http://localhost";

    private static String PORT = "8080";

    public void init( ServletConfig config ) throws ServletException {
        String server = config.getServletContext().getServerInfo().toLowerCase();
        logger.debug( "the server info is: {}", server );
        starter( server );
    }

    private void starter( String server ) {

        try {

            String os = System.getProperty( "os.name" );

            if ( os.startsWith( "Mac OS" ) ) {

                Class< ? > clz    = Class.forName( "com.apple.eio.FileManager" );
                Method     method = clz.getDeclaredMethod( "openURL", String.class );
                method.invoke( null, URL );

            } else if ( os.startsWith( "Windows" ) ) {

                // 判断服务器,并根据不同的服务器来捕获不同的端口号
                if ( server.contains( "jetty" ) && !StringUtils.isBlank( System.getProperty( "jetty.port" ) ) ) {
                    PORT = System.getProperty( "jetty.port" );
                } else if ( server.contains( "tomcat" ) && !StringUtils.isBlank( System.getProperty( "tomcat.port" ) ) ) {
                    PORT = System.getProperty( "tomcat.port" );
                }

                logger.debug( "start browser on the port : {}", PORT );

                try {
                    // 启动Google
                    Runtime.getRuntime().exec( new String[]{
                            "cmd",
                            "/c",
                            "start chrome ",
                            URL.concat( ":" ).concat( PORT )
                    } );
                } catch ( Exception ex ) {
                    // 启动IE
                    Runtime.getRuntime().exec( "rundll32 url.dll, FileProtocolHandler "URL );
                }

            } else {

                String[] browsers = {
                        "firefox",
                        "opera",
                        "konqueror",
                        "epiphany",
                        "mozilla",
                        "netscape"
                };
                String browser = null;

                for ( int count = 0; count < browsers.length && browser == null; count ) {
                    if ( Runtime.getRuntime().exec( new String[]{
                            "which",
                            browsers[ count ]
                    } ).waitFor() == 0 ) {
                        browser = browsers[ count ];
                    }
                }

                if ( browser == null ) {
                    logger.warn( "======== Could not find web browser ========" );
                } else {
                    Runtime.getRuntime().exec( new String[]{
                            browser,
                            URL
                    } );
                }

            }

        } catch ( ClassNotFoundException e ) {
            logger.error( e.getMessage(), e );
        } catch ( NoSuchMethodException e ) {
            logger.error( e.getMessage(), e );
        } catch ( InvocationTargetException e ) {
            logger.error( e.getMessage(), e );
        } catch ( IllegalAccessException e ) {
            logger.error( e.getMessage(), e );
        } catch ( IOException e ) {
            logger.error( e.getMessage(), e );
        } catch ( InterruptedException e ) {
            logger.error( e.getMessage(), e );
        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
        }

    }

}