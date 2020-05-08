package neuralNetwork.activation;

import java.io.Serializable;

public class SigmoidActivation implements ActivationFunction, Serializable {
    public double activate(double weightedSum) {
        return 1.0 / (1 + Math.exp(-1.0 * weightedSum));
        // f(x) =  1/(1 + e^-x)
    }

    public double derive(double weightedSum) {
        return weightedSum * (1.0 - weightedSum);
        // sigma'(x) = sigma(x)*(1 - sigma(x))
        // the activated value is already gonna be passed through the derivation function, so that's why the input replaces
        // "sigma(x)" here
    }

    public SigmoidActivation clone() {
        return new SigmoidActivation();
    }
}
