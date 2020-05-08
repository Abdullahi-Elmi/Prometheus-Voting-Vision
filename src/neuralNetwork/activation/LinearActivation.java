package neuralNetwork.activation;

import java.io.Serializable;

public class LinearActivation implements ActivationFunction, Serializable {
	
	@Override
    public double activate(double weightedSum) {
        return weightedSum;
        // f(x) = x
    }

    @Override
    public double derive(double weightedSum) {
        return 1;
        // f(x) = x, ==> f'(x) = 1
    }

    @Override
    public ActivationFunction clone() {
        return new LinearActivation();
    }
}
