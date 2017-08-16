package edu.iastate.research.influence.maximization.models;

public class AlgorithmParameters {
    private static AlgorithmParameters ourInstance = new AlgorithmParameters();

    public static AlgorithmParameters getInstance() {
        return ourInstance;
    }

    private AlgorithmParameters() {
    }

    public int getNumberOfSimulations() {
        return numberOfSimulations;
    }

    public void setNumberOfSimulations(int numberOfSimulations) {
        this.numberOfSimulations = numberOfSimulations;
    }

    private int numberOfSimulations;
    private int budget;
    private int nonTargetThreshold;
}
