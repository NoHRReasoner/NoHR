/**
 *
 */
package unittest;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Nuno Costa
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    BasicLazyGraphClosureTest.class,
    ELHermitQueryTest.class,
    ELKoncludeTest.class,
    ELQueryTest.class,
    HybridKBTest.class,
    ModelTest.class,
    ParserTest.class,
    QLHermitQueryTest.class,
    QLKoncludeTest.class,
    QLQueryTest.class,
    QueryProcessorTest.class,
    RLHermitQueryTest.class,
    RLKoncludeTest.class,
    RLQueryTest.class,
    TBoxGraphTest.class
})

public class AllTests {

}
