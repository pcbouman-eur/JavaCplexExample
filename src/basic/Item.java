package basic;
/**
 * Class that models a knapsack item with a profit and a weight
 * @author Paul Bouman
 */

public class Item
{
	private final int profit;
	private final int weight;
	
	/**
	 * Default constructor for a knapsack item
	 * @param profit the profit for this item (we want to maximize this)
	 * @param weight the weight of the item (this consumes capacity)
	 */
	public Item(int profit, int weight)
	{
		super();
		this.profit = profit;
		this.weight = weight;
	}

	/**
	 * The profit of the current item. This is what we want to maximize.
	 * @return the profit
	 */
	public int getProfit()
	{
		return profit;
	}

	/**
	 * The weight of the current item. This consume capacity.
	 * @return the weight
	 */
	public int getWeight()
	{
		return weight;
	}

	@Override
	public String toString()
	{
		return "Item [profit=" + profit + ", weight=" + weight + "]";
	}
	
	
	
}
