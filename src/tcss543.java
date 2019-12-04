/**
 * This is the main class.
 * 
 * TCSS543 - Autumn 2019 The Final Project: Empirical study project on network flow
 * 
 * @author Asmita Singla
 * @author Deepthi Warrier
 * @author Thuan Lam
 * @date 12-06-2019
 */
public class tcss543 {
	/**
	 * The main function. This function checks if the first argument is a
	 * text file name or not. Then try to open the given text file name.
	 * 
	 * @param args the list of arguments. We need at least 1 parameter that is the
	 *             input file name.
	 */
	public static void main(String[] args) {
		if (args.length == 0)
			System.out.println("Error: Input file needed");
		else if (!args[0].contains(".txt"))
			System.out.println("Error: Input file must be a text .txt file");
		else
			runAlgorithms(args[0]);
	}

	/**
	 * The function that actually runs all algorithms.
	 * 
	 * @param theInputFileName the input file name.
	 */
	public static void runAlgorithms(String theInputFileName) {

		int NUMBER_OF_RUNNINGS = 1;
		long totalRunningTime = 0;
		System.out.println("Input file: " + theInputFileName);
		
		// Calculate Max flow using Ford-Fulkerson Algorithm
		double fordFulkersonMaxflow = 0.0;
		for (int i = 0; i < NUMBER_OF_RUNNINGS; i++) {
			SimpleGraph graph = new SimpleGraph();
			GraphInput.LoadSimpleGraph(graph, theInputFileName);
			FordFulkerson fordFulkerson = new FordFulkerson(graph);

			long startTime = System.nanoTime();
			fordFulkersonMaxflow = fordFulkerson.getMaxFlow();
			long endTime = System.nanoTime();
			totalRunningTime += (endTime - startTime) / 1000000;
		}
		System.out.println("Ford Fulkerson took: " + totalRunningTime + " ms.");

		// Capacity Scaling
		double capacityScalingMaxFlow = 0.0;
		totalRunningTime = 0;
		for (int i = 0; i < 1; i++) {
			SimpleGraph graph = new SimpleGraph();
			GraphInput.LoadSimpleGraph(graph, theInputFileName);
			CapacityScalingFordFulkerson capacityScaling = new CapacityScalingFordFulkerson(graph);

			long startTime = System.nanoTime();
			capacityScalingMaxFlow = capacityScaling.getMaxFlow();
			long endTime = System.nanoTime();
			totalRunningTime += (endTime - startTime) / 1000000;
		}
		System.out.println("Scaling Ford Fulkerson took: " + totalRunningTime + " ms");

		// Pre-flow Push
		double preflowPushMaxFlow = 0.0;
		totalRunningTime = 0;
		for (int i = 0; i < NUMBER_OF_RUNNINGS; i++) {
			SimpleGraph graph = new SimpleGraph();
			GraphInput.LoadSimpleGraph(graph, theInputFileName);
			PreflowPush preflow = new PreflowPush(graph);

			long startTime = System.nanoTime();
			preflowPushMaxFlow = preflow.getMaxFlow();
			long endTime = System.nanoTime();
			totalRunningTime += (endTime - startTime) / 1000000;
		}
		System.out.println("Preflow Push took: " + totalRunningTime + " ms");

		System.out.println("");
		System.out.println("Ford-Fulkerson maximum flow: " + fordFulkersonMaxflow);
		System.out.println("Capacity Scaling Ford-Fulkerson maximum flow: " + capacityScalingMaxFlow);
		System.out.println("Preflow Push maximum flow: " + preflowPushMaxFlow);
	}
}
