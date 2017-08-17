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

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getNonTargetThreshold() {
        return nonTargetThreshold;
    }

    public void setNonTargetThreshold(int nonTargetThreshold) {
        this.nonTargetThreshold = nonTargetThreshold;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double epsilon;  //For TIM
    private int budget;
    private int nonTargetThreshold;
}
