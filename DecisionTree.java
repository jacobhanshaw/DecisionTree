import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class DecisionTree {

	private static String[] classLabels;
	private static ArrayList<Attribute> attributesGlobal = new ArrayList<Attribute>();
	private static ArrayList<String[]> casesGlobal = new ArrayList<String[]>();

	private static int modelFlag;
	private static int numAttributeValues;

	public  static Node root; 

	public static void main(String[] args) {

		modelFlag = Integer.parseInt(args[0]);
		File trainingFile = new File(args[1]);
		File testingFile = new File(args[2]);

		parse(trainingFile);

		buildTree();

		if(modelFlag == 1) printTree();
		else if(modelFlag == 2) solve(testingFile);
	}

	private static void printTree() {
		System.out.print("Root ");
		String tabString = "";
		printNode(root, tabString);
	}

	private static void printNode(Node currentNode, String tabString) {
		System.out.println("{" + currentNode.attributeDecided.name + "?}");
		tabString += "\t";

		for(int i = 0; i < numAttributeValues; ++i){
			System.out.print(tabString + currentNode.attributeDecided.possibleValues[i] + " ");
			if(currentNode.childIsResult[i]) System.out.println("(" + currentNode.result[i] + ")");
			else printNode(currentNode.childNodes.get(i), tabString);
		}

	}

	private static void parse(File input) {
		Scanner in;
		try {
			in = new Scanner(input);
			String line;
			while(in.hasNextLine())
			{
				line = in.nextLine();

				if (line.charAt(0) == '%' && line.charAt(1) == '%') 
				{
					line = line.substring(2);
					classLabels =  line.split("[\\s,]");
				}

				else if (line.charAt(0) == '#' && line.charAt(1) == '#')
				{
					line = line.substring(2);
					Attribute current = new Attribute();
					String[] tokens =  line.split("[\\s,]");
					current.name = tokens[0];
					current.possibleValues = new String[tokens.length-1];
					if(numAttributeValues == 0) numAttributeValues = current.possibleValues.length;
					for(int i = 1; i < tokens.length; ++i)
					{
						current.possibleValues[i-1] = tokens[i];
					}
					current.originalIndex = attributesGlobal.size();
					attributesGlobal.add(current);
				}

				else if (line.charAt(0) != '/' && line.charAt(1) != '/') 
				{
					casesGlobal.add(line.split("[\\s,]"));
				}

			}
			in.close();
		}
		catch (StringIndexOutOfBoundsException e) {}
		catch (FileNotFoundException e) {}
		catch (NoSuchElementException e) {} 
		catch (NullPointerException e) {}
	}

	private static void solve(File input) {
		Scanner in;
		try {
			in = new Scanner(input);
			String line;
			while(in.hasNextLine())
			{
				line = in.nextLine();

				if ((line.charAt(0) != '%' && line.charAt(1) != '%') && (line.charAt(0) != '#' && line.charAt(1) != '#') && (line.charAt(0) != '/' && line.charAt(1) != '/'))
				{
					String[] attributes = line.split("[\\s,]");
					Node currentNode = root;
					while(!currentNode.childIsResult[getIndexOfValue(attributesGlobal.get(currentNode.attributeDecided.originalIndex), attributes[currentNode.attributeDecided.originalIndex])]){
						currentNode = currentNode.childNodes.get(getIndexOfValue(attributesGlobal.get(currentNode.attributeDecided.originalIndex), attributes[currentNode.attributeDecided.originalIndex]));
					}
					System.out.println(currentNode.result[getIndexOfValue(attributesGlobal.get(currentNode.attributeDecided.originalIndex), attributes[currentNode.attributeDecided.originalIndex])]);
				}

			}
			in.close();
		}
		catch (StringIndexOutOfBoundsException e) {}
		catch (FileNotFoundException e) {}
		catch (NoSuchElementException e) {} 
		catch (NullPointerException e) {}
	}

	private static int getIndexOfValue(Attribute attribute, String value){

		for(int i = 0; i < numAttributeValues; ++i)
		{
			if(attribute.possibleValues[i].equals(value)) return i;
		}

		return -1;
	}

	private static void buildTree()
	{
		double entropy = calculateEntropy(casesGlobal);
		ArrayList<String[]>cases = new ArrayList<String[]>(casesGlobal);
		addNextNodeToTree(null, entropy, cases, attributesGlobal, -1);
	}

	private static void addNextNodeToTree(Node parent, double prevEntropy, ArrayList<String[]> cases, ArrayList<Attribute> attributes, int numAttribute)
	{
		//find best attribute
		int    bestIndex  = -1;
		double minEntropy = Integer.MAX_VALUE;
		for(int j = 0; j < attributes.size(); ++j)
		{
			double entropy = calculateConditionalEntropy(cases, attributes.get(j), j);
			if(modelFlag == 0){
				double informationGain = prevEntropy - entropy;
				System.out.println(attributes.get(j).name + " " + informationGain);
			}
			if(entropy < minEntropy)
			{
				minEntropy = entropy;
				bestIndex = j;
			}
		}
		Node newNode = new Node(numAttributeValues);
		newNode.attributeDecided = attributes.get(bestIndex);
		if(parent == null)
			root = newNode;
		else 
			parent.childNodes.put(numAttribute, newNode);

		if(modelFlag != 0)
		{
			for(int k = 0; k < numAttributeValues; ++k)
			{
				if(!childIsLeaf(newNode, k, cases, attributes)){
					cases = arrayListWhereIndexIsValue(cases, bestIndex, attributes.get(bestIndex).possibleValues[k]);
					cases = arrayListWithoutAttribute(cases, bestIndex);
					attributes.remove(bestIndex);
					addNextNodeToTree(newNode, minEntropy, cases, attributes, k);
				}
			}
		}

	}

	private static boolean childIsLeaf(Node parent, int attributeIndex, ArrayList<String[]> cases, ArrayList<Attribute> attributes){
		//as far as can go
		if(attributes.size()-1 <= 0){
			parent.childIsResult[attributeIndex] = true;
			parent.result[attributeIndex] = classLabels[getMajority(cases)];

			return true;
		}
		//all same
		boolean[] allNotSame = new boolean[classLabels.length];
		for(int j = 0; j < cases.size(); ++j)
		{
			for(int k = 0; k < classLabels.length; ++k)
			{
				String[] example = cases.get(j);
				if(!(example[example.length-1].equals(classLabels[k]))) allNotSame[k] = true;
			}
		}

		for(int j = 0; j < classLabels.length; ++j)
		{
			if(!allNotSame[j]){
				parent.childIsResult[attributeIndex] = true;
				parent.result[attributeIndex] = classLabels[j];

				return true;			
			}
		}

		return false;

	}

	private static int getMajority(ArrayList<String[]> cases)
	{
		//get majority
		double a[] = new double[classLabels.length];
		int maxIndex = 0;
		for(int i = 0; i < cases.size(); ++i)
		{
			for(int j = 0; j < classLabels.length; ++j)
			{
				String[] example = cases.get(i);
				if(example[example.length-1].equals(classLabels[j])){ 
					a[j]++;
					if(a[j] > maxIndex) maxIndex = j;
				}
			}
		}
		return maxIndex;
	}

	private static double calculateEntropy(ArrayList<String[]> cases)
	{
		//count of each class
		double a[] = new double[classLabels.length];
		double total = cases.size();
		for(int i = 0; i < cases.size(); ++i)
		{
			for(int j = 0; j < classLabels.length; ++j)
			{
				String[] example = cases.get(i);
				if(example[example.length-1].equals(classLabels[j])) a[j]++;
			}
		}

		double result = 0.0;
		for(int i = 0; i < classLabels.length; ++i)
		{
			double prob = a[i]/total;
			if(prob != 0 && a[i] != 0 && total != 0) result -= prob * (Math.log(prob)/Math.log(classLabels.length));
		}

		return result;
	}

	private static double calculateConditionalEntropy(ArrayList<String[]> cases, Attribute attrib, int index)
	{

		double countWhereAttribIsValue[]     = new double[numAttributeValues];
		double countForAttribThatIsClass[][] = new double[numAttributeValues][classLabels.length];
		double totalForAttribThatIsClass[]   = new double[numAttributeValues];

		for(int i = 0; i < cases.size(); ++i)
		{

			for(int j = 0; j < numAttributeValues; ++j)
			{

				if(cases.get(i)[index].equals(attrib.possibleValues[j])){ 
					countWhereAttribIsValue[j]++;
					for(int k = 0; k < classLabels.length; ++k)
					{
						String[] example = cases.get(i);
						if(example[example.length-1].equals(classLabels[k])) 
						{
							totalForAttribThatIsClass[j]++;
							countForAttribThatIsClass[j][k]++;
						}
					}
				}

			}

		}

		double entropy[] = new double[numAttributeValues];
		double result = 0.0;
		for(int i = 0; i < numAttributeValues; ++i)
		{
			for(int j = 0; j < classLabels.length; ++j)
			{
				double prob = countForAttribThatIsClass[i][j]/totalForAttribThatIsClass[i];
				if(prob != 0 && countForAttribThatIsClass[i][j] != 0 && totalForAttribThatIsClass[i] != 0) entropy[i] -= prob * (Math.log(prob)/Math.log(classLabels.length));
			}
			double count = countWhereAttribIsValue[i]/(double)cases.size();
			result += count * entropy[i];
		}

		return result;
	}

	private static ArrayList<String[]> arrayListWhereIndexIsValue(ArrayList<String[]> original, int index, String value)
	{
		for(int i = 0; i < original.size(); ++i)
		{
			if(!(original.get(i)[index].equals(value))) original.remove(i);

		}
		return original;
	}

	private static ArrayList<String[]> arrayListWithoutAttribute(ArrayList<String[]> original, int index)
	{
		for(int i = 0; i < original.size(); ++i)
		{
			original.set(i, removeElements(original.get(i), index));
		}
		return original;
	}

	public static String[] removeElements(String[] input, int index) {
		ArrayList<String> result = new ArrayList<String>();
		int size = input.length;
		for(int i = 0; i < size; ++i){
			if(i != index) result.add(input[i]);
		}

		String[] newArr = new String[input.length-1];
		result.toArray(newArr);

		return newArr;
	}

}
