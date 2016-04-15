package answers;
import java.util.HashMap;
import java.util.TreeMap;

import org.tartarus.snowball.ext.PorterStemmer;

public class CountEntities {

	/**
	 * @param args
	 */
	HashMap<String, Double> answerScoreMap =  new HashMap<String, Double>();
	HashMap<String, String> urlEntityMap = new HashMap<String, String>();
	HashMap<String, Double> urlCountMap = new HashMap<String, Double>();
	
	public String query;
	public HashMap<String,String> queryent;
	
	CountEntities(HashMap<String,Double> ans2 , HashMap<String, String> eumap,String param1,HashMap<String,String> param2){
		queryent=param2;
		urlEntityMap = eumap;
		answerScoreMap = ans2;
		for (String key : urlEntityMap.keySet()){
			urlCountMap.put(key, 0.0);
		}
		query=param1;

			
	}
	
	// This function creates a boolean array corressponding to each entity showing its presence in a answer
 
	public HashMap<String, Double> contains(){
		  
		
			HashMap<String,Double> temp=new HashMap<String,Double>();
			PorterStemmer stemmer=new PorterStemmer();
			//System.out.println(answerScoreMap.keySet());
			for (String ans : answerScoreMap.keySet()){
				stemmer.setCurrent(ans);
				stemmer.stem();
				double score = answerScoreMap.get(ans);
				//answerScoreMap.remove(ans);
				String newAnswer = stemmer.getCurrent();
				temp.put(newAnswer, score);
			}
			
			answerScoreMap=temp;
			//System.out.println(answerScoreMap);
	
			
			
			//Creating an entity map to the stemmed string in the urlEntityMap
			
			String[] temp1={"cars"};
			//potAns is an array of potential answers.
			
			
			
			
			                           
			
			 for (String key : urlEntityMap.keySet()){
				 
				 stemmer.setCurrent(urlEntityMap.get(key));
				 stemmer.stem();
				 urlEntityMap.put(key, stemmer.getCurrent());				 
			 }
			
			  for (String key : urlEntityMap.keySet())
				  for(String ans : answerScoreMap.keySet() ){
					  if((ans.split(urlEntityMap.get(key)).length-1)>0 || (ans.split(key).length-1)>0)
						  if(urlCountMap.keySet().contains(key))
							  urlCountMap.put(key,  urlCountMap.get(key)+answerScoreMap.get(ans));
						  else
							  urlCountMap.put(key, answerScoreMap.get(ans));
				  }
			
			  return urlCountMap;
	}
	
	
	public HashMap<String,Double> wikiBoosting(String[] param1){
		
		int index=0;
		
		String[] q_ent=new String[queryent.keySet().size()];
		for(String key:queryent.keySet()){
			q_ent[index]=key;
			index++;
		}
		
		InfoBox obj=new InfoBox(param1, query.split(" "), q_ent);
		
		try{
			obj.readEntities();
		}catch(Exception e){
			
		}
		
		//System.out.println("added scores"+obj.scoreMap);
		
		return(obj.scoreMap);
	
	}
	
	
	/*
	 public void contains(){
		  
		  String[] ansArray;
		 
		//  delimiter 
		  String delimiter = "~";
		  ansArray = answers.split(delimiter);
		  
		  for(int i = 0; i < ansArray.length ; i++){
			  for (String key : urlEntityMap.keySet())
				  for(int j = 0 ; j < urlEntityMap.get(key).size(); j++){
					  if(ansArray[i].contains(urlEntityMap.get(key).get(j))){
						  urlCountMap.put(key, urlCountMap.get(key)+1);
					  }
						  
				  }
			  

		  }
		    
			
	}
	
*/
/*public static void main(String args[]){
		//String ans = "let dr rajendra prasad rajendra prasad was the first president of India around the year 1950 The Constituent Assembly completed the work of drafting the constitution on 26 November 1949 on 26 January 1950 the Republic of India was officially proclaimed The Constituent Assembly elected Dr rajendra Prasad as the first President of India taking over from Governor General Rajgopalachari oNeMoRe Dr rajendra prasad oNeMoRe Both the people above have given correct answers to your question Our First Present was Dr rajendra prasad Apart from correcting your question you also need to spell your name correctly It should either be Syed Zindgi or Saeed Zindgi oNeMoRe lemnik ors evnas oNeMoRe The current president is Pratibha Patil The first president was Dr rajendra prasad oNeMoRe rajendra prasad oNeMoRe He groomed political leaders such as Nehru to lead the country He was not interested in the operations of government as he was already in his 70s He was at odds to some degree with the Congress Party on the partition of the country at the time independence came oNeMoRe Dr rajendra prasad Hindi December 3 1884 February 28 1963 was the first President of India pkn oNeMoRe Mahatma Gandhi a smart good Dude oNeMoRe list out the presidents of Indian Repuplic since its inception write first second etc FRAME YOUR QUESTION AS ABOVE oNeMoRe ";
		HashMap<String, Double> ans1 = new HashMap<String,Double>();
		ans1.put("let the first dr rajendra prasad rajendra prasad", 1.0);
		ans1.put("rajendra prasad rajendra prasad", 1.0);
		ans1.put("lthe first president rajendra prasad of India around the ected Dr rajendra Prasad as the first President of India taking over from Governor General Rajgopalachari oNeMoRe Dr rajendra prasad", 3.0);
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("rajendra prasad", "rajendra prasad");
		temp.put("gavaskar", "gavaskar");
		temp.put("president", "president");
		CountEntities c = new CountEntities(ans1,temp);
		HashMap<String, Double> t1 = c.contains();
		System.out.println(t1.get("rajendra prasad"));
		System.out.println(t1.get("president"));
		System.out.println(t1.get("gavaskar"));
}*/

}
	
	/*
	 public void contains(){
		  
		  String[] ansArray;
		 
		//  delimiter 
		  String delimiter = "~";
		  ansArray = answers.split(delimiter);
		  
		  for(int i = 0; i < ansArray.length ; i++){
			  for (String key : urlEntityMap.keySet())
				  for(int j = 0 ; j < urlEntityMap.get(key).size(); j++){
					  if(ansArray[i].contains(urlEntityMap.get(key).get(j))){
						  urlCountMap.put(key, urlCountMap.get(key)+1);
					  }
						  
				  }
			  

		  }
		    
			
	}
	

public static void main(String args[]){
		String ans = "let dr rajendra prasad rajendra prasad was the first president of India around the year 1950 The Constituent Assembly completed the work of drafting the constitution on 26 November 1949 on 26 January 1950 the Republic of India was officially proclaimed The Constituent Assembly elected Dr rajendra Prasad as the first President of India taking over from Governor General Rajgopalachari oNeMoRe Dr rajendra prasad oNeMoRe Both the people above have given correct answers to your question Our First Present was Dr rajendra prasad Apart from correcting your question you also need to spell your name correctly It should either be Syed Zindgi or Saeed Zindgi oNeMoRe lemnik ors evnas oNeMoRe The current president is Pratibha Patil The first president was Dr rajendra prasad oNeMoRe rajendra prasad oNeMoRe He groomed political leaders such as Nehru to lead the country He was not interested in the operations of government as he was already in his 70s He was at odds to some degree with the Congress Party on the partition of the country at the time independence came oNeMoRe Dr rajendra prasad Hindi December 3 1884 February 28 1963 was the first President of India pkn oNeMoRe Mahatma Gandhi a smart good Dude oNeMoRe list out the presidents of Indian Repuplic since its inception write first second etc FRAME YOUR QUESTION AS ABOVE oNeMoRe ";
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("rajendra prasad", "rajendra prasad");
		temp.put("gavaskar", "gavaskar");
		temp.put("Gavaskar", "Gavaskar");
		CountEntities c = new CountEntities(ans,temp);
		HashMap<String, Integer> t1 = c.contains();
		System.out.println(t1.get("rajendra prasad"));
		System.out.println(t1.get("gavaskar"));
		System.out.println(t1.get("Gavaskar"));
}
*/

