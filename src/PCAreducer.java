import org.apache.commons.math3.*;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class PCAreducer {
	private RealMatrix transformMatrix;
	
	public PCAreducer(double [][] jets, int dimension) {
		this.transformMatrix=calculateTransformMatrix(jets, dimension);
	}

	public RealMatrix calculateTransformMatrix(double [][] jets, int dimension){
		double[][] cJets = centralize(jets);		
		RealMatrix cMatrix = MatrixUtils.createRealMatrix(cJets);

		Covariance cov = new Covariance(cMatrix);
		RealMatrix covMatrix = cov.getCovarianceMatrix();

		EigenDecomposition ed = new EigenDecomposition(covMatrix);
		RealMatrix eigenVecters = ed.getV();
		RealMatrix tMatrix = eigenVecters.getSubMatrix(0, jets[0].length-1, 0, dimension-1);
		return tMatrix;
		 
		
	}
	public double[][] centralize(double[][] jets) {
        double[] sum = new double[jets[0].length];
        double[] average = new double[jets[0].length];
        double[][] centralizedArray = new double[jets.length][jets[0].length];
        for (int i = 0; i < jets[0].length; i++) {
            for (int j = 0; j < jets.length; j++) {
                sum[i] += jets[j][i];
            }
            average[i] = sum[i] / jets.length;
        }
        for (int i = 0; i < jets[0].length; i++) {
            for (int j = 0; j < jets.length; j++) {
            	centralizedArray[j][i] = jets[j][i] - average[i];
            }
        }
        return centralizedArray;
    }
	
	public double[] transformOne(double [] jet) {
		return jet;
	}
	
    public double[][] transformMultiple(double [][] jets) {
    	RealMatrix jetMatrix = MatrixUtils.createRealMatrix(jets);
    	RealMatrix rowJetMatrix = jetMatrix.transpose();
    	RealMatrix rowTransMatrix = transformMatrix.transpose();
    	double [][] results = rowTransMatrix.multiply(rowJetMatrix).transpose().getData();
		return results;
	}
	
	
}
