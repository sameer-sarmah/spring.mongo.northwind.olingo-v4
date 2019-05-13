package northwind.models;

public class QueryParam {
	String key;
	Object value;
	Operator operator;

	public QueryParam(String key, Object value, Operator operator) {
		super();
		this.key = key;
		this.value = value;
		this.operator = operator;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public Operator getOperator() {
		return operator;
	}

}
