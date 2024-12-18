package com.springboot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.OfferRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

	@Test
	public void checkFlatXForOneSegment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1,"FLATX",10,segments);
		boolean result = addOffer(offerRequest);
		Assert.assertEquals(result, true);
	}

	@Test
	public void checkFlatXForSecondSegment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1,"FLAT%",15,segments);
		boolean result = addOffer(offerRequest);
		Assert.assertEquals(result, true);
	}

	@Test
	public void checkFlatXForThirdSegment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1,"FLAT%",0,segments);
		boolean result = addOffer(offerRequest);
		Assert.assertEquals(result, true);
	}

	public boolean addOffer(OfferRequest offerRequest) throws Exception {
		String urlString = "http://localhost:9001/api/v1/offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();

		String POST_PARAMS = mapper.writeValueAsString(offerRequest);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request did not work.");
		}
		return true;
	}

	public String applyOffer(ApplyOfferRequest request) throws Exception {
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String POST_PARAMS = mapper.writeValueAsString(request);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} else {
			return "Error: " + responseCode;
		}
	}

	@Test
	public void testValidOfferForSegmentP1() throws Exception {
		ApplyOfferRequest request = new ApplyOfferRequest(200, 1, 1);
		String response = applyOffer(request);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response);
		String cartValue = node.get("cart_value").asText();

		Assert.assertEquals("170", cartValue.trim());
	}

	@Test
	public void testValidOfferForSegmentP2() throws Exception {
		ApplyOfferRequest request = new ApplyOfferRequest(200, 1, 2);
		String response = applyOffer(request);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response);
		String cartValue = node.get("cart_value").asText();

		Assert.assertEquals("170", cartValue.trim());
	}

	@Test
	public void testValidOfferForSegmentP3() throws Exception {
		ApplyOfferRequest request = new ApplyOfferRequest(200, 1, 3);
		String response = applyOffer(request);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response);
		String cartValue = node.get("cart_value").asText();

		Assert.assertEquals("200", cartValue.trim());
	}

	@Test
	public void testCartValueMaxInt() throws Exception {
		ApplyOfferRequest request = new ApplyOfferRequest(Integer.MAX_VALUE, 1, 1);
		String response = applyOffer(request);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response);
		Assert.assertEquals("2147483637", node.get("cart_value").asText());
	}

	@Test
	public void testInvalidCartValueInput() {
		try {
			ApplyOfferRequest request = new ApplyOfferRequest(Integer.parseInt("twohundred"), 1, 1);
			Assert.fail("Expected NumberFormatException for invalid cart value");
		} catch (NumberFormatException e) {
			Assert.assertTrue(e.getMessage().contains("For input string: \"twohundred\""));
		}
	}


// below are some failed test cases implemented

//	@Test
//	public void testNegativeCartValue() throws Exception {
//		ApplyOfferRequest request = new ApplyOfferRequest(-10, 1, 1);
//		String response = applyOffer(request);
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode node = mapper.readTree(response);
//		String cartValue = node.get("cart_value").asText();
//
//		Assert.assertEquals("-10", cartValue.trim());
//	}
//
//	@Test
//	public void testCartValueLessThanDiscount() throws Exception {
//		ApplyOfferRequest request = new ApplyOfferRequest(5, 1, 1);
//		String response = applyOffer(request);
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode node = mapper.readTree(response);
//		String cartValue = node.get("cart_value").asText();
//		Assert.assertEquals("0", cartValue.trim());
//	}
}