package answers;
import static com.freebase.json.JSON.a;
import static com.freebase.json.JSON.o;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;

public class freebaseReader {
	HashMap<String,String> entityCategory;
	public freebaseReader(){
		entityCategory=new HashMap<String,String>();
	}
	public static void readFreeBase(){
		Freebase freebase = Freebase.getFreebase();
		JSON query = o(
				"id", null,
				"type", "/film/film",
				"name", "Blade Runner",
				"directed_by", a(o(
						"id", null,
						"name", null
						))
				);


		JSON result=freebase.mqlread(query);
		String director=result.get("result").get("directed_by").get(0).get("name").string();
		//System.out.println(director);		


	}

	public static String readMQL(String entity)throws Exception{
		String ret;
        entity=entity.replace("\'", "");
		String url="https://api.freebase.com/api/service/mqlread?queries=";
		String query="{'q':{'query':{'name':'"+entity+"','type':[]}}}";
		url=url+query;


		url=url.replaceAll("'", "%22");
		url=url.replaceAll(" ", "%20");





		URL oracle = new URL(url);


		HttpURLConnection connection = (HttpURLConnection) oracle.openConnection();


		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String inputLine;


		StringBuffer sb=new StringBuffer();

		while ((inputLine = in.readLine()) != null)
			sb.append(inputLine);




		JSONParser parser = new JSONParser();


		JSONObject obj=(JSONObject)parser.parse(sb.toString());


		//System.out.println(obj);
		try{

			/*

        JSONObject obj1=(JSONObject)parser.parse(obj.get("q").toString());
        JSONArray obj2=(JSONArray)parser.parse(obj1.get("messages").toString());
        JSONObject obj3=(JSONObject)parser.parse(obj2.get(0).toString());
        JSONObject obj4=(JSONObject)parser.parse(obj3.get("info").toString());

        JSONArray obj5=(JSONArray)parser.parse(obj4.get("result").toString());

        JSONObject obj6=(JSONObject)parser.parse(obj5.get(0).toString());

        JSONArray obj7=(JSONArray)parser.parse(obj6.get("type").toString());
        return obj7.get(1).toString();
			 */
			Pattern p = Pattern.compile("common(.*)$");
			

			Matcher matcher = p.matcher(sb.toString().replaceAll("[^a-zA-Z0-9,]+"," "));
			String temp=sb.toString().replaceAll("[^a-zA-Z0-9,\\[\\]]+"," ");
			//System.out.println(temp);
			if(matcher.matches()){
				//System.out.println("some");
			}
			
			String[] topics=temp.split("type \\[");
			topics=topics[1].split("\\]");
			
			
			
			
			//topics=topics[1].split(",");
			
			return (topics[0]);
			
			
//            System.out.println(sb.toString().replaceAll("[^a-zA-Z0-9,]+"," "));
		}catch(Exception e){
			return null;
		}



	}

	public  HashMap<String,String> category(ArrayList<String> param1)throws Exception{

		ArrayList<Runnable> runs=new ArrayList<Runnable>();
		for(String key:param1){
			final String newKey=key;
			runs.add( new Runnable() {
				public void run() {
					synchronized (this) {
						try {
							//System.out.println(newKey);
							update(newKey);
						} catch (InterruptedException iex) {}
						//							notifyAll();
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			});	
		}
		Thread[] th=new Thread[runs.size()];
		for(int i=0;i<runs.size();i++){
			th[i]=new Thread(runs.get(i));
			th[i].start();
		}
		for(Thread t:th){
			t.join();
		}
		//		Boolean wait=true;
		//		while(wait){
		//			for(Thread t:th){
		//				int count=0;
		//				if(t.isAlive()){
		//					count+=1;
		//				}
		//				if(count==0){
		//					wait=false;
		//				}
		//			}
		//		}
		return this.entityCategory;
	}
	public  void update(String key) throws Exception{
		String categ=readMQL(key);
		//System.out.println(categ);
		this.entityCategory.put(key, categ);
	}
	public static void main(String args[]) throws Exception{

		/*System.setProperty("https.proxyHost", "netmon.iitb.ac.in");
		System.setProperty("https.proxyPort", "80");
		System.setProperty("http.proxyHost", "netmon.iitb.ac.in");
		System.setProperty("http.proxyPort", "80");
		//System.setProperty("javax.net.debug", "ssl");
		Authenticator.setDefault (new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication ("siddhanth", "14101989".toCharArray());
			}
		});*/

		ArrayList<String> temp=new ArrayList<String>();
		temp.add("blaise pascal");
		
		/*temp.add("abdul kalam");
		temp.add("pranab mukherjee");
		temp.add("india");
		temp.add("car");
		temp.add("scooter");
		temp.add("bugatti veyron");
		temp.add("bill gates");
		temp.add("carlos slim");
		temp.add("Larry ellison");
		temp.add("president of india");
		 */

		long start=System.currentTimeMillis();
		freebaseReader abc=new freebaseReader();
		
		//System.out.println(abc.category(temp));
		//System.out.println(System.currentTimeMillis()-start);
		//readMQL("pratibha patil");
	}



}
