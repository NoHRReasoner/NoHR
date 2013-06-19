package union.logger;

import java.util.ArrayList;

public class Logger implements Log {
	private ArrayList<Observer> observers = new ArrayList<Observer>();
    private String log;
    public Logger() {
		
	}

    public void log(String text) {
		log = text;
		notifyObservers();
	}
	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		for (Observer ob : observers) {
            ob.update(this.log);
		}
		
	}
	

}
