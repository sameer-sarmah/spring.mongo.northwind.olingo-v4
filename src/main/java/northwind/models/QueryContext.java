package northwind.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryContext {
	private Map<Operator, List<QueryParam>>  operatorToParam =new HashMap<>();
	private QueryOptions queryOptions ;
	private SortOptions sortOptions ;
	
	public void addQueryParam(QueryParam param,Operator operator) {
		if(operator.equals(Operator.AND) || operator.equals(Operator.OR)) {
			if(operatorToParam.get(operator) == null) {
				operatorToParam.put(operator, new ArrayList<QueryParam>());
			}
			operatorToParam.get(operator).add(param);
		}
	}
	
	public List<QueryParam> andParams(){
		return operatorToParam.get(Operator.AND);
	}
	
	public List<QueryParam> orParams(){
		return operatorToParam.get(Operator.OR);
	}

	public QueryOptions getQueryOptions() {
		return queryOptions;
	}

	public void setQueryOptions(QueryOptions queryOptions) {
		this.queryOptions = queryOptions;
	}

	public SortOptions getSortOptions() {
		return sortOptions;
	}

	public void setSortOptions(SortOptions sortOptions) {
		this.sortOptions = sortOptions;
	}
	
	
	
}
