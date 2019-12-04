import java.util.*;

/**
 * Capacity Scaling Ford-Fulkerson algorithm Implementation.
 * 
 * @author Asmita
 * @date 06-12-2019
 */
public class CapacityScalingFordFulkerson {
	private SimpleGraph myGraph;
	private int mySinkVertexIndex = -1;
	private String mySourceVertexName = "s";
	private String mySinkVertexName = "t";
	
	/**
	 * 
	 * @param simpleGraph: the input graph.
	 * @throws Exception if the given graph has no Sink vertex 't'.
	 */
	public CapacityScalingFordFulkerson(SimpleGraph simpleGraph) {
		myGraph = simpleGraph;
		addBackEdges();
		mySinkVertexIndex = findVertexIndexByName(this.mySinkVertexName);
		if (mySinkVertexIndex < 0) {
			System.out.println("Cannot find the Sink vertex 't'");
		}
	}

	/**
	 * Get the max flow of the graph.
	 * 
	 * @return the flow value.
	 */
	public double getMaxFlow() {
		double maxFlow = 0.0;
		int deltaValue = getDelta();
		
		while (deltaValue > 0) {
			while (findAugmentingPath(deltaValue)) {
				double bottleNeckValue = updateGraph();
				 // set bottleneckEdge in find path
				maxFlow += bottleNeckValue;
			}

			deltaValue = (deltaValue >> 1);
		}
		return maxFlow;
	}

	/**
	 * Add reverse edges to the graph.
	 */
	private void addBackEdges() {
		Vertex start, end;
		Edge e, e2;
		Iterator<Vertex> i;
		Iterator<Edge> j;
		Iterator<Edge> k;
		double be = 0;
		boolean backedge = false;
		for (i = myGraph.vertices(); i.hasNext();) {
			start = i.next();
			for (j = myGraph.incidentEdges(start); j.hasNext();) {
				e = j.next();
				end = e.getFirstEndpoint();
				end = e.getSecondEndpoint();
				for (k = myGraph.incidentEdges(end); k.hasNext();) {
					e2 = k.next();
					if (e2.getSecondEndpoint().getName().equals(start.getName())) {

						backedge = true;
					}
				}
				if (backedge == false) {
					myGraph.insertEdge(end, start, be, null);

				}
				backedge = false;
			}
		}
	}

	/**
	 * 
	 * @param theVertexName
	 * @return the index of the vertex in the list of vertices. Return -1 if not found.
	 */
	private int findVertexIndexByName(String theVertexName) {
		int index = 0;
		for (Iterator<Vertex> ite = myGraph.vertices(); ite.hasNext(); index++) {
			Vertex vertex = ite.next();
			if (vertex.getName().toString().equals(theVertexName)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Try to find the augmenting path by using Depth-First-Search algorithm.
	 * 
	 * @return true if an augmenting path was found. Otherwise, false
	 */
	private boolean findAugmentingPath(double theDeltaValue) {
		boolean[] visited = new boolean[myGraph.numVertices()];
		for (int j = 0; j < myGraph.numVertices(); j++)
			visited[j] = false;

		visited[0] = true;
		return dfsOnGraph(myGraph.vertexList.getFirst(), visited, theDeltaValue);
	}

	/**
	 * Get the index of the given edge from the list of edges.
	 * 
	 * @param theStartVertex the start vertex.
	 * @param theEndVertex   the end vertex.
	 * @return the index of the edge that connects 2 given vertices if found.
	 *         Otherwise, return -1.
	 */
	private int findEdgeIndex(Vertex theStartVertex, Vertex theEndVertex) {
		for (Iterator<Edge> ite = myGraph.incidentEdges(theStartVertex); ite.hasNext();) {
			Edge edge = ite.next();
			if (edge.getSecondEndpoint() == theEndVertex)
				return myGraph.edgeList.indexOf(edge);
		}
		return -1;
	}

	/**
	 * Get the delta value based on all edges' capacities.
	 * 
	 * @return an integer as the delta value.
	 */
	private int getDelta() {
		double maxCapacity = Double.MIN_VALUE;
		int sourceVertexIndex = findVertexIndexByName(this.mySourceVertexName);
		Vertex sourceVertex = this.myGraph.vertexList.get(sourceVertexIndex);

		for (Iterator<Edge> edges = this.myGraph.incidentEdges(sourceVertex); edges.hasNext();) {
			Edge edge = edges.next();
			maxCapacity = Math.max(maxCapacity, (double) edge.getData());			
		}
		return 1 << (int) (Math.log(maxCapacity) / Math.log(2));
	}

	/**
	 * A recursive function that uses the Depth-First-Search algorithm with the
	 * delta value to find a path from the given vertex to the sink.
	 * 
	 * @param theStartVertex a vertex that we start from.
	 * @param theVisitedList a list of boolean values that we use to track all
	 *                       vertices.
	 * @return true if we can find a path to the sink. Otherwise, false.
	 */
	private boolean dfsOnGraph(Vertex theStartVertex, boolean[] theVisitedList, double theDeltaValue) {
		if (!theVisitedList[mySinkVertexIndex]) {
			for (Iterator<Edge> ite = myGraph.incidentEdges(theStartVertex); ite.hasNext();) {
				Edge edge = ite.next();
				Vertex vertex = edge.getSecondEndpoint();

				int index = myGraph.vertexList.indexOf(vertex);
				if (!theVisitedList[index] && (Double) edge.getData() >= theDeltaValue) {
					theVisitedList[index] = true;
					vertex.setData(edge.getFirstEndpoint());
					if (dfsOnGraph(vertex, theVisitedList, theDeltaValue))
						return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Update the graph based on the augmenting path. The augmenting path
	 * information is in the sink vertex.
	 * 
	 * @return the bottle neck value.
	 */
	private double updateGraph() {
		Vertex previousVertex;
		Vertex currentVertex = myGraph.vertexList.get(mySinkVertexIndex);
		Double bottleNeckValue = Double.MAX_VALUE;
		int edgeIndex;

		// find bottle neck of the augmenting path
		while (currentVertex != null) {
			previousVertex = (Vertex) currentVertex.getData();
			if (previousVertex != null) {
				edgeIndex = findEdgeIndex(previousVertex, currentVertex);
				Edge edge = myGraph.edgeList.get(edgeIndex);
				bottleNeckValue = Math.min((double) bottleNeckValue, (double) edge.getData());
			}
			currentVertex = previousVertex;
		}

		// update all edges of the augmenting path.
		currentVertex = myGraph.vertexList.get(mySinkVertexIndex);
		while (currentVertex != null) {
			previousVertex = (Vertex) currentVertex.getData();
			if (previousVertex != null) {
				// update the forward flow
				edgeIndex = findEdgeIndex(previousVertex, currentVertex);
				Edge edge = myGraph.edgeList.get(edgeIndex);
				edge.setData((Double) edge.getData() - bottleNeckValue);

				// update the backward flow
				edgeIndex = findEdgeIndex(currentVertex, previousVertex);
				edge = myGraph.edgeList.get(edgeIndex);
				edge.setData((Double) edge.getData() + bottleNeckValue);
			}
			currentVertex = previousVertex;
		}
		return bottleNeckValue;
	}

	/**
	 * @param mySourceVertexName
	 */
	public void setSourceVertexName(String mySourceVertexName) {
		this.mySourceVertexName = mySourceVertexName;
	}
}