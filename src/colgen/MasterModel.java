package colgen;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ilog.concert.IloColumn;
import ilog.concert.IloConversion;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.DoubleParam;

/**
 * Class that implements the Master Problem of the Cutting Stock problem
 * 
 * This class performs column generation on the LP-relaxation of the master problem.
 * It is also possible to solve the current version of the model, including the
 * generated columns, as an Integer problem. 
 * 
 * Note that this class will not necesarilly find the optimal solution to the problem,
 * as a full-blown branch-and-price approach is required to do so. However, the lower-bound
 * provided by the LP-relaxation can be used to determine the gap between the solution found
 * and the optimal solution.
 * 
 * @author Paul Bouman
 *
 */
public class MasterModel
{
	private double threshold = 1e-14;
	
	private Instance instance;
	private IloCplex model;
	
	private IloObjective obj;
	private Map<Pattern,IloNumVar> vars;
	private Map<Integer,IloRange> constraints;
	
	private PricingModel pricing;
	
	private double lowerbound = 0;
	private Solution solution;
	
	/**
	 * Constructor that builds the Master Problem of a Column Generation model
	 * for the Cutting Stock problem
	 * @param instance the instance for which to build a column generation model
	 * @throws IloException if something is wrong with CPLEX
	 */
	public MasterModel(Instance instance) throws IloException
	{
		this.instance = instance;
		this.model = new IloCplex();
		
		this.vars = new LinkedHashMap<>();
		this.constraints = new LinkedHashMap<>();
		this.pricing = new PricingModel(instance);
		
		initPatternsAndVars();
		initConstraints();
		initObjective();
		model.setOut(null);
	}
	
	/**
	 * This method initializes some basic patterns, where each item size is
	 * cut multiple times from a single piece of stock.
	 * @throws IloException
	 */
	private void initPatternsAndVars() throws IloException
	{
		for (Integer size : instance.getSizes()) {
			Map<Integer,Integer> cutMap = new LinkedHashMap<>();
			cutMap.put(size, instance.getCapacity()/size);
			Pattern pattern = new Pattern(cutMap);
			IloNumVar var = model.numVar(0, Double.POSITIVE_INFINITY);
			vars.put(pattern, var);
		}
	}
	
	/**
	 * This method initializes the constraints that for each size of items
	 * that need to be produced, enough items are cut.
	 * @throws IloException
	 */
	private void initConstraints() throws IloException
	{
		for (Integer size : instance.getSizes())
		{
			IloNumExpr expr = model.constant(0);
			for (Entry<Pattern,IloNumVar> e : vars.entrySet())
			{
				Pattern pattern = e.getKey();
				if (pattern.containsSize(size))
				{
					IloNumVar var = e.getValue();
					IloNumExpr term = model.prod(var, pattern.getAmount(size));
					expr = model.sum(expr, term);
				}
			}
			IloRange constraint = model.addGe(expr, instance.getAmount(size));
			constraints.put(size, constraint);
		}
	}
	
	/**
	 * Initializes the minimization objective. Each time one of the patterns
	 * is used, one unit of base stock is utilized, so the objective is to
	 * minimize the sum of the decision variables.
	 * @throws IloException
	 */
	private void initObjective() throws IloException
	{
		IloNumExpr expr = model.constant(0);
		for (IloNumVar var : vars.values())
		{
			expr = model.sum(expr,var);
		}
		obj = model.addMinimize(expr);
	}
	
	/**
	 * This method can be used to add a new pattern to the current model.
	 * The steps perform are to introduce a new decision variable,
	 * add this decision variable to the relevant constraints and add
	 * it to the objective
	 * @param p the pattern to introduce as a new column in the model
	 * @throws IloException if something goes wrong with CPLEX
	 */
	private void addPattern(Pattern p) throws IloException
	{
		if (vars.containsKey(p)) {
			// This should never happen in a correct column generation procedure
			throw new IllegalArgumentException("This pattern was already added to the model");
		}
		
		// We build a column object by first defining the contribution to the objective as 1
		IloColumn column = model.column(obj, 1);
		for (Entry<Integer,IloRange> e : constraints.entrySet())
		{
			int size = e.getKey();
			if (p.containsSize(size))
			{
				// Retrieve the constraint associated with this 
				IloRange rng = e.getValue();
				// We define a partial column based on the constraint and the contribution to the constraint
				IloColumn coefficient = model.column(rng, p.getAmount(size));
				// The column is expanded with the coefficient just created
				column = column.and(coefficient);
			}
		}
	
		// We introduce a new decision variable for this column
		IloNumVar var = model.numVar(column, 0, Double.POSITIVE_INFINITY);
		// Finally, we store the variable in our map
		vars.put(p, var);
	}
	
	/**
	 * Generates a map of duals, with a dual for each size of item that can be generated
	 * @return a map with duals for the different items
	 * @throws IloException if something goes wrong with CPLEX
	 */
	private Map<Integer,Double> getDuals() throws IloException
	{
		Map<Integer,Double> map = new LinkedHashMap<>();
		for (Entry<Integer,IloRange> e : constraints.entrySet())
		{
			// This is the size of the items related to this particular constraint
			int size = e.getKey();
			// This is the objective referred to the constraint itself
			IloRange constraint = e.getValue();
			// Retrieve the dual and store it in the map of duals
			double dual = model.getDual(constraint);
			map.put(size, dual);
		}
		return map;
	}
	
	/**
	 * This method generates a single column and if it has positive reduced costs
	 * adds it to the model
	 * @return whether a column with positive reduced costs was found
	 * @throws IloException when something goes wrong with CPLEX
	 */
	private boolean generateColumn() throws IloException
	{
		Map<Integer, Double> duals = getDuals();
		pricing.setDuals(duals);
		pricing.solve();
		Pattern pattern = pricing.getPattern();
		if (pricing.getObjective() > threshold && !vars.containsKey(pattern))
		{
			addPattern(pricing.getPattern());
			return true;
		}
		return false;
	}
	
	/**
	 * This method solves the Master Problem using Column Generation. This provides
	 * the optimal LP-relaxation of the master model
	 * @throws IloException when something goes wrong with CPLEX
	 */
	public void solveRelaxation() throws IloException
	{
		do
		{
			// Solve the LP-relaxation
			model.solve();
		} while (generateColumn()); // As long as new columns with positive reduced costs are generated
	}
	
	/**
	 * This method solves the IP version of the Master Problem based on the current columns in the model.
	 * This method first executed the column generation procedure on the relaxed version of the problem.
	 * @throws IloException
	 */
	public void solveInteger() throws IloException
	{
		// Store the relaxed solution as a lowerbound
		// Note that we know the integer-objective must be integer,
		// so we can take the ceil of the LP-relaxation for this
		// particular problem. We subtract the current numeric precision to avoid
		// situations where the lower bound is greater than the optimal solution.
		solveRelaxation();
		lowerbound = Math.ceil(model.getObjValue() - model.getParam(DoubleParam.EpOpt));
		
		// Convert the model to an integer model and solve
		List<IloConversion> conversions = new ArrayList<>();
		for (IloNumVar var : vars.values())
		{
			IloConversion conv = model.conversion(var, IloNumVarType.Int);
			model.add(conv);
			conversions.add(conv);
		}
		model.solve();
		
		// Construct a solution based on the IP-solution
		Map<Pattern,Integer> result = new LinkedHashMap<>();
		for (Entry<Pattern,IloNumVar> e : vars.entrySet())
		{
			Pattern pattern = e.getKey();
			IloNumVar var = e.getValue();
			int copies = (int)Math.round(model.getValue(var));
			if (copies > 0)
			{
				result.put(pattern, copies);
			}
		}
		solution = new Solution(instance, result);
		
		// Undo the integer conversion
		for (IloConversion conv : conversions)
		{
			model.remove(conv);
		}
	}
	
	/**
	 * Clears the CPLEX model from memory. Recommended if you do not need the model any more,
	 * as the JNI-based memory used by the model is not automatically collected by the garbage collector.
	 * @throws IloException
	 */
	public void cleanUp() throws IloException
	{
		model.clearModel();
		model.end();
		pricing.cleanUp();
	}
	
	/**
	 * Retrieve the last solution found when solveInteger() was called
	 * @return the best solution found during the most recent call to solveInteger()
	 */
	public Solution getSolution()
	{
		return solution;
	}
	
	/**
	 * Gives the lower bound to the problem based on the LP-relaxation. This is a lower bound
	 * on the best possible solution to the problem and can be used to compute how far the
	 * computed integer solution is possibly away from optimality.
	 * @return a lower bound on the best possible solution to the problem
	 */
	public double getLowerBound()
	{
		return lowerbound;
	}
}
