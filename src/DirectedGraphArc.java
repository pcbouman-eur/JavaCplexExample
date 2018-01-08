/**
 * Class that models arcs in the directed arcs. Stores both the end-points,
 * as well as the data associated with the arc.
 * @author Paul Bouman
 *
 * @param <V> the type of data associated with nodes in the graph
 * @param <A> the type of data associated with arcs in the graph
 */
public class DirectedGraphArc<V,A>
{
	private final V from;
	private final V to;
	private final A data;
	
	/**
	 * Construct an arc of the graph
	 * @param from the origin of this arc
	 * @param to the destination of this arc
	 * @param data the data asociated with this arc
	 */
	public DirectedGraphArc(V from, V to, A data)
	{
		this.from = from;
		this.to = to;
		this.data = data;
	}

	/**
	 * Used to retrieve the origin of this arc
	 * @return the origin of this arc
	 */
	public V getFrom()
	{
		return from;
	}

	/**
	 * Used to retrieve the destination of this arc
	 * @return the destination of this arc
	 */
	public V getTo()
	{
		return to;
	}

	/**
	 * Used to retrieve the data associated with this arc
	 * @return the data associated with this arc
	 */
	public A getData()
	{
		return data;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectedGraphArc<?,?> other = (DirectedGraphArc<?,?>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Arc [from=" + from + ", to=" + to + ", data=" + data + "]";
	}
	
}
