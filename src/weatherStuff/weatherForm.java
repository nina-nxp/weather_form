package weatherStuff;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
class Form extends JFrame
{
   private JTextField textField1;
  
	Form()
	{
	    JFrame frame1 = new JFrame();
	   
	    JPanel panel1 = new JPanel();
	    panel1.setLayout(null);
	    panel1.setBackground(Color.LIGHT_GRAY);
	    frame1.add(panel1); // == form1.getContentPane().add(panel1)
	   
	    JLabel label1 = new JLabel();
	    label1.setText("City:");
	    label1.setBounds(10,10,165,30);
	    panel1.add(label1);
	    textField1 = new JTextField(10);
	    textField1.setBounds(50, 10, 160, 30);
	    panel1.add(textField1);
	    JLabel label2 = new JLabel();
	    label2.setText("Current time:");
	    label2.setBounds(10,45,215,30);
	    panel1.add(label2);
	    JLabel label3 = new JLabel();
	    label3.setText("Current Temperature (F): ");
	    label3.setBounds(10,80,200,30);
	    panel1.add(label3);
	   
	    JLabel label4 = new JLabel();
	    label4.setText("Relative Humidity:");
	    label4.setBounds(10,115,200,30);
	    panel1.add(label4);
	   
	    JLabel label5 = new JLabel();
	    label5.setText("Weather Description:");
	    label5.setBounds(10,150, 200, 30);
	    panel1.add(label5);
	   
	    JButton button1 = new JButton("Save");
	    button1.setBounds(230, 180, 70, 30);
	    button1.addActionListener(new ActionListener ()
	    {
	        public void actionPerformed(ActionEvent e)
	        {
	        	try {
	        		String city = textField1.getText();
	        		JSONObject cityLocationData = (JSONObject) getLocationData(city);
					double latitude = (double) cityLocationData.get("latitude");
					double longitude = (double) cityLocationData.get("longitude");
					JSONObject data = displayWeatherData(latitude, longitude);
					String time = (String) data.get("time");
					label2.setText("Current time: " + time);
					
					double temperature = (double) data.get("temperature_2m");
					label3.setText("Current Temperature (F): " + temperature);
					
					long relativeHumidity = (long) data.get("relative_humidity_2m");
					label4.setText("Relative Humidity: " + relativeHumidity);
				    label4.setBounds(10,115,165,30);
					
					double windSpeed = (double) data.get("wind_speed_10m");
					label5.setBounds(10,150, 165, 30);
					label5.setText("Weather Description: " + windSpeed);
	        	}catch(Exception e1) {
	        		e1.printStackTrace();
	        	}
	        }
	    });
	   
	    panel1.add(button1);
	    frame1.setSize(325, 250);
	    frame1.setVisible(true);
	    frame1.setResizable(true);
	    frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}
private String readApiResponse(HttpURLConnection apiConnection) {
	try {
		StringBuilder resultJson = new StringBuilder();
		Scanner scanner = new Scanner(apiConnection.getInputStream());
		while(scanner.hasNext()) {
			resultJson.append(scanner.nextLine());
		}
		scanner.close();
		return resultJson.toString();
	}catch(IOException e) {
		e.printStackTrace();
	}
	return null;
}
private HttpURLConnection fetchApiResponse(String urlString) throws URISyntaxException {
	try {
		URI uri = new URI (urlString);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		conn.setRequestMethod("GET");
			
		return conn;
	}catch(IOException e) {
		e.printStackTrace();
	}
	return null;
}
private JSONObject getLocationData(String city){
	city = city.replaceAll(" ", "+");
		
	String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + city + "&count=1&language=en&format=json";
		
	try {
		HttpURLConnection apiConnection = fetchApiResponse(urlString);
			
		if(apiConnection.getResponseCode() != 200) {
			//label2.setText("Error: Could not connect to API");
			return null;
		}
			
		String jsonResponse = readApiResponse(apiConnection);
			
		JSONParser parser = new JSONParser();
		JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);
			
		JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
		return (JSONObject) locationData.get(0);
			
	}catch(Exception e) {
		e.printStackTrace();
	}
	return null;
}
private JSONObject displayWeatherData(double latitude, double longitude){
	try {
		String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m&temperature_unit=fahrenheit";
		HttpURLConnection apiConnection = fetchApiResponse(url);
			
		if(apiConnection.getResponseCode() != 200) {
			//label2.setText("Error: Could not connect to API");
			return null;
		}
		String jsonResponse = readApiResponse(apiConnection);
			
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
		JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");
		return currentWeatherJson;
	}catch(Exception e) {
		e.printStackTrace();
	}
	return null;
	}
}	
public class weatherForm {
	public static void main(String[] args) {
		Form form1 = new Form();
	}
}

