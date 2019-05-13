package northwind.models;

import org.springframework.data.domain.Sort;

public class SortOptions {
	private String fieldName;
	private Sort.Direction order;

	public SortOptions(String fieldName, Sort.Direction order) {
		super();
		this.fieldName = fieldName;
		this.order = order;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Sort.Direction getOrder() {
		return order;
	}

}
