package answers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;

public class entityFilter {
	
	public static HashMap<String,String> run(String query, HashMap<String,String> entitySet){
		
		String answerType=null;
		
		HashMap<String,String> entityCateg=new HashMap<String,String>();
		HashMap<String,String> newEntitySet=new HashMap<String,String>();
		
		
		if(query.contains("who"))
			answerType="person";
		else if(query.contains("where"))
			answerType="location";
		
		if(answerType!=null){
			
			//find the type of the entities and boost the code which
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
			
			ArrayList<String> param1=new ArrayList<String>();
			for(String key:entitySet.keySet()){
				param1.add(key);
			}
			
			try{
				freebaseReader obj=new freebaseReader();
				entityCateg=obj.category(param1);
			}catch(Exception e){
				//System.out.println(e);
			}
			//System.out.println(entityCateg);
			
			HashMap<String,String> nullkeys=new HashMap<String,String>();
			
			for(String key:entityCateg.keySet()){				
				if(entityCateg.get(key)!=null && entityCateg.get(key).contains(answerType)){
					newEntitySet.put(key, entitySet.get(key));			
				}
				else{
					
					nullkeys.put(entitySet.get(key),key);
				}
			}
			
			
			HashMap<String,String> moreEntities=new HashMap<String,String>();
			ArrayList<String> tt=new ArrayList<String>();
			for(String key:nullkeys.keySet()){
				tt.add(key);
			}
			
			
			try{
				freebaseReader obj=new freebaseReader();
				moreEntities=obj.category(tt);
			}catch(Exception e){
				//System.out.println(e);
			}
			
			
			for(String key:moreEntities.keySet()){
				if(moreEntities.get(key)!=null && moreEntities.get(key).contains(answerType)){
					newEntitySet.put(nullkeys.get(key), key);
				}
			}
			
			
			
			return newEntitySet;
		}
		else
			return entitySet;		
		
	}

}
