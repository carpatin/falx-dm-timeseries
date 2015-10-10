package falx.dm.timeseries;

import java.util.*;

public class TimedSeries {

    protected double measurements[];
    protected int count;

    /**
     * Creates TimedSeries instance from a list of doubles.
     *
     * @param values
     * @return TimedSeries
     */
    public static TimedSeries fromList(List<Double> values) {
        TimedSeries series = new TimedSeries(values.size());
        for (double measurement : values) {
            series.addMeasurement(measurement);
        }
        return series;
    }

    /**
     * Constructor
     *
     * @param length
     */
    public TimedSeries(int length) {
        measurements = new double[length];
        count = 0;
    }

    /**
     * Adds a measurement at the end.
     *
     * @param measurement
     */
    public void addMeasurement(double measurement) {
        measurements[count++] = measurement;
    }

    public int getCount() {
        return count;
    }

    public double[] getMeasurements() {
        return measurements;
    }

    
}
