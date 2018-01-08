import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * Model class that converts a directed graph representing a
 * precedence constrained knapsack problem into a mathematical programming
 * model managed by CPLEX.
 * @author Paul Bouman
 */

public class Model
{
	private DirectedGraph<Item,String> instance;
	private int capacity;
	
	private IloCplex cplex;
	
	private Map<Item,IloNumVar> varMap;
	
	/**
	 * Constructor that takes a directed graph with the items and precedence constraints
	 * @param instance a directed graph with items
	 * @param capacity the capacity of the knapsack
	 * @throws IloException if something goes wrong with CPLEX
	 */
	
	public Model(DirectedGraph<Item,String> instance, int capacity) throws IloException
	{
		// Initialize the instance variables
		this.instance = instance;
		this.capacity = capacity;
		this.cplex = new IloCplex();
		
		// Create a map to link items to variables
		this.varMap = new HashMap<>();
		
		// Initialize the model. It is important to initialize the variables first!
		addVariables();
		addKnapsackConstraint();
		addPrecedenceConstraints();
		addObjective();
		
		// Optionally: export the model to a file, so we can check the mathematical
		// program generated by CPLEX
		cplex.exportModel("model.lp");
		// Optionally: suppress the output of CPLEX
		cplex.setOut(null);
	}
	
	/**
	 * Disable an item in the model (fix it to 0) or enable it (either 0 or 1)
	 * @param i the item to manipulate
	 * @param enabled whether to enable it (0 or 1) or disable it (always 0)
	 * @throws IloException if something is wrong with CPLEX
	 */
	public void setItem(Item i, boolean enabled) throws IloException
	{
		IloNumVar var = varMap.get(i);
		if (enabled) {
			// If it is enabled, the lower bound is 0 and the upper bound is 1
			var.setLB(0);
			var.setUB(1);
		}
		else {
			// If it is disabled, both lower and upper bound are set to 0
			var.setLB(0);
			var.setUB(0);
		}
	}
	
	/**
	 * Solve the Mathematical Programming Model
	 * @throws IloException if something is wrong with CPLEX
	 */
	public void solve() throws IloException
	{
		cplex.solve();
	}
	
	/**
	 * Checks whether the current solution to the model is feasible
	 * @return the feasibility of the model
	 * @throws IloException if something is wrong with CPLEX
	 */
	public boolean isFeasible() throws IloException
	{
		return cplex.isPrimalFeasible();
	}
	
	/**
	 * Create a list of the items for which the decision variables
	 * are one in the current solution of the mathematical program.
	 * @return a list of selected items
	 * @throws IloException if something is wrong with CPLEX
	 */
	public List<Item> getSolution() throws IloException
	{
		List<Item> result = new ArrayList<>();
		for (Item i : instance.getNodes())
		{
			IloNumVar var = varMap.get(i);
			double value = cplex.getValue(var);
			if (value >= 0.5)
			{
				result.add(i);
			}
		}
		return result;
	}
	
	/**
	 * Cleans up the CPLEX model in order to free up some memory.
	 * This is important if you create many models, as memory used
	 * by CPLEX is not freed up automatically by the JVM. 
	 * @throws IloException if something goes wrong with CPLEX
	 */
	public void cleanup() throws IloException
	{
		cplex.clearModel();
		cplex.end();
	}

	private void addObjective() throws IloException
	{
		// Initialize the objective sum to 0
		IloNumExpr obj = cplex.constant(0);
		for (Item i : instance.getNodes())
		{
			IloNumVar var = varMap.get(i);
			// Take the product of the decision variable and the profit of the item
			IloNumExpr term = cplex.prod(var, i.getProfit());
			// Add the term to the current sum
			obj = cplex.sum(obj, term);
		}
		// Add the obj expression as a maximization objective
		cplex.addMaximize(obj);
	}

	private void addPrecedenceConstraints() throws IloException
	{
		for (DirectedGraphArc<Item,String> arc : instance.getArcs())
		{
			IloNumVar from = varMap.get(arc.getFrom());
			IloNumVar to = varMap.get(arc.getTo());
			cplex.addLe(from, to);
		}
	}

	private void addKnapsackConstraint() throws IloException
	{
		// Initialize the left-hand side of our constraint to 0
		IloNumExpr lhs = cplex.constant(0);
		for (Item i : instance.getNodes())
		{
			IloNumVar var = varMap.get(i);
			// Take the product of the decision variable and the item weight
			IloNumExpr term = cplex.prod(i.getWeight(), var);
			// Add the term to the left hand side summation
			lhs = cplex.sum(lhs, term);
		}
		// Add the constraint lhs <= capacity to the model
		cplex.addLe(lhs, capacity);
	}

	private void addVariables() throws IloException
	{
		for (Item i : instance.getNodes())
		{
			IloNumVar var = cplex.boolVar();
			varMap.put(i, var);
		}
	}
	
	
}
