package nohr.model;

import java.util.List;

import other.Utils;

public class RuleImpl implements Rule {
	
	private PositiveLiteral head;
	
	private List<Literal> body;
	
	public RuleImpl(PositiveLiteral head, List<Literal> body) {
		this.head = head;
		this.body = body;
	}

	@Override
	public List<Literal> getBody() {
		return body;
	}

	@Override
	public PositiveLiteral getHead() {
		return head;
	}

	@Override
	public boolean isFact() {
		return body.isEmpty();
	}
	
	@Override
	public String toString() {
		return head + ":-" + Utils.concat(",", body) + ".";
	}

}
