package edu.nimby.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
/**
 * Similar structure will be used when creating content to nimbyAccServer
 * @author Mikael Stolpe
 *
 */
public class ShipUpdate {

	public static void main(String[] args) {

		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/nimbyAccServer/Ship/update/-3918567022230039415");
			request.accept("application/json");

			String input = 
				"{" +
					   "\"id\":{" +
					      "\"name\":\"Awesomness\"," +
					      "\"builder\":\"Majkisa\"" +
					   "}," +
					   "\"description\":\"this is an updated ship\""+
					"}";
			request.body("application/json", input);
			
			ClientResponse<String> response = request.put(String.class);

//			if (response.getStatus() != 201) {
//				String output = "Failed : HTTP error code : " + response.getStatus();
//				System.out.println(output);
//			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

				System.out.println(output);
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}