package lll.Loc;

//=============================================
/*[class] Vfunc              5/20/2008 by Classiclll
 *
 * Model of the Vector Function with some parameters
 *	f : R^n.R^m -> R
 *
 * can solve the followings with Simplex(Nelder) and Newton
 *		for given p, find x such that f(x;p) = 0
 *		for given p, find extreme point x* such that f(x*;p)/dx = 0
 *		estimate parameters p* such that ssr(obs,samples,p*)/dp = 0
 *			based the observations, {obs[i] in R, samples[i] in R^n}
 *
 Vfunc(int domX, int domP) // construct 
 			//	& setup the internal inplementations of EqSys as solver

 int domDim() {return domX;} 		// dimension of the domain (n)
 int paramDim() {return domP;}		// dimension of the params (m)

 abstract double valueAt(Vec x, Vec p); 	// this parameterized function (R^n.R^m -> R)
 abstract Vec gradAt(Vec x, Vec p);		// this gradient arround x, given p
 abstract Vec gradParamAt(Vec x, Vec p);	// this gradient arround p, given x

 double valueAt(Vec x) 	// conbinient method with no parameters.
 Vec gradAt(Vec x) 	// conbinient method with no parameters.
 
 Vec diffAt(Vec x, Vec d, Vec p) 	// gradient estimator at x+d using valueAt() 
 Vec diffParamAt(Vec x, Vec d, Vec p)// param grad estimator at p+d using valueAt()

 // 1. for solving the equation of this function.
  	private class thisEqSys extends EqSys  // an implementation of Equation System
 Vec solveByNewton(Vec x0, Vec p)  // find the solution by newton, start at x0
 Vec solveBySimplex(Vec x0, Vec p, int limit) // find the solution by Simplex, start at x0

 // 2. for finding of the extreme point of this function
  	private class gradEqSys extends EqSys // an implementation of Equation System
  	//find one extreme point, start at x0
 Vec findeExtremeByNewton(Vec x0, Vec p)
 Vec findeExtremeBySimplex(Vec x0, Vec p, int limit)
 Vec findeExtremeBySimplex(Vec x0, Vec p, int limit, int tryal)

 // 3. least sqare equation system, gradient by params
  	private class paramEqSys extends EqSys // an implementation of Equation System
 	// estimate the params by Least Square Sum
 Vec bestPrmByNewton(Vec p, Vec obs, Mat samples)
 Vec bestPrmBySimplex(Vec p, Vec obs, Mat samples, int limit)
 Vec bestPrmBySimplex(Vec p, Vec obs, Mat samples, int limit, int tryal)
 	// the Formulation for the Least Square Sum of the Sum of Square residuals
 double ssrAt(Vec p, Vec obs, Mat samples) //calcurate the Square Sum of Residuals at "p".
 residual(Vec p, Vec obs, Mat samples) // calculate the each residual at "p"
 Vec ssrGradAt(Vec p, Vec obs, Mat samples) // gradient of ssr based gradParamAt()
 Vec ssrJacobAt(Vec p, Vec obs, Mat samples) // jacobian of ssr based gradParamAt()
 */
//=============================================
public abstract class Vfunc {
	protected int domX;
	protected int domP;

	public Vfunc(int domX, int domP) { // setup the internal EqSys
										// inplementations for solver
		this.domX = domX;
		this.domP = domP;
		self = new thisEqSys(this);
		grad = new gradEqSys(this);
		gradParam = new paramEqSys(this);
	}

	abstract public double valueAt(Vec x, Vec p); // this parameterized function (R^n,R^m -> R)
	abstract public Vec gradAt(Vec x, Vec p); // this gradient arround x, given p
	abstract public Vec gradParamAt(Vec x, Vec p); // this gradient arround p, given x

	public double valueAt(Vec x) {	// conbinient method with no parameters.
		return valueAt(x, null);
	}
	public Vec gradAt(Vec x) {	// conbinient method with no parameters.
		return gradAt(x, null);
	}

	public Mat jacobAt(Vec x, Vec p) { // in some case, need to be overridden
		return dDiffAt(x, x.mul(0.01), p);
	}

	public Mat jacobParamAt(Vec x, Vec p) {  // in some case, need to be overridden
		return dDiffParamAt(x, p.mul(0.01), p);
	}

	public int domDim() { return domX; } // dimension of the domain (n)
	public int paramDim() { return domP; } // dimension of the params (m)

	public Vec diffAt(Vec x, Vec d, Vec p) { // gradient estimator at x+d
		// using valueAt()
		if (x.length()!=d.length()) return Vec.NaN.copy();
		Vec ret = new Vec(domX), xx=x.copy();
		double[] retRef = ret.arrayRef();
		double[] xxRef = xx.arrayRef(), dRef = d.arrayRef();
		double b = valueAt(x, p);
		for (int i = 0; i < domX; i++) {
			double bk = xxRef[i];
			xxRef[i] += dRef[i];
			retRef[i] = (valueAt(xx, p) - b) / dRef[i];
			xxRef[i] = bk;
		}
		return ret;
	}
	
	public Mat dDiffAt(Vec x, Vec d, Vec p) { // jacobian estimator at x+d
		// using gradAt()
		if (x.length()!=d.length()) return Mat.NaN.copy();
		Mat ret = new Mat(domX, domX);
		Vec xx = x.copy();
		double[][] retRef = ret.arrayRef();
		double[] xxRef = xx.arrayRef(), dRef = d.arrayRef();
		Vec b = gradAt(x, p);
		for (int i = 0; i < domX; i++) {
			double bk = xxRef[i];
			xxRef[i] += dRef[i];
			retRef[i] = gradAt(xx, p).sub(b).div(new Vec(dRef[i],domX)).arrayRef();
			xxRef[i] = bk;
		}
		return ret;
	}

	public Vec diffParamAt(Vec x, Vec d, Vec p) { // param grad estimator at p+d
		// using valueAt()
		if (p.length()!=d.length()) return Vec.NaN.copy();
		Vec ret = new Vec(domP), pp=p.copy();
		double[] retRef = ret.arrayRef();
		double[] ppRef = pp.arrayRef(), dRef = d.arrayRef();
		double b = valueAt(x, p);
		for (int i = 0; i < domP; i++) {
			double bk = ppRef[i];
			ppRef[i] += dRef[i];
			retRef[i] = (valueAt(x, pp) - b) / dRef[i];
			ppRef[i] = bk;
		}
		return ret;
	}

	public Mat dDiffParamAt(Vec x, Vec d, Vec p) { // jacobian estimator at p+d
		// using gradParamAt()
		if (p.length()!=d.length()) return Mat.NaN.copy();
		Mat ret = new Mat(domP, domP);
		Vec pp = p.copy();
		double[][] retRef = ret.arrayRef();
		double[] ppRef = pp.arrayRef(), dRef = d.arrayRef();
		Vec b = gradParamAt(x, p);
		for (int i = 0; i < domP; i++) {
			double bk = ppRef[i];
			ppRef[i] += dRef[i];
			retRef[i] = gradParamAt(x, pp).sub(b).div(new Vec(dRef[i],domP)).arrayRef();
			ppRef[i] = bk;
		}
		return ret;
	}

	// ************* solver for this equation func(x;p) = 0 *****************
	private thisEqSys self; // for solving the equation of this function.
	private class thisEqSys extends EqSys { // an implementation of Equation System
		public Vfunc parent;
		public Vec param;
		private void setParam(Vec p) { param = p; }
		public thisEqSys(Vfunc parent) {
			super(parent.domX, parent.domX);
			this.parent = parent;
		}
		public Vec valueAt(Vec x) { // the equation system (R^n -> R^m)
			return new Vec( new double[]{ parent.valueAt(x, param) });
		}
		public Mat jacobAt(Vec x) { // if not continuouse, use diffAt(x,d)
			return new Mat(new double[][]{ parent.gradAt(x, param).toArray() });
		}
	}
								//	 find the solution by newton, start at x
	public Vec solveByNewton(Vec x0, Vec p) { 
		self.setParam(p);
		return self.solveByNewton(x0);
	}
								//find the solution by Simplex, start at x
	public Vec solveBySimplex(Vec x0, Vec p, int limit) {
		self.setParam(p);
		return self.solveBySimplex(x0, limit);
	}

	// ************* exteme point finder for this equation d[func(x;p)] = 0
	private gradEqSys grad; // for finding of the extreme point of this function
	private class gradEqSys extends EqSys { // an implementation of Equation											// System
		public Vfunc parent;
		public Vec param;
		private void setParam(Vec p) { param = p; }
		public gradEqSys(Vfunc parent) {
			super(parent.domX, parent.domX);
			this.parent = parent;
		}
		public Vec valueAt(Vec x) { return parent.gradAt(x, param); }
		public Mat jacobAt(Vec x) { return parent.jacobAt(x, param); }
	}

	public Vec findeExtremeByNewton(Vec x0, Vec p) {
		grad.setParam(p);
		return grad.solveByNewton(x0);
	}
	public Vec findeExtremeBySimplex(Vec x0, Vec p, int limit) {
		grad.setParam(p);
		return grad.solveBySimplex(x0, limit);
	}
	public Vec findeExtremeBySimplex(Vec x0, Vec p, int limit, int tryal) {
		grad.setParam(p);
		return grad.solveBySimplex(x0, limit, tryal);
	}

	// ************* parameter estimater for some observations *****************
	private paramEqSys gradParam; // least sqare equation system, gradient by params
	private class paramEqSys extends EqSys { // an implementation of Equation System
		public Vfunc parent;
		public Vec obs;
		public Mat samples;
		public paramEqSys(Vfunc parent) {
			super(parent.domP, parent.domP);
			this.parent = parent;
		}
		private void setObs(Vec obs, Mat samples) {
			this.obs = obs;
			this.samples = samples;
		}
		public Vec valueAt(Vec p) { return parent.ssrGradAt(p, obs, samples); }
		public Mat jacobAt(Vec p) { return parent.ssrJacobAt(p, obs, samples); }
	}

	// calcurate the Square Sum of Residuals at "p". for use the Least Square
	// Sum estimation method.
	public double ssrAt(Vec p, Vec obs, Mat samples) {
		double ret = 0;
		for (int i = 0; i < obs.length(); i++) {
			ret += valueAt(samples.rowVec(i), p) - obs.elem(i);
		}
		return ret;
	}

	public Vec ssrGradAt(Vec p, Vec obs, Mat samples) {
		Vec ret = new Vec(domP);
		for (int j = 0; j < obs.length(); j++) {
			Vec xj = samples.rowVec(j);
			double dj = valueAt(xj, p) - obs.elem(j);
			ret = ret.add(gradParamAt(xj,p).mul(2*dj));
		}
		return ret;
	}
	
	public Mat ssrJacobAt(Vec p, Vec obs, Mat samples) {
		Mat ret = new Mat(domP,domP);
		for (int j = 0; j < obs.length(); j++) {
			Vec xj = samples.rowVec(j);
			double dj = this.valueAt(xj, p);
			Vec gradj = gradParamAt(xj, p);
			Mat jacobj = jacobParamAt(xj, p);
			ret = ret.add(
					gradj.tensor(gradj).mul(2).add(jacobj.mul(2*dj))
					);
		}
		return ret;
	}

	// estimate the params by solving the gradient equations of the Square Sum
	// of Residuals.
	public Vec bestPrmByNewton(Vec p, Vec obs, Mat samples) {
		gradParam.setObs(obs, samples);
		return gradParam.solveByNewton(p);
	}
	public Vec bestPrmBySimplex(Vec p, Vec obs, Mat samples, int limit) {
		gradParam.setObs(obs, samples);
		return gradParam.solveBySimplex(p, limit);
	}
	public Vec bestPrmBySimplex(Vec p, Vec obs, Mat samples, int limit, int trial) {
		gradParam.setObs(obs, samples);
		return gradParam.solveBySimplex(p, limit, trial);
	}
}
