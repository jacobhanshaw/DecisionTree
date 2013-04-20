import java.util.*;

public class Node {

	public Attribute attributeDecided;

	public boolean[] childIsResult;
	public String [] result;
	public Dictionary<Integer, Node> childNodes;

	public Node(int numChildren){
		if(numChildren > 0){
			childNodes    = new Hashtable<Integer, Node>();
			childIsResult = new boolean[numChildren];
			result        = new String [numChildren];
		}
	}

}
