package fr.martdel.rolecraft;

public class ConditionBuilder {
	
	private boolean main;
	
	public ConditionBuilder(boolean condition) {
		this.main = condition;
	}
	
	public ConditionBuilder and(boolean condition) {
		this.main = main && condition;
		return this;
	}
	public ConditionBuilder and(ConditionBuilder condition) {
		this.main = main && condition.result();
		return this;
	}
	
	public ConditionBuilder or(boolean condition) {
		this.main = main || condition;
		return this;
	}
	public ConditionBuilder or(ConditionBuilder condition) {
		this.main = main || condition.result();
		return this;
	}
	
	public boolean result() {
		return this.main;
	}

}
