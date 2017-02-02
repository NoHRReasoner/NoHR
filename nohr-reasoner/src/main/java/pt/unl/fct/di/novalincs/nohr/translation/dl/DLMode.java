package pt.unl.fct.di.novalincs.nohr.translation.dl;

public enum DLMode {

    HERMIT,
    KONCLUDE;

    public static DLMode getDLMode(String value) {
        switch (value) {
            case "HERMIT":
                return HERMIT;
            case "KONCLUDE":
                return KONCLUDE;
        }

        return HERMIT;
    }

    @Override
    public String toString() {
        switch (this) {
            case HERMIT:
                return "HERMIT";
            case KONCLUDE:
                return "KONCLUDE";
        }

        return "HERMIT";
    }
}
