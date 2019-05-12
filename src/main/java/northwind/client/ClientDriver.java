package northwind.client;

import java.util.Map;

import org.springframework.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import northwind.exception.CoreException;

public class ClientDriver {
	private static final String userService = "https://localhost:8080/api/user";
	private static final String permissionService = "https://localhost:8080/api/user/%s/%s";
	private static final String READ = "READ";
	private static final String WRITE = "WRITE";

	public static void main(String[] args) {
		String str="'9'";
		System.out.println(str.replaceAll("'", ""));;
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
