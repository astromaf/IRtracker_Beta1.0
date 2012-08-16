package lll.Loc;
import java.util.*;

//=============================================
/*[class] EqSys              5/20/2008 by Classiclll
  *
  *Model of multi-value Vector Functions, the Equation System
  *		f(x) = [ fi(x) | fi : R^n -> R^m ]
  * can solve the multi-value non-linear equation, with Simplex(Nelder) and Newton
  * 		f(x) = 0
  *
	EqSys(int dom, int rng) 		//constructor with dimension of domain & codomain.

    int domDim()					//dimension of the domain (n)
    int rngDim()					//dimension of the range (m)
    
    abstract Vec valueAt(Vec x)	//value of the equation system(R^n -> R^m) at x
		//Jacobian at x, if not continuouse, you can return diffAt(x,d)
    abstract Mat jacobAt(Vec x)
	 [Sample]  Jacobian of f(x) = { x+y+z-1, y+z-1, x+z-1 }
	 		double[][] jacobi =	{ {1, 1, 1},		// { {dxf0(X), dyf0(X), dzf0(X)}
							  	  {0, 1, 1},		//   {dxf1(X), dyf1(X), dzf1(X)}
							  	  {1, 0, 1} };	//   {dxf2(X), dyf2(X), dzf2(X)} }
    
    Vec diffAt(Vec x, Vec d)		// overwride if needed

    Vec solveByNewton(Vec x0)		// find the solution by the newton, start at x0
    Vec solveBySimplex(Vec x0, int limit)	// find the solution by the simplex, start at x0
		// find the solution by the simplex, start at x0
    Vec solveBySimplex(Vec x0, int limit, int tryal)
 */
//=============================================

public abstract class EqSys {
    protected int dom, rng;
    int domDim() {return dom;} 	 //dimension of the domain (n)
    int rngDim() {return rng;}	 //dimension of the range (m)
    
	public EqSys(int dom, int rng) {	//construct and setup this equation system
		this.dom = dom;
		this.rng = rng;
	}
	
    abstract public Vec valueAt(Vec x);	//value of the equation system (R^n -> R^m) at x
    abstract public Mat jacobAt(Vec x);	//Jacobian at x, if not continuouse,
    										//			you can return diffAt(x,d)
    
	public Mat diffAt(Vec x, Vec d) {	  // gradient estimator, overwride if needed
		if (x.length()!=d.length()) return Mat.NaN.copy();
    		Mat ret = new Mat(dom, rng);double[][] retRef = ret.arrayRef();
    		double[] xRef = x.arrayRef(), dRef = d.arrayRef();
    		Vec b = valueAt(x);
    		for (int i=0;i<dom;i++) {
    			double bk = xRef[i];xRef[i] += dRef[i];
    			for (int j=0;j<rng;j++) {
    				retRef[i] = valueAt(x).sub(b).div(d).arrayRef();
    			}
    			xRef[i] = bk;
    		}
    		return ret;
    }
	
/****************** solvers ********************/
    public Vec solveByNewton(Vec x0) {  // find the solution by the newton, start at x0
		/* consider the following series of vector [x]k, k=0,1,2,....
 			[x]k = [x]k-1 - Dlt(k)
 			[x]0 = initial value, given.*/
		Vec xk = new Vec(x0);
		Vec dlt;
		Vec dltOld = new Vec(Double.MAX_VALUE, dom);
    		/* let J(F([x])) be
	 			J(F([x])) = {fij([x])} = {PD.by(xj) fi([x])}
	 		and, let Dlt(k) = (J(F([x]k)))^(-1) * F([x]k) */
		while (true) {
    			dlt = jacobAt(xk).inverse().operate(valueAt(xk));
    		/* now we can find the value of ko such that
	 		    |Dlt(ko) - Dlt(ko-1)| <= epsilon << 1
	 	   and [x]ko,  is the solution of the above problem. */
    			if (dlt.isNaN()
    					||dlt.dist(dltOld)<Vec.ENOUGH_SMALL
    					||dlt.norm()<Vec.ENOUGH_SMALL) break;
    			dltOld = dlt;
    			xk = xk.sub(dlt);
		}
    		return (dlt.isNaN() ? Vec.NaN.copy() : xk);
    }
    public Vec solveBySimplex(Vec x0, int limit) { // find the solution by the simplex, start at x0
		SimplexMethod smplx = new SimplexMethod(this);
		SimplexMethod.Vertices vv = smplx.minimize(limit, x0, x0.add(1).mul(-1));
		return vv.getPoint();
    }
    public Vec solveBySimplex(Vec x0, int limit, int trial) { // find the solution by the simplex, start at x0
		SimplexMethod smplx = new SimplexMethod(this);
		SimplexMethod.Vertices vv
			= smplx.minimize(limit, x0, x0.add(1).mul(-1),
							trial, (long) (Long.MAX_VALUE*Math.random()));
		return vv.getPoint();
    }

	/***************************************************
	 *  internal class of 
	 * 		Modified Simplex Method by Nelder&Mead
	 * Simplex 5/20/2008 by Classiclll 
	 ***************************************************/
	protected class SimplexMethod {
		private double rho; // Reflection coefficient.
		private double khi; // Expansion coefficient.
		private double gamma; // Contraction coefficient.
		private double sigma; // Shrinkage coefficient.
		protected Vertices[] simplex; // Simplex.
		protected Vertices[] prevStartSimplex; // Simplex
		private int evaluations; // Number of evaluations already performed.
		private int starts; // Number of starts to go.
		private EqSys eqSys; // associated equation system.
		private Random rgen = new Random(); // randomgenerator with own seed
		private Vertices[] minima; // Found minima for mullti start.
		// evaluated point ( pair of point and cost )
		public class Vertices {
			private final Vec point;
			private final double cost;
			public Vertices(Vec point, double cost) {
				this.point = point.copy();
				this.cost = cost;
			}
			public Vec getPoint() { return point.copy(); }
			public double getCost() { return cost; }
		}
		/* cost evaluator and convergence checker */
		protected double evaluateCost(Vec x) {
			evaluations++;
			return eqSys.valueAt(x).norm();
		}
		private boolean converged(Vertices[] simplex) {
			Vertices smallest = simplex[0];
			Vertices largest = simplex[simplex.length - 1];
			return (largest.getCost() - smallest.getCost()) < Vec.TOO_SMALL;
		}
		/* Comparator for {@link Vertices vertices} objects. */
		private Comparator VerticesComparator = new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 == null) {
					return (o2 == null) ? 0 : +1;
				} else if (o2 == null) {
					return -1;
				}
				double cost1 = ((Vertices) o1).getCost();
				double cost2 = ((Vertices) o2).getCost();
				return (cost1 < cost2) ? -1 : ((o1 == o2) ? 0 : +1);
			}
		};

		public SimplexMethod(EqSys eqs) {
			super();
			this.eqSys = eqs;
			this.rho = 1.0;
			this.khi = 2.0;
			this.gamma = 0.5;
			this.sigma = 0.5;
		}

		protected void iterateSimplex() {
			int n = simplex.length - 1;
			double smallest = simplex[0].getCost();
			double secondLargest = simplex[n - 1].getCost();
			double largest = simplex[n].getCost();
			Vec xLargest = simplex[n].getPoint();

			Vec centroid = new Vec(n);
			double[] cent = centroid.arrayRef();

			for (int i = 0; i < n; ++i) {
				Vec x = simplex[i].getPoint();
				for (int j = 0; j < n; ++j) {
					cent[j] += x.elem(j);
				}
			}

			double scaling = 1.0 / n;
			for (int j = 0; j < n; ++j) {
				cent[j] *= scaling;
			}
			// compute the reflection point
			Vec xR = new Vec(n);
			double[] xRR = xR.arrayRef();
			for (int j = 0; j < n; ++j) {
				xRR[j] = cent[j] + rho * (cent[j] - xLargest.elem(j));
			}

			double costR = evaluateCost(xR);
			if ((smallest <= costR) && (costR < secondLargest)) {
				// accept the reflected point
				replaceWorstPoint(new Vertices(xR, costR));
			} else if (costR < smallest) {
				// compute the expansion point
				Vec xE = new Vec(n);
				double[] xER = xE.arrayRef();
				for (int j = 0; j < n; ++j) {
					xER[j] = cent[j] + khi * (xRR[j] - cent[j]);
				}
				double costE = evaluateCost(xE);
				if (costE < costR) {
					// accept the expansion point
					replaceWorstPoint(new Vertices(xE, costE));
				} else {
					// accept the reflected point
					replaceWorstPoint(new Vertices(xR, costR));
				}
			} else {
				if (costR < largest) {
					// perform an outside contraction
					Vec xC = new Vec(n);
					double[] xCR = xC.arrayRef();
					for (int j = 0; j < n; ++j) {
						xCR[j] = cent[j] + gamma * (xRR[j] - cent[j]);
					}
					double costC = evaluateCost(xC);
					if (costC <= costR) {
						// accept the contraction point
						replaceWorstPoint(new Vertices(xC, costC));
						return;
					}
				} else {
					// perform an inside contraction
					Vec xC = new Vec(n);
					double[] xCR = xC.arrayRef();
					for (int j = 0; j < n; ++j) {
						xCR[j] = cent[j] - gamma * (cent[j] - xLargest.elem(j));
					}
					double costC = evaluateCost(xC);
					if (costC < largest) {
						// accept the contraction point
						replaceWorstPoint(new Vertices(xC, costC));
						return;
					}
				}
				// perform a shrink
				Vec xSmallest = simplex[0].getPoint();
				for (int i = 1; i < simplex.length; ++i) {
					Vec x = simplex[i].getPoint();
					double[] xxR = x.arrayRef();
					for (int j = 0; j < n; ++j) {
						xxR[j] = xSmallest.elem(j) + sigma
								* (xxR[j] - xSmallest.elem(j));
					}
					simplex[i] = new Vertices(x, Double.NaN);
				}
				evaluateSimplex();
			}
		}

		public Vertices minimize(int maxEvaluations, Vec vertexA, Vec vertexB) {
			buildSimplex(vertexA, vertexB);
			setSingleStart();
			return minimize(maxEvaluations);
		}

		public Vertices minimize(int maxEvaluations, Vec vertexA, Vec vertexB,
				int starts, long seed) {
			buildSimplex(vertexA, vertexB);
			rgen.setSeed(seed);
			setMultiStart(starts);
			return minimize(maxEvaluations);
		}

		private void buildSimplex(Vec vertexA, Vec vertexB) {
			int n = vertexA.length();
			simplex = new Vertices[n + 1];
			for (int i = 0; i <= n; ++i) {
				Vec vertex = new Vec(n);
				if (i > 0) {
					System.arraycopy(vertexB.arrayRef(), 0, vertex.arrayRef(),
							0, i);
				}
				if (i < n) {
					System.arraycopy(vertexA.arrayRef(), i, vertex.arrayRef(),
							i, n - i);
				}
				simplex[i] = new Vertices(vertex, Double.NaN);
			}
			prevStartSimplex = (Vertices[]) simplex.clone();
		}

		private void buildAnotherSimplex() {
			// use first vector size to compute the number of points
			int n = prevStartSimplex[0].getPoint().length();
			Vec point = new Vec(n);
			double[] pRef = point.arrayRef();
			simplex = new Vertices[n + 1];
			for (int j = 0; j < n; j++) {
				pRef[j] = (1 + rgen.nextDouble() - 0.5)
						* prevStartSimplex[0].getPoint().elem(j);
			}
			simplex[0] = new Vertices(point, Double.NaN);
			// fill up the vertex
			for (int i = 1; i <= n; ++i) {
				for (int j = 0; j < n; j++) {
					pRef[j] = (1 + rgen.nextDouble() - 0.5)
							* prevStartSimplex[i].getPoint().elem(j);
				}
				simplex[i] = new Vertices(point, Double.NaN);
			}
		}

		private void setSingleStart() {
			starts = 1;
			minima = null;
		}

		private void setMultiStart(int starts) {
			if (starts < 2) {
				this.starts = 1;
				minima = null;
			} else {
				this.starts = starts;
				minima = null;
			}
		}

		public Vertices[] getMinima() {
			return (Vertices[]) minima.clone();
		}

		private Vertices minimize(int maxEvaluations) {
			minima = new Vertices[starts];
			// multi-start loop
			for (int i = 0; i < starts; ++i) {
				evaluations = 0;
				evaluateSimplex();
				for (boolean loop = true; loop;) {
					if (converged(simplex)) {
						// we have found a minimum
						minima[i] = simplex[0];
						loop = false;
					} else if (evaluations >= maxEvaluations) {
						// this start did not converge, try a new one
						minima[i] = null;
						loop = false;
					} else {
						iterateSimplex();
					}
				}
				if (i < (starts - 1)) buildAnotherSimplex();
			}

			Arrays.sort(minima, VerticesComparator);
			// return the found point given the lowest cost
			if (minima[0] == null) {
				return new Vertices(Vec.NaN.copy(), Double.NaN);
			}
			return minima[0];
		}

		protected void evaluateSimplex() {
			for (int i = 0; i < simplex.length; ++i) {
				Vertices pair = simplex[i];
				if (Double.isNaN(pair.getCost())) {
					simplex[i] = new Vertices(pair.getPoint(),
							evaluateCost(pair.getPoint()));
				}
			}
			// sort the simplex from lowest cost to highest cost
			Arrays.sort(simplex, VerticesComparator);
		}

		protected void replaceWorstPoint(Vertices vertices) {
			int n = simplex.length - 1;
			for (int i = 0; i < n; ++i) {
				if (simplex[i].getCost() > vertices.getCost()) {
					Vertices tmp = simplex[i];
					simplex[i] = vertices;
					vertices = tmp;
				}
			}
			simplex[n] = vertices;
		}
	}
  }