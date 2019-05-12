package northwind.listner;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NorthwindApplicationListener implements ApplicationListener<ApplicationEvent> {


	  @Override
	  public void onApplicationEvent(ApplicationEvent event) {
	     if(event instanceof ApplicationStartedEvent) {

	     }
	  }
	
}
