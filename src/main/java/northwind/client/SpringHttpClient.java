package northwind.client;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import northwind.exception.CoreException;


@Component
public class SpringHttpClient{
    
	final static Logger logger = Logger.getLogger(SpringHttpClient.class);
    final private RestTemplate restTemplate = new RestTemplate();
    public String request(final String url,final HttpMethod method,Map<String,String> headers ,
                          Map<String,String> queryParams,final String jsonString) throws CoreException{
    	 HttpHeaders headersSpring = new HttpHeaders();
    	 headersSpring.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            if(headers != null){
                headers.forEach((key,value)->{
                	headersSpring.add(key, value);
                });
            }
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            if(queryParams != null) {
                queryParams.forEach((key, value) -> {
                	builder.queryParam(key,value);
                });
            }
            UriComponents uriComp = builder.build();
            HttpEntity<String> requestEntity;
            if(method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)){
                requestEntity =  new HttpEntity<String>(jsonString, headersSpring);
            }
            else {
            	requestEntity =  new HttpEntity<String>(headersSpring);
            }

        ResponseEntity<String> strResponse = restTemplate.exchange(uriComp.toUriString(),method, requestEntity,
                            String.class);

							return strResponse.getBody().toString();
    	
    }
}
