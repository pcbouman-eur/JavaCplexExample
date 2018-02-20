package colgen;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a solution to the Cutting Stock problem.
 * It contains a reference to the instance solved, cutting patterns
 * and how many times each cutting must be applied to a unit of stock
 * in order to produce the items in the demand of the instance.
 * @author Paul Bouman
 *
 */
public class Solution
{
	private Instance instance;
	private Map<Pattern,Integer> patterns;
	private int stockNeeded;
	
	/**
	 * Construct a solution based on a instance, and a map that specifies
	 * cutting patterns and how often each pattern must be applied to
	 * cover the demand.
	 * @param instance the instance for which this is a solution
	 * @param solution a map with patterns and times each pattern must be applied
	 */
	public Solution(Instance instance, Map<Pattern,Integer> solution)
	{
		this.instance = instance;
		this.patterns = new LinkedHashMap<>(solution);
		this.stockNeeded = patterns.values()
		                           .stream()
		                           .mapToInt(i -> i)
		                           .sum();
	}
	
	/**
	 * Provides a set of unique patterns that are utilized within this solution
	 * @return the set of utilized patterns
	 */
	public Set<Pattern> getPatterns()
	{
		return Collections.unmodifiableSet(patterns.keySet());
	}
	
	/**
	 * Provides how many times a given pattern is applied in order to cover demand
	 * @param p the pattern
	 * @return how often it must be applied in order to cover demand
	 */
	public int getCopies(Pattern p)
	{
		return patterns.getOrDefault(p, 0);
	}
	
	/**
	 * Gives the total number of base stock needed to cover all the demand.
	 * This is basically the sum over the number of times each pattern must be applied.
	 * @return the number of base stock required to cover the demand
	 */
	public int getStockNeeded()
	{
		return stockNeeded;
	}
	
	/**
	 * Gives the instance for which this solution was computed
	 * @return the instance for which this is a solution
	 */
	public Instance getInstance()
	{
		return instance;
	}
	
	@Override
	public String toString()
	{
		return patterns.toString();
	}
	
}
