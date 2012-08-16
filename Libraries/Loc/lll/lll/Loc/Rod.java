package lll.Loc;
/* 
 * Class Rod, the model of partial line on the 3D space,
 * 			spanned from it's stern to it's bow
 *					created by Classiclll, 6/28/2008
 *  Rod(Loc s, Loc b)			// only constructor you can use
 *  	Loc at(float t)			// location at t, at(0):stern, at(me.length):bow
	Rod move(Loc to)			// move stern to "to"
	Rod shift(Loc diff)		// shift stern by "diff"
	Rod shiftI(Loc diff)		// inverse shift
	Rod scale(float factor)	// scale the size of me by factor
	Rod scaleI(float factor)	// inverse scaling
	Loc intersect2D(Rod to) 	// intersect position on the x-y plane
	float signedDist2D(Rod pt)// distance with sign of left/right side on the x-y plane
	float dist2D(Loc pt)		// distance between "me" and "pt" on the x-y plane
	Loc normal(Loc pt)		// the shortest rod connecting from "me" to "pt"
	Rod ortho(Loc pt)		// the shortest rod connecting from "me" to "pt"
	Rod nearest(Rod to)		// the shortest rod connecting from "me" to "to"
	float dist(Loc pt)		// distance between "me" and "pt"
	boolean having(Loc pt)	// is "me" having "pt" on myself ?
	float dist(Rod to)		// least distance between "me" and line "to"
	boolean crossing(Rod to)// are "me" and "to" crossing each other ?
	float length()			// length of me
	Loc stern()				// location of the stern of me
	Loc bow()				// location of the bow of me
	Loc fore()				// foward direction of me, the unit vector
	Boolean equals(Rod to)
	String toString()
 */
public class Rod {
	protected Loc stern;
	protected Loc fore;
	protected float length;
	protected Rod last=null;
	protected float lastDist;
	protected Loc lastT;
	protected Loc lastS;
	protected Rod() { // don't use this default constructor
	}
	public Rod(Loc s, Loc b) {
		stern = new Loc(s);
		Loc diff = b.sub(s);
		fore = diff.unit();
		length = diff.norm();
	}
	public Rod(Rod frm) {
		stern = new Loc(frm.stern);
		fore = new Loc(frm.fore);
		length = frm.length();
	}
	public Object clone() {return new Rod(this);}
	public Loc at(float t) {
		return fore.mul(t).shift(stern);
	}
	public Rod move(Loc to) {
		stern = stern.move(to);
		last=null;
		return this;
	}
	public Rod shift(Loc diff) {
		stern = stern.shift(diff);
		last=null;
		return this;
	}
	public Rod shiftI(Loc diff) {
		stern = stern.shiftI(diff);
		last=null;
		return this;
	}
	public Rod scale(float factor) {
		length = length * factor;
		return this;
	}
	public Rod scaleI(float factor) {
		length = length / factor;
		return this;
	}
	public Rod rotate(Loc ax, float angle) {
		stern.rotate(ax,angle);
		fore.rotate(ax,angle);
		return this;
	}
	public Rod rotate(Rod ax, float angle) {
		stern = stern.shiftI(ax.stern).rotate(ax.fore,angle).shift(ax.stern);
		fore.rotate(ax.fore,angle);
		return this;
	}
	public Loc intersect2D(Rod b) {	// get intersect position on the x-y plane
		float d2 = cross2D(fore, b.fore);
		if (Math.abs(d2) < 1.e-2)	return null;
		return at(cross2D(b.fore, fore)/ d2);
	}
	public float signedDist2D(Loc pt) {//orthogonal dist with sign of rigtht/left side
		if (length < Vec.TOO_SMALL)	return stern.dist(pt);
		return cross2D(fore, pt.sub(stern));
	}
	public Loc normal(Loc pt) {
		/*< Proposition > 
			 	let the line "me" be [A]+s*[d],
			        and the nearest point from [pt]
			    		- distance {[A]+s*[d]-[pt]}*{[A]+s*[d]-[pt]}
			    		- gradient 2*([A]+s*[d]-[pt])*[d] -> 0
			    then the nearest point on line "me" to "pt" is
			    		- s = -[d]*{[A]-[pt]}/{[d]*[d]}
			    		    = -[d]*{[A]-[pt]}
			    and, the distans |[pt]-{[A]+s*[d]}| is zero on "me"
		 */
		float s = -fore.dot(stern.sub(pt));
		return at(s).shiftI(pt);
	}
	public Rod ortho(Loc pt) {
		float s = -fore.dot(stern.sub(pt));
		return new Rod(pt,at(s));
	}
	public float dist(Loc pt) {	// orthogonal distance between "me" and to
		float s = -fore.dot(stern.sub(pt));
		return pt.dist(at(s));
	}
	public boolean having(Loc pt) {	// "me" and to are crossing each other ?
		return dist(pt)<Vec.ENOUGH_SMALL;
	}
	public Rod nearest(Rod to) {	// shortest Rod
		if (last!=null && last.equals(to)) return new Rod(lastS, lastT);
		intersectAnalysis(to);
		return new Rod(lastS, lastT);
	}
	public float dist2D(Loc pt) {	// orthogonal distance between "me" and to
		return Math.abs(signedDist2D(pt));
	}
	public float dist(Rod to) {		// least distance between "me" and to
		if (last!=null && last.equals(to)) return lastDist;
		intersectAnalysis(to);
		return lastDist;
	}
	public boolean crossing(Rod to) {	// "me" and to are crossing each other ?
		if (last!=null && last.equals(to)) return lastDist<Vec.ENOUGH_SMALL;
		intersectAnalysis(to);
		return lastDist<Vec.ENOUGH_SMALL;
	}
	public float length() {return length;}
	public Loc stern() {return (Loc) stern.clone();}
	public Loc bow() {return stern.add(fore.mul(length));}
	public Loc fore() {return (Loc) fore.clone();}
//
	public boolean equals(Object object) {
		if (object == null ) return false;
		if (object == this ) return true;
		if (object instanceof Rod == false) return false;
		Rod m = (Rod) object;
		if (Float.floatToIntBits(length) != 
    			Float.floatToIntBits(m.length)) return false;
		if (!stern.equals(m.stern)) return false;
		if (!fore.equals(m.fore)) return false;
		return true;
	}
	public String toString() {
		return "Rod["+stern+" - "+bow()+"]";
	}
//
	private float cross2D(Loc frm, Loc to) {
		return frm.x * to.y - frm.y * to.x;
	}
	private void intersectAnalysis(Rod to) {
		/*< Proposition > 
			 * solved by Mr.re_chestnut [http://blogs.yahoo.co.jp/re_chestnut]
			 	let the couple of start position be [A],[B]
			 		and the couple of unit foreection be [d1],[d2]
			    then the point on each lines are
			    		- [P] = [A] + s*[d1]
			    		- [Q] = [B] + t*[d2]
			    then the nearest point on each lines are
			    		- s = {([d1]-cosTh*[d2])}*[AB]/sinTh^2
			    		- t = {([d2]-cosTh*[d1])}*[BA]/sinTh^2
			    where Th is the angle between the each foreections
			    and, the nearest distance, |[Q]-[P]| is zero when the crossed.
		 */
			last = to;
			float cth = fore.cosine(to.fore);
			float sth2 = 1 - cth*cth;
			Loc AB= to.stern.sub(stern);
			float s =  AB.dot(fore.sub(to.fore.mul(cth))) / sth2;
			float t = -AB.dot(to.fore.sub(fore.mul(cth))) / sth2;
			lastS = this.at(s);
			lastT = to.at(t);
			lastDist = lastS.dist(lastT);
		}
}
