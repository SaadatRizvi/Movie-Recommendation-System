import java.io.*;
import java.util.ArrayList;

public class ConvertData {

	public static void main(String[] args) {
		
		System.out.println(args);
		
		Double averageRatting = 3.52986;
		
		String currentLine;
		ArrayList<String> inputLines = new ArrayList<String>();
		
		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(new File("u.data-2.csv")));
			
			while ((currentLine = inputReader.readLine()) != null) {
				System.out.println(currentLine);
				inputLines.add(currentLine);
			}
			
			inputReader.close();
			
			Double[][] finalResult = new Double[943][1682];
			
			for (int i=0; i<943; i++) {
				for (int j=0; j<1682; j++) {
					finalResult[i][j] = averageRatting;
				}
			}
			
			String[] tempArray;
			System.out.println();
			System.out.println("Converting");
			System.out.println();
			System.out.println();
			
			for (int i=0; i<inputLines.size(); i++) {
				tempArray = inputLines.get(i).split(",");
				finalResult[ Integer.parseInt(tempArray[0])-1 ][ Integer.parseInt(tempArray[1])-1 ] = (double) Integer.parseInt(tempArray[2]);
			}
			
			FileWriter fw1 = new FileWriter("u.data-raheel.csv", false);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			PrintWriter writer = new PrintWriter(bw1);
			
			for (int i=0; i<943; i++) {
				String tempString = Integer.toString(i+1);
				for (int j=0; j<1682; j++) {
					tempString += "," + finalResult[i][j]/5;
				}
				writer.println(tempString);
			}
			
			writer.close();
			
			System.out.println(args.length);
			System.out.println(args[0]);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
