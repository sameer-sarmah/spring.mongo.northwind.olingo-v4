package northwind.odata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.core.MetadataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@WebServlet(urlPatterns= {"/odata.svc/*"})
public class NorthwindServlet extends HttpServlet implements ApplicationContextAware{

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(NorthwindServlet.class);
  
  private static ApplicationContext context;
  
  public ApplicationContext getApplicationContext() {
      return context;
  }
   
  @Override
  public void setApplicationContext(ApplicationContext ac)
          throws BeansException {
      context = ac;
      System.out.println("############## ApplicationContext set ##############");
  }
  
  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
    OData odata = OData.newInstance();
    InputStream is=request.getServletContext().getResourceAsStream("/WEB-INF/classes/edmx/northwind.xml");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	MetadataParser metadataParser =new MetadataParser();
	ServiceMetadata edm = null;
	try {
		 edm = metadataParser.buildServiceMetadata(reader);
	} catch (XMLStreamException e1) {
		e1.printStackTrace();
	}
    
    try {
    if(context != null) {
        ODataHttpHandler handler = odata.createHandler(edm);
        NorthwindEntityCollectionProcessor collectionProcessor = context.getBean(NorthwindEntityCollectionProcessor.class);
        NorthwindEntityProcessor entityProcessor = context.getBean(NorthwindEntityProcessor.class);
        handler.register(collectionProcessor);
        handler.register(entityProcessor);
        handler.process(request, response);	
    }	

    } catch (RuntimeException e) {
      throw new ServletException(e);
    }

  }

}

