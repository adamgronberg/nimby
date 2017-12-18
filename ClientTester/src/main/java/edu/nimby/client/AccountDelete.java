package edu.nimby.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
/**
 * Similar structure will be used when getting content from nimbyAccServer
 * @author Mikael Stolpe
 *
 */
public class AccountDelete {

	public static void main(String[] args) {
		try {

			ClientRequest request = new ClientRequest(
					"http://localhost:8080/nimbyAccServer/Account/delete/" + tokenStrings.Majaakissaaa + "/developer");
			ClientResponse<String> response = request.delete(String.class);


			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

				System.out.println(output);
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}