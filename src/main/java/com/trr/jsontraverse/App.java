package com.trr.jsontraverse;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import com.google.gson.*;


public class App 
{
	
    public static void main( String[] args )
    {
    	    
        String jsonString = readJsonFile("https://raw.githubusercontent.com/jdolan/quetoo/master/src/cgame/default/ui/settings/SystemViewController.json");   
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        HashMap<String,Object> map = buildMapFromJson(jsonObject);
        getInput(map);
        
        
    }
    /**
     *  Method for taking in inputs
     *  
     * 
     * 
     * 
     * */
    public static void getInput(HashMap<String,Object> map) {
    	
    	Scanner input = new Scanner(System.in);
    	
    	System.out.println("Enter a search query OR Ctrl-d to exit");
    	
    	while(input.hasNextLine()) {
    		
    		String query = input.nextLine();
    		
    		if(Character.isLetter(query.charAt(0)))
    			searchForView(map,"class",query);
    		else if(query.charAt(0) == '.')
    			searchForView(map,"classNames",query.substring(1, query.length()));
    		else if(query.charAt(0) == '#')
    			searchForView(map,"classNames",query.substring(1, query.length()));
    		
    		System.out.println("Enter a search query OR Ctrl-d to exit");
    	}
    	
    	input.close();
    } 
    
    
    /**
     *  Method for reading the JSON file remotely
     * */
    public static String readJsonFile(String urlString) {
    	
    	String jsonData="";
    	
    	try {
    		URL url = new URL(urlString);
    		Scanner input = new Scanner(new InputStreamReader(url.openStream()));
    		while(input.hasNextLine()) {
    			jsonData += input.nextLine() + "\n";
    		}
    		input.close();
    		
    	}
    	catch(Exception e) {
    		
    	}
    	return jsonData;
    }
    
    
    /**
     *  Method for parsing JSON file recursively and storing it into a HashMap
     *  
     * 
     * 
     * 
     * */
    public static HashMap<String,Object> buildMapFromJson(JsonObject jsonObject){
    	
    	HashMap<String,Object> map = new HashMap<String,Object>(); // key is always a string
    	
    	for(Entry<String,JsonElement> entry: jsonObject.entrySet()) {
    		String key = entry.getKey();
    		
    		
    		
    		JsonElement value = entry.getValue();
    		
    		
    		map.put(entry.getKey(), getValue(value));
    	}
    	
    	return map;
    }
    
    public static Object getValue(JsonElement element) { // four types
    	
    	if(element.isJsonNull()) { // if element is null return nothing
    		return null;
    	}
    	
    	else if(element.isJsonObject()) { // if element is an object, return object back to map builder and traverse again
    		
    		return buildMapFromJson(element.getAsJsonObject());
    	}
    	
    	else if(element.isJsonArray()) { 
    		ArrayList<Object> list = new ArrayList<Object>(element.getAsJsonArray().size());
    		for(JsonElement ele: element.getAsJsonArray()) {
    			list.add(getValue(ele));
    		}
    		
    		return list;
    	}
    	
    	else { // primitive
    		JsonPrimitive primElement = element.getAsJsonPrimitive();
    		
    		if(primElement.isString())
    			return element.getAsString();
    		else if(primElement.isBoolean())
    			return element.getAsBoolean();
    		else if(primElement.isNumber())
    			return primElement.getAsDouble();
    		
    	}
    	return null;
    	
    }
    
    
    
    public static void searchForView(HashMap<String,Object> map,String key ,String searchValue) {
        
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	
    	for(Entry<String, Object> entry : map.entrySet()){
            Object value = entry.getValue();
            
            
              if(value instanceof HashMap) {
            	
            	HashMap hash = (HashMap)value;
            	if(matchValues(hash ,key, searchValue)) {
            		System.out.println(gson.toJson(map)); // print entire JSON as the matched key,value pair form a portion a bigger JSON
            		System.out.println("=================================");
            	}
    			searchForView((HashMap)value,key,searchValue);
                
            }
              
            else if(value instanceof ArrayList) {
            	ArrayList<Object> vals = (ArrayList<Object>)value;
            	
            	for(Object val: vals) {
            		
            		if(val instanceof HashMap) {
            			
            			if(matchValues((HashMap)val ,key, searchValue)) {
                    		System.out.println(gson.toJson(val)); // Print only the element of the list, since the list itself contains the JSON object
                    		System.out.println("=================================");
            			}
            			searchForView((HashMap)val, key, searchValue);
            		}
            		
            	}
            	
            }
                
        }
    }
    
    public static boolean matchValues(HashMap map, String key, String value) {
    	
    		
			if(map.keySet().contains(key)) {
				
				if(map.get(key) instanceof ArrayList) {
					ArrayList list = (ArrayList)map.get(key);
					if(list.contains(value))
						return true;
				}
				else if(map.containsValue(value)) {
					  return true;
				}
				
			
			}
    
			return false;
     }  
    
    
}
