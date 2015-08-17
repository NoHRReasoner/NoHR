/**
 *
 */
package pt.unl.fct.di.centria.nohr.reasoner;

import java.util.HashSet;
import java.util.Set;

import pt.unl.fct.di.centria.nohr.model.Rule;

/**
 * @author nunocosta
 *
 */
public class RuleBase {

    private boolean hasChanges;
    private final Set<RuleBaseListner> listners;
    private final Set<Rule> rules;

    public RuleBase() {
	rules = new HashSet<Rule>();
	listners = new HashSet<RuleBaseListner>();
    }

    public boolean add(Rule rule) {
	final boolean added = rules.add(rule);
	if (added) {
	    hasChanges = true;
	    for (final RuleBaseListner listener : listners)
		listener.added(rule);
	}
	return added;
    }

    public void addListner(RuleBaseListner listner) {
	listners.add(listner);
    }

    public void clear() {
	if (!rules.isEmpty()) {
	    hasChanges = true;
	    for (final RuleBaseListner listner : listners)
		listner.cleaned();
	}
	rules.clear();
    }

    public Set<Rule> getRules() {
	return rules;
    }

    public boolean hasChanges() {
	return hasChanges(false);
    }

    public boolean hasChanges(boolean reset) {
	final boolean result = hasChanges;
	if (reset)
	    hasChanges = false;
	return result;
    }

    public boolean remove(Rule rule) {
	final boolean removed = rules.remove(rule);
	if (removed) {
	    hasChanges = true;
	    for (final RuleBaseListner listener : listners)
		listener.removed(rule);
	}
	return removed;
    }

    public void removeListener(RuleBaseListner listener) {
	listners.remove(listener);
    }

    public int size() {
	return rules.size();
    }

    public boolean update(Rule oldRule, Rule newRule) {
	final boolean updated = remove(oldRule) && add(newRule);
	if (updated) {
	    hasChanges = true;
	    for (final RuleBaseListner listner : listners)
		listner.updated(oldRule, newRule);
	}
	return updated;
    }
}