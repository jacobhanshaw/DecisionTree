import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class DecisionTree {

	private static String[] classLabels;
	private static ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private static ArrayList<String> cases = new ArrayList<String>();
	
	private static double totalExamples;
	
	public  static Node root; 
	
	public static void main(String[] args) {
		
		int modeFlag = Integer.parseInt(args[0]);
		File trainingFile = new File(args[1]);
		File testingFile = new File(args[2]);
		
		parse(trainingFile, false);
		
		
		
		
		
	}
	
	private static void parse(File input, boolean testSet) {
		Scanner in;
		try {
			in = new Scanner(input);
			String line;
			while(in.hasNextLine())
			{
				line = in.nextLine();
			
				if (!testSet && line.charAt(0) == '%' && line.charAt(1) == '%') 
				{
					line = line.substring(2);
					String delims = "[\\s,]"; 
					classLabels =  line.split(delims);
				}
				
				else if (!testSet && line.charAt(0) == '#' && line.charAt(1) == '#')
				{
					line = line.substring(2);
					Attribute current = new Attribute();
					String delims = "[\\s,]"; 
					String[] tokens =  line.split(delims);
					current.name = tokens[0];
					current.possibleValues = new String[tokens.length-1];
					for(int i = 1; i < tokens.length-1; ++i)
					{
						current.possibleValues[i] = tokens[i];
					}
					attributes.add(current);
				}
				
				else if (line.charAt(0) != '/' && line.charAt(1) != '/') 
				{
					++totalExamples;
					line = line.replaceAll("[\\s,]", "");
					cases.add(line);
				}
			
			}
			in.close();
		}
		catch (StringIndexOutOfBoundsException e) {}
		catch (FileNotFoundException e) {}
		catch (NoSuchElementException e) {} 
		catch (NullPointerException e) {}
	}
	
	private static double calculateInformationGain(){
		return 0.0;
	}
	
	private static double calculateEntropy(){
		return 0.0;
	}
	
}
