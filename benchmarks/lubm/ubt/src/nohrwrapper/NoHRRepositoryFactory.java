package nohrwrapper;

import edu.lehigh.swat.bench.ubt.api.Repository;
import edu.lehigh.swat.bench.ubt.api.RepositoryFactory;

public class NoHRRepositoryFactory extends RepositoryFactory {

	public NoHRRepositoryFactory() {
	}

	@Override
	public Repository create() {
		return new NoHRRepository();
	}

}
