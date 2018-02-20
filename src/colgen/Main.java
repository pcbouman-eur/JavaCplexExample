package colgen;
import java.util.Random;

import ilog.concert.IloException;

/**
 * Main class that generates random instances and solves them using the Column Generation model
 * @author Paul Bouman
 *
 */
public class Main
{
	public static void main(String [] args) throws IloException
	{
		long time = System.currentTimeMillis();
		Random ran = new Random(54321);
		for (int i=0; i < 100; i++)
		{
			Instance instance = Instance.randomInstance(ran.nextLong(), 50, 13, 20);
			
			MasterModel mm = new MasterModel(instance);
			mm.solveInteger();
			Solution sol = mm.getSolution();
			double lb = mm.getLowerBound();
			
			System.out.println("Instance: "+instance);
			System.out.println("Solution: "+sol);
			System.out.println("Integer solution: "+sol.getStockNeeded());
			System.out.println("Lowerbound: "+lb);
			if (lb > sol.getStockNeeded()) {
				System.out.println("This is very strange...");
			}
			System.out.println();
			mm.cleanUp();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Runtime: "+time+"ms");
	}
}
