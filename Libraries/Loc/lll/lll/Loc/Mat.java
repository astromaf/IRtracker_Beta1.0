package lll.Loc;
//=============================================
/*[class] Mat              5/20/2008 by Classiclll
 * the model of Linear Algebra with the linear operations.
 * 	The index of Matrices is 0-based -- e.g.,
 * 		elem(0, 0) : the element in the first row, first column
 * 		elem(n, m) : the element in the (n-1)th. row, (m-1)th column.
 * 
 * can solve the linear equations, with LU decomposition
 * 		[[cij]] * [xj] = [bj]
 *
 * [Caution!]
 * 	The LU matrix is cached and reused on subsequent calls,
 *    solve(), isSingular(), det(), inverse()
 * 	If data are modified via references getDataRef(), then the 
 * 	stored LU decomposition will not be discarded.  In this case,
 * 	you need to explicitly invoke LUDecompose() to recompute
 * 	The LU decomposition is performed before using any of the 
 * 	methods above.

 [members]
 static final Mat NaN						:constant Mat of one Double.NaN(not a number)
 
 [constructer]
 Mat()									:construct empty
 Mat(int rows, int cols)					:construct sized by rows*cols 
 Mat(double d, int rows, int cols)		:construct sized by rows*cols, all having d 
 Mat(double[][] d)						:construct having d[row][col]
 Mat(Mat m)								:construct having d[row][col]
 Mat(double[] v)							:construct single raw having v[col] 
 Mat(Vec v)								:construct having d[row][col]
 Mat(Loc v)								:construct 3D single row, (x,y,z)
 
 [methods]
<group #0 - Matrix Operation - generator>
 Mat copy()								:return the copy of me.
 Mat getIdentity()						:return the identity mtx, sized by min(rows,cols)
 Mat add(Mat m)							:return me[i][j] + m[i][j]
 Mat add(double d)						:return me[i][j] + d (scalar)
 Mat sub(Mat m)							:return me[i][j] - m[i][j]
 Mat mul(double d)						:return me[i][j] - d (scalar)
 Mat mul(Mat m)							:return me[i][k] * m[k][j]
 Mat preMul(Mat m)						:return m[i][k] * me[k][j]
 Mat setSubMat(Mat subMat, int row, int col):ret[row+i][col+j] <= subMat[i][j] 
 Mat setSubMat(double[][] subMat, int row, int col):  <same above> 
 Mat setRowMat(Vec v, int row)			:return Mat({me[row][0],.....,me[row][col-1]})
 Mat setColMat(Vec v, int col)			:return {MAT(me[0][col],.....,me[row-1][col]})
 Mat SubMat(int startRow, int endRow,		:return me[sRow->eRow][sCol->eCol]
 			    int startCol, int endCol)
 Mat SubMat(int[] selectedRows,			:retturn {{.},..{me[selRow][selCol]},..{.}}
 			    int[] selectedCols)	
 Mat rowMat(int row)						:return Mat({me[row][0],.....,me[row][col-1]})
 Mat colMat(int col)						:return {MAT(me[0][col],.....,me[row-1][col]})
 Mat transpose()							:return the transpose of me.
 Mat inverse()							:"me" must be square, otherwize null returned
 
<group #1 - Vector Operation - generator>
 Vec rowVec(int row)				:return array({me[row][0],.....,me[row][col-1]})
 Vec colVec(int col)				:return array({me[0][col],.....,me[row-1][col]})
 Vec preMul(Vec v)				:reurn v[]*me[][]
 Vec operate(Vec v)				:return me[][]*transpose(v[])
 
<group #2 - Scalar - information>
 int rowDim()							:return the number of rows
 int colDim()							:return the numbrt of columns
 double elem(int row, int column)			:return the specified element
 double norm()							:return the infinit(=maximum) norm of me.
 double det()							:return the deturminant of me.
 double trace()							:return the trace of me.
 boolean isSquare()						:return is "me" square? (rows==columns)
 boolean isSingular()						:return is "me" singular?
 boolean equals(Object object)			:return shallow equolity between me and object 
 boolean hasNaN()						:return has me some Double.NaN 
 boolean hasInf()						:return has me some Double.Infinity
 boolean isNaN()							:return is me containing NaN or Infinity
 
 <group #3 - Utilities - generator>
 double[][] toArray()						:return 2d array of me
 double[][] arrayRef()					:retuen the reference to 2d array of "me"
 											modification may cause some trouble.
 String toString()                        	:get the string expression of me.
  
 <group #4 - High level operator>
 Vec leqValueAt(Vec x)					:same as operate(Vec x)
 Vec solve(Vec b)
     : Returns a matrix of (column) solution vectors for linear systems with
            [me] * [x] = b[].
       when no solution, null refference will be returned.
 Mat solve(Mat b)
     : Returns a matrix of (column) solution vectors for linear systems with
            [me] * [x] = [b].
       when no solution, null refference will be returned.
 Mat luDecompose()
     :  Returns the LU decomposition of me as a Mat,
          a fresh copy of the cached LU matrix if this has been computed;
       When none chashed
          the composition is computed and cached for use by other methods.
          		solve(), isSingular(), det(), inverse()
       The matrix returned is a compact representation of the LU decomposition.
        *Example:
          Returned matrix          L                  U
              2  3  1           1  0  0            2  3  1
              5  4  6           5  1  0            0  4  6
              1  7  8           1  7  1            0  0  8
       The L and U matrices satisfy the matrix equation LU = permuteRows of me.
       when "me" is singular, null refference will be returned.

 <group #3 - not public - utilities>
 protected int[] getPermutation()
     * Returns the permutation associated with the lu decomposition.
     * The entries of the array represent a permutation of the numbers
     *  0, ... , nRows - 1.
 private double[][] copyOut()
 private void copyIn(double[][] in)
 private boolean isValidCoordinate(int row, int col)
*/
//=============================================

public class Mat {
	public static final Mat NaN = new Mat(Vec.NaN);
    private double data[][] = null;
    private double lu[][] = null;
    private int[] permutation = null;
    private int parity = 1;

    public Mat() {
    }

    public Mat(int rows, int cols) {
        if (rows > 0 && cols > 0) {
        data = new double[rows][cols];
        lu = null;
        }
    }

    public Mat(double d, int rows, int cols) {
        if (rows > 0 && cols > 0) {
        data = new double[rows][cols];
        lu = null;
        for(int i=0;i<rows;i++) for(int j=0;j<cols;j++) data[i][j]=d;
        }
    }

    public Mat(double[][] d) {
        this.copyIn(d);
        lu = null;
    }

    public Mat(Mat m) {
        this.copyIn(m.arrayRef());
        lu = null;
    }

    public Mat(double[] v) {
        int nRows = v.length;
        data = new double[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v[row];
        }
    }

    public Mat(Vec v) {
        int nRows = v.arrayRef().length;
        data = new double[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v.elem(row);
        }
    }

    public Mat(Loc v) {
        data = new double[3][1];
        data[0][0] = v.x;
        data[1][0] = v.y;
        data[2][0] = v.z;
    }

    public Mat copy() {
        return new Mat(this.copyOut());
    }

    public Mat add(Mat m) {
        if (this.colDim() != m.colDim() ||
                this.rowDim() != m.rowDim())  return NaN.copy();
        int rows = this.rowDim();
        int cols = this.colDim();
        double[][] outData = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                outData[row][col] = data[row][col] + m.elem(row, col);
            }  
        }
        return new Mat(outData);
    }

    public Mat sub(Mat m) {
        if (this.colDim() != m.colDim() ||
                this.rowDim() != m.rowDim()) return NaN.copy();
        int rows = this.rowDim();
        int cols = this.colDim();
        double[][] outData = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                outData[row][col] = data[row][col] - m.elem(row, col);
            }
        }
        return new Mat(outData);
    }

    public Mat add(double d) {
        int rowCount = this.rowDim();
        int columnCount = this.colDim();
        double[][] outData = new double[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                outData[row][col] = data[row][col] + d;
            }
        }
        return new Mat(outData);
    }

    public Mat mul(double d) {
        int rows = this.rowDim();
        int cols = this.colDim();
        double[][] outData = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                outData[row][col] = data[row][col] * d;
            }
        }
        return new Mat(outData);
    }

    public Mat mul(Mat m) {
        if (this.colDim() != m.rowDim()) return NaN.copy();
        int nRows = this.rowDim();
        int nCols = m.colDim();
        int nSum = this.colDim();
        double[][] outData = new double[nRows][nCols];
        double sum = 0;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                sum = 0;
                for (int i = 0; i < nSum; i++) {
                    sum += data[row][i] * m.elem(i, col);
                }
                outData[row][col] = sum;
            }
        }
        return new Mat(outData);
    }

    public Mat preMul(Mat m) { return m.mul(this); }

    public double[][] toArray() { return copyOut(); }

    public double[][] arrayRef() { return data; }

    public double norm() {
        double maxColSum = 0;
        for (int col = 0; col < this.colDim(); col++) {
            double sum = 0;
            for (int row = 0; row < this.rowDim(); row++) {
                sum += Math.abs(data[row][col]);
            }
            maxColSum = Math.max(maxColSum, sum);
        }
        return maxColSum;
    }

    public Mat subMat(int startRow, int endRow, int startColumn, int endColumn) {
        if (startRow < 0 || startRow > endRow || endRow > data.length ||
             startColumn < 0 || startColumn > endColumn ||
             endColumn > data[0].length )
        {
        		return NaN.copy();
        }
        Mat subMat = new Mat(endRow - startRow+1,  endColumn - startColumn+1);
        double[][] subMatrixData = subMat.arrayRef();
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                    subMatrixData[i - startRow][j - startColumn] = data[i][j];
                }
            }
        return subMat;
    }

    public Mat subMat(int[] selectedRows, int[] selectedColumns){
        if (selectedRows.length * selectedColumns.length == 0) return NaN.copy();
        Mat subMatrix = new Mat(selectedRows.length, selectedColumns.length);
        double[][] subMatrixData = subMatrix.arrayRef();
        for (int i = 0; i < selectedRows.length; i++) {
            for (int j = 0; j < selectedColumns.length; j++) {
                subMatrixData[i][j] = data[selectedRows[i]][selectedColumns[j]];
            }
        }
        return subMatrix;
    }

    public Mat rowMat(int row) {
        if ( !isValidCoordinate( row, 0)) return NaN.copy();
        int ncols = this.colDim();
        double[][] out = new double[1][ncols]; 
        System.arraycopy(data[row], 0, out[0], 0, ncols);
        return new Mat(out);
    }

    public Mat colMat(int column) {
        if ( !isValidCoordinate( 0, column)) return NaN.copy();
        int nRows = this.rowDim();
        double[][] out = new double[nRows][1]; 
        for (int row = 0; row < nRows; row++) {
            out[row][0] = data[row][column];
        }
        return new Mat(out);
    }

    public Vec rowVec(int row){
        if ( !isValidCoordinate( row, 0 ) ) return Vec.NaN.copy();
        int ncols = this.colDim();
        Vec out = new Vec(ncols);
        System.arraycopy(data[row], 0, out.arrayRef(), 0, ncols);
        return out;
    }

    public Vec colVec(int col) {
        if ( !isValidCoordinate(0, col) ) return Vec.NaN.copy();
        int nRows = this.rowDim();
        Vec out = new Vec(nRows);
        for (int row = 0; row < nRows; row++) {
            out.arrayRef()[row] = data[row][col];
        }
        return out;
    }

    public double elem(int row, int column) {
        if (!isValidCoordinate(row,column)) return Double.NaN;
        return data[row][column];
    }

    public Mat transpose() {
        int nRows = this.rowDim();
        int nCols = this.colDim();
        Mat out = new Mat(nCols, nRows);
        double[][] outData = out.arrayRef();
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                outData[col][row] = data[row][col];
            }
        }
        return out;
    }

    public Mat inverse() { return solve(rightIdentity()); }

    public double det() {
        if (!isSquare()) return Double.NaN;
        if (isSingular()) {   // note: this has side effect of attempting LU decomp if lu == null
            return 0d;
        } else {
            double det = parity;
            for (int i = 0; i < this.rowDim(); i++) {
                det *= lu[i][i];
            }
            return det;
        }
    }

    public boolean isSquare() { return (this.colDim() == this.rowDim()); }

    public boolean isSingular() {
        if (lu == null) {
            return (luDecompose()==null||luDecompose().equals(Mat.NaN) ? true : false);
        } else { // LU decomp must have been successfully performed
            return false; // so the matrix is not singular
        }
    }

    public Mat leftIdentity() {
	    int dim = rowDim();
	    Mat out = new Mat(dim, dim);
	    double[][] d = out.arrayRef();
	    for (int row = 0; row < dim; row++) {
	    		for (int col = 0; col < dim; col++) {
	    			d[row][col] = row == col ? 1d : 0d;
	    		}
	    }
	    return out;
    }

    public Mat rightIdentity() {
	    int dim = colDim();
	    Mat out = new Mat(dim, dim);
	    double[][] d = out.arrayRef();
	    for (int row = 0; row < dim; row++) {
	    		for (int col = 0; col < dim; col++) {
	    			d[row][col] = row == col ? 1d : 0d;
	    		}
	    }
	    return out;
    }

    public int rowDim() { return data.length; }

    public int colDim() { return data[0].length; }

    public double trace() {
        if (!isSquare()) return Double.NaN;
        double trace = data[0][0];
        for (int i = 1; i < this.rowDim(); i++) {
            trace += data[i][i];
        }
        return trace;
    }

    public Vec operate(Vec v) {
        if (v.length() != this.colDim()) return Vec.NaN.copy();
        int nRows = this.rowDim();
        int nCols = this.colDim();
        Vec out = new Vec(v.length());
        double[] vref = v.arrayRef();
        double[] outref = out.arrayRef();
        for (int row = 0; row < nRows; row++) {
            double sum = 0;
            for (int i = 0; i < nCols; i++) {
                sum += data[row][i] * vref[i];
            }
            outref[row] = sum;
        }
        return out;
    }
    
    public Vec preMul(Vec v) {
        int nRows = this.rowDim();
        if (v.length() != nRows) return Vec.NaN.copy();
        int nCols = this.colDim();
        Vec out = new Vec(nCols);
        double[] vref = v.arrayRef();
        double[] outref = out.arrayRef();
        for (int col = 0; col < nCols; col++) {
            double sum = 0;
            for (int i = 0; i < nRows; i++) {
                sum += data[i][col] * vref[i];
            }
            outref[col] = sum;
        }
        return out;
    }

    public Vec solve(Vec b) {
        int nRows = this.rowDim();
        if (b.length() != nRows) return Vec.NaN.copy();
        Mat bMatrix = new Mat(b);
        double[][] solution = ((Mat) (solve(bMatrix))).arrayRef();
        double[] out = new double[nRows];
        for (int row = 0; row < nRows; row++) {
            out[row] = solution[row][0];
        }
        return new Vec(out);
    }

    public Vec leqValueAt(Vec x) { return operate(x); }

    public Mat solve(Mat b) {
        if (b.rowDim() != this.rowDim()) return NaN.copy();
        if (!this.isSquare()) return NaN.copy();
        if (this.isSingular()) return NaN.copy();

        int nCol = this.colDim();
        int nColB = b.colDim();
        int nRowB = b.rowDim();

        // Apply permutations to b
        double[][] bp = new double[nRowB][nColB];
        for (int row = 0; row < nRowB; row++) {
            for (int col = 0; col < nColB; col++) {
                bp[row][col] = b.elem(permutation[row], col);
            }
        }

        // Solve LY = b
        for (int col = 0; col < nCol; col++) {
            for (int i = col + 1; i < nCol; i++) {
                for (int j = 0; j < nColB; j++) {
                    bp[i][j] -= bp[col][j] * lu[i][col];
                }
            }
        }

        // Solve UX = Y
        for (int col = nCol - 1; col >= 0; col--) {
            for (int j = 0; j < nColB; j++) {
                bp[col][j] /= lu[col][col];
            }
            for (int i = 0; i < col; i++) {
                for (int j = 0; j < nColB; j++) {
                    bp[i][j] -= bp[col][j] * lu[i][col];
                }
            }
        }

        Mat outMat = new Mat(bp);
        return outMat;
    }

    public Mat luDecompose() {
        int nRows = this.rowDim();
        int nCols = this.colDim();
        
        if (nRows != nCols) return NaN.copy();
        lu = this.toArray();

        // Initialize permutation array and parity
        permutation = new int[nRows];
        for (int row = 0; row < nRows; row++) permutation[row] = row;
        parity = 1;

        // Loop over columns
        for (int col = 0; col < nCols; col++) {
            double sum = 0;
            // upper
            for (int row = 0; row < col; row++) {
                sum = lu[row][col];
                for (int i = 0; i < row; i++) {
                    sum -= lu[row][i] * lu[i][col];
                }
                lu[row][col] = sum;
            }
            // lower
            int max = col; // permutation row
            double largest = 0d;
            for (int row = col; row < nRows; row++) {
                sum = lu[row][col];
                for (int i = 0; i < col; i++) {
                    sum -= lu[row][i] * lu[i][col];
                }
                lu[row][col] = sum;
                // maintain best permutation choice
                if (Math.abs(sum) > largest) {
                    largest = Math.abs(sum);
                    max = row;
                }
            }
            // Singularity check
            if (Math.abs(lu[max][col]) < Vec.TOO_SMALL) {
                lu = null;
                return NaN.copy();
            }
            // Pivot if necessary
            if (max != col) {
                double tmp = 0;
                for (int i = 0; i < nCols; i++) {
                    tmp = lu[max][i];
                    lu[max][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = permutation[max];
                permutation[max] = permutation[col];
                permutation[col] = temp;
                parity = -parity;
            }
            //Divide the lower elements by the "winning" diagonal elt.
            for (int row = col + 1; row < nRows; row++) {
                lu[row][col] /= lu[col][col];
            }
        }
        return new Mat(lu);
    }

    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("Mat{");
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                res.append(i>0?"\n":"");
                res.append(i>0?"    {":"{");
                for (int j = 0; j < data[0].length; j++) {
                    if (j > 0) res.append(", ");
                    res.append(data[i][j]);
                } 
                res.append(i==data.length-1?"}":"},");
            } 
        }
        res.append("}");
        return res.toString();
    } 
    public boolean equals(Object object) {
    		if (object == null) return false;
        if (object == this) return true;
        if (object instanceof Mat == false) return false;
        
        Mat m = (Mat) object;
        int nRows = rowDim();
        int nCols = colDim();
        if (m.colDim() != nCols || m.rowDim() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                if (Double.doubleToLongBits(data[row][col]) != 
                    Double.doubleToLongBits(m.elem(row, col))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean hasNaN() {
        for (int i=0; i<data.length; i++)
          for (int j=0; j<data[i].length; j++){
            if (Double.isNaN(data[i][j])) return true;
        }
        return false;
    }
    public boolean hasInf() {
        for (int i=0; i<data.length; i++)
          for (int j=0; j<data[i].length; j++){
            if (Double.isInfinite(data[i][j])) return true;
        }
        return false;
    }

    public boolean isNaN() {
        for (int i=0; i<data.length; i++)
          for (int j=0; j<data[i].length; j++){
            if (Double.isNaN(data[i][j])||Double.isInfinite(data[i][j])) return true;
        }
        return false;
    }

    public Mat setSubMat(double[][] subMatrix, int row, int column) {
		if ((row < 0) || (column < 0)) return NaN.copy();
		int nRows = subMatrix.length;
		if (nRows == 0) return NaN.copy();
		int nCols = subMatrix[0].length;
		if (nCols == 0) return NaN.copy();
		for (int r = 1; r < nRows; r++) {
			if (subMatrix[r].length != nCols) return NaN.copy();
		}       
		if (data == null) {
			if ((row > 0)||(column > 0)) return NaN.copy();
			data = new double[nRows][nCols];
			System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);          
		}   
		if (((nRows + row) > this.rowDim()) ||
			(nCols + column > this.colDim())) return NaN.copy();
		for (int i = 0; i < nRows; i++) {
			System.arraycopy(subMatrix[i], 0, data[row + i], column, nCols);
		} 
		lu = null;
		return this;
}

    public Mat setSubMat(Mat subMatrix, int row, int column) {
		if ((row < 0) || (column < 0)) return NaN.copy();
		double[][] subRef = subMatrix.arrayRef();
		int nRows = subRef.length;
		if (nRows == 0) return NaN.copy();
		int nCols = subRef[0].length;
		if (nCols == 0) return NaN.copy();
		for (int r = 1; r < nRows; r++) {
			if (subRef[r].length != nCols) return NaN.copy();
		}       
		if (data == null) {
			if ((row > 0)||(column > 0)) return NaN.copy();
			data = new double[nRows][nCols];
			System.arraycopy(subRef, 0, data, 0, subRef.length);          
		}   
		if (((nRows + row) > this.rowDim()) ||
			(nCols + column > this.colDim())) return NaN.copy();
		for (int i = 0; i < nRows; i++) {
			System.arraycopy(subRef[i], 0, data[row + i], column, nCols);
		} 
		lu = null;
		return this;
}

    public Mat setRowVec(Vec rowVec, int row) {
		if (row < 0) return NaN.copy();
		int nCols = rowVec.length();
		if (nCols==0) return NaN.copy();
		if (data == null) data = new double[row+1][nCols];
		nCols = Math.min(nCols, data.length);
		System.arraycopy(rowVec.arrayRef(), 0, data[row], 0, nCols);
		lu = null;
		return this;
}

    public Mat setColVec(Vec colVec, int col) {
		if (col < 0) return NaN.copy();
		int nRows = colVec.length();
		if (nRows == 0) return NaN.copy();
		if (data == null) data = new double[nRows][col+1];
		nRows = Math.min(nRows, data[0].length);
		for (int i=0;i<nRows;i++) data[i][col] = colVec.elem(i);
		lu = null;
		return this;
}

    //------------------------ Protected methods

    protected int[] getPermutation() {
        int[] out = new int[permutation.length];
        System.arraycopy(permutation, 0, out, 0, permutation.length);
        return out;
    }

    //------------------------ Private methods

    private double[][] copyOut() {
        int nRows = this.rowDim();
        double[][] out = new double[nRows][this.colDim()];
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(data[i], 0, out[i], 0, data[i].length);
        }
        return out;
    }

    private void copyIn(double[][] in) {
        setSubMat(in,0,0);
    }

    private boolean isValidCoordinate(int row, int col) {
        int nRows = this.rowDim();
        int nCols = this.colDim();
        return !(row < 0 || row > nRows - 1 || col < 0 || col > nCols -1);
    }

}


