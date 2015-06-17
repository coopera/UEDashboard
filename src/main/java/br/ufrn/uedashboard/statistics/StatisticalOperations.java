package br.ufrn.uedashboard.statistics;


public class StatisticalOperations {
	
	public static double sum(int[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

	public static double mean(int[] values) {
		if (values.length == 0) return Double.NaN;
        double sum = sum(values);
        return sum / values.length;
	}

	public static double variance(int[] values) {
		if (values.length == 0)
			return Double.NaN;
		double avg = mean(values);
		double sum = 0.0;
		for (int i = 0; i < values.length; i++) {
			sum += (values[i] - avg) * (values[i] - avg);
		}
		return sum / (values.length - 1);

	}

	public static double standardDeviation(int[] values) {
		return Math.sqrt(variance(values));
	}

}
