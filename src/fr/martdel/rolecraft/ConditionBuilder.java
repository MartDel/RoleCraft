package fr.martdel.rolecraft;

public class ConditionBuilder {
	
	private boolean main;
	
	public ConditionBuilder(boolean condition) {
		this.main = condition;
	}
	
	public void and(boolean condition) {
		this.main = main && condition;
	}
	public void and(ConditionBuilder condition) {
		this.main = main && condition.result();
	}
	
	public void or(boolean condition) {
		this.main = main || condition;
	}
	public void or(ConditionBuilder condition) {
		this.main = main || condition.result();
	}
	
	public boolean result() {
		return this.main;
	}

}
