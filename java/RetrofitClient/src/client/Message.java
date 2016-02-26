package client;

import com.google.gson.annotations.SerializedName;

public class Message {
	
	@SerializedName("Num1")
	int num1;
	
	@SerializedName("Num2")
	int num2;
	
	@SerializedName("Res")
	int res;
	
	public Message(int Num1, int Num2, int Res) {
		this.num1 = Num1;
		this.num2 = Num2;
		this.res = Res;
	}

	@Override
	public String toString() {
		return "{\"Num1\":" + num1 + ",\"Num2\":" + num2 + ",\"Res\":" + res + "}";
	}
}
