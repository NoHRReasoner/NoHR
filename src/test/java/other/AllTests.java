package other;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import testsuite.QueryTest;
import testsuite.TranslationTest;

@RunWith(Suite.class)
@SuiteClasses({ RuleCreatorQLTest.class, TBoxGraphTest.class, TranslationTest.class, QueryTest.class })
public class AllTests {

}
