package helpers;

public class Rule {

	String format;
	Object[] args;

	public Rule(String fmt, Object... args) {
		format = fmt;
		this.args = args;
	}

	@Override
	public String toString() {
		return String.format(format, args);
	}

}