package client;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
//import retrofit.http.Query;

public interface EndpointInterface {
	@GET("/last")
	Call<Message> getLast();
	
	/*@POST("/sumForm")
	Call<Message> sumForm(@Query("Num1") int num1, @Query("Num2") int num2);*/
	
	@FormUrlEncoded
	@POST("/sumForm")
	Call<Message> sumForm(@Field("Num1") int num1, @Field("Num2") int num2);
	
	@POST("/sumJson")
	Call<Message> sumJson(@Body Message msg);
	
	@FormUrlEncoded
	@PUT("/updateFile")
	Call<String> updateFile(@Field("fileName") String fileName, @Field("fileSize") int fileSize);
}
