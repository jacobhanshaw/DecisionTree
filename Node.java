import java.util.ArrayList;

public class Node {

	public Attribute attributeDecided;

	public boolean[] childIsResult;
	public String [] result;
	public ArrayList<Node> childNodes;

	public Node(int numChildren){
		if(numChildren > 0){
			childIsResult = new boolean[numChildren];
			result        = new String [numChildren];
		}
	}

}
