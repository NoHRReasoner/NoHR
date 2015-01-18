package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ QueryTest.class, RuleCreatorQLTest.class, TranslationTest.class, TBoxGraphTest.class})
public class AllTests {

}
