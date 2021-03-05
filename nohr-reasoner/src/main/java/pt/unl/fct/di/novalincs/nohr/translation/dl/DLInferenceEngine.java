package pt.unl.fct.di.novalincs.nohr.translation.dl;

public enum DLInferenceEngine {

    HERMIT,
    KONCLUDE;

    public static DLInferenceEngine getDLInferenceEngine(String value) {
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
