package lll.Loc;
/* 
* Class Tag, the model of (partial) plane on the 3D space,
 * 			spanned from it's stern and star to it's bow and port
 *					created by Classiclll, 7/28/2008
 *  	Tag(Loc s, Loc b, Loc r)	// only constructor you can use, and
 *  	Tag(Tag frm)			// constructor you can use
 *  	Loc at(float s, float b)	// location at (s,b),
					// at(0,0):stern, at(0,me.length):bow, at(me.width,0):star
	Tag move(Loc to)		// move stern to "to"
	Tag shift(Loc diff)		// shift stern by "diff"
	Tag shiftI(Loc diff)		// inverse shift
	Tag scale(float factor)		// scale the size of me by factor
	Tag scaleI(float factor)	// inverse scaling
	Rod intersect(Tag to) 		// intersect line with me and to
	Loc intersect(Rod ln) 		// intersect point with me and ln
	float angle(Tag to)		// angle between each normal vector, me and to
	float dist(Loc pt)		// distance between "me" and "pt", = length of ortho(pt)
	Rod ortho(Loc pt)		// the orthogonal rod connecting from "pt" to "me"
	boolean having(Loc pt)		// is "me" having "pt" on myself ?
	float length()			// length of me
	float width()			// width of me
	Loc stern()			// location of the stern of me, the base position
	Loc bow()			// location of the bow of me
	Loc starBoard()		// location of the star of me
	Loc fore()			// foward direction of me, the unit vector
	Loc star()			// right direction of me, the unit vector
	Loc normal()			// upword direction of me, the unit vector
	Rod keel()			// line model of me's front/end, the Rod
	Boolean equals(Tag to)
	String toString()
 */
public class Tag {
	protected Rod keel;
	protected Loc star;
	protected Loc normal;
	protected float width;
	private Tag() { // don't use this default constructor
	}
	public Tag(Loc s, Loc b, Loc r) {
		keel = new Rod(s,b);
		Loc diff = r.sub(s);
		star = diff.unit();
		width = diff.norm();
		normal = star.cross(keel.fore);
	}
	public Tag(Loc s, Loc normal) {
		keel = new Rod(s,new Loc(s.y,s.z,s.x));
		star = normal.cross(keel.fore).unit();
		width = 1;
		this.normal = normal;
	}
	public Tag(Tag frm) {
		keel = new Rod(frm.keel);
		star = new Loc(frm.star);
		width = frm.width;
		normal = new Loc(frm.normal);
	}
	public Object clone() {return new Tag(this);}
	public Loc at(float f, float s) {
		return keel.fore.mul(f).shift(star.mul(s)).shift(keel.stern);
	}
	public Tag move(Loc to) {
		keel.move(to);
		return this;
	}
	public Tag shift(Loc diff) {
		keel.shift(diff);
		return this;
	}
	public Tag shiftI(Loc diff) {
		keel.shiftI(diff);
		return this;
	}
	public Tag scale(float factor) {
		keel.scale(factor);
		width = width * factor;
		return this;
	}
	public Tag scaleI(float factor) {
		keel.scaleI(factor);
		width = width / factor;
		return this;
	}
	public Tag rotate(Loc ax, float angle) {
		keel.rotate(ax,angle);
		star.rotate(ax,angle);
		return this;
	}
	public Tag rotate(Rod ax, float angle) {
		keel.rotate(ax,angle);
		star.rotate(ax.fore,angle);
		return this;
	}
	public Rod intersect(Tag to) {	//  intersect line
	/*< Proposition > 
	 	let the intersect Rod be Po+t*[dir]
	 	then dir = [normal] x [tnormal] / |[normal] x [tnormal]|
	 	     ([stern]-P)*[normal] = 0  -> P*[normal] - [stern]*[normal] =0
	 	     and ([to.s]-P)*[to.n] = 0 -> P*[to.n] - [to.s]*[to.n] = 0
	    implies
	      P.x = dir.z*(normal.y*d2-ton.y*d1) - dir.y*(normal.z*d2-ton.z*d1)
	      P.y = dir.x*(normal.z*d2-ton.z*d1) - dir.z*(normal.x*d2-ton.x*d1)
	      P.z = dir.y*(normal.x*d2-ton.x*d1) - dir.x*(normal.y*d2-ton.y*d1)
	      	where
	    		  d1 = - [stern]*[normal]
	    		  d2 = - [to.s]*[to.n]
 	*/
		Loc dir = normal.cross(to.normal).unit();
		float d1 = -keel.stern.dot(normal);
		float d2 = -to.keel.stern.dot(to.normal);
		Loc p = new Loc(
			dir.z*(normal.y*d2-to.normal.y*d1) - dir.y*(normal.z*d2-to.normal.z*d1),
			dir.x*(normal.z*d2-to.normal.z*d1) - dir.z*(normal.x*d2-to.normal.x*d1),
			dir.y*(normal.x*d2-to.normal.x*d1) - dir.x*(normal.y*d2-to.normal.y*d1)
			);
		return new Rod(p, dir.shift(p));
	}
	public float angle(Tag to)	{	// angle between each normal vector, me and to
		return normal.angle(to.normal);
	}
	public Loc intersect(Rod ln)  {		// intersect point with me and ln
	/*< Proposition > 
	 	let the line ln be [s]+t*[f],
	    then  ([s]+t*[f] - Stern)*normal = 0
	    		- t = (stern-[s])・normal/([f]・normal) 
	*/
		float t = keel.stern().sub(ln.stern()).dot(normal)/ln.fore().dot(normal);
		return ln.at(t);
	}
	public Rod ortho(Loc pt)	{	// the orthogonal rod connecting from "pt" to "me"
		float t = keel.stern.sub(pt).dot(normal);
		return new Rod(pt,normal.mul(t).shift(pt));
	}
	public float dist(Loc pt) {	// orthogonal distance between "me" and pt
	/*< Proposition > 
	 	let the normal vec of me be N,
	        and the orthogonal vector via pt is pt+t*N
	    then
	    		- t = (stern-pt)・N/(N・N) 
	*/
		float t = keel.stern.sub(pt).dot(normal);
		return t;
	}
	public boolean having(Loc pt) {	// "me" and to are crossing each other ?
		return dist(pt)<Vec.ENOUGH_SMALL;
	}
	public float length() {return keel.length;}
	public Loc stern() {return new Loc(keel.stern);}
	public Loc bow() {return keel.stern.add(keel.fore.mul(keel.length));}
	public Loc fore() {return new Loc(keel.fore);}
	public float width() {return width;}
	public Loc starBoard() {return keel.stern.add(star.mul(width));}
	public Loc star() {return new Loc(star);}
	public Rod keel() {return new Rod(keel);}
//
	public boolean equals(Object object) {
		if (object == null ) return false;
		if (object == this ) return true;
		if (object instanceof Tag == false) return false;
		Tag m = (Tag) object;
		if (!keel.equals(m.keel)) return false;
		if (!star.equals(m.star)) return false;
		if (!normal.equals(m.normal)) return false;
		return true;
	}
	public String toString() {
		return "Tag["+keel.stern+" - "+bow()+" | "+starBoard()+"]";
	}
}
