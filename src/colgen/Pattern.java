package colgen;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Models a cutting pattern for a base unit of stock.
 * It indicates how many pieces of a particular size
 * are to be cut from a piece of base stock.
 * @author Paul Bouman
 *
 */
public class Pattern
{
	
	// In this map, the keys represent the sizes of items to be cut,
	// and the values represent how many copies of such an items are
	// to be cut according to this pattern.
	private Map<Integer,Integer> cuts;
	// The total length of this cutting pattern. Should not exceed the
	// capacity of a base unit of stock
	private int size;
	
	
	/**
	 * Constructor for a cutting pattern. The list should contain
	 * the sizes of the items to be cut.
	 * @param cuts a list indicating the size of each item to be cut
	 *             from the base stock. Item sizes can be repeated
	 *             multiple times if the same size must be cut from
	 *             the base stock more than once.
	 */
	public Pattern(List<Integer> cuts)
	{
		this.cuts = new LinkedHashMap<>();
		cuts.forEach(i -> this.cuts.merge(i, 1, Integer::sum));
		this.size = cuts.stream()
				        .mapToInt(i -> i)
				        .sum();
	}
	
	/**
	 * Constructor for a cutting pattern. The map should contain keys
	 * indicating the sizes of items that must be cut from the base
	 * stock. The values in the map indicate how many copies of such
	 * an item must be cut from the base stock.
	 * @param cuts a map representing the sizes of items to be cut (keys)
	 *             and the number of copies of those items (values)
	 */
	public Pattern(Map<Integer,Integer> cuts)
	{
		this.cuts = new LinkedHashMap<>(cuts);
		this.size = cuts.entrySet()
						.stream()
						.mapToInt(e -> e.getKey()*e.getValue())
						.sum();
	}
	
	/**
	 * Check whether this cutting pattern contains an item of a
	 * particular size.
	 * @param size the size for which to check items are generated
	 *             in this cutting pattern
	 * @return true if the item size occurs in this cutting pattern
	 */
	public boolean containsSize(int size)
	{
		return cuts.containsKey(size);
	}
	
	/**
	 * Provides a set of unique sizes that occur in this cutting pattern.
	 * Note that a single cutting pattern may cut the same size multiple
	 * times from a base stock. In order to deduce how often an item size
	 * occurs in this cutting pattern, the getAmount() method must be used.
	 * @return a set with the item sizes that occur in this cutting pattern
	 */
	public Set<Integer> getSizes()
	{
		return Collections.unmodifiableSet(cuts.keySet());
	}
	
	/**
	 * Retrieves the number of copies of an item of a particular size
	 * are creating when this cutting pattern is applied to the base stock.
	 * @param size the size of the item for which to check the number of copies
	 * @return the number of copies produces when this cutting pattern is applied
	 */
	public int getAmount(int size)
	{
		return cuts.getOrDefault(size, 0);
	}
	
	/**
	 * The total size of this cutting pattern. If multiple items with the same
	 * size are created within this cutting pattern, the number of copies is
	 * multiplied with the size of the item. 
	 * @return the total size of this cutting pattern
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Represents this cutting pattern as a list of item sizes,
	 * where item sizes that are cut multiple times are repeated
	 * in the list.
	 * @return a list of item sizes that represents this cutting pattern
	 */
	public List<Integer> asList()
	{
		return cuts.entrySet()
				   .stream()
				   .flatMap(e -> Stream.generate(e::getKey).limit(e.getValue()))
				   .collect(Collectors.toList());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cuts == null) ? 0 : cuts.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pattern other = (Pattern) obj;
		if (cuts == null) {
			if (other.cuts != null)
				return false;
		} else if (!cuts.equals(other.cuts))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return asList().toString();
	}
	
}
