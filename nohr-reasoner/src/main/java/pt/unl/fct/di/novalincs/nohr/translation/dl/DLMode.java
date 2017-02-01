package pt.unl.fct.di.novalincs.nohr.translation.dl;

import pt.unl.fct.di.novalincs.nohr.translation.InferenceEngine;

public enum DLMode {
    HERMIT, KONCLUDE;

    public InferenceEngine getInferenceEngine() {
        if (this == HERMIT) {
            return new HermitInferenceEngine();
        } else {
            return new KoncludeInferenceEngine(System.getenv("KONCLUDE_BIN"));
        }
    }
}
