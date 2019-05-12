package northwind.odata;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.springframework.stereotype.Component;

import northwind.documents.Customer;
import northwind.documents.Order;
import northwind.documents.Product;
import northwind.documents.Shipper;
import northwind.exception.CoreException;
import northwind.models.NorthwindReadHandler;

@Component
public class NorthwindEntityProcessor implements EntityProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;

	private static final String CUSTOMER = "Customer";
	private static final String PRODUCT = "Product";
	private static final String ORDER = "Order";
	private static final String SHIPPER = "Shipper";

	private NorthwindReadHandler readHandler;
	private Map<String,Class> entityNametoClass = new HashMap<>();

	public NorthwindEntityProcessor(NorthwindReadHandler readHandler) {
		super();
		this.readHandler = readHandler;
		entityNametoClass.put(CUSTOMER, Customer.class);
		entityNametoClass.put(PRODUCT, Product.class);
		entityNametoClass.put(ORDER, Order.class);
		entityNametoClass.put(SHIPPER, Shipper.class);
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}
//http://localhost:8080/odata/odata.svc/Products(productID='9')
//http://localhost:8080/odata/odata.svc/Products('9')
	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		UriResource uriResource = uriInfo.getUriResourceParts().get(0);

		if (uriResource instanceof UriResourceEntitySet) {
			handleEntityRead(request, response, uriInfo, responseFormat);
		} else {
			throw new ODataApplicationException("Only EntitySet is supported",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
		}

	}

	private void handleEntityRead(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, SerializerException {

		Entity responseEntity = null;
		EdmEntitySet responseEdmEntitySet = null; // we need this for building the contextUrl

		// 1st step: retrieve the requested Entity: can be "normal" read operation, or
		// navigation (to-one)
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();
		int segmentCount = resourceParts.size();

		UriResource uriResource = resourceParts.get(0); // in our example, the first segment is the EntitySet
		if (!(uriResource instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Only EntitySet is supported",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
		}

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
		EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		String id= keyPredicates.get(0).getText();
		id=id.replaceAll("'", "");
		if (segmentCount == 1) {
			responseEdmEntitySet = startEdmEntitySet;
			String entityName=uriResourceEntitySet.getEntityType().getName();
			try {
				responseEntity = this.readHandler.readEntity(id, entityNametoClass.get(entityName));
			} catch (CoreException e) {
				e.printStackTrace();
				throw new ODataApplicationException(e.getMessage(), e.getStatusCode(), Locale.ENGLISH);
			}

		} else if (segmentCount == 2) { 
			/*
			 * order to customer
			 * order to shipper
			 * */
			UriResource navSegment = resourceParts.get(1); 
			if (navSegment instanceof UriResourceNavigation) {
				String sourceEntityName = uriResourceEntitySet.getEntityType().getName();
				UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) navSegment;
				EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
				String targetEntityName = edmNavigationProperty.getName();
				try {
					this.readHandler.readNavigationEntity(id,this.entityNametoClass.get(sourceEntityName),this.entityNametoClass.get(targetEntityName));
				} catch (CoreException e) {
					e.printStackTrace();
					throw new ODataApplicationException(e.getMessage(), e.getStatusCode(), Locale.ENGLISH);
				}
				
			}
		} else {
			throw new ODataApplicationException("Not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),Locale.ENGLISH);
		}

		if (responseEntity == null) {
			throw new ODataApplicationException("Entity not found.", HttpStatusCode.NOT_FOUND.getStatusCode(),Locale.ENGLISH);
		}
		EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();
		ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).suffix(Suffix.ENTITY).build();
		EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).build();
		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, responseEntity, opts);
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

}
