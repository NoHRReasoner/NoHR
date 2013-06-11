package union.logger;

public interface IListenerSupport<T> {
    /**
     * Register new Listener
     */
    public void addListener(T listener);
 
    /**
     * Delete previously registered listener
     */
    public void removeListener(T listener);
}