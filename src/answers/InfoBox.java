package answers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.tartarus.snowball.ext.PorterStemmer;

public class InfoBox {

	public String[] quesEntity;
	public String[] quesKeywords;
	public HashMap<String,Double> scoreMap;
	public String[] entitiesAns;
	public String[] entitiesQuery;
	
	public InfoBox(String[] potAns , String[] query , String[] queryEnt){
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
		quesEntity = queryEnt;
		quesKeywords = query;
		scoreMap = new HashMap<String,Double>();
	    for(int i = 0; i < potAns.length ; i++){
	    	scoreMap.put(potAns[i],0.0);
	    }
	    
		
		entitiesAns=new String[potAns.length];
		entitiesQuery=new String[queryEnt.length];
		int i=0;
		//System.out.println("entitiesAns");
		for(String temp:potAns){
			//System.out.println(temp);
			entitiesAns[i]=firstCapital(temp).replaceAll(" ", "_");
			//System.out.println(entitiesAns[i]);
			i++;
		}
		int j=0;
		//System.out.println("entitiesQuery");
		for(String temp1:queryEnt){
			//System.out.println(temp1);
			entitiesQuery[j]=firstCapital(temp1).replaceAll(" ", "_");
			//System.out.println(entitiesQuery[j]);
			j++;
		}

		
		/*System.out.println("quesKeywords");
		for(String temp:quesKeywords){
			System.out.println(temp);
		}*/
		
	}
	
	public void parseInfoBox(String info, int call, String ansEntity) throws Exception{
		//System.out.println("infooo  "+ info);
		
		if(call == 1){
			// in query entity pages check for potential ans entities
			for(String key : scoreMap.keySet()){
				if(info!=null){
		    	if(info.toLowerCase().split(key.toLowerCase()).length-1>0){
		    		 scoreMap.put(key, scoreMap.get(key)+1);
		    	}
				}
		    }
		}
		else if(call == 2){
			// in potential answer pages check for query keywords/entity
			for(String key : quesKeywords){
				
				if(info!=null){
					if(info.toLowerCase().split(key.toLowerCase()).length-1>0){
			    		
			    			scoreMap.put(ansEntity, scoreMap.get(ansEntity)+2);
			    		
			    			
			    		
			    		 
			    	}
				}	
		    }
		}
			
		    
        
}
	public void parseTextBox(String text, int call, String ansEntity ) throws Exception{
		if(call == 1){
			// in query entity pages check for potential ans entities
			for(String key : scoreMap.keySet()){
				if(text!=null){
		    	if(text.toLowerCase().split(key.toLowerCase()).length-1>0){
		    		 scoreMap.put(key, scoreMap.get(key)+0.5);
		    	}
				}
		    }
		}
		if(call == 2){
			// in potential answer pages check for query keywords/entity
			for(String key : quesKeywords){
				if(text!=null){
		    	if(text.toLowerCase().split(key.toLowerCase()).length-1>0){
		    		
		    	
		    			scoreMap.put(ansEntity, scoreMap.get(ansEntity)+1);
		    		
		    	}}
		    }
		}

    
}
	public static String readURL(String url)throws Exception{
		
		//System.out.println(url);
		URL oracle = new URL(url);
     	HttpURLConnection connection = (HttpURLConnection) oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer sb=new StringBuffer();

        while ((inputLine = in.readLine()) != null)
        	sb.append(inputLine);
        
        //System.out.println(sb.toString());
    	String str1 = null;
        JSONParser parser = new JSONParser();
        JSONObject obj=(JSONObject)parser.parse(sb.toString());
        JSONObject obj1=(JSONObject)obj.get("query");
        JSONObject obj2=(JSONObject)obj1.get("pages");
        Object[] key=obj2.keySet().toArray();
        JSONObject obj3=(JSONObject)obj2.get(key[0].toString());
        if(obj3.keySet().contains("revisions")){
        
        	JSONArray obj4=(JSONArray)parser.parse(obj3.get("revisions").toString());
            String str=obj4.get(0).toString();
            JSONObject obj5=(JSONObject)parser.parse(str);
            str1 = obj5.get("*").toString();
        }
        
        
        return str1;
        
	}
	
	
	public void readEntities() throws Exception{
		
		
		
		
		ArrayList<Runnable> runs=new ArrayList<Runnable>();
		for(String key:entitiesQuery){
			final String newKey=key;
			runs.add( new Runnable() {
				public void run() {
					synchronized (this) {
						try {
							
							//System.out.println(newKey);
							update(newKey,1,null);
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

		runs.clear();
		for(String key1:entitiesAns){
			final String newKey=key1;
			final String ansEnt=key1.replaceAll("_", " ").toLowerCase();
			runs.add( new Runnable() {
				public void run() {
					synchronized (this) {
						try {
							
							//System.out.println(newKey);
							update(newKey,2,ansEnt);
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
		th=new Thread[runs.size()];
		for(int i=0;i<runs.size();i++){
			th[i]=new Thread(runs.get(i));
			th[i].start();
		}
		for(Thread t:th){
			t.join();
		}

	}
	public void update(String key,int call,String ansEnt)throws Exception{
		if(ansEnt!=null){
		PorterStemmer obj=new PorterStemmer();
		obj.setCurrent(key);
		obj.stem();
		String stemmedKey=obj.getCurrent();
		obj.setCurrent(ansEnt);
		obj.stem();
		String stemmedAnsEnt=obj.getCurrent();
		
		if(stemmedAnsEnt.compareTo(stemmedKey)!=0){
		
			String info=null;
			String text=null;
			info=readURL("http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles="+key+"&rvsection=0");
			text=readURL("http://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles="+key+"&rvprop=content&format=json");
			parseInfoBox(info,call,ansEnt);
			parseTextBox(text,call,ansEnt);
		}
	}

	}

	public static void main(String args[]) throws Exception{
/*
		System.setProperty("https.proxyHost", "netmon.iitb.ac.in");
		System.setProperty("https.proxyPort", "80");
		System.setProperty("http.proxyHost", "netmon.iitb.ac.in");
		System.setProperty("http.proxyPort", "80");
		//System.setProperty("javax.net.debug", "ssl");
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication ("siddhanth", "14101989".toCharArray());
		    }
		});
		
	*/	
		String[] potAns = {"Karl von Drais", "Pratibha Patil", "Bill Gates"};
		String[] query = {"inventor", "bicycle"};
		String[] queryEnt = {"bicycle"};
		InfoBox in = new InfoBox(potAns, query, queryEnt);
		String[] entitiesAns=new String[potAns.length];
		String[] entitiesQuery=new String[queryEnt.length];
		int i=0;
		for(String temp:potAns){
			entitiesAns[i]=temp.replaceAll(" ", "_");
			i++;
		}
		int j=0;
		for(String temp1:queryEnt){
			entitiesQuery[j]=temp1.replaceAll(" ", "_");
			j++;
		}
		
		in.readEntities();
		//System.out.println(in.scoreMap);
	}
	
	 public static String firstCapital(String place){
         
         int i=0;
         place=place.toLowerCase();
         
         StringBuffer abc=new StringBuffer();
         for(i=0;i<place.length();i++){
                 if(i==0){
                	 int temp=(int)place.charAt(i);
                	 	if(place.charAt(i)>=97&&place.charAt(i)<=122){
                	 		temp=temp-32;
                         }
                         abc.append((char)temp);
                 }
                 else if(place.charAt(i)==32){
                         abc.append(place.charAt(i));
                         i=i+1;
                         int temp=place.charAt(i);
                         if(temp>=97&&temp<=122){
                        	 temp=temp-32;
                         }
                         abc.append((char)(temp));                         
                 }
                 else
                         abc.append(place.charAt(i));
                         
         }
         return abc.toString();
 }


}
