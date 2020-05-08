package neuralNetwork.activation;
// Just the template for the activation functions, we have 3 currently implemented
public interface ActivationFunction {
    double activate(double sum);
    double derive(double sum);
    ActivationFunction clone();
}
