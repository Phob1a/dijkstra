import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.omg.CORBA.Current;

/**
 * An Undirected Graph class implemented in an Adjacency List style
 * 
 * This is an adjacency list representation of a graph.
 * Nodes are integers starting at 0. The textbooks usually describe
 * an adjacency list as a list (or array), indexed by node numbers,
 * of connection lists. If we did that in Java we would continually
 * end up checking for our node numbers being within range. Further,
 * we normally want a whole object for the node rather than just an
 * integer, so a {@code Map} instead of an array is a good choice.
 * 
 * The graph is represented by a {@code Map} which maps the node number
 * to a LinkedList of Connection objects, which represent the nodes that
 * are directly connected to this node. 
 *
 */
public class Graph
{
	// The underlying map representing the graph
	private Map<Integer, List<Connection>> graph = new HashMap<>();


	
	/**
	 * For marking purposes
	 * 
	 * @return Your student id
	 */
	public static String getStudentID()
	{
		// Change this return value to return your student id number, e.g. 
		// return "1234567";
		return "1925829";
	}

	/**
	 * For marking purposes
	 * 
	 * @return Your name
	 */
	public static String getStudentName()
	{
		// Change this return value to return your name, e.g.
		// return "John Smith";
		return "Yiran Wen";
	}

	/**
	 * It is okay to create an empty graph, as we can add edges to it
	 */
	public Graph()
	{
	}

	/**
	 * Create a graph from an array of edges. Each edge is itself an array of 3 integers,
	 * the source node, the destination node and the distance between them.
	 * 
	 * Each edge in the input array will be added using {@code addEdge(node1,node2,distance)},
	 * implying that the reverse of each edge is also added and should NOT be explicitly in
	 * the input array.
	 * 
	 * @param connections the array of edges to add
	 * @throws GraphException if there are not exactly 3 integers in each edge array
	 */
	public Graph(int[][] connections)
		throws GraphException
	{
		for (int[] connection : connections)
		{
			if (connection.length != 3)
				throw new GraphException("Connections in Graphs must have 3 integers: node 1, node 2 and the distance between them. This connection did not: "
						+ Arrays.toString(connection));
			addEdge(connection[0], connection[1], connection[2]);
		}
	}

	/**
	 * Get an array of edges in the same form as the array of edges Graph constructor.
	 * One important difference is that this returns ALL the individual edges, thus
	 * one for the forward and one for the reverse direction of each true edge.
	 * @return the array of edges in the graph
	 */
	public int[][] getConnections()
	{
		ArrayList<int[]> connections = new ArrayList<>();
		for (Map.Entry<Integer, List<Connection>> entry : graph.entrySet())
		{
			int node = entry.getKey();
			for (Connection edge : entry.getValue())
				connections.add(new int[] { node, edge.getNode(), edge.getDistance() });
		}

		return connections.toArray(new int[0][0]);
	}

	/**
	 * Add an edge to the graph
	 * 
	 * This graph is UNDIRECTED, so any time we add an edge we must add the reverse edge as well.
	 * 
	 * @param node1 The source node
	 * @param node2 The destination node
	 * @param distance The distance or weight on the edge
	 * @throws GraphException if the distance is negative
	 */
	public void addEdge(int node1, int node2, int distance)
		throws GraphException
	{
		if (distance < 0)
			throw new GraphException(String.format("All distances must be greater than or equal to 0: attempted to add node %d to node %d with distance %d",
					node1, node2, distance));
		List<Connection> edgeList = graph.get(node1);
		if (edgeList == null)
		{
			edgeList = new LinkedList<>();
			graph.put(node1, edgeList);
		}
		edgeList.add(new Connection(node2, distance));

		//now add the reverse connection
		edgeList = graph.get(node2);
		if (edgeList == null)
		{
			edgeList = new LinkedList<>();
			graph.put(node2, edgeList);
		}
		edgeList.add(new Connection(node1, distance));
	}

	/**
	 * Contract a node that has exactly two connecting edges
	 * <p>
	 * This is the simplest case of contracting nodes. If node X is connected to node A with
	 * distance a and node B with distance b, then this should remove both the X-A and the X-B
	 * edge (essentially removing X from the graph), and add a new edge (A-B) with distance a+b
	 * </p>
	 * <p>
	 * Note that there may already be a pre-existing edge between A and B. This should not be changed
	 * or removed.
	 * </p>
	 * @param node the node to be contracted
	 * @throws GraphException if the node to be contracted does not exist in the graph or
	 * does not have precisely two edges
	 */
	 /* node������ �� throw exception
	  * ���������������2
	  * �׳��쳣
	  * ��õ�ǰ�ڵ����������
	 * �����ǰ��������Ϊ2
	 * ������ָ��������ڵ�����
	 * ɾ��ԭ����
	 * ɾ��ԭ�ڵ�
	 */
	public void contractNodeWithTwoEdges(int node)
		throws GraphException
	{
		// WRITE YOUR CODE HERE
		List<Connection> connections = graph.get(node);
		int[] neighboor = new int[graph.get(node).size()];
		int[] nodeDistance = new int[graph.get(node).size()];
		
		if(connections.size() !=2) {
			throw new GraphException();
		}
 		
		if(connections.size() == 2) {
			for(int j=0;j<2;j++) {
				neighboor[j] = connections.get(j).getNode();
				nodeDistance[j] = connections.get(j).getDistance();
			}
			addEdge(neighboor[0],neighboor[1],nodeDistance[0]+nodeDistance[1]);
		}
		for(int i=0;i<2;i++) {
			for(int z=0;z<graph.get(neighboor[i]).size();z++) {
				Connection connection = graph.get(neighboor[i]).get(z);
				if(connection.getNode() == node) {
					graph.get(neighboor[i]).remove(connection);
					break;
				}
			}
		}
		graph.remove(node);
	}

	/**
	 * Apply Dijkstra's algorithm to find the distance between 2 nodes in the graph
	 * <p>
	 * The version of the algorithm to implement is Version 1 from the Theory part
	 * handout of the module. Do not try to use Java's PriorityQueue to implement Version
	 * 2 as that cannot cope with the priority of an object in the queue changing while 
	 * the object is in the queue.
	 * </p>
	 * <p>
	 * Rather than using the arrays as described in the handout, I recommend you use 
	 * <ul>
	 * <li>
	 * {@code Integer.MAX_VALUE} for infinity 
	 * </li>
	 * <li>
	 * A {@code Map<Integer, Integer>} for the {@code D} array in the notes, to record the
	 * minimum distance found so far from the start node for each node in the graph
	 * </li>
	 * <li>
	 * A {@code Set<Integer>} called {@code nonTight} of nodes to represent the set of nodes
	 * for which you have not yet found tight distance values for. This is equivalent to the
	 * {@code tight} array of booleans in the handout but it is much easier and more efficient to
	 * iterate over the {@code nonTight} set than iterate over the nodes whose boolean values
	 * in the {@code tight} array are true 
	 * </li>
	 * </ul>
	 * </p>
	 * There is no need to have a {@code pred} variable as there is no need to calculate
	 * or return the route of the shortest path 
	 * <p>
	 * </p>
	 * @param node1 the start node in the pair between which the distance is to be found
	 * @param node2 the final node in the pair between which the distance is to be found
	 * @return the distance between the pair of nodes
	 * @throws GraphException if either of the nodes are not in the graph or there is no path
	 * between them
	 */
	/*���û�е�ǰ�ڵ� �������ڵ�û������·�����׳��쳣
	 * �õ���ʼ�ڵ����������
	 * ����һ�����ϼ�¼SĿ��ڵ㣨��ʼ��ֻ������ʼ�ڵ㣩
	 * ����һ�����ϼ�¼��ʼ�ڵ�Q�����нڵ������S�����·����
	 * �����ʼ�ڵ㵽��������ֱ�����ӵĽڵ�ľ���
	 * �����ҵ�������·������Q�У������������·����Ŀ��ڵ����S�С�
	 * �����ʼ�ڵ㵽��S�д���ĵ���ֱ�������ĵ�����о���
	 * �����ҵ�������·������Q�� ������ִ��ڳ�ʼ�ڵ㵽��S�еĽڵ��µ����·���������Q�����·����ֵ��������·�������·����Ŀ��ڵ����S�С�
	 * ����
	 *�����нڵ㶼����S��ʱ��������ɡ�
	 *�����¼��ϼ�¼������е�������·�������������·����
	 * */
	public int dijkstra(int node1, int node2)
		throws GraphException
	{
		// WRITE YOUR CODE HERE AND CHANGE THE RETURN STATEMENT
		if( !graph.containsKey(node1) ) {
			throw new GraphException();
		}else if( !graph.containsKey(node2) ) {
			throw new GraphException();
		}
		int n=graph.size();
		//Map<Integer,Integer> dis=new HashMap<>();
		boolean[] visited=new boolean[n+1]; //set all as false;
		int[] minDistance=new int[n+1];
		visited[node1]=true;
		List<Connection> tmp = graph.get(node1);
		List<Connection> connections=new ArrayList<>();
		for(Connection c:tmp){
			connections.add(c);
		}
		int[] neighboors = new int[graph.get(node1).size()];
		int[] nodeDistance = new int[graph.size()+1];
		for(int i=0;i<=graph.size();i++){
			nodeDistance[i]=-1;
		}
		//int[][] dist = new int[ graph.get(node2).get(0).getNode()][graph.get(node2).get(0).getNode()];
		for(int i = 0;i<connections.size();i++) {
			neighboors[i] = connections.get(i).getNode();
			nodeDistance[neighboors[i]] = connections.get(i).getDistance();
		}
		int cnt=1;
		while(cnt<=n){
			int newNode=-1;
			int minDis=-1;
			for(int i=0;i<connections.size();i++){
				int tmpNode=connections.get(i).getNode();
				if(visited[tmpNode]==true) continue;
				if(minDis==-1){
					minDis=nodeDistance[tmpNode];
					newNode=tmpNode;
				}
				else if(minDis>nodeDistance[tmpNode]){
					minDis=nodeDistance[tmpNode];
					newNode=tmpNode;
				}
			}
			if(newNode==-1) break;
			cnt++;
			List<Connection> newConnections=graph.get(newNode);
			visited[newNode]=true;
			for(int i=0;i<newConnections.size();i++){
				int nxtNode=newConnections.get(i).getNode();
				int nxtDistance=newConnections.get(i).getDistance();
				if(visited[nxtNode]==true) continue;
				if(nodeDistance[nxtNode]==-1){
					nodeDistance[nxtNode]=minDis+nxtDistance;
					connections.add(new Connection(nxtNode,nodeDistance[nxtNode]));
					//graph.put()
				}
				else if(nodeDistance[nxtNode]>minDis+nxtDistance){
					nodeDistance[nxtNode]=minDis+nxtDistance;
				}
			}
			//if(nodeDistance[node2]!=-1) break;
		}
		//int currentNeighboor = neighboors[0];
		int minDis=nodeDistance[node2];
		//System.out.println(minDis);
		return minDis;
	}


	public static void main(String[] args) {
	}
}
