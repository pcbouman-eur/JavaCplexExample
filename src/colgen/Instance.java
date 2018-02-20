package colgen;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class models an instance of the cutting-stock problem.
 * The cutting stock problem consists of a number of orders
 * of certain sizes. The question is how we can most efficiently
 * cut the orders out of larger stock such that we need the least
 * amount of large stock.
 * 
 * For example, we have to produce metal bars. The orders indicate
 * how many bars of a certain length we must produce. The capacity
 * indicates how long a bar is that we can cut up into smallers bars
 * required for the order. The goal is to cut up as few large bars
 * as possible.
 * @author Paul Bouman
 *
 */
public class Instance
{
	private int capacity;
	private Map<Integer,Integer> orders;
	
	/**
	 * Creates a cutting stock instance based on the provided orders and a capacity
	 * @param orders the orders that need to be produced
	 * @param capacity the length or capacity of the base stock we must cut up
	 */
	public Instance(Map<Integer,Integer> orders, int capacity)
	{
		this.orders = new LinkedHashMap<>(orders);
		this.capacity = capacity;
	}
	
	/**
	 * Provides a list of sizes that occur in the orders in this instance.
	 * The size of the order is the amount of stock required to produce a
	 * single item of a particular size.
	 * @return a list with the sizes of the order.
	 */
	public Set<Integer> getSizes()
	{
		return Collections.unmodifiableSet(orders.keySet());
	}
	
	/**
	 * Provides how many copies of items of the provides size need to be
	 * produces
	 * @param size the size of the item to be produced
	 * @return how many copies of the item must be produced
	 */
	public int getAmount(int size)
	{
		return orders.getOrDefault(size, 0);
	}
	
	/**
	 * Provides the capacity of a unit of base stock that will be cut up
	 * in order to produce the items in the orders.
	 * @return the capacity of a unit of base stock
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * Method that can be used to generate a random cutting-stock instance
	 * @param seed a random seed
	 * @param orders the number unique sizes that occur in the orders
	 * @param maxStep the maximum step size of the sizes in the orders
	 * @param maxAmount the maximum amount that can be ordered of a single size
	 * @return a random instance based on the provided information
	 */
	public static Instance randomInstance(long seed, int orders, int maxStep, int maxAmount)
	{
		Random ran = new Random(seed);
		Map<Integer,Integer> map = new LinkedHashMap<>();
		int curLength = 0;
		for (int t=0; t < orders; t++)
		{
			curLength += 1 + ran.nextInt(maxStep);
			int amount = 1 + ran.nextInt(maxAmount);
			map.put(curLength, amount);
		}
		curLength += 1 + ran.nextInt(maxStep);
		return new Instance(map, curLength);
	}

	@Override
	public String toString() {
		return "Instance [capacity=" + capacity + ", orders=" + orders + "]";
	}
}
