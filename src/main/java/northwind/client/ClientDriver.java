package northwind.client;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import northwind.config.MongoConfig;
import northwind.dao.NorthwindDAO;
import northwind.documents.Product;
import northwind.exception.CoreException;
import northwind.models.FilterConstants;
import northwind.models.Operator;
import northwind.models.QueryContext;
import northwind.models.QueryParam;

public class ClientDriver {
	private static final String userService = "https://localhost:8080/api/user";
	private static final String permissionService = "https://localhost:8080/api/user/%s/%s";
	private static final String READ = "READ";
	private static final String WRITE = "WRITE";

	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class);
		NorthwindDAO dao = ctx.getBean(NorthwindDAO.class);
//		List<Product> products =  dao.useAND_OR();
//		System.out.println(products.size());
		QueryContext context =new QueryContext();
		
		QueryParam lt100 = new QueryParam(FilterConstants.PRICE,100,Operator.LT);
		QueryParam gt20 = new QueryParam(FilterConstants.PRICE,20,Operator.GT);
		context.addQueryParam(lt100, Operator.AND);
		context.addQueryParam(gt20, Operator.AND);
		
		QueryParam diary = new QueryParam(FilterConstants.CATEGORY_NAME,"Dairy Products",Operator.EQ);
		QueryParam poultry = new QueryParam(FilterConstants.CATEGORY_NAME,"Meat/Poultry",Operator.EQ);
		context.addQueryParam(diary, Operator.OR);
		context.addQueryParam(poultry, Operator.OR);
		dao.handleRead(context, Product.class);
//		
//		ProductRepo repo = ctx.getBean(ProductRepo.class);
//		products = repo.findProductBetweenPrice(100,20);
//		System.out.println(products.size());
		//identity("123");
	}
	
    public static <T> T identity(T obj) {
		System.out.println(obj.getClass().getName());
		System.out.println(obj.getClass().equals(Product.class));
		return obj;
		
	} 

	private static void executeHttpClient(SpringHttpClient httpClient, String service, HttpMethod method,
			Map<String, String> headers, Map<String, String> queryParams, String payload) {
		try {
			String jsonResponse = httpClient.request(service, method, headers, queryParams, payload);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(jsonResponse);
			String prettyJsonString = gson.toJson(je);
			System.out.println(prettyJsonString);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
