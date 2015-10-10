package falx.dm.timeseries.algo;

import falx.dm.timeseries.TimedSeries;

/**
 * Applies Moving Averages Algorithm to predict the next value in a time series.
 *
 * @author Dan Homorodean
 */
public class MovingAverageAlgorithm implements PredictiveAlgorithm {

    /**
     * Maximum number of measurements used in computing averages. This number of
     * measurements from the end of the timed series will be used in searching
     * for the least squared error.
     */
    protected int maxSamplesUsed;

    /**
     * Count of values in workData array.
     */
    private int workDataCount;

    /**
     * An array with work values, measurements.
     */
    private double workData[];

    /**
     * Computed best samples count to use.
     */
    private int bestSamplesCount;

    /**
     * Constructor taking only timed series.
     *
     * @param series
     * @throws Exception
     */
    public MovingAverageAlgorithm(TimedSeries series) throws Exception {
        this(series, 3);
    }

    /**
     * Constructor taking also the max of tested samples counts.
     *
     * @param series
     * @param maxSamplesUsed
     * @throws Exception
     */
    public MovingAverageAlgorithm(TimedSeries series, int maxSamplesUsed) throws Exception {
        if (maxSamplesUsed < 3) {
            throw new Exception("Maximum previous measurements used must be >= 3");
        }

        workDataCount = series.getCount();
        workData = series.getMeasurements();
        this.maxSamplesUsed = Math.min(maxSamplesUsed, workDataCount);
    }

    /**
     * Predicts the next value in the timed series.
     *
     * @return double
     */
    @Override
    public double predictNext() {
        // Ensure best count is found
        if (bestSamplesCount == 0) {
            bestSamplesCount = findBestSamplesCount();
        }

        double sum = 0;
        for (int i = workDataCount - bestSamplesCount; i < workDataCount; i++) {
            sum += workData[i];
        }

        double prediction = sum / bestSamplesCount;

        addPredictionToWorkData(prediction);

        return prediction;
    }

    /**
     * Finds and returns the best number of samples to use for predicting a
     * value using moving average.
     *
     * @return
     */
    private int findBestSamplesCount() {
        // Define an array to store MSEs, then calsulate MSEs for all of the samples counts
        double mse[] = new double[maxSamplesUsed - 3];
        for (int i = 3; i <= maxSamplesUsed; i++) {
            mse[i - 3] = calculateMeanSquaredError(i);
        }

        // Find the index of the minimum mean squared error, then compute and return best samples count
        int indexOfMin = indexOfMinimum(mse);
        return indexOfMin + 3;
    }

    /**
     * Calculates the MSE for the given number of samples used.
     *
     * @param samplesCount
     * @return
     */
    private double calculateMeanSquaredError(int samplesCount) {

        double errors[] = new double[workDataCount - samplesCount];

        // Compute errors for each of the set of samples tested
        for (int i = samplesCount; i < workDataCount; i++) {
            double sum = 0;
            for (int j = i - samplesCount; j < i; j++) {
                sum += workData[j];
            }
            errors[i] = Math.pow(sum / samplesCount - workData[i], 2);
        }

        // Compute the mean of squared errors
        double errorsSum = 0;
        for (int k = samplesCount; k < workDataCount; k++) {
            errorsSum += errors[k];
        }
        return errorsSum / (workDataCount - samplesCount);
    }

    /**
     * Returns index in array of the minimum value.
     *
     * @param doubles
     * @return
     */
    private int indexOfMinimum(double doubles[]) {
        double min = doubles[0];
        int indexOfMin = 0;
        for (int i = 1; i < doubles.length; i++) {
            if (doubles[i] < min) {
                min = doubles[i];
                indexOfMin = i;
            }
        }
        return indexOfMin;
    }

    /**
     * Adds the prediction at the end of the work data array.
     *
     * @param prediction
     */
    private void addPredictionToWorkData(double prediction) {
        // Check if enough space in array
        if (workDataCount == workData.length) {
            // Resize
            double resized[] = new double[workData.length + 5];
            System.arraycopy(workData, 0, resized, 0, workData.length);
            workData = resized;
        }

        workData[workDataCount++] = prediction;
    }

}
