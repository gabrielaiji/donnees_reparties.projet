public class MyInteger implements java.io.Serializable {
	int 		integer;
	public MyInteger() {
		integer = 0;
	}
	
	public void incr() {
		integer++;
	}
	public int getInt() {
		return integer;	
	}
	
}