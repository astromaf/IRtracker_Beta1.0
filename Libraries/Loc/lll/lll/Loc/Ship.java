package lll.Loc;
/* 
* Class Tag, the model of partial space on the 3D space,
 * 			spanned from it's stern and star to it's bow and port
 *					created by Classiclll, 7/28/2008
 *  	Ship(Loc s, Loc b, Loc r)	// only constructor you can use, and
 *  	Ship(Tag frm)			// constructor you can use
 *  	Loc at(float f, float s, float h)	// location at (s,b,u),
					// at(0,0,0):stern, at(0,me.length,0):bow, at(me.width,0,0):star
	Ship move(Loc to)		// move stern to "to"
	Ship shift(Loc diff)		// shift stern by "diff"
	Ship shiftI(Loc diff)		// inverse shift
	Ship scale(float factor)		// scale the size of me by factor
	Ship scaleI(float factor)	// inverse scaling
	boolean having(Loc pt)		// is "me" having "pt" on myself ?
	float length()			// length of me
	float width()			// width of me
	float hight()			// hight of me
	Loc stern()			// location of the stern of me, the base position
	Loc bow()			// location of the bow of me
	Loc starBoard()		// location of the star of me
	Loc bridgeTop()		// location of the bridge of me
	Loc fore()			// foward direction of me, the unit vector
	Loc star()			// right direction of me, the unit vector
	Loc up()				// upword direction of me, the unit vector
	Rod keel()			// front/end line of me, the Rod
	Tag sea()			// horrisontal plane of me, the Tag
	Boolean equals(Tag to)
	String toString()
 */
public class Ship {
	protected Loc bridge;
	protected float height;
	protected Tag sea;

	public Ship(Loc s, Loc b, Loc r, Loc h) {
		sea = new Tag(s, b, r);
		bridge = new Loc(h);
		height = bridge.norm();
	}
	public Ship(Tag frm, Loc h) {
		sea = new Tag(frm);
		bridge = new Loc(h);
		height = bridge.norm();
	}
	public Ship(Ship frm) {
		sea = new Tag(frm.sea);
		bridge = new Loc(frm.bridge);
		height = bridge.norm();
	}
//
	public Object clone() {return new Ship(this);}
	
	public Loc at(float f, float s, float h) {
		return sea.keel.fore.mul(f).shift(sea.star.mul(s))
					.shift(bridge.mul(h)).shift(sea.keel.stern);
	}
	public Ship move(Loc to) {
		sea.keel.move(to);
		return this;
	}
	public Ship shift(Loc diff) {
		sea.keel.shift(diff);
		return this;
	}
	public Ship shiftI(Loc diff) {
		sea.keel.shiftI(diff);
		return this;
	}
	public Ship scale(float factor) {
		sea.keel.scale(factor);
		sea.width *=  factor;
		return this;
	}
	public Ship scaleI(float factor) {
		sea.keel.scaleI(factor);
		sea.width /= factor;
		return this;
	}
	public Ship rotate(Loc ax, float angle) {
		sea.rotate(ax,angle);
		bridge.rotate(ax,angle);
		return this;
	}
	public Ship rotate(Rod ax, float angle) {
		sea.rotate(ax,angle);
		bridge.rotate(ax.fore,angle);
		return this;
	}
	public float length() {return sea.keel.length;}
	public float width() {return sea.width;}
	public float height() {return height;}
	public Loc stern() {return new Loc(sea.keel.stern);}
	public Loc bow() {return sea.keel.stern.add(sea.keel.fore.mul(sea.keel.length));}
	public Loc starBoard() {return sea.keel.stern.add(sea.star.mul(sea.width));}
	public Loc bridgeTop() {return sea.keel.stern.add(bridge.mul(height));}
	public Loc fore() {return new Loc(sea.keel.fore);}
	public Loc star() {return new Loc(sea.star);}
	public Loc up() {return new Loc(bridge);}
	public Rod keel() {return sea.keel;}
	public Tag sea() {return new Tag(sea);}
//
	public boolean equals(Object object) {
		if (object == null ) return false;
		if (object == this ) return true;
		if (object instanceof Ship == false) return false;
		Ship m = (Ship) object;
		if (Float.floatToIntBits(sea.keel.length) != 
    			Float.floatToIntBits(m.sea.keel.length)) return false;
		if (!sea.equals(m.sea)) return false;
		if (!bridge.equals(m.bridge)) return false;
		return true;
	}
	public String toString() {
		return "Ship["+sea.keel.stern+" - "+bow()+"|"+starBoard()+"|"+bridge+"]";
	}

}
