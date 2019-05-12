package northwind.odata;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.springframework.stereotype.Component;

import northwind.documents.Customer;
import northwind.documents.Order;
import northwind.documents.Product;
import northwind.documents.Shipper;
import northwind.models.NorthwindReadHandler;


@Component
public class NorthwindEntityCollectionProcessor implements EntityCollectionProcessor{
	private OData odata;
	private ServiceMetadata serviceMetadata;

	private static final String CUSTOMER = "Customer";
	private static final String PRODUCT = "Product";
	private static final String ORDER = "Order";
	private static final String SHIPPER = "Shipper";

	private NorthwindReadHandler readHandler;
	private Map<String,Class> entityNametoClass = new HashMap<>();

	public NorthwindEntityCollectionProcessor(NorthwindReadHandler readHandler) {
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
	//http://localhost:8080/odata/odata.svc/Products
	public void readEntityCollection(ODataRequest request, ODataResponse response,
      UriInfo uriInfo, ContentType responseFormat)
      throws ODataApplicationException, SerializerException {
    final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);
    
    if(firstResourceSegment instanceof UriResourceEntitySet) {
      readEntityCollectionInternal(request, response, uriInfo, responseFormat);
    } else {
      throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
          Locale.ENGLISH);
    }
  }
	
	
	private void readEntityCollectionInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo,
	    ContentType responseFormat) throws ODataApplicationException, SerializerException {

	EdmEntitySet edmEntitySet = null; 
    EntityCollection entityCollection = null; 
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    int segmentCount = resourceParts.size();

    UriResource uriResource = resourceParts.get(0); 
    if (!(uriResource instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Not supported",HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
    }

    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
    EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
    String startEntityName = uriResourceEntitySet.getEntityType().getName();
    if (segmentCount == 1) { 
      edmEntitySet = startEdmEntitySet;
      entityCollection = this.readHandler.readEntitySet(this.entityNametoClass.get(startEntityName));
    } 
    else { 
      throw new ODataApplicationException("Not supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    List<Entity> modifiedEntityList = entityCollection.getEntities();
    EntityCollection modifiedEntityCollection = new EntityCollection();
    modifiedEntityCollection.getEntities().addAll(modifiedEntityList);
    ODataSerializer serializer = odata.createSerializer(responseFormat);
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
                                                                              .contextURL(contextUrl)
                                                                              .id(id)
                                                                              .build();
    SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType,                                                                    modifiedEntityCollection, opts);
    InputStream serializedContent = serializerResult.getContent();
    response.setContent(serializedContent);
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}
	
  private List<Entity> applyTopQueryOption(List<Entity> entityList, TopOption topOption)
      throws ODataApplicationException {

    if (topOption != null) {
      int topNumber = topOption.getValue();
      if (topNumber >= 0) {
        if(topNumber <= entityList.size()) {
          entityList = entityList.subList(0, topNumber);
        }  // else the client has requested more entities than available => return what we have
      } else {
        throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
    
    return entityList;
  }

  private List<Entity> applySkipQueryOption(List<Entity> entityList, SkipOption skipOption)
      throws ODataApplicationException {

    if (skipOption != null) {
      int skipNumber = skipOption.getValue();
      if (skipNumber >= 0) {
        if(skipNumber <= entityList.size()) {
          entityList = entityList.subList(skipNumber, entityList.size());
        } else {
          // The client skipped all entities
          entityList.clear();
        }
      } else {
        throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
    
    return entityList;
  }

  private List<Entity> applyCountQueryOption(EntityCollection entityCollection, List<Entity> modifiedEntityList, 
      CountOption countOption) {

    // handle $count: always return the original number of entities, without considering $top and $skip
    if (countOption != null) {
      boolean isCount = countOption.getValue();
      if (isCount) {
        entityCollection.setCount(modifiedEntityList.size());
      }
    }
    
    return modifiedEntityList;
  }

  private List<Entity> applyOrderQueryOption(List<Entity> entityList, OrderByOption orderByOption) {

    if (orderByOption != null) {
      List<OrderByItem> orderItemList = orderByOption.getOrders();
      final OrderByItem orderByItem = orderItemList.get(0); // in our example we support only one
      Expression expression = orderByItem.getExpression();
      if (expression instanceof Member) {
        UriInfoResource resourcePath = ((Member) expression).getResourcePath();
        UriResource uriResource = resourcePath.getUriResourceParts().get(0);
        if (uriResource instanceof UriResourcePrimitiveProperty) {
          EdmProperty edmProperty = ((UriResourcePrimitiveProperty) uriResource).getProperty();
          final String sortPropertyName = edmProperty.getName();

          // do the sorting for the list of entities  
          Collections.sort(entityList, new Comparator<Entity>() {

            // we delegate the sorting to the native sorter of Integer and String
            public int compare(Entity entity1, Entity entity2) {
              int compareResult = 0;

              if (sortPropertyName.equals("ID")) {
                Integer integer1 = (Integer) entity1.getProperty(sortPropertyName).getValue();
                Integer integer2 = (Integer) entity2.getProperty(sortPropertyName).getValue();

                compareResult = integer1.compareTo(integer2);
              } else {
                String propertyValue1 = (String) entity1.getProperty(sortPropertyName).getValue();
                String propertyValue2 = (String) entity2.getProperty(sortPropertyName).getValue();

                compareResult = propertyValue1.compareTo(propertyValue2);
              }

              // if 'desc' is specified in the URI, change the order of the list 
              if (orderByItem.isDescending()) {
                return -compareResult; // just convert the result to negative value to change the order
              }

              return compareResult;
            }
          });
        }
      }
    }
    
    return entityList;
  }
  
  private void validateNestedExpxandSystemQueryOptions(final ExpandOption expandOption) 
      throws ODataApplicationException {
    if(expandOption == null) {
      return;
    }
    
    for(final ExpandItem item : expandOption.getExpandItems()) {
      if(    item.getCountOption() != null 
          || item.getFilterOption() != null 
          || item.getLevelsOption() != null
          || item.getOrderByOption() != null
          || item.getSearchOption() != null
          || item.getSelectOption() != null
          || item.getSkipOption() != null
          || item.getTopOption() != null) {
        
        throw new ODataApplicationException("Nested expand system query options are not implemented", 
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),Locale.ENGLISH);
      }
    }
  }
  
  private List<Entity> applyFilterQueryOption(List<Entity> entityList, FilterOption filterOption)
      throws ODataApplicationException {

    if (filterOption != null) {
      try {
        Iterator<Entity> entityIterator = entityList.iterator();

        // Evaluate the expression for each entity
        // If the expression is evaluated to "true", keep the entity otherwise remove it from the entityList
        while (entityIterator.hasNext()) {
          // To evaluate the the expression, create an instance of the Filter Expression Visitor and pass
          // the current entity to the constructor
          Entity currentEntity = entityIterator.next();
          Expression filterExpression = filterOption.getExpression();
          FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);

          // Start evaluating the expression
          Object visitorResult = filterExpression.accept(expressionVisitor);

          // The result of the filter expression must be of type Edm.Boolean
          if (visitorResult instanceof Boolean) {
            if (!Boolean.TRUE.equals(visitorResult)) {
              // The expression evaluated to false (or null), so we have to remove the currentEntity from entityList
              entityIterator.remove();
            }
          } else {
            throw new ODataApplicationException("A filter expression must evaulate to type Edm.Boolean",
                HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
          }
        }

      } catch (ExpressionVisitException e) {
        throw new ODataApplicationException("Exception in filter evaluation",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
      }
    }
    
    return entityList;
  }

}
