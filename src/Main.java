import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ilog.concert.IloException;

/**
 * Main class for the Java/CPLEX example
 * @author Paul Bouman
 *
 */

public class Main
{
	public static void main(String [] args)
	{
		try
		{
			// Read the graph from the file
			DirectedGraph<Item,String> instance = read(new File("instance.txt"));
			System.out.println("The following instance was read:");
			System.out.println(instance);
			
			// Create a model instance based on the directed graph
			Model model = new Model(instance, 9);
			// Solve the model
			model.solve();
			// Print the solution
			System.out.println(model.getSolution());
			System.out.println("Feasible? "+model.isFeasible());
			
			// Take an item and manipulate it in the model
			Item i = instance.getNodes().get(0);
			model.setItem(i, false);
			model.solve();
			System.out.println(model.getSolution());
			
			// Undo the manipulation of the model
			model.setItem(i, true);
			model.solve();
			System.out.println(model.getSolution());
			
			model.cleanup();
		}
		catch (IloException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a Precedence Constrained Knapsack Problem as a directed graph from a file
	 * @param f the file to read
	 * @return a directed graph containing the Precedence Constrained Knapsack Problem
	 * @throws FileNotFoundException if the file to read does not exist
	 */
	public static DirectedGraph<Item,String> read(File f) throws FileNotFoundException
	{
		try (Scanner scan = new Scanner(f))
		{
			DirectedGraph<Item,String> result = new DirectedGraph<>();
			List<Item> items = new ArrayList<>();
			
			// Reading the items
			int numItems = scan.nextInt();
			for (int i=0; i < numItems; i++)
			{
				int profit = scan.nextInt();
				int weight = scan.nextInt();
				Item item = new Item(profit,weight);
				items.add(item);
				result.addNode(item);
			}
			
			// Reading the arcs / precedence constraints
			int numArcs = scan.nextInt();
			for (int i=0; i < numArcs; i++)
			{
				int fromIndex = scan.nextInt();
				int toIndex = scan.nextInt();
				String reason = scan.next();
				
				Item from = items.get(fromIndex);
				Item to = items.get(toIndex);
				result.addArc(from, to, reason);
			}
			
			return result;
		}
	}
	
}
