package northwind.models;

public class QueryOptions {

	private int skip =0,limit = Integer.MAX_VALUE;
	public QueryOptions(int skip, int limit) {
		super();
		this.skip = skip;
		this.limit = limit;
	}

	public int getSkip() {
		return skip;
	}

	public int getLimit() {
		return limit;
	}
}
