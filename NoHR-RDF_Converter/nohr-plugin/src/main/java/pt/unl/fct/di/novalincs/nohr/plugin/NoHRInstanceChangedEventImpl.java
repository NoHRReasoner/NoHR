package pt.unl.fct.di.novalincs.nohr.plugin;

public class NoHRInstanceChangedEventImpl implements NoHRInstanceChangedEvent {

    private final NoHRInstanceChangedEventType type;

    public NoHRInstanceChangedEventImpl(NoHRInstanceChangedEventType type) {
        this.type = type;
    }

    @Override
    public NoHRInstanceChangedEventType getType() {
        return this.type;
    }
}
