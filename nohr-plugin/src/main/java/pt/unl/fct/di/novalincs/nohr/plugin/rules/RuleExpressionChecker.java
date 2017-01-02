/**
 *
 */
package pt.unl.fct.di.novalincs.nohr.plugin.rules;

/*
 * #%L
 * nohr-plugin
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.Messages;

/**
 * An {@link OWLExpressionChecker} for {@link Rule rules}.
 *
 * @author Nuno Costa
 */
public class RuleExpressionChecker implements OWLExpressionChecker<Rule> {

    private final NoHRParser parser;

    public RuleExpressionChecker(NoHRParser parser) {
        this.parser = parser;
    }

    @Override
    public void check(String str) throws OWLExpressionParserException {
        createObject(str);
    }

    @Override
    public Rule createObject(String str) throws OWLExpressionParserException {
        try {
            return parser.parseRule(str);
        } catch (final ParseException e) {
            throw new OWLExpressionParserException(Messages.invalidExpressionMessage(str, e), e.getBegin(), e.getEnd(),
                    false, false, false, false, false, false, null);
        }
    }
}
