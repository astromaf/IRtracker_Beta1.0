package lll.Loc;
//=============================================
/*[class] Loc              5/1/2006 by Classiclll
                          6/24/2006 modified
                          6/6/2007 Librarized.
 the model of geometric point with location and geometric calculations.
 [members]
 float x, y, z; :location of 3D space.
 double TOL;  common tolerance constant
 
 [constructer]
 Loc(float lx, float ly, float lz)        :construct a Loc object with 3D location.
 Loc(Loc l)                               :cloninng
 Loc(float lng, float ltt)                :construct UnitVector with Polar expression.
 
 [methods]
 <group #0 - Generator>
 static Loc newLoc(float x, float y, float z) :same as 'new Loc(x,y,z)'
 static Loc newPolar(float r, float lng, float ltt)  :get the new Loc at (r,longitude,latitude)
 
 <group #1 - Scalar result - without Side Effect>
 float longitude()                        :get longitude of me
 float latitude()                         :get latitude of me
 float dist(float tx, float ty, float tz) :measure the distance to (xtx,ty,tz). (euclid distance) 
 float dist(Loc loc)                      :measure the distance to loc. (euclid distance)
 float dist1(Loc loc)                     :measure the distance to loc. (manhattan distance)
 float dist2(Loc loc)                     :measure square of the distance. (euclid distance)
 float norm()                             :get measure the norm of me. = sqrt(sq(x)+sq(y)+sq(z))
 float innerP(Loc l)                      :get the inner production of l, same to dot().
 float dot(Loc to)                        :dot operator, = norm(me)*norm(l)*cos(angle)
 float cosine(Loc l)                      :get cosine between l and me. = dot/(norm(me)+norm(l))
 float angle(Loc l)                       :get angle(-PI~PI) between me&l, = acos(me.cosine(l))
 boolean isSameGrid(Loc to)               :Am "I" on the same Grid Point to "to"?
 boolean isEqualTo(Loc to)                :Am "I" the same location to "to"?
 boolean isNearTo(Loc to)                 :Am "I" near to "to"? (based on euclid distance)
 boolean isNearTo(Loc to, float tol)      :Am "I" near to "to"? (based on euclid distance)
 boolean isNear1To(Loc to)                :Am "I" near to "to"? (based on manhattan distance)
 boolean isNear1To(Loc to, float tol)     :Am "I" near to "to"? (based on manhattan distance)
 boolean isWithin(Loc another, float tol) :Is "another" in the range "tol" from me?(fast)
 String toString()                        :get the string expression by axis.
 
 <group #2 - Vector result - without Side Effect>
 Loc clone()                              :newLoc having same location of me
 Loc getPlar()                            :newLoc having a polar coord of me (r,lg,lt)
 Loc getPlarDiff(Loc to)                  :newLoc having a polar coord diff bet "me" to "to"
 Loc getPlarDiff(float dx, float dy, float dz) :newLoc having a polar coord diff
 Loc add(Loc off)                         :newLoc equl to me + ofst (elementwise - Loc ver.)
 Loc add(float dx, float dy, float dz)    :newLoc equl to me + ofst (elementwise - float ver.)
 Loc sub(Loc off)                         :newLoc equl to me - ofst (elementwise - Loc ver.)
 Loc sub(float dx, float dy, float dz)    :newLoc equl to me - ofst (elementwise - float ver.)
 Loc inv()                                :newLoc equl to inverse direction of me. 
 Loc mul(float fact)                      :newLoc equl to scalar production of me.
 Loc mul(Loc fact)                        :newLoc equl to elementwise scalar production of me.
 Loc div(float fact)                      :newLoc equl to scalar division of me.
 Loc div(Loc fact)                        :newLoc equl to elementwise scalar division of me.
 Loc projection(Loc dirX, Loc dirY, Loc dirZ):newLoc projected to the space spaned by given 3-dir's
 Loc outerP(Loc l)                        :newLoc equl to outer production, same to cross().
 Loc cross(Loc to)                        :cross operator,  get newLoc orthogonal to me and l
 Loc unit()                               :newLoc equl to unit vector allong to me.( norm(v)=1 ) 
 Loc dir(Loc l)                           :newLoc equl to direction vector from me to Loc l.
 Loc ortho(Loc l)                         :newLoc orthogonal unit vector from l to me.
 Loc turned(Loc shaft, float angle)       :newLoc turned arround the direction of 'shaft' vector
 Loc turned(float pan, float swing, float tilt):newLoc turned to pan(Zax)->swing(Yax)->tilt(Xax).
 Loc turnedZ(float pan)                   :newLoc turned arround Zax of the World coordinate.
 Loc turnedY(float swing)                 :newLoc turned arround Yax of the World coordinate.
 Loc turnedX(float tilt)                  :newLoc turned arround Xax of the World coordinate.

 <group #3 - Vector result - with Self Side Effect>
 Loc movePolar(float r, float lng, float ltt)  :move me to Polar loc at (r,longi,latti)
 Loc shiftPolar(float r, float lng, float ltt) :shift me to Polar dir with (r,longi,latti)
 Loc move(float dx, float dy, float dz)   :move me to another location. (float ver.)
 Loc move(Loc pos)                        :move me to another location. (Loc ver.)
 Loc shift(float dx, float dy, float dz)  :shift me from current to offset location.
 Loc shift(Loc dlt)                       :shift me from current to offset location.
 Loc scale(Loc fact)                      :scale me based vector.(multipled elementwise)
 Loc scale(float fact)                    :scale me based scalar.(same for each coordinates)
 Loc shiftI(Loc dlt)                      :inverse shift me from current to offset location. 
 Loc scaleI(Loc fact)                     :inverse scale me based vector.(devided elementwise)
 Loc scaleI(float fact)                   :inverse scale me based scalar.
 Loc rotate(Loc shaft, float angle)       :rotate me arround the direction of 'shaft' vector
 Loc rotate(float pan, float swing, float tilt):rotate me to pan(Zax)->swing(Yax)->tilt(Xax).
 Loc rotateZ(float pan)                   :rotate me to pan(arround Z of the World coordinate).
 Loc rotateY(float swing)                 :rotate me to swing(arround Y of the World coordinate).
 Loc rotateX(float tilt)                  :rotate me to tilt(arround X of the World coordinate).
 */
//=============================================
public class Loc{
  public float x, y, z;
  public double TOL = 1e-5;
  //--------------------------------------
  public Loc(){
    x = 0; 
    y = 0;  
    z = 0;
  }
  //--------------------------------------
  public Loc(float lx, float ly, float lz){
    x = lx; 
    y = ly;  
    z = lz;
  }
  //--------------------------------------
  public Loc(Loc l) {
    x = l.x; 
    y = l.y;  
    z = l.z;
  }
  //--------------------------------------  
  public Loc(float lng, float ltt){
    float clt = (float)Math.cos(ltt);
    float slt = (float)Math.sin(ltt);
    float cln = (float)Math.cos(lng);
    float sln = (float)Math.sin(lng);
    x = cln*clt;
    y = cln*slt;
    z = sln;
  }
  //--------------------------------------  
static Loc newLoc(float x, float y, float z) { return new Loc(x,y,z); }
  //--------------------------------------  
static Loc newPolar(float r, float lng, float ltt) {
    float clt = (float)Math.cos(ltt);
    float slt = (float)Math.sin(ltt);
    float cln = (float)Math.cos(lng);
    float sln = (float)Math.sin(lng);
    return new Loc(r*cln*clt, r*cln*slt, r*sln);
}
  //--------------------------------------  
 public Loc movePolar(float r, float lng, float ltt){
    float clt = (float)Math.cos(ltt);
    float slt = (float)Math.sin(ltt);
    float cln = (float)Math.cos(lng);
    float sln = (float)Math.sin(lng);
    x = r * cln*clt;
    y = r * cln*slt;
    z = r * sln;
    return this;
  }
  //--------------------------------------  
  public Loc shiftPolar(float r, float lng, float ltt){
    float clt = (float)Math.cos(ltt);
    float slt = (float)Math.sin(ltt);
    float cln = (float)Math.cos(lng);
    float sln = (float)Math.sin(lng);
    x += r * cln*clt;
    y += r * cln*slt;
    z += r * sln;
    return this;
  }
  //--------------------------------------  
  public float longitude(){
	    float r = norm();
	    return (r>TOL ? (float)Math.asin(z/r) : 0);
	  }
  //--------------------------------------  
  public float latitude(){
    return (Math.abs(x)>TOL ? (float)Math.atan2(y,x):0);
  }
  //--------------------------------------  
  public Loc getPolar(){
    return new Loc(norm(),longitude(),latitude());
  }
  //--------------------------------------  
  public Loc getPolarDiff(Loc p){
    return getPolar().shiftI(p.getPolar());
  }
  //--------------------------------------  
  public Loc getPolarDiff(float tx, float ty, float tz){
    return getPolarDiff(new Loc(tx, ty, tz));
  }
  //--------------------------------------
  public boolean isWithin(Loc another, float tol){
    if ((float)Math.abs(x-another.x)>tol) return false;    
    if ((float)Math.abs(y-another.y)>tol) return false;    
    if ((float)Math.abs(z-another.z)>tol) return false;    
    if (this.dist(another)>tol) return false;
    return true;
  }
  //--------------------------------------
  public Loc move(float dx, float dy, float dz){
    x = dx; 
    y = dy; 
    z = dz;
    return this;    
  }
  //--------------------------------------
  public Loc move(Loc pos){
    x = pos.x; 
    y = pos.y; 
    z = pos.z;    
    return this;    
  }
  //--------------------------------------
  public Loc shift(float dx, float dy, float dz){
    x += dx; 
    y += dy; 
    z += dz;    
    return this;    
  }
  //--------------------------------------
  public Loc shift(Loc dlt){
    x += dlt.x; 
    y += dlt.y; 
    z += dlt.z;    
    return this;    
  }
  //--------------------------------------
  public Loc scale(float fact){
    x *= fact; 
    y *= fact; 
    z *= fact;    
    return this;    
  }
  //--------------------------------------
  public Loc scale(Loc fact){
    x *= fact.x; 
    y *= fact.y; 
    z *= fact.z;    
    return this;    
  }
  //--------------------------------------
  public Loc shiftI(Loc dlt){
    x -= dlt.x; 
    y -= dlt.y; 
    z -= dlt.z;    
    return this;    
  }
  //--------------------------------------
  public Loc scaleI(Loc fact){
    x /= fact.x; 
    y /= fact.y; 
    z /= fact.z;    
    return this;    
  }
  //--------------------------------------
  public Loc scaleI(float fact){
    x /= fact; 
    y /= fact; 
    z /= fact;    
    return this;    
  }
  //--------------------------------------!!!!!!!!!  
  public Loc rotate(Loc shaft, float angle){
    float nx, ny, nz;
    float cs = (float)Math.cos(angle);
    float ic = (1-cs);
    float sn = (float)Math.sin(angle);
    Loc sh = shaft.unit();
    nx = x*(cs+ic*sq(sh.x))       + y*(ic*sh.y*sh.x+sn*sh.z) + z*(ic*sh.z*sh.x-sn*sh.y);
    ny = x*(ic*sh.x*sh.y-sn*sh.z) + y*(cs+ic*sq(sh.y))       + z*(ic*sh.z*sh.y+sn*sh.x);
    nz = x*(ic*sh.x*sh.z+sn*sh.y) + y*(ic*sh.y*sh.z-sn*sh.x) + z*(cs+ic*sq(sh.z));
    x = nx;
    y = ny;
    z = nz;
    return this;    
  }
  //--------------------------------------  
  public Loc rotate(float pan, float swing, float tilt){
	    float nx, ny, nz;
	    float cp = (float)Math.cos(pan);
	    float sp = (float)Math.sin(pan);
	    float cs = (float)Math.cos(swing);
	    float ss = (float)Math.sin(swing);
	    float ct = (float)Math.cos(tilt);
	    float st = (float)Math.sin(tilt);
	    nx = x*cs*ct + y*cs*st - z*ss;
	    ny = x*(st*ss*cp-ct*sp) + y*(st*ss*sp+ct*cp) + z*st*cs;
	    nz = x*(ct*ss*cp+st*sp) + y*(ct*ss*sp-st*cp) + z*ct*cs;
	    x = nx;
	    y = ny;
	    z = nz;
	    return this;    
//	    return rotateZ(pan).rotateY(swing).rotateX(tilt);  
	  }
	  //--------------------------------------  
  public Loc rotateZ(float pan){
    float nx, ny;
    float ct = (float)Math.cos(pan);
    float st = (float)Math.sin(pan);
    nx = x*ct + y*st;
    ny = -x*st + y*ct;
    x = nx;
    y = ny;
    return this;    
  }
  //--------------------------------------  
  public Loc rotateY(float swing){
    float nx, nz;
    float ct = (float)Math.cos(swing);
    float st = (float)Math.sin(swing);
    nx = x*ct - z*st;
    nz = x*st + z*ct;
    x = nx;
    z = nz;
    return this;    
  }
  //--------------------------------------  
  public Loc rotateX(float tilt){
    float ny, nz;
    float ct = (float)Math.cos(tilt);
    float st = (float)Math.sin(tilt);
    ny = y*ct + z*st;
    nz = -y*st + z*ct;
    y = ny;
    z = nz;
    return this;    
  }
  //--------------------------------------
  public float dist(float tx, float ty, float tz){
    return dist(x, y, z, tx, ty, tz) ;
  }
  //--------------------------------------
  public float dist(Loc loc){
    return dist(x, y, z, loc.x, loc.y, loc.z) ;
  }
  //--------------------------------------
  public float dist0(Loc loc){
    return Math.max(Math.max(Math.abs(x-loc.x),Math.abs(y-loc.y)),Math.abs(z-loc.z));    
  }
  //--------------------------------------
  public float dist1(Loc loc){
    return Math.abs(x-loc.x)+Math.abs(y-loc.y)+Math.abs(z-loc.z);    
  }
  //--------------------------------------
  public float dist2(Loc loc){
    return (x-loc.x)*(x-loc.x)+(y-loc.y)*(y-loc.y)+(z-loc.z)*(z-loc.z);    
  }
  //--------------------------------------
  protected Object clone() { return new Loc(x,y,z);}
  //--------------------------------------
  public Loc add(Loc off){
    return this.add(off.x, off.y, off.z);    
  }
  //--------------------------------------
  public Loc add(float dx, float dy, float dz){
    return new Loc(x+dx, y+dy, z+dz);    
  }
  //--------------------------------------
  public Loc sub(Loc off){
    return this.sub(off.x, off.y, off.z);    
  }
  //--------------------------------------
  public Loc sub(float dx, float dy, float dz){
    return new Loc(x-dx, y-dy, z-dz);    
  }
  //--------------------------------------
  public Loc inv(){
    return new Loc(-x, -y, -z);
  }
  //--------------------------------------  
  public Loc mul(float fact){
    return new Loc(x*fact, y*fact, z*fact);    
  }
  //--------------------------------------  
  public Loc mul(Loc fact){
    return new Loc(x*fact.x, y*fact.y, z*fact.z);    
  }
  //--------------------------------------
  public Loc div(float fact){
    return new Loc(x/fact, y/fact, z/fact);    
  } 
  //--------------------------------------
  public Loc div(Loc fact){
    return new Loc(x/fact.x, y/fact.y, z/fact.z);    
  } 
  //--------------------------------------
  public Loc projection(Loc dx, Loc dy, Loc dz){
    return new Loc(this.dot(dx), this.dot(dy), this.dot(dz));    
  } 
  //--------------------------------------
  public float norm(){
    return (float) Math.sqrt(x*x+y*y+z*z) ;
  }  
  //--------------------------------------
  public float norm0(){
    return Math.max(Math.max(Math.abs(x),Math.abs(y)),Math.abs(z));
  }  
  //--------------------------------------
  public float norm1(){
    return Math.abs(x)+Math.abs(y)+Math.abs(z);
  }  
  //--------------------------------------
  public float innerP(Loc l){
    return this.dot(l);    
  }  
  //--------------------------------------  
  public float dot(Loc to){
    return x*to.x + y*to.y + z*to.z;
  }
  //--------------------------------------
  public float cosine(Loc l){
	float norm2 = norm()*l.norm();
    return (norm2<TOL ? 1f : dot(l)/norm2);
  }  
  //--------------------------------------
  public float angle(Loc l){
    return (float)Math.acos(cosine(l));
  }  
  //--------------------------------------
  public float antiClockwiseAngle(Loc l){
    double ang = Math.acos(cosine(l));
    float x=l.dot(this.cross(l).cross(this));
    return (float) (Math.abs(x)<TOL ? 0 :(x>0 ? ang : 2*Math.PI-ang));
  }  
  //--------------------------------------
  public float angle(float tx, float ty, float tz){
    return (float)Math.acos(cosine(new Loc(tx,ty,tz)));
  }  
  //--------------------------------------  
  public Loc outerP(Loc l){
    return this.cross(l);
  }  
  //--------------------------------------  
  public Loc cross(Loc to){
    return new Loc(y*to.z-z*to.y,z*to.x-x*to.z,x*to.y-y*to.x);
  }
  //--------------------------------------  
  public Loc unit(){
    float nr = this.norm();
    return (nr<TOL ? new Loc(this) : this.div(nr));    
  }  
  //--------------------------------------  
  public Loc dir(Loc l){
    return l.sub(this).unit();    
  }
  //--------------------------------------  
  public Loc ortho(Loc l){
    return l.sub(this.mul(this.innerP(l))).unit();
  }
  //--------------------------------------!!!!!!!!!  
  public Loc turned(Loc shaft, float angle){
	  return new Loc(this).rotate(shaft, angle);
  }
  //--------------------------------------  
  public Loc turned(float pan, float swing, float tilt){
	  return new Loc(this).rotate(pan,swing,tilt);
  }
  //--------------------------------------  
  public Loc turnedZ(float pan){
	    return new Loc(this).rotateZ(pan);
  }
  //--------------------------------------  
  public Loc turnedY(float swing){
	    return new Loc(this).rotateY(swing);
  }
  //--------------------------------------  
  public Loc turnedX(float tilt){
    return new Loc(this).rotateX(tilt);
  }
  //--------------------------------------  
  public boolean isSameGrid(Loc to){
    return Math.round(x)==Math.round(to.x)&&Math.round(y)==Math.round(to.y)&&Math.round(z)==Math.round(to.z);
  }
  //--------------------------------------  
  public boolean isEqualTo(Loc to){
    return x==to.x&&y==to.y&&z==to.z;
  }
  //--------------------------------------  
  public boolean isNearTo(Loc to){
    return dist(to)<TOL;
  }
  //--------------------------------------  
  public boolean isNearTo(Loc to, float tol){
    return dist(to)<tol;
  }
  //--------------------------------------  
  public boolean isNearTo(int toX, int toY){
    return dist(toX,toY,0)<TOL;
  }
  //--------------------------------------  
  public boolean isNearTo(int toX, int toY, float tol){
    return dist(toX,toY,0)<tol;
  }
  //--------------------------------------  
  public boolean isNear1To(Loc to){
    return dist1(to)<TOL;
  }
  //--------------------------------------  
  public boolean isNear1To(Loc to, float tol){
    return dist1(to)<tol;
  }
  //--------------------------------------  
  public int whichDir(Loc to){ //-1:reverse dir., 0:not on the same line, 1:same dir.
	float t = cosine(to);
    return (Math.abs(1.0-Math.abs(t))>TOL? 0 :(t>0 ? 1 : -1));
  }
  //--------------------------------------  
  static private float dist (float x0, float y0, float z0, float x1, float y1, float z1) {
	  return (float) Math.sqrt((x0-x1)*(x0-x1)+(y0-y1)*(y0-y1)+(z0-z1)*(z0-z1));
  }
  static private float sq(float x) {
	  return x*x;
  }
  public String toString() {
    return "Loc("+x+","+y+","+z+") ";
  }
  public boolean equals(Object object) {
      if (object == null ) return false;
      if (object == this ) return true;
      if (object instanceof Loc == false) return false;
      Loc m = (Loc) object;
      if (Float.floatToIntBits(x) != 
    	  	Float.floatToIntBits(m.x)) return false;
      if (Float.floatToIntBits(y) != 
    	  	Float.floatToIntBits(m.y)) return false;
      if (Float.floatToIntBits(z) != 
    	  	Float.floatToIntBits(m.z)) return false;
      return true;
  }
}

