
import java.util.Iterator;

/**
 * Preflow-Push Algorithm Implementation.
 * 
 * @author Asmita
 * @author Deepthi Warrier
 * @author Thuan Lam
 * @date 11-25-2019
 */
public final class PreflowPush {
	/** The given graph that needs to calculate the max flow value. */
	private SimpleGraph myGraph;
	/** The array that stores the excess flow. */
	private double[] myExcess;
	/** The index of the sink vertex in the list of vertices. */
	private int mySinkVertexIndex = -1;
	/** The name of the sink vertex. t by default */
	private String mySinkVertexName = "t";
	Double maxflow = (double) 0;

	/**
	 * The constructor of the Preflow Push class.
	 * 
	 * @param theGraph the input graph that need to calculate max flow.
	 */
	public PreflowPush(SimpleGraph theGraph) {
		this.myGraph = theGraph;
		addBackEdges();
		this.myExcess = new double[this.myGraph.numVertices()];
		this.mySinkVertexIndex = findVertexIndexByName(this.mySinkVertexName);
	}

	/**
	 * Get the index of the vertex by the vertex's name.
	 * 
	 * @param theVertexName a string that is the name of a vertex.
	 * @return the index of the vertex in the list of vertices. Return -1 if not
	 *         found.
	 */
	private int findVertexIndexByName(String theVertexName) {
		int index = 0;
		for (Iterator<?> ite = myGraph.vertices(); ite.hasNext(); index++) {
			Vertex vertex = (Vertex) ite.next();
			if (vertex.getName().toString().equals(theVertexName)) {
				return index;
			}
		}
		return -1;
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
		for (Iterator<?> ite = myGraph.incidentEdges(theStartVertex); ite.hasNext();) {
			Edge edge = (Edge) ite.next();
			if (edge.getSecondEndpoint() == theEndVertex)
				return myGraph.edgeList.indexOf(edge);
		}
		return -1;
	}

	/**
	 * Rename the given vertex by its index to the current flow value.
	 * 
	 * @param theVertexIndex the index of the given vertex.
	 */
	private void relabelVertex(int theVertexIndex) {
		Vertex vertex = (Vertex) myGraph.vertexList.get(theVertexIndex);
		vertex.setData((double) vertex.getData() + 1);
	}

	/**
	 * Add back edges of this graph.
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
	 * Recalculate excess flow and push the excess flow.
	 * 
	 * @param theExcess     the excess value.
	 * @param theCapability the capability value.
	 * @param theIndex1     the index 1.
	 * @param theIndex2     the index 2.
	 * @param theIndex3     the index 3.
	 * @param theIndex4     the index 4.
	 */
	public void pushFlow(double theExcess, double theCapability, int theIndex1, int theIndex2, int theIndex3, int theIndex4) {
		Double hold = Math.min(theExcess, theCapability);

		Edge edgeOne = (Edge) myGraph.edgeList.get(theIndex1);
		Edge edgeTwo = (Edge) myGraph.edgeList.get(theIndex2);

		edgeOne.setName((Double) edgeOne.getName() - hold);
		edgeTwo.setName((Double) edgeTwo.getName() + hold);

		myExcess[theIndex3] = myExcess[theIndex3] - hold;
		myExcess[theIndex4] = myExcess[theIndex4] + hold;
	}

	/**
	 * Set the flow in the network.
	 *
	 */
	private void setFlows() {
		Vertex start, end; // End points of edge, vertices.
		Edge edgeOne, edgeTwo; // edges incoming and outgoing
		Iterator<?> vertex; // Vertex iterator
		Iterator<?> edgeOneIt; // Edge iterator
		double hold; // holder variable for values of flow
		int index; // index for vertices.

		/*
		 * Name the edges with the data/their value.
		 */
		for (vertex = myGraph.vertices(); vertex.hasNext();) {
			start = (Vertex) vertex.next();
			for (edgeOneIt = myGraph.incidentEdges(start); edgeOneIt.hasNext();) {
				edgeOne = (Edge) edgeOneIt.next();
				edgeOne.setName(edgeOne.getData());
			}
		}

		/*
		 * Calculate and set the flow value in the graph.
		 */
		for (edgeOneIt = myGraph.incidentEdges((Vertex) myGraph.vertexList.getFirst()); edgeOneIt.hasNext();) {
			edgeOne = (Edge) edgeOneIt.next();
			hold = (Double) edgeOne.getName();

			edgeOne.setName((double) 0);
			end = edgeOne.getSecondEndpoint();

			index = findEdgeIndex(end, (Vertex) myGraph.vertexList.getFirst());
			edgeTwo = (Edge) myGraph.edgeList.get(index);

			edgeTwo.setName(hold);
		}
	}

	/**
	 * Method to calculate and set excess flow in the graph
	 */
	private void setSurplus() {
		double hold = new Double(0); // temporary holder variable
		Iterator<?> edgeIt; // edge iterator
		Edge edge; // edge variable
		Vertex vertex; // vertex variable

		/*
		 * Calculate excess flow for edges.
		 */
		for (edgeIt = myGraph.incidentEdges((Vertex) myGraph.vertexList.getFirst()); edgeIt.hasNext();) {
			edge = (Edge) edgeIt.next();
			vertex = edge.getSecondEndpoint();

			int index = myGraph.vertexList.indexOf(vertex);
			myExcess[index] = (Double) edge.getData();

			hold += (Double) edge.getData();
		}

		myExcess[0] = -hold;
	}

	/**
	 * Get the max flow of the graph.
	 * 
	 * @return the flow value.
	 */
	public double getMaxFlow() {
		Iterator<?> vertexOne, vertexTwo;
		Iterator<?> edgeOne, edgeTwo; 
		Vertex start, end, vertex;
		Edge edgeStart, edgeEnd;
		int indexOne = 0, indexTwo = -10, counter = 0; // index variables; counter
		boolean flag = false; // checker variable to find end

		//start by setting labels to zero to surplus values.
		for (vertexTwo = myGraph.vertices(); vertexTwo.hasNext();) {
			start = (Vertex) vertexTwo.next();
			start.setData((double) 0);
			myExcess[indexOne] = 0;
			indexOne++;
		}

		indexOne = 0;
		start = (Vertex) myGraph.vertexList.getFirst();
		start.setData((double) myGraph.numVertices());
		setFlows();
		setSurplus();

		//While the counter is not zero, calculate the excess flow.
		while (true) {
			//Till sink is not reached, update indices for calculation of excess flow.
			for (vertexOne = myGraph.vertices(); vertexOne.hasNext();) {
				vertex = (Vertex) vertexOne.next();
				if (myExcess[indexOne] > 0 && !(vertex.getName().equals("t"))) {
					start = vertex;
					indexTwo = indexOne;
					counter++;
					break;
				}
				indexOne++;
			}

			indexOne = 0;
			if (counter == 0)
				break;
			counter = 0;

			//Push the excess flow back in the network following all the edges.	
			for (edgeOne = myGraph.incidentEdges(start); edgeOne.hasNext();) {
				edgeStart = (Edge) edgeOne.next();
				end = edgeStart.getSecondEndpoint();
				if ((Double) edgeStart.getName() > 0 && (Double) start.getData() > (Double) end.getData()) {
					int indexThree = findEdgeIndex(end, start);
					pushFlow(myExcess[indexTwo], (Double) edgeStart.getName(), myGraph.edgeList.indexOf(edgeStart),
							indexThree, indexTwo, myGraph.vertexList.indexOf(end));
					flag = true;
					break;
				}
			}
			if (!flag)
				relabelVertex(myGraph.vertexList.indexOf(start));

			flag = false;
		}

		//Calculate maxflow by getting all edges values.
		for (edgeTwo = myGraph.incidentEdges((Vertex) myGraph.vertexList.get(mySinkVertexIndex)); edgeTwo.hasNext();) {
			edgeEnd = (Edge) edgeTwo.next();
			maxflow = maxflow + (Double) edgeEnd.getName();
		}
		return maxflow;
	}
}
