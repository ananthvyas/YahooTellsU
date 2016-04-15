package answers;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.WordUtils;
import org.tartarus.snowball.ext.PorterStemmer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Simple Demo to show the power of YQL
 * @see http://idojava.blogspot.com/
 * @author Green Ido
 */
public class YahooAnswers {

	/**
	 * Find 'food' places for the JPR
	 * @param args
	 * @throws Exception
	 */
	public static String query;
	public static PorterStemmer stemmer=new PorterStemmer();
	public static HashMap<String, Double> answerscore;
	public static HashMap<String, Boolean> stopWords;
	private static String filterStopWords(String question){ 
		StringBuilder strb= new StringBuilder();
		String[] questionWords=question.split("\\s+");
		for(String word:questionWords){
			if(!stopWords.containsKey(word)){
				strb.append(word).append(" ");
			}
		}
		return strb.toString().trim();
	}
	public static void main(String[] args) throws Exception {
		/*final String authUser = "ananthv";
    		final String authPassword = "Swisscake1,08";

    		System.setProperty("http.proxyHost", "netmon.iitb.ac.in");
    		System.setProperty("http.proxyPort", "80");
    		System.setProperty("http.proxyUser", authUser);
    		System.setProperty("http.proxyPassword", authPassword);

    		Authenticator.setDefault(
    				new Authenticator() {
    					public PasswordAuthentication getPasswordAuthentication() {
    						return new PasswordAuthentication(authUser, authPassword.toCharArray());
    					}
    				}
    				);*/
		if(args.length==0){
			System.out.println("Please provide the query");
			System.exit(0);
		}
		
		stopWords=new HashMap<String, Boolean>();
		try {
			FileInputStream fstream=new FileInputStream("stopwords.txt");
			DataInputStream input = new DataInputStream(fstream);
			BufferedReader buffer= new BufferedReader(new InputStreamReader(input));
			String line;
			while((line=buffer.readLine())!=null){
				stopWords.put(line.trim(), true);
			}
		}catch (FileNotFoundException e) {
			System.err.println("Error:"+e.getMessage());
		} catch (IOException e) {
			System.err.println("Error:"+e.getMessage());
		}
		
		query="";
		for(int i=0;i<args.length;i++){
			query+=args[i].toLowerCase().replaceAll("[^a-zA-Z0-9]+"," ").trim()+" ";
		}
		query=filterStopWords(query);
		
		String request = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20answers.search(0%2C30)%20where%20query%3D%22"+query.replaceAll(" ", "%20")+"%22%20and%20type%3D%22resolved%22&diagnostics=true";

		HttpClient client = new HttpClient();
		HostConfiguration config = client.getHostConfiguration();
		/*config.setProxy("netmon.iitb.ac.in", 80);

		String username = "siddhanth";
		String password = "14101989";
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		AuthScope authScope = new AuthScope("netmon.iitb.ac.in", 80);

		client.getState().setProxyCredentials(authScope, credentials);*/
		GetMethod method = new GetMethod(request);

		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
		} else {
			InputStream rstream = null;
			rstream = method.getResponseBodyAsStream();
			// Process response
			Document response = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(rstream);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			// Get all search Result nodes
			NodeList nodes = (NodeList) xPath.evaluate("query/results/Question",
					response, XPathConstants.NODESET);
			int nodeCount = nodes.getLength();
			// iterate over search Result nodes
			String answers="";
			String questions="";
			//int length = nl.getLength();
			HashMap<String,String> questID=new HashMap<String,String>();
			HashMap<String,String> IDanswer=new HashMap<String,String>();
			for (int i = 0; i < nodeCount; i++) {
				// Get each xpath expression as a string
				String title = (String) xPath.evaluate("Subject", nodes.item(i),
						XPathConstants.STRING);
				String summary = (String) xPath.evaluate("ChosenAnswer", nodes
						.item(i), XPathConstants.STRING);
				//String chosenid = (String) xPath.evaluate("ChosenAnswerId", nodes
				//		.item(i), XPathConstants.STRING);
				//String url = (String) xPath.evaluate("Url", nodes.item(i),
				//XPathConstants.STRING);
				// print out the Title, Summary, and URL for each search result
				//System.out.println("Question: " + title);
				questions+=title+'~';
				SecureRandom random = new SecureRandom();
				String hash=new BigInteger(130, random).toString(32);
				questID.put(title, hash);  
				IDanswer.put(hash, summary);
				//System.out.println("URL: " + url);
				//answers+=summary+" oNeMoRe ";
				//System.out.println("-----------");

			}
			Penalizer penalizer=new Penalizer();
			HashMap<String, Double> score = penalizer.penalize(questions,query.replaceAll("%20", " ").trim());
			ArrayList<String> keys=new ArrayList<String>();
			for(String key:score.keySet()){
				if(score.get(key)<=0){
					keys.add(key);
				}
			}
			for(String key:keys){
				//System.out.println(key);
				score.remove(key);
			}
			if(score.size()<3){
				System.out.println("Hmm, guess we don't know ! Wanna <a href=\"http://answers.yahoo.com/\">ask</a>?");
				System.exit(0);
			}
			ValueComparator bvc =  new ValueComparator(score);
			TreeMap<String,Double> sorted_map = new TreeMap(bvc);
			sorted_map.putAll(score);
			int count=0;
			answerscore=new HashMap<String, Double>();
			answers=query+" qUeStIoNbReAk ";
			for (String key : sorted_map.keySet()) {
				if(++count > 10) break;
				//System.out.println(key);
				answerscore.put(IDanswer.get(questID.get(key)).toLowerCase(), score.get(key));
				//System.out.println(IDanswer.get(questID.get(key)));
				answers+=key+" "+IDanswer.get(questID.get(key))+" oNeMoRe ";
			}
			//System.out.println(answers.replaceAll("[^a-zA-Z0-9]+"," ").replaceAll("\n", " "));
			yahooContentAnalysis(answers.replaceAll("[^a-zA-Z0-9]+"," ").replaceAll("\n", " ").replaceAll(" ", "%20"));
		}
	}
	public static void yahooContentAnalysis(String query) throws Exception{

		//String request = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20local.search%20where%20query%3D%22food%22%20and%20location%3D%22crested%20butte%2C%20co%22&format=xml";
		String totalans="";
		String[] queries=query.split("(?<=\\G.{6000})");
		for(int i=0;i<queries.length;i++){
			String request = "http://wikipedia-miner.cms.waikato.ac.nz/services/wikify?source="+queries[i]+"&minProbability=0.4";
			//System.out.println(queries[i].replaceAll("%20", " "));
			HttpClient client = new HttpClient();
			HostConfiguration config = client.getHostConfiguration();
			/*config.setProxy("netmon.iitb.ac.in", 80);

			String username = "siddhanth";
			String password = "14101989";
			Credentials credentials = new UsernamePasswordCredentials(username, password);
			AuthScope authScope = new AuthScope("netmon.iitb.ac.in", 80);

			client.getState().setProxyCredentials(authScope, credentials);*/

			GetMethod method = new GetMethod(request);


			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			} else {
				InputStream rstream = null;
				rstream = method.getResponseBodyAsStream();
				// Process response
				Document response = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(rstream);

				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();
				// Get all search Result nodes
				NodeList nodes = (NodeList) xPath.evaluate("message",
						response, XPathConstants.NODESET);
				int nodeCount = nodes.getLength();
				// iterate over search Result nodes
				for (int j = 0; j < nodeCount; j++) {
					// Get each xpath expression as a string
					String title = (String) xPath.evaluate("wikifiedDocument", nodes.item(j),
							XPathConstants.STRING);
					//String summary = (String) xPath.evaluate("wiki_url", nodes
					//              .item(i), XPathConstants.STRING);
					//String url = (String) xPath.evaluate("Url", nodes.item(i),
					//              XPathConstants.STRING);
					// print out the Title, Summary, and URL for each search result
					totalans+=title;
					
					//System.out.println("Wiki: " + summary);
					//System.out.println("URL: " + url);
					//System.out.println(title);

				}
			}
		}
		//System.out.println(totalans);
		extractEntities(totalans,query);
	}
	public static void extractEntities(String doc,String answer){
		//String doc1="[[prim minister]] aksjbkajsd [[aksjbjasbd]]";
		String questent=doc.split(" qUeStIoNbReAk ")[0];
		Pattern p = Pattern.compile("\\[\\[(.*?)\\]\\]");
		Matcher m = p.matcher(questent);
		HashMap<String,String> quesent=new HashMap<String,String>();
		while (m.find()) {
			//System.out.println(m.group(1));	
			String temp=m.group(1);
			//System.out.println(temp.trim().split("\\|")[0]);
			String entity="";
			String text="";
			if(temp.contains("|")){
				entity=temp.toString().split("\\|")[0];
				text=temp.toString().split("\\|")[1];
			}else{
				entity=temp;
				text=temp;
			}
			if(!quesent.containsKey(entity)){
				quesent.put(entity.toLowerCase(), text.toLowerCase());
			}else{
				quesent.put(entity.toLowerCase(), quesent.get(entity.toLowerCase())+"~"+text.toLowerCase());
			}
		}
		m = p.matcher(doc);
		HashMap<String,String> ent=new HashMap<String,String>();
		while (m.find()) {
			//System.out.println(m.group(1));	
			String temp=m.group(1);
			//System.out.println(temp.trim().split("\\|")[0]);
			String entity="";
			String text="";
			if(temp.contains("|")){
				entity=temp.toString().split("\\|")[0];
				text=temp.toString().split("\\|")[1];
			}else{
				entity=temp;
				text=temp;
			}
			if(!ent.containsKey(entity)){
				ent.put(entity.toLowerCase(), text.toLowerCase());
			}else{
				ent.put(entity.toLowerCase(), ent.get(entity.toLowerCase())+"~"+text.toLowerCase());
			}
		}
		
		String[] rem=query.split(" ");
		ArrayList<String> keys=new ArrayList<String>();
		Set<String> mySet = new HashSet<String>(Arrays.asList(rem));
		//System.out.println(mySet);
		for(String key:ent.keySet()){			
			//System.out.println(new HashSet<String>(Arrays.asList(filterStopWords(key).split(" "))));
			if(mySet.containsAll(new HashSet<String>(Arrays.asList(filterStopWords(key).split(" ")))) || mySet.containsAll(new HashSet<String>(Arrays.asList(filterStopWords(ent.get(key)).split(" "))))){
				keys.add(key);
			}
		}
		for(String key:keys){
			ent.remove(key);
		}
		/*for(int i=0;i<rem.length;i++){
			if(ent.containsKey(rem[i].toLowerCase())){
				ent.remove(rem[i].toLowerCase());
			}
		}*/
		
		HashMap<String,String> newent=entityFilter.run(query, ent);
		
		CountEntities ce=new CountEntities(answerscore,ent,query,quesent);
		HashMap<String,Double> fin=ce.contains();
		
		for(String key:fin.keySet()){
			if(newent.containsKey(key)){
				double value=fin.get(key)*1.3;
				fin.put(key, value);
			}
		}

		ValueComparator bvc =  new ValueComparator(fin);
		TreeMap<String,Double> sorted_map = new TreeMap(bvc);
		sorted_map.putAll(fin);
		//System.out.println(sorted_map);
		
		String[] top3=new String[3];
		int index=0;
		for(String key:sorted_map.keySet()){
			if(index==3)break;
			top3[index]=key;
			index++;
		}
		
		HashMap<String,Double> boostedScores=ce.wikiBoosting(top3);
		for(String key:boostedScores.keySet()){
			boostedScores.put(key, fin.get(key)+(0.5*boostedScores.get(key)));
		}
		
		ValueComparator bvc1 =  new ValueComparator(boostedScores);
		TreeMap<String,Double> sorted_map1 = new TreeMap(bvc1);
		sorted_map1.putAll(boostedScores);
		
		//System.out.println("final rank"+sorted_map1);
		

		//System.out.print("Answer : ");
		//System.out.println(sorted_map1.firstKey());
		for (String key : sorted_map1.keySet()) {
			System.out.println(WordUtils.capitalize(key));// + "/"+sorted_map1.get(key));
		}


	}	
}

class ValueComparator implements Comparator {

	Map base;
	public ValueComparator(Map base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {

		if((Double)base.get(a) < (Double)base.get(b)) {
			return 1;
		} else if((Double)base.get(a) == (Double)base.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}
