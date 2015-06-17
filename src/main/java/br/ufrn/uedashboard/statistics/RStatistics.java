package br.ufrn.uedashboard.statistics;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RStatistics {

	static {
		System.loadLibrary("jri");
	}

	private Rengine rengine;

	public RStatistics() {
		rengine = new Rengine(new String[] { "--vanilla" }, false, null);
		System.out.println("Rengine created, waiting for R");

		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!rengine.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

	}

	public Double getMean(int[] values) {
		rengine.assign("values", values);
		REXP result = rengine.eval("mean(values)");
		System.out.println("rexp: " + result.asDouble());
		return result.asDouble();
	}

	public Double getStandardDeviation(int[] values) {
		rengine.assign("values", values);
		REXP result = rengine.eval("sd(values)");
		System.out.println("rexp: " + result.asDouble());
		return result.asDouble();
	}

}
