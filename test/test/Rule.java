package test;

class Rule {
	
	String format;
	Object[] args;

	Rule(String fmt, Object ... args) {
		this.format = fmt;
		this.args = args;
	}

}