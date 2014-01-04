package union.logger;

import java.util.ArrayList;

/**
 * The Class Logger.
 */
public class Logger implements Log {
    /** The observers. */
    private ArrayList<Observer> observers = new ArrayList<Observer>();

    /** The log. */
    private String log;

    /**
     * Instantiates a new logger.
     */
    public Logger() {
    }

    /**
     * Log.
     * 
     * @param text
     *            the text
     */
    public void log(String text) {
	log = text;
	notifyObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see union.logger.Log#notifyObservers()
     */
    @Override
    public final void notifyObservers() {
	for (Observer ob : observers) {
	    ob.update(log);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see union.logger.Log#registerObserver(union.logger.Observer)
     */
    @Override
    public void registerObserver(Observer observer) {
	observers.add(observer);
    }

    /**
     * Removes the all observers.
     */
    public void removeAllObservers() {
	observers = new ArrayList<Observer>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see union.logger.Log#removeObserver(union.logger.Observer)
     */
    @Override
    public void removeObserver(Observer observer) {
	observers.remove(observer);
    }

}
