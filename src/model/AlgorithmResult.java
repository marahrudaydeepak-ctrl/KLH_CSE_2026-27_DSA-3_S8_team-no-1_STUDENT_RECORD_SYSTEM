package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard framework model encapsulating all execution metrics, complexity measures,
 * step logs, and output of any executed algorithm across the application.
 */
public class AlgorithmResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String algorithmName;
    private String category;
    private String purpose;
    private int inputSize;
    private String dataStructureUsed;
    private Object output;
    private long executionTimeNs;
    private long comparisons;
    private long iterations;
    private long swaps;
    private long recursionCount;
    private String timeComplexity;
    private String spaceComplexity;
    private String explanation;
    private List<String> steps;

    public AlgorithmResult() {
        this.steps = new ArrayList<>();
    }

    public AlgorithmResult(String algorithmName, String category, String purpose, int inputSize,
                           String dataStructureUsed, Object output, long executionTimeNs,
                           long comparisons, long iterations, long swaps, long recursionCount,
                           String timeComplexity, String spaceComplexity, String explanation,
                           List<String> steps) {
        this.algorithmName = algorithmName;
        this.category = category;
        this.purpose = purpose;
        this.inputSize = inputSize;
        this.dataStructureUsed = dataStructureUsed;
        this.output = output;
        this.executionTimeNs = executionTimeNs;
        this.comparisons = comparisons;
        this.iterations = iterations;
        this.swaps = swaps;
        this.recursionCount = recursionCount;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
        this.explanation = explanation;
        this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
    }

    public String getAlgorithmName() { return algorithmName; }
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public int getInputSize() { return inputSize; }
    public void setInputSize(int inputSize) { this.inputSize = inputSize; }

    public String getDataStructureUsed() { return dataStructureUsed; }
    public void setDataStructureUsed(String dataStructureUsed) { this.dataStructureUsed = dataStructureUsed; }

    public Object getOutput() { return output; }
    public void setOutput(Object output) { this.output = output; }

    public long getExecutionTimeNs() { return executionTimeNs; }
    public void setExecutionTimeNs(long executionTimeNs) { this.executionTimeNs = executionTimeNs; }

    public double getExecutionTimeMs() {
        return executionTimeNs / 1_000_000.0;
    }

    public long getComparisons() { return comparisons; }
    public void setComparisons(long comparisons) { this.comparisons = comparisons; }

    public long getIterations() { return iterations; }
    public void setIterations(long iterations) { this.iterations = iterations; }

    public long getSwaps() { return swaps; }
    public void setSwaps(long swaps) { this.swaps = swaps; }

    public long getRecursionCount() { return recursionCount; }
    public void setRecursionCount(long recursionCount) { this.recursionCount = recursionCount; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps != null ? steps : new ArrayList<>(); }
    public void addStep(String step) { this.steps.add(step); }

    /**
     * Renders formatted output strictly complying with Section 60 specification.
     */
    public String toFormattedDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("             ALGORITHM EXECUTION RESULT         \n");
        sb.append("================================================\n");
        sb.append("Algorithm:\n").append(algorithmName).append("\n\n");
        sb.append("Category:\n").append(category).append("\n\n");
        sb.append("Purpose:\n").append(purpose != null ? purpose : "Student Record Operation").append("\n\n");
        sb.append("Input Size:\n").append(inputSize).append(" records\n\n");
        sb.append("Data Structure:\n").append(dataStructureUsed != null ? dataStructureUsed : "Custom Data Structure").append("\n\n");
        sb.append("Output:\n").append(output != null ? output.toString() : "Success").append("\n\n");
        sb.append("Comparisons:\n").append(comparisons).append("\n\n");
        sb.append("Swaps:\n").append(swaps).append("\n\n");
        sb.append("Iterations:\n").append(iterations).append("\n\n");
        if (recursionCount > 0) {
            sb.append("Recursive Calls:\n").append(recursionCount).append("\n\n");
        }
        sb.append(String.format("Execution Time:\n%.4f ms (%,d ns)\n\n", getExecutionTimeMs(), executionTimeNs));
        sb.append("Time Complexity:\n").append(timeComplexity).append("\n\n");
        sb.append("Space Complexity:\n").append(spaceComplexity).append("\n");
        if (explanation != null && !explanation.isEmpty()) {
            sb.append("\nExplanation:\n").append(explanation).append("\n");
        }

        if (steps != null && !steps.isEmpty()) {
            sb.append("------------------------------------------------\n");
            sb.append("EXECUTION STEPS\n");
            sb.append("------------------------------------------------\n");
            for (int i = 0; i < steps.size(); i++) {
                sb.append("Step ").append(i + 1).append(":\n").append(steps.get(i)).append("\n\n");
            }
        }
        sb.append("================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s | Size: %d | Time: %.3f ms | Cmp: %d | Swp: %d | Complexity: %s]",
                algorithmName, inputSize, getExecutionTimeMs(), comparisons, swaps, timeComplexity);
    }
}
