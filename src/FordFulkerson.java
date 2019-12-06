import java.util.*;

/**
 * Implementation class for Ford-Fulkerson Algorithm 
 * @author Deepthi Warrier
 * @since 12-06-2019
 */
public class FordFulkerson {
	private SimpleGraph simpleGraph;
	private int sinkVertexIdx = -1;
	private String sinkVertexName = "t";

	/**
	 * The constructor of the FordFulkerson class 
	 * @param inputGraph: the input graph for which the max flow is calculated
	 */
	public FordFulkerson(SimpleGraph inputGraph) {
		simpleGraph = inputGraph;
		addBackwardEdges();
		sinkVertexIdx = findVertexIndexByName(this.sinkVertexName);
	}	
	
	/**
	 * Add backward edges to this graph.
	 */
	private void addBackwardEdges() {
		Vertex start, end;
		Edge e, e2;
		Iterator<Vertex> i;
		Iterator<Edge> j;
		Iterator<Edge> k;
		double be = 0;
		boolean backedge = false;
		for (i = simpleGraph.vertices(); i.hasNext();) {
			start = i.next();
			for (j = simpleGraph.incidentEdges(start); j.hasNext();) {
				e = j.next();
				end = e.getFirstEndpoint();
				end = e.getSecondEndpoint();
				for (k = simpleGraph.incidentEdges(end); k.hasNext();) {
					e2 = k.next();
					if (e2.getSecondEndpoint().getName().equals(start.getName())) {
						backedge = true;
					}
				}
				if (backedge == false) {
					simpleGraph.insertEdge(end, start, be, null);
				}
				backedge = false;
			}
		}
	}
	
	/**
	 * returns the index of the vertex by the vertex name.
	 * @param vertexName name of the vertex.
	 * @return the index of the vertex in the list of vertices.
	 *			Returns -1 if the vertex is not found.
	 */
	private int findVertexIndexByName(String vertexName) {
		int index = 0;
		for (Iterator<Vertex> ite = simpleGraph.vertices(); ite.hasNext(); index++) {
			Vertex vertex = ite.next();
			if (vertex.getName().toString().equals(vertexName)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the max flow for the input graph
	 * @return the flow value.
	 */
	public double getMaxFlow() {
		Double maxFlow = 0.0;
		while (getAugmentingPath()) {
			Double bottleNeckVal = updateGraph();
			maxFlow += bottleNeckVal;
		}
		return maxFlow;
	}	

	/**
	 * Checks if there is an augmenting path using Depth First Search Algorithm
	 * Calls dfsOnGraph method internally to identify the augmenting path.
	 * @return returns true if an augmenting path was found.
	 * 			Otherwise, false
	 */
	private boolean getAugmentingPath() {
		boolean[] visited = new boolean[simpleGraph.numVertices()];
		for (int j = 0; j < simpleGraph.numVertices(); j++) {
			visited[j] = false;
		}
		visited[0] = true;
		return dfsOnGraph(simpleGraph.vertexList.getFirst(), visited);
	}

	/**
	 * Get the index of the given edge from the list of edges 
	 * @param startVertex start vertex.
	 * @param endVertex   end vertex.
	 * @return the index of the edge that connects 2 given vertices if found.
	 *         Otherwise, return -1.
	 */
	private int findEdgeIndex(Vertex startVertex, Vertex endVertex) {
		for (Iterator<Edge> ite = simpleGraph.incidentEdges(startVertex); ite.hasNext();) {
			Edge edge = ite.next();
			if (edge.getSecondEndpoint() == endVertex)
				return simpleGraph.edgeList.indexOf(edge);
		}
		return -1;
	}

	/**
	 * Depth-First-Search algorithm to find a path from the given vertex to the sink.
	 * This function is called recursively.
	 * @param startVertex start Vertex.
	 * @param visitedList a list of boolean values that we use to track all
	 *                       vertices.
	 * @return true if we can find a path to the sink. 
	 * 			Otherwise, false.
	 */
	private boolean dfsOnGraph(Vertex startVertex, boolean[] visitedList) {
		if (!visitedList[sinkVertexIdx]) {
			for (Iterator<Edge> ite = simpleGraph.incidentEdges(startVertex); ite.hasNext();) {
				Edge edge = ite.next();
				Vertex vertex = edge.getSecondEndpoint();
				
				int index = simpleGraph.vertexList.indexOf(vertex);
				if (!visitedList[index] && (Double) edge.getData() > 0) {
					visitedList[index] = true;
					vertex.setData(edge.getFirstEndpoint());
					if (dfsOnGraph(vertex, visitedList))
						return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Update the graph based on the augmenting path.
	 * @return the bottle neck value.
	 */
	private double updateGraph() {
		Vertex previousVertex;
		Vertex currentVertex = simpleGraph.vertexList.get(sinkVertexIdx);
		Double bottleNeckVal = Double.MAX_VALUE;
		int edgeIndex;

		// find bottle neck of the augmenting path
		while (currentVertex != null) {
			previousVertex = (Vertex) currentVertex.getData();
			if (previousVertex != null) {
				edgeIndex = findEdgeIndex(previousVertex, currentVertex);
				Edge edge = simpleGraph.edgeList.get(edgeIndex);
				bottleNeckVal = Math.min((double) bottleNeckVal, (double) edge.getData());
			}
			currentVertex = previousVertex;
		}

		// update all edges of the augmenting path.
		currentVertex = simpleGraph.vertexList.get(sinkVertexIdx);
		while (currentVertex != null) {
			previousVertex = (Vertex) currentVertex.getData();
			if (previousVertex != null) {
				// update the forward flow
				edgeIndex = findEdgeIndex(previousVertex, currentVertex);
				Edge edge = simpleGraph.edgeList.get(edgeIndex);
				edge.setData((Double) edge.getData() - bottleNeckVal);
				
				// update the backward flow
				edgeIndex = findEdgeIndex(currentVertex, previousVertex);
				edge = simpleGraph.edgeList.get(edgeIndex);
				edge.setData((Double) edge.getData() + bottleNeckVal);
			}
			currentVertex = previousVertex;
		}
		return bottleNeckVal;
	}
	
	/**
	 * Sets for the sink Vertex name
	 * @param sinkName - sinkName
	 */
	public void setSinkVertexName(String sinkName) {
		this.sinkVertexName = sinkName;
	}
}