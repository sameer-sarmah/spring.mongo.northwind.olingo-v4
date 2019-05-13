package northwind.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import northwind.documents.Product;
import northwind.models.FilterConstants;
import northwind.models.Operator;
import northwind.models.QueryContext;
import northwind.models.QueryParam;
import northwind.models.SortOptions;
import northwind.models.SortableFields;

@Component
public class NorthwindDAO {

	private MongoTemplate mongoTemplate;
	
	private List<String> allowedFilters =new ArrayList<>();
	private List<String> sortableFields =new ArrayList<>();
	public NorthwindDAO(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
		allowedFilters.add(FilterConstants.ID);
		allowedFilters.add(FilterConstants.COMPANY_NAME);
		allowedFilters.add(FilterConstants.CUSTOMER_NAME);
		allowedFilters.add(FilterConstants.PRICE);
		allowedFilters.add(FilterConstants.PRODUCT_NAME);
		allowedFilters.add(FilterConstants.CATEGORY_NAME);
		
		sortableFields.add(SortableFields.ID);
		sortableFields.add(SortableFields.COMPANY_NAME);
		sortableFields.add(SortableFields.CUSTOMER_NAME);
		sortableFields.add(SortableFields.PRICE);
		sortableFields.add(SortableFields.PRODUCT_NAME);
	}
	
	
	
	/*
	db.product.find({ $and : [{ unitPrice :{ $lt: 100 }},{ unitPrice:{ $gte: 20 }}] ,
    $or : [ {"category.categoryName":"Dairy Products"} ,{"category.categoryName":"Meat/Poultry"} ] })
   .sort({_id:-1}) 
	 * */
	public List<Product> useAND_OR() {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		//better way
		//criteria.orOperator(Criteria.where("category.categoryName").is("Dairy Products"),Criteria.where("category.categoryName").is("Meat/Poultry"));
		//criteria.andOperator(Criteria.where("unitPrice").gte(20),Criteria.where("unitPrice").lt(100));
		//query.addCriteria(Criteria.where("unitPrice").gte(20).lt(100));
		
		List<Criteria> andCriteria =new ArrayList<>();
		andCriteria.add(Criteria.where("unitPrice").gte(20));
		andCriteria.add(Criteria.where("unitPrice").lt(100));
		
		List<Criteria> orCriteria =new ArrayList<>();
		orCriteria.add(Criteria.where("category.categoryName").is("Dairy Products"));
		orCriteria.add(Criteria.where("category.categoryName").is("Meat/Poultry"));
		
		Criteria[] andCriteriaArr= new Criteria[andCriteria.size()];
		andCriteriaArr = andCriteria.toArray(andCriteriaArr);
		Criteria[] orCriteriaArr= new Criteria[orCriteria.size()];
		orCriteriaArr = orCriteria.toArray(orCriteriaArr);
		
		criteria.orOperator(orCriteriaArr);
		criteria.andOperator(andCriteriaArr);
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.ASC, "_id"));
		List<Product> products =mongoTemplate.find(query, Product.class);
		return products;
		
	}
	
	public void handleRead(QueryContext queryContext,Class klass) {
		if(klass.equals(Product.class)) {
			List<QueryParam> andQueryParam = queryContext.andParams();
			List<QueryParam> orQueryParam = queryContext.orParams();
			List<Criteria> andCriterias =new ArrayList<>();
			List<Criteria> orCriterias =new ArrayList<>();
			Query query = new Query();
			Criteria criteria = new Criteria();
			if(andQueryParam != null && !andQueryParam.isEmpty()) {
				andCriterias=createCriteria(andQueryParam,Operator.AND,criteria);
			}
			
			if(orQueryParam != null && !orQueryParam.isEmpty()) {
				orCriterias=createCriteria(orQueryParam,Operator.OR,criteria);
			}
			
			if(queryContext.getQueryOptions() != null) {
				int skip = queryContext.getQueryOptions().getSkip();
				if(skip > 0) {
					query.skip(skip);
				}
				int limit = queryContext.getQueryOptions().getLimit();
				if(limit > 0) {
					query.limit(limit);
				}
			}

			if(queryContext.getSortOptions() != null) {
				SortOptions sortOptions = queryContext.getSortOptions();
				if(this.sortableFields.contains(sortOptions.getFieldName())) {
					query.with(new Sort(sortOptions.getOrder(),sortOptions.getFieldName()));
				}
			}
			Criteria[] andCriteriaArr= new Criteria[andCriterias.size()];
			andCriteriaArr = andCriterias.toArray(andCriteriaArr);
			Criteria[] orCriteriaArr= new Criteria[orCriterias.size()];
			orCriteriaArr = orCriterias.toArray(orCriteriaArr);
			criteria.orOperator(orCriteriaArr);
			criteria.andOperator(andCriteriaArr);
			query.addCriteria(criteria);
			List<Product> products =mongoTemplate.find(query, Product.class);
			System.out.println(products.size());
		}
	}
	
	private List<Criteria> createCriteria(List<QueryParam> andQueries,Operator op,Criteria criteria) {
		List<Criteria> criterias = new  ArrayList<>();
		
		for(QueryParam queryParam : andQueries) {
			if(allowedFilters.contains(queryParam.getKey())) {
				criterias.add(createCriteria(queryParam.getKey(),queryParam.getValue(),queryParam.getOperator()));
			}
		}
		return criterias;
	}
	
	private Criteria createCriteria(String key,Object value,Operator op) {
		if(op.equals(Operator.EQ)) {
			return Criteria.where(key).is(value);
		}
		else if(op.equals(Operator.GE)) {
			return Criteria.where(key).gte(value);
		}
		else if(op.equals(Operator.GT)) {
			return Criteria.where(key).gt(value);
		}
		else if(op.equals(Operator.LE)) {
			return Criteria.where(key).lte(value);
		}
		else if(op.equals(Operator.LT)) {
			return Criteria.where(key).lt(value);
		}
		return null;
	}
}
