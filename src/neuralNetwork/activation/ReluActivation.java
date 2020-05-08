package neuralNetwork.activation;

import java.io.Serializable;
// An earlier run of this was not working properly, one of the changes I've attempted is add the more standard, ReLu as 
// an activation function
public class ReluActivation implements ActivationFunction, Serializable {
	//
	public double activate(double weightedSum) {
		if(weightedSum > 0) {
			return weightedSum;
		}
		else {
			return 0;
		}
		// Fairly simply activation, f(x) = max(0,x)
    }

    public double derive(double weightedSum) {
    	if(weightedSum > 0) {
			return 1;
			// because f(x) = x, f'(x) = 1
		}
		else {
			return 0;
			// because f(x) = 0, f'(x) = 0
		}
    }

    public ReluActivation clone() {
        return new ReluActivation();
    }
}
