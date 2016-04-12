
public class AnyClass implements AnyInterface {
	public int field1;
	private char field2;
	private String field3;
	
	public AnyClass(int f1, char f2, String f3) {
		field1 = f1;
		field2 = f2;
		field3 = f3;
	}
	
	public AnyClass() {
		this(0, '\u0000', null);
	}
	
	public int getField1() { return field1; }
	public char getField2() { return field2; }
	public String getField3() { return field3; }

	@Override
	public String toString() {
		String str = field1 + " " + field2 + " ";
		str += field3;
		return  str;
	}

	@Override
	public int anyIterfaceMethod() {
		return 0;
	}
}
