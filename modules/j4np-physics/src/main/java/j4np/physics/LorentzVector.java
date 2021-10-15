/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.physics;

/**
 *
 * @author gavalian
 */
public class LorentzVector {

	Vector3 vector;
	double energy;

	/*
	 * Default constructor creates the 0 - length lorenz vector
	 */
	public LorentzVector() {
		vector = new Vector3();
		energy = 0.0;
	}
        
	public LorentzVector(LorentzVector v) {
		this.vector = new Vector3();
		this.vector.copy(v.vector);
		this.energy = v.energy;
	}


	public LorentzVector(double px, double py, double pz, double en) {
		vector = new Vector3();
		vector.setXYZ(px, py, pz);
		energy = en;
	}

        public static LorentzVector from(LorentzVector vL){
           return new LorentzVector(vL.px(),vL.py(),vL.pz(),vL.e());
        }

        public static LorentzVector withPxPyPzM(double px, double py, double pz, double m){
            LorentzVector v = new LorentzVector();
            v.setPxPyPzM(px, py, pz, m);
            return v;
        }
        public static LorentzVector add(LorentzVector v1, LorentzVector v2){
            LorentzVector vec = LorentzVector.from(v1);
            return vec.add(v2);
        }
        
	public void setPxPyPzE(double px, double py, double pz, double e) {
		vector.setXYZ(px, py, pz);
		energy = e;
	}

	public void setPxPyPzM(double px, double py, double pz, double m) {
		vector.setXYZ(px, py, pz);
		energy = Math.sqrt(m * m + vector.mag2());
	}

	public void setVectM(Vector3 vect, double m) {
		vector = vect;
		energy = Math.sqrt(m * m + vector.mag2());
	}

        public double angle(LorentzVector vec){
            return this.vector.angle(vec.vector);
        }
        
	public void rotateX(double angle) {
		vector.rotateX(angle);
	}

	public void rotateY(double angle) {
		vector.rotateY(angle);
	}

	public void rotateZ(double angle) {
		vector.rotateZ(angle);
	}

	public double px() {
		return vector.x();
	}

	public double py() {
		return vector.y();
	}

	public double pz() {
		return vector.z();
	}

	public double p() {
		return vector.mag();
	}
        
        public double pt(){
            return vector.rho();
        }
        
	public double theta() {
		return vector.theta();
	}

	public double phi() {
		return vector.phi();
	}

	public double mass2() {
		return (energy * energy - vector.mag2());
	}

	public double mass() {
		double m2 = this.mass2();
		if (m2 < 0)
			return -Math.sqrt(-m2);
		return Math.sqrt(m2);
	}

	public double e() {
		return energy;
	};

	void setE(double e) {
		energy = e;
	}

        public void reset(){
            vector.setXYZ(0.0, 0.0, 0.0);
            energy = 0.0;
        }
        
        @Override
        public String toString(){
            String str = String.format("%9.4f %9.4f %9.4f %9.4f", vector.x(),vector.y(),vector.z(),mass());
            return str;
        }
	public Vector3 boostVector() {
		if (energy == 0)
			return new Vector3(0., 0., 0.);
		return new Vector3(px() / energy, py() / energy, pz() / energy);
	}

	/*
	 * Boosts the vector to the x y z components of a given vector.
	 */
	public void boost(double bx, double by, double bz) {
		double b2 = bx * bx + by * by + bz * bz;
		double gamma = 1.0 / Math.sqrt(1.0 - b2);
		// System.out.println("GAMMA = " + gamma + " b2 = " + b2);
		double bp = bx * px() + by * py() + bz * pz();
		double gamma2 = b2 > 0 ? (gamma - 1.0) / b2 : 0.0;

		vector.setXYZ(px() + gamma2 * bp * bx + gamma * bx * e(), py() + gamma2 * bp * by + gamma * by * e(),
		        pz() + gamma2 * bp * bz + gamma * bz * e());
		energy = gamma * (e() + bp);
	}

	public void copy(LorentzVector vect) {
		energy = vect.e();
		vector.setXYZ(vect.px(), vect.py(), vect.pz());
	}

	public void boost(Vector3 vect) {
		boost(vect.x(), vect.y(), vect.z());
	}

	public Vector3 vect() {
		return vector;
	}

	public LorentzVector add(LorentzVector vLor) {
		vector.add(vLor.vect());
		energy = this.e() + vLor.e();
                return this;
	}
        
        public LorentzVector add(double px, double py, double pz, double mass){
            vector.add(px, py, pz);
            energy = energy + Math.sqrt(px*px+py*py+pz*pz+mass*mass);
            return this;
        }
        
	public LorentzVector sub(LorentzVector vLor) {
            vector.sub(vLor.vect());
            energy = this.e() - vLor.e();
                return this;
	}
        
        public LorentzVector sub(double px, double py, double pz, double mass){
            vector.sub(px, py, pz);
            energy = energy - Math.sqrt(px*px+py*py+pz*pz+mass*mass);
            return this;
        }
        
	public void invert() {
            this.vector.setXYZ(-this.vector.x(), -this.vector.y(), -this.vector.z());
            this.energy = -this.energy;
	}
        
	public void print() {
            System.out.format("L Vect : %12.6f %12.6f %12.6f %12.6f %12.6f\n", this.px(), this.py(), this.pz(), this.p(), this.mass());
	}
}
