package ist.meic.cmu.server.storage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ist.meic.cmu.server.tool.StringParser;

public class Storage {

	private ArrayList<GPSLocation> GPSLocation;
	private ArrayList<WifiLocation> wifiLocation;
	private ArrayList<User> user;
	private ArrayList<Post> post;
	private ArrayList<Property> property;
	public Storage(){
		GPSLocation=new ArrayList<GPSLocation>();
		wifiLocation=new ArrayList<WifiLocation>();
		user=new ArrayList<User>();
		post=new ArrayList<Post>();
		property=new ArrayList<Property>();
		User Alice=new User("Alice", "passA");
		User Bob=new User("Bob", "passB");
		Alice.addProperty(new Property("Sport", "Volleyball"));
		Alice.addProperty(new Property("Favorite TV Show", "Friends"));
		Bob.addProperty(new Property("Sport", "Basketball"));
		Bob.addProperty(new Property("Favorite Movie", "Matrix"));
		user.add(Bob);
		user.add(Alice);
		GPSLocation.add(new GPSLocation("Terreiro do Paço",38.707708,-9.136522, 50));
		wifiLocation.add(new WifiLocation("eduroam", "eduroam"));
		/*
		DateFormat format = new SimpleDateFormat("dd/MM/yy hh:mm");
		Date start=null;
		Date end=null;
		try {
			start = format.parse("01/01/2016 16:20");
			end = format.parse("01/01/2020 16:20");
			ArrayList<Property> p=new ArrayList<Property>();
			p.add(new Property("a", "a"));
			post.add(new Post("title1", "message1", "a", start, end, "Jardim do Arco do Cego", "Blacklist", "Centralized", p));
			post.add(new Post("title2", "message2", "a", start, end, "Instituto Superior Técnico", "Whitelist", "Centralized", p));
			//post.add(new Post("title2", "message2", "a", start, end, "Instituto Superior Técnico", "Whitelist", "Decentralized", p));
		} catch (ParseException e) {
			System.out.println("Date format error!!!");
		}
		*/
		
		
		
		
	}
	public boolean createUser(String username, String password){
		if(user.size()!=0)
		for (User tmpUser : user) {
			if(tmpUser.getUsername().equals(username)){
				return false;
			}
		}
		System.out.println(username+"==> Creating");
		user.add(new User(username, password));
		return true;
		
	}
	public boolean verifyUser(String username, String password){
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getPassword().equals(password)){
					return true;
				}
			}
		return false;
	}
	public String giveSessionID(String username, String password){
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getPassword().equals(password)&&tmpUser.getSessionID().equals("")){
					String random = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					for(int i=0;i<4;i++){
						random+=""+(new Random().nextInt(9 - 0 + 1) + 0);
					}
					tmpUser.setSessionID(random);
					return random;
				}
			}
		return "";
	}
	public boolean logout(String username, String session){
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getSessionID().equals(session)){
					tmpUser.setSessionID("");
					return true;
				}
			}
		return false;
	}
	public boolean addLocation(String name, String lat, String lon, String radius){

		if(GPSLocation.size()!=0)
			for (GPSLocation tmplocation : GPSLocation) {
				if(tmplocation.getName().equals(name)||
						(tmplocation.getLatitude()==Double.parseDouble(lat)&&
						tmplocation.getLongitude()==Double.parseDouble(lon))){
					return false;
				}
			}
		System.out.println("Location added:\n"+"name: "+name+
			"latitude: "+Double.parseDouble(lat)+
				"longitude: "+Double.parseDouble(lon)+
			"radius: "+Integer.parseInt(radius));
		GPSLocation.add(new GPSLocation(name, Double.parseDouble(lat), Double.parseDouble(lon), Integer.parseInt(radius)));
		return true;
	}
	public boolean removeLocation(String name, String lat, String lon) {
		if(GPSLocation.size()!=0)
			for (int i=0;i<GPSLocation.size();i++) {
				if(GPSLocation.get(i).getName().equals(name)&&
						GPSLocation.get(i).getLatitude()==Double.parseDouble(lat)&&
								GPSLocation.get(i).getLongitude()==Double.parseDouble(lon)){
					GPSLocation.remove(i);
					return true;
				}
			}
		return false;
	}
	public JSONObject getLocation() {
		JSONObject json=new JSONObject();
		if(GPSLocation.size()!=0)
			for (int i=0;i<GPSLocation.size();i++) {
				JSONArray jsonLocation = new JSONArray();
				
					jsonLocation.add(GPSLocation.get(i).getName());
					jsonLocation.add(GPSLocation.get(i).getLatitude());
					jsonLocation.add(GPSLocation.get(i).getLongitude());
					jsonLocation.add(GPSLocation.get(i).getRadius());
					json.put("location"+i, jsonLocation);
			}
			System.out.println("json output: "+json.toJSONString());
		return json;
	}
	public boolean userLocation(String username, String lat, String lon, String sessionid) {
		
		return true;
	}
	public boolean sendPost(String title, String message, String username, String startDate, String endDate, String location, String filter, String mode, String property) {
		if(post.size()!=0)
			for(Post p:post){
				if(p.getTitle().equals(title)
						&& p.getMessage().equals(message)
						&& p.getUsername().equals(username)
						&& p.getLocation().equals(location)){
					return false;
				}
			}
		DateFormat format = new SimpleDateFormat("dd/MM/yy hh:mm");
		Date start=null;
		Date end=null;
		try {
			start = format.parse(startDate);
			end = format.parse(endDate);
		} catch (ParseException e) {
			System.out.println("Date format error!!!");
			return false;
		}
		ArrayList<Property> prof=new ArrayList<>();
		if(!property.equals("null")){
			String[] propertyParser=StringParser.getProperty(property);
			for(int i=0;i<propertyParser.length;i+=2){
				prof.add(new Property(propertyParser[i], propertyParser[i+1]));
			}
			post.add(new Post(title, message, username, start, end, location, filter, mode, prof));
			System.out.println("post with property added :"+post.get(post.size()-1).getTitle());
			return true;
		}else{
			post.add(new Post(title, message, username, start, end, location, filter, mode, null));
			System.out.println("post without property added :"+post.get(post.size()-1).getTitle());
			return true;
		}
		
	}
	public JSONObject getPost(String username, String latitude, String longitude) {
		JSONObject json=new JSONObject();
		ArrayList<Property> property=new ArrayList<Property>();
		if(user.size()!=0&&post.size()!=0&&GPSLocation.size()!=0){
			for (int i=0;i<user.size();i++) {
				if(user.get(i).getUsername().equals(username)){
					property=user.get(i).getProperty();
//					System.out.println("User exists!");
					break;
				}
			}
			System.out.println("user has location: "+latitude+":"+longitude);
			
			for(int i=0,w=0;i<GPSLocation.size();i++){
//				System.out.println("Location: "+GPSLocation.get(i).getLatitude()+":"+GPSLocation.get(i).getLongitude()+
//						"\nuser: "+latitude+":"+longitude);
//				System.out.println("Result: "+Algorithm.distFrom(GPSLocation.get(i).getLatitude(), GPSLocation.get(i).getLongitude(), Double.parseDouble(latitude), Double.parseDouble(longitude)));
				if(Algorithm.distFrom(GPSLocation.get(i).getLatitude(), GPSLocation.get(i).getLongitude(), Double.parseDouble(latitude), Double.parseDouble(longitude))<=
								GPSLocation.get(i).getRadius()){
					for(int j=0 ; j<post.size();j++){
						JSONArray arrayPost=new JSONArray();
						System.out.println("Saber: "+post.get(j).getLocation()+":"+GPSLocation.get(i).getName());
						if(post.get(j).getLocation().equals(GPSLocation.get(i).getName())){
							System.out.println("Post with: "+post.get(j).getFilter());
							if(post.get(j).getUsername().equals(username)){
								if(post.get(j).getUsername().equals(username)){
								arrayPost.add(post.get(j).getTitle());
								arrayPost.add(post.get(j).getMessage());
								arrayPost.add(post.get(j).getUsername());
								arrayPost.add(post.get(j).getStartDate());
								arrayPost.add(post.get(j).getEndDate());
								arrayPost.add(post.get(j).getLocation());
								arrayPost.add(post.get(j).getFilter());
								arrayPost.add(post.get(j).getMode());
								if(post.get(j).getPropertyString()!=null)
									arrayPost.add(post.get(j).getPropertyString());
								else
									arrayPost.add(" ");
								json.put("post"+w, arrayPost);
								w++;
								System.out.println("Userpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
								}
							}
							else if(post.get(j).getFilter().equals("Whitelist")&&property.size()!=0){
								for(Property p:post.get(j).getProperty()){
									for(int s=0;s<property.size();s++){
										System.out.println("test whitelist:"+property.get(s).getKey()+":"+p.getKey());
										System.out.println("test whitelist:"+property.get(s).getValue()+":"+p.getValue());
									if(("\""+property.get(s).getKey()+"\"").equals(p.getKey())&&
											("\""+property.get(s).getValue()+"\"").equals(p.getValue())){
										arrayPost.add(post.get(j).getTitle());
										arrayPost.add(post.get(j).getMessage());
										arrayPost.add(post.get(j).getUsername());
										arrayPost.add(post.get(j).getStartDate());
										arrayPost.add(post.get(j).getEndDate());
										arrayPost.add(post.get(j).getLocation());
										arrayPost.add(post.get(j).getFilter());
										arrayPost.add(post.get(j).getMode());
										arrayPost.add(post.get(j).getPropertyString());
										
										json.put("post"+w, arrayPost);
										w++;
										System.out.println("whitepost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
										break;
									}
									}
								}
							}else if(post.get(j).getFilter().equals("Blacklist")){
								if(property.size()!=0){
									for(Property p:post.get(j).getProperty()){
										for(int s=0;s<property.size();s++){
										if(!property.get(s).getKey().equals(p.getKey())&&
												!property.get(s).getValue().equals(p.getValue())){
											arrayPost.add(post.get(j).getTitle());
											arrayPost.add(post.get(j).getMessage());
											arrayPost.add(post.get(j).getUsername());
											arrayPost.add(post.get(j).getStartDate());
											arrayPost.add(post.get(j).getEndDate());
											arrayPost.add(post.get(j).getLocation());
											arrayPost.add(post.get(j).getFilter());
											arrayPost.add(post.get(j).getMode());
											if(post.get(j).getPropertyString()!=null)
												arrayPost.add(post.get(j).getPropertyString());
											else
												arrayPost.add(" ");
											
											json.put("post"+w, arrayPost);
											w++;
											System.out.println("blackpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
											break;
										}
										}
									}
								}else{
									arrayPost.add(post.get(j).getTitle());
									arrayPost.add(post.get(j).getMessage());
									arrayPost.add(post.get(j).getUsername());
									arrayPost.add(post.get(j).getStartDate());
									arrayPost.add(post.get(j).getEndDate());
									arrayPost.add(post.get(j).getLocation());
									arrayPost.add(post.get(j).getFilter());
									arrayPost.add(post.get(j).getMode());
									if(post.get(j).getPropertyString()!=null)
										arrayPost.add(post.get(j).getPropertyString());
									else
										arrayPost.add(" ");
									
									json.put("post"+w, arrayPost);
									w++;
									System.out.println("else blackpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
									break;
									
								}
							}
						}
					}
				}else{
					for(int j=0 ; j<post.size();j++){
						JSONArray arrayPost=new JSONArray();
						if(post.get(j).getUsername().equals(username)){
						arrayPost.add(post.get(j).getTitle());
						arrayPost.add(post.get(j).getMessage());
						arrayPost.add(post.get(j).getUsername());
						arrayPost.add(post.get(j).getStartDate());
						arrayPost.add(post.get(j).getEndDate());
						arrayPost.add(post.get(j).getLocation());
						arrayPost.add(post.get(j).getFilter());
						arrayPost.add(post.get(j).getMode());
						if(post.get(j).getPropertyString()!=null)
							arrayPost.add(post.get(j).getPropertyString());
						else
							arrayPost.add(" ");
						json.put("post"+w, arrayPost);
						w++;
						System.out.println("Userpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
						}
					}
				}
			}
		}
		System.out.println("Post result: "+json.toJSONString());
		return json;
	}
	public boolean deletePost(String title, String message, String username, String location) {
		if(post.size()!=0)
			for(Post p:post){
				if(p.getTitle().equals(title)
						&& p.getMessage().equals(message)
						&& p.getUsername().equals(username)
						&& p.getLocation().equals(location)){
					post.remove(p);
					System.out.println("Remove successful");
					return true;
				}
			}
		return false;
	}
	
	public boolean addProperty(String username, String sessionid, String key, String value) {
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getSessionID().equals(sessionid)){
					for(Property property:tmpUser.getProperty()){
						if(property.getKey().equals(key)&&property.getValue().equals(value))
						return false;
					}
					System.out.println("add: "+key+" : "+value);
					Property p=new Property(key, value);
					tmpUser.addProperty(p);
					if(!property.contains(p.getKey().equals(key)))
						property.add(p);
					return true;
				}
			}
		return false;
	}
	public JSONObject getProperty(String username, String sessionid) {
		JSONObject json=new JSONObject();
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getSessionID().equals(sessionid)){
					for(int i=0; i<tmpUser.getProperty().size();i++){
						JSONArray jsonProperty=new JSONArray();
						jsonProperty.add(tmpUser.getProperty().get(i).getKey());
						jsonProperty.add(tmpUser.getProperty().get(i).getValue());
						json.put("property"+i, jsonProperty);
					}
				}
			}
		return json;
	}
	public JSONObject getAllProperties() {
		JSONObject json=new JSONObject();
		if(property.size()!=0)
			for(int i=0; i<property.size();i++){
				JSONArray jsonProperty=new JSONArray();
				jsonProperty.add(property.get(i).getKey());
				jsonProperty.add(property.get(i).getValue());
				json.put("property"+i, jsonProperty);
			}
		return json;
	}
	public boolean removeProperty(String username, String sessionid, String key, String value) {
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getSessionID().equals(sessionid)){
					for(Property property:tmpUser.getProperty()){
						if(property.getKey().equals(key)&&property.getValue().equals(value)){
							tmpUser.removeProperty(key, value);
							System.out.println("remove: "+key+" : "+value);
							return true;
						}
					}
				}
			}
		return false;
	}
	public boolean editProperty(String username, String sessionid, String oldkey, String oldvalue, String newkey,
			String newvalue) {
		JSONObject json=new JSONObject();
		if(user.size()!=0)
			for (User tmpUser : user) {
				if(tmpUser.getUsername().equals(username)&&tmpUser.getSessionID().equals(sessionid)){
					for(int i=0; i<tmpUser.getProperty().size();i++){
						if(tmpUser.getProperty().get(i).getKey().equals(oldkey)&&tmpUser.getProperty().get(i).getValue().equals(oldvalue)){
							tmpUser.getProperty().get(i).setKey(newkey);
							tmpUser.getProperty().get(i).setValue(newvalue);
							return true;
						}
					}
				}
			}
		return false;
	}
	public boolean addWifiLocation(String name, String ssid) {
		
		if(wifiLocation.size()!=0)
			for (WifiLocation tmplocation : wifiLocation) {
				if(tmplocation.getName().equals(name)&&
						(tmplocation.getSsid().equals(ssid))){
					return false;
				}
			}
		System.out.println("Wifi Location added:\n"+"name: "+name+
			" ssid: "+ssid);
		wifiLocation.add(new WifiLocation(name, ssid));
		return false;
	}
	
	public boolean removeWifiLocation(String name, String ssid){
		
		if(wifiLocation.size()!=0)
			for (WifiLocation tmplocation : wifiLocation) {
				if(tmplocation.getName().equals(name)&&
						(tmplocation.getSsid().equals(ssid))){
					wifiLocation.remove(tmplocation);
					return false;
				}
			}
		System.out.println("Wifi Location removed:\n"+"name: "+name+
			"ssid: "+ssid);
		return false;
		
	}
	public JSONObject getWifiLocation() {
		JSONObject json=new JSONObject();
		if(wifiLocation.size()!=0)
			for (int i=0;i<wifiLocation.size();i++) {
				JSONArray jsonLocation = new JSONArray();
				
					jsonLocation.add(wifiLocation.get(i).getName());
					jsonLocation.add(wifiLocation.get(i).getSsid());
					json.put("location"+i, jsonLocation);
			}
			System.out.println("json output: "+json.toJSONString());
		return json;
	}
	public JSONObject getWifiPost(String username, String ssid) {
		JSONObject json=new JSONObject();
		ArrayList<Property> property=new ArrayList<Property>();
		if(user.size()!=0&&post.size()!=0&&wifiLocation.size()!=0){
			for (int i=0;i<user.size();i++) {
				if(user.get(i).getUsername().equals(username)){
					property=user.get(i).getProperty();
					System.out.println("User exists!");
					break;
				}
			}
			
			for(int i=0,w=0;i<wifiLocation.size();i++){
				if(wifiLocation.get(i).getSsid().equals(ssid)){
				System.out.println("Location: "+wifiLocation.get(i).getName()+":"+wifiLocation.get(i).getSsid());
					for(int j=0 ; j<post.size();j++){
						JSONArray arrayPost=new JSONArray();
						if(post.get(j).getLocation().equals(wifiLocation.get(i).getName())){
							System.out.println("Post with: "+post.get(j).getFilter());
							if(post.get(j).getFilter().equals("Whitelist")&&property.size()!=0){
								for(Property p:post.get(j).getProperty()){
									if(property.contains(p)){
										arrayPost.add(post.get(j).getTitle());
										arrayPost.add(post.get(j).getMessage());
										arrayPost.add(post.get(j).getUsername());
										arrayPost.add(post.get(j).getStartDate());
										arrayPost.add(post.get(j).getEndDate());
										arrayPost.add(post.get(j).getLocation());
										arrayPost.add(post.get(j).getFilter());
										arrayPost.add(post.get(j).getMode());
										arrayPost.add(post.get(j).getPropertyString());
										
										json.put("post"+w, arrayPost);
										w++;
										System.out.println("whitepost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
										break;
									}
								}
							}else if(post.get(j).getFilter().equals("Blacklist")){
								if(property.size()!=0){
									for(Property p:post.get(j).getProperty()){
										if(!property.contains(p)){
											arrayPost.add(post.get(j).getTitle());
											arrayPost.add(post.get(j).getMessage());
											arrayPost.add(post.get(j).getUsername());
											arrayPost.add(post.get(j).getStartDate());
											arrayPost.add(post.get(j).getEndDate());
											arrayPost.add(post.get(j).getLocation());
											arrayPost.add(post.get(j).getFilter());
											arrayPost.add(post.get(j).getMode());
											if(post.get(j).getPropertyString()!=null)
												arrayPost.add(post.get(j).getPropertyString());
											else
												arrayPost.add(" ");
											
											json.put("post"+w, arrayPost);
											w++;
											System.out.println("blackpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
											break;
										}
									}
								}else{
									arrayPost.add(post.get(j).getTitle());
									arrayPost.add(post.get(j).getMessage());
									arrayPost.add(post.get(j).getUsername());
									arrayPost.add(post.get(j).getStartDate());
									arrayPost.add(post.get(j).getEndDate());
									arrayPost.add(post.get(j).getLocation());
									arrayPost.add(post.get(j).getFilter());
									arrayPost.add(post.get(j).getMode());
									if(post.get(j).getPropertyString()!=null)
										arrayPost.add(post.get(j).getPropertyString());
									else
										arrayPost.add(" ");
									
									json.put("post"+w, arrayPost);
									w++;
									System.out.println("blackpost: "+post.get(j).getTitle()+" : "+post.get(j).getMode()+" : "+post.get(j).getFilter()+" : "+post.get(j).getPropertyString());
									break;
									
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
}
