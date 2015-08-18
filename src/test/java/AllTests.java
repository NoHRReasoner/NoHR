import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 */

/**
 * @author Nuno Costa
 */
@RunWith(Suite.class)
@SuiteClasses({ BasicLazyGraphClosureTest.class, ELQueryTest.class, ModelTest.class, ParserTest.class,
		QLQueryTest.class, QueryProcessorTest.class, TBoxGraphTest.class })
public class AllTests {

}
