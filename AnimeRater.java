import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Random;

/*************************************************************
 * Class to create an ai model that rates anime
 *
 * @author Aniekan Ekarika
 ************************************************************/
public class AnimeRater
{
    private double[] weights;
    private double bias;
    private double learningRate;
    private int epochs;
    
    public AnimeRater()
    {
        Random gen = new Random();
        // make an array of weights, each weight is -1 to 1 by default and make bias 5 by default
        weights = new double[13];
        for (int i=0; i<weights.length; i++)
        {
            weights[i] = gen.nextDouble()*2 - 1;
            //weights[i] = 1;
        }
        bias = 5;
        
        learningRate = 0.0005;
        epochs = 100;
    }
    
    /*************************************************************
     * Method to train a model based on csv data
     * 
     * @param String pathname to the csv data
     ************************************************************/
    public void train(String pathname)
    {
        double[][] data;
        ArrayList<double[]> rows = new ArrayList<>();
        
        // Read CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(pathname))) {
            String line;
            boolean isFirstRow = true;
            
            while ((line = br.readLine()) != null) {
                // Skip the first row (headers)
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
            
                // Split line into parts and ignore the first column
                String[] parts = line.split(",");
                double[] row = new double[parts.length - 1]; // Exclude the first column
            
                for (int i = 1; i < parts.length; i++) { // Start from index 1
                    row[i - 1] = Double.parseDouble(parts[i]);
                }
                rows.add(row);
            }
        } 
        catch (IOException error) 
        {
            error.printStackTrace();
        }
        
        data = rows.toArray(new double[0][]);
        
        double total=0, correct=0;
        double totalLoss = 0;
        // Repeat the training process for a set number of epochs to allow the model to converge
        for (int i=0; i<epochs; i++)
        {
            // Use SGD and mean absolute error as loss
            // Go through each row and predict my rating based on weights
            for (double[] input : data) {
                double trueVal, predVal, dLoss;
                // true value is the last value in the row which is the rating
                trueVal = Math.round(input[input.length-1] * 10) / 10;
                
                // prediction = az + by + cx ... + bias where 'z' upward are teh weights while 'a' down are the input features
                predVal = bias;
                for (int j=0; j<input.length-1; j++) {
                    predVal = predVal + weights[j] * input[j];
                }
                
                //Squash predVal between 1 and 0
                predVal = 1 / (1+Math.exp(-predVal));
    
                // Make predVal a rating out of 10
                predVal = Math.round(predVal*10 * 10) / 10;
                
                // calculate delta Loss and totalLoss
                totalLoss += Math.abs(predVal - trueVal);
                dLoss = Math.signum(predVal - trueVal);
                
                System.out.println("Total Loss is " + totalLoss);
                System.out.println("Loss gradient is " + dLoss);
                
                if (predVal == trueVal)
                {
                    correct += 1;
                }
                total += 1;
                
                //Change wieghts and bias based on dLoss
                for(int j=0; j<input.length-1; j++)
                {
                    weights[j] = weights[j] - learningRate * dLoss*input[j];
                }
                bias = bias - learningRate * dLoss;
            }
        }
        
        // Save weights and bias to a binary file
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("model_weights.dat"))) {
            // Write weights
            for (double weight : weights) {
                dos.writeDouble(weight);
            }
            // Write bias
            dos.writeDouble(bias);
            
            System.out.println("Weights and bias saved successfully!");
        } 
        catch (IOException error) 
        {
            error.printStackTrace();
        }
        double accuracy = (correct/total)*100;
        System.out.println("Accuracy is " + accuracy + "%");
    }
         
    
    /*********************************************************************
     * Method to load weights for this model based on binary file data
     * 
     * @param String pathname to the weights
     *******************************************************************/
    public void load(String pathname)
    {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(pathname))) {
            // Read weights
            for (int i = 0; i < weights.length; i++) {
                weights[i] = dis.readDouble();
            }
            // Read bias
            bias = dis.readDouble();
            
            System.out.println("Loaded Weights and bias succesfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /*********************************************************************
     * Method to predict my rating based on the genre of the anime
     * 
     * @param double array, anime vector
     * @return double rating out of 10
     *******************************************************************/
    public double predict(double[] features)
    {
        // prediction = az + by + cx ... + bias where 'z' upward are the weights while 'a' down are the input features
        double prediction = bias;
        for (int i=0; i<features.length; i++)
        {
            prediction += features[i] * weights[i];
        }
        
        //Squash predVal between 1 and 0
        prediction = 1 / (1+Math.exp(-prediction));

        // Make predVal a rating out of 10
        prediction = prediction*10;
        
        return prediction;
    }
}
