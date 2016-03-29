import com.google.gson.Gson;

public class Convert {

	public static void main(String[] args) {
		Data data = new Data(1, "test");
		Gson gson = new Gson();
		String json = gson.toJson(data);
		System.out.println(json);
		
		Data data2 = gson.fromJson(json, Data.class);
		System.out.println(data2);
	}
}
