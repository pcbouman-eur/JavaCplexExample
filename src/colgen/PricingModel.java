package colgen;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

/**
 * A model for the Pricing Problem of the Cutting Stock problem.
 * This basically solves a Knapsack Problem, where the profits of the
 * items are based on the reduced costs, and the capacity of the knapsack
 * is the capacity of a single piece of base stock.
 * @author Paul Bouman
 *
 */
public class PricingModel
{
	private Instance instance;
	private IloCplex model;
	
	private IloObjective obj;
	private Map<Integer,IloNumVar> vars;
	
	
	/**
	 * Initializes a model for the Pricing Problem of the Cutting Stock problem.
	 * @param instance the instance for which to initialize the pricing problem model.
	 * @throws IloException if something goes wrong with CPLEX
	 */
	public PricingModel(Instance instance) throws IloException
	{
		this.instance = instance;
		this.model = new IloCplex();
		this.vars = new LinkedHashMap<>();
		
		initVars();
		initCapacityConstraint();
		initObjective();
		model.setOut(null);
	}
	
	private void initVars() throws IloException
	{
		for (Integer size : instance.getSizes())
		{
			IloNumVar var = model.intVar(0, Integer.MAX_VALUE);
			vars.put(size, var);
		}
	}
	
	private void initObjective() throws IloException
	{
		obj = model.addMaximize();
	}
	
	private void initCapacityConstraint() throws IloException
	{
		IloNumExpr expr = model.constant(0);
		for (Entry<Integer,IloNumVar> e : vars.entrySet())
		{
			int size = e.getKey();
			IloNumVar var = e.getValue();
			IloNumExpr term = model.prod(size, var);
			expr = model.sum(expr, term);
		}
		model.addLe(expr, instance.getCapacity());
	}
	
	/**
	 * Update the objective based on a map of duals, where the current
	 * shadow costs of each item size is provided
	 * @param duals a dual or shadow cost for each item size in the instance
	 * @throws IloException if something goes wrong with CPLEX
	 */
	public void setDuals(Map<Integer,Double> duals) throws IloException
	{
		// Create a new expression for the objective
		IloNumExpr expr = model.constant(-1);
		for (Entry<Integer,Double> e : duals.entrySet())
		{
			int size = e.getKey();
			double dual = e.getValue();
			IloNumVar var = vars.get(size);
			IloNumExpr term = model.prod(var, dual);
			expr = model.sum(expr, term);
		}
		// Replace the objective with the new expression
		obj.setExpr(expr);
	}
	
	
	/**
	 * Clears the model from the memory. This is required because CPLEX uses
	 * JNI-allocated memory that is not automatically garbage collected.
	 * @throws IloException if something goes wrong with CPLEX
	 */
	public void cleanUp() throws IloException
	{
		model.clearModel();
		model.end();
	}
	
	/**
	 * Solve the model with the most recentely configured objective
	 * @throws IloException when something goes wrong with CPLEX
	 */
	public void solve() throws IloException
	{
		model.solve();
	}
	
	/**
	 * Provides the objective value (the reduced costs) of the last time
	 * the pricing problem was solved
	 * @return the objective value / reduced costs of the last solution
	 * @throws IloException in case something goes wrong with CPLEX
	 */
	public double getObjective() throws IloException
	{
		return model.getObjValue();
	}
	
	/**
	 * Obtain a cutting pattern based on the most recent solution found
	 * for this pricing problem. 
	 * @return a cutting pattern that minimizes the reduced costs
	 * @throws IloException in case something goes wrong with CPLEX
	 */
	public Pattern getPattern() throws IloException
	{
		Map<Integer,Integer> resultMap = new LinkedHashMap<>();
		for (Entry<Integer,IloNumVar> e : vars.entrySet())
		{
			int size = e.getKey();
			IloNumVar var = e.getValue();
			int cuts = (int)Math.round(model.getValue(var));
			resultMap.put(size, cuts);
		}
		return new Pattern(resultMap);
	}
}
