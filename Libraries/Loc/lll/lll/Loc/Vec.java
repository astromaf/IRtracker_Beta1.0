package lll.Loc;
//=============================================
/*[class] Vec              5/20/2008 by Classiclll
 * the model of Linear Algebra with the linear operations.
 * 		The index of Vecrices is 0-based -- e.g.,
 * 			elem(0) : the left mostelement
 * 			elem(idx) : the (idx+1)th element.
 * can solve the polynomial equation, all the roots are complex with DKA method
 * 		v[0]*x^n + v[1]*x^(n-1) + .... + v[n-1]*x^1 = b
 *   - you can use the convinient, Vec realRoots(Mat root)
 *     	to get unique real roots if exist.
 *   
 [members]
 static final Vec NaN						:constant Vec of one Double.NaN(not a number)
 
 [constructer]
 Vec()									:construct empty
 Vec(int length)							:construct zero vector sized by "length"
 Vec(Vec v)								:construct same to v
 Vec(double[] d, int length)				:construct having d 
 Vec(Loc v)								:construct 3D vector, (x,y,z)
 
 [methods]
<group #1 - Vector Operation - generator>
 Vec copy()								:return the copy of me.
 Vec add(Vec m)							:return me[i] + m[i]
 Vec add(double d)						:return me[i] + d (scalar)
 Vec sub(Vec m)							:return me[i] - m[i]
 Vec sub(double d)						:return me[i] - d (scalar)
 Vec mul(Vec v)							:return me[i] * v[i]
 Vec mul(double d)						:return me[i] * d (scalar)
 Vec div(Vec v)							:return me[i] / v[i]
 Vec div(double d)						:return me[i] / d (scalar)
 Vec setSubVec(Vec subVec, int row)		:ret[row+i] <= subVec[i][j] 
 Vec SubVec(int start, int end)			:return me[sRow->eRow]
 Vec SubVec(int[] selected)				:retturn {{.},..{me[selRow][selCol]},..{.}}
 Mat transpose()							:return the transpose of me.
 Vec inverse()							:"me" must be square, otherwize null returned 
 Mat cross(Vec v)						:return Mat it's trace has me[i]*v[i]
 
<group #2 - Scalar - information>
 double dot(Vec v)						:return sum of me[i] * m[i]
 int length()							:return the number of elmements
 double elem(int idx)						:return the specified element
 double norm()							:return the infinit(=maximum) norm of me.
 double normL2()							:return the L2 norm of me.
 double sqNormL2()						:return the sqare of the L2 norm of me.
 double dist(Vec v)						:return euclid distance (=L2) between v and me.
 double dist2(Vec v)						:return square of distance between v and me.
 boolean equals(Object object)			:return value equolity between me and object 
 boolean hasNaN()						:return has me some Double.NaN 
 boolean hasInf()						:return has me some Double.Infinity
 boolean isNaN()							:return is me containing NaN or Infinity
 
 <group #3 - Utilities - generator>
 double[] toArray()						:return 2d array of me
 Loc toLoc()								:return Loc of me (length must be 3)
 double[] arrayRef()						:retuen the reference to 2d array of "me"
 											modification may cause some trouble.
 String toString()                        	:get the string expression of me.
  
 <group #4 - High level operator>
 double polyValueAt(double x)				:return me[0]*x^n + ... + me[n-1]*x
 Vec realRoots(Mat root)
     : Returns only unique real root with chopping the small imagenary of solve(b).
 Mat solve(double b)
     : Returns the all roots of the following polynomial equation by the DKA Method.
            me[0]*x^n + ...+me[k]*x^(n-k)+... + me[n-1]*x - b = 0.
 solution matrix has
   solution.rowDim()  always 2, (number of parts of the complex expression)
   solution.colDim()  the number of solution pairs
   solution[0][k]     the real part of (k+1)th solution
   solution[1][k]     the imaginary part of (k+1)th solution
*/
//=============================================

public class Vec {
	private static double[] vNaN = { Double.NaN };
	public static final Vec NaN = new Vec(vNaN);
    public static double TOO_SMALL = 1E-12;
    public static double ENOUGH_SMALL = 1E-8;
    private double[] data;

    public Vec() {
    }
    public Vec(int length) {
        if (length > 0) {
        data = new double[length];
        }
    }
    public Vec(double d, int len) {
    		if (len>0) {
    			if (data==null) data = new double[len];
			for (int i=0;i<len;i++) { data[i] = d; }
    		}
    }
    public Vec(double[] d) {
    		if (data==null) data = new double[d.length];
        System.arraycopy(d, 0, data, 0, d.length);
    }
   
    public Vec(Vec v) {
		if (data==null) data = new double[v.length()];
        System.arraycopy(v.arrayRef(), 0, data, 0, v.length());
    }
    public Vec(Loc v) {
        data = new double[3];
        data[0] = v.x;
        data[1] = v.y;
        data[2] = v.z;
    }

//
    public Vec copy() {
        double[] out = new double[data.length];
        System.arraycopy(data, 0, out, 0, data.length);
        return new Vec(out);
    }
    public Vec add(Vec v) {
        if (length() != v.length()) {
            return NaN.copy();
        }
        double[] outData = new double[length()];
        double[] vd = v.arrayRef();
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] + vd[idx];
        }
        return new Vec(outData);
    }
    public Vec sub(Vec v) {
        if (length() != v.data.length) {
            return NaN.copy();
        }
        double[] outData = new double[data.length];
        double[] vd = v.arrayRef();
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] - vd[idx];
        }
        return new Vec(outData);
    }
    public Vec mul(Vec v) {
        if (length() != v.data.length) {
            return NaN.copy();
        }
        double[] outData = new double[data.length];
        double[] vd = v.arrayRef();
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] * vd[idx];
        }
        return new Vec(outData);
    }
    public Vec div(Vec v) {
        if (length() != v.data.length) {
            return NaN.copy();
        }
        double[] outData = new double[data.length];
        double[] vd = v.arrayRef();
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] / vd[idx];
        }
        return new Vec(outData);
    }
   public Vec add(double d) {
        double[] outData = new double[data.length];
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] + d;
        }
        return new Vec(outData);
    }
    public Vec sub(double d) {
        double[] outData = new double[data.length];
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] - d;
        }
        return new Vec(outData);
    }
    public Vec mul(double d) {
        double[] outData = new double[data.length];
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] * d;
        }
        return new Vec(outData);
    }
    public Vec div(double d) {
        double[] outData = new double[data.length];
        for (int idx = 0; idx < data.length; idx++) {
            outData[idx] = data[idx] / d;
        }
        return new Vec(outData);
    }
    public double dot(Vec v) {
        double sum = 0;
        for (int i=0;i<data.length;i++) {
        	  sum += data[i]*v.elem(i);
        }
        return sum;
    }
    public Vec cross(Vec v) {
	    if (data.length!=v.length()) return Vec.NaN.copy();
	    if (data.length!=3) return Vec.NaN.copy();
	    return new Vec(new double[]
	                   { data[1]*v.elem(2)-data[2]*v.elem(1),
						data[2]*v.elem(0)-data[0]*v.elem(2),
						data[0]*v.elem(1)-data[1]*v.elem(0) } );
    }
    public Mat tensor(Vec v) {
	    if (data.length!=v.length()) return Mat.NaN.copy();
	    Mat ret = new Mat(data.length, data.length);
	    double [][] retRef = ret.arrayRef();
	    for (int i=0;i<data.length;i++) {
	    		for (int j=0;j<data.length;j++) {
	    			retRef[i][j] = data[i]*v.elem(j);
	    		}
	    }
	    return ret;
    }
    public double norm() {
		double ret=0;
		for (int i=0;i<data.length;i++) {
			ret = Math.max(ret, Math.abs(data[i]));
		}
		return ret;
    }
    public double normL2() {
		return Math.sqrt(sqrNormL2());
    }
    public double sqrNormL2() {
		double ret=0;
		for (int i=0;i<data.length;i++) {
			ret += data[i]*data[i];
		}
		return ret;
    }
    public double dist(Vec v) {
        return Math.sqrt(dist2(v));
    }
    public double dist2(Vec v) {
        if (data.length != v.length()) {
            return Double.NaN;
        }
        double sum = 0;
        double[] vd = v.arrayRef();
        for (int idx = 0; idx < data.length; idx++) {
            sum += (data[idx]-vd[idx])*(data[idx]-vd[idx]);
        }
        return sum;
    }
    public Vec setSubVec(Vec sub, int start, int len) {
	    if (start < 0 || len-start+1 > data.length) {
      	  return NaN.copy();
        }
        Vec subVec = new Vec(len - start+1);
        double[] subVecData = subVec.arrayRef();
        for (int i = start; i < len-start; i++) {
            data[i] = subVecData[i - start];
        }
        return this;
    }
    public Vec subVec(int start, int end) {
	    if (start < 0 || end > data.length) {
	    		return NaN.copy();
	    }
	    Vec subVec = new Vec(end - start+1);
    		double[] subVecData = subVec.arrayRef();
    		for (int i = start; i <= end; i++) {
    			subVecData[i - start] = data[i];
    		}
    		return subVec;
    }
    public Vec subVec(int[] select){
        if (select.length == 0) {
        	  return NaN.copy();
        }
        Vec subVec = new Vec(select.length);
        double[] subVecData = subVec.arrayRef();
        for (int i = 0; i < select.length; i++) {
                subVecData[i] = data[select[i]];
        }
        return subVec;
    }
    public double elem(int idx) {
        if (idx<0 || idx>=data.length) {
        	   return Double.NaN;
        }
        return data[idx];
    }
    public int length() {
        return data.length;
    }

    public double[] toArray() {
        double[] out = new double[data.length];
        System.arraycopy(data, 0, out, 0, data.length);
        return out;
    }
    public Loc toLoc() {
    		return (data.length==3
    				  ? new Loc((float)data[0], (float)data[1], (float)data[2])
    				  : new Loc(Float.NaN, Float.NaN, Float.NaN)
    				);
    }
    public double[] arrayRef() {
        return data;
    }

    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("Vec(");
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                res.append(data[i]);
                if (i<data.length-1) res.append(" ,");
            } 
        }
        res.append(")");
        return res.toString();
    } 
    public boolean equals(Object object) {
    	    if (object == null) return false;
        if (object == this) return true;
        if (object instanceof Vec == false) return false;
        Vec m = (Vec) object;
        if (m.length() != data.length)  return false;
        for (int idx = 0; idx < data.length; idx++) {
            if (Double.doubleToLongBits(data[idx]) != 
                Double.doubleToLongBits(m.elem(idx))) {
                return false;
            }
        }
        return true;
    }
    public boolean hasNaN() {
        for (int idx = 0; idx < data.length; idx++) {
            if (Double.isNaN(data[idx])) return true;
        }
        return false;
    }
    public boolean hasInf() {
        for (int idx = 0; idx < data.length; idx++) {
            if (Double.isInfinite(data[idx])) return true;
        }
        return false;
    }
    public boolean isNaN() {
        for (int idx = 0; idx < data.length; idx++) {
            if (Double.isNaN(data[idx])||Double.isInfinite(data[idx])) return true;
        }
        return false;
    }
    public double polyValueAt(double x) { // return me[0]*x^n + ... + me[n-1]*x
		double ret = data[0];
		for (int i=1;i<data.length;i++) {
			ret *= ret*x + data[i];
		}
		return ret;
}
    public Vec realRoots(double b) { // return me[0]*x^n + ... + me[n-1]*x
		Mat root = solve(b);
		double[][] rRef = root.arrayRef();
		int n=0;
		for (int i=0;i<root.colDim();i++) {
			if (ENOUGH_SMALL*Math.abs(rRef[0][i])>Math.abs(rRef[1][i])) {
				n++;
			} else {
				rRef[0][n]=rRef[0][i];
			}
		}
		int m=n;
		for (int i=0;i<n-1;i++) {
			for (int j=i+1;j<n;j++) {
				if (Math.abs(rRef[0][i]-rRef[0][j])<TOO_SMALL) {
					if (j+1<n) rRef[0][j] = rRef[0][j+1];
					m--;
				}
			}
		}
		double[] ret = new double[m];
		System.arraycopy(rRef[0],0,ret,0,m);
		return new Vec(ret);
}
    public Mat solve(double b) { // solve by DKA Method
		int n = data.length;
    		Mat ret = new Mat(2, n);
    		double[][] retRef = ret.arrayRef();
    		double[] a = new double[n+1];
    		double w1,w2,f1,f2,p1,p2,err,a1,a2; 
    		for (int i=0; i<n; ++i) a[i] = data[i]/data[0];
    		a[n] = -b/data[0];
    		double[] g = { -a[1]/n, Math.random()/n };
   		double max = 0;
    		for (int i = 1; i <= n; i++) max = Math.max(max, Math.abs(a[i]));
    		double[] r = {Math.abs(g[0])+1+max, 0};
    		for (int i = 0; i < n; i++) {
    		    double theta = 2 * i * Math.PI / n + Math.PI / (2 * n);
    		    retRef[0][i] = g[0] + r[0] * Math.cos(theta);	//exp(I * theta);
    		    retRef[1][i] = g[1] + r[1] * Math.sin(theta);
    		}
    		do {
    			err = 0.0;
    			for (int i=0; i<n; ++i) { 
    		/* numerator */ 
    				f1 = 1.0; 
    				f2 = 0.0; 
    				for (int j=0; j<n; ++j){ 
    					w1 = f1*retRef[0][i]-f2*retRef[1][i]; 
    					w2 = f2*retRef[0][i]+f1*retRef[1][i]; 
    					f1 = w1+a[j+1];
    					f2 = w2; 
    				} 
    		/* denominator */ 
    				p1 = 1.0;
    				p2 = 0.0; 
    				for (int j=0; j<n; ++j){
    					if (j == i) continue; 
    					w1 = p1*(retRef[0][i]-retRef[0][j])-p2*(retRef[1][i]-retRef[1][j]); 
    					w2 = p1*(retRef[1][i]-retRef[1][j])+p2*(retRef[0][i]-retRef[0][j]); 
    					p1 = w1; 
    					p2 = w2; 
    				} 
    	    	/* evaluation */ 
    				a1 = (f1*p1+f2*p2)/(p1*p1+p2*p2);
    				a2 = (f2*p1-f1*p2)/(p1*p1+p2*p2);
    				err = Math.max(err, Math.sqrt(a1*a1+a2*a2)); 
    				retRef[0][i] -= a1;
    				retRef[1][i] -= a2;
    			}
    		}  while (err>ENOUGH_SMALL);
        return ret;
    }
}