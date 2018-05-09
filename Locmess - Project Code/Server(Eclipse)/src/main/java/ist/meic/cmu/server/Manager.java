package ist.meic.cmu.server;
import ist.meic.cmu.server.storage.Storage;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class Manager {
	
	private Storage storage=new Storage();
	/*
	private DBConnection conn=DBConnection.getInstance();
	public void reloadLocalization(){
		location=conn.getAllLocation();
	}
	*/
    @RequestMapping(value="/create", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject createAccount(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String password=(String) json.get("password");
    	System.out.println("Preparing for register...");
    	if(storage.createUser(username, password)){
    		System.out.println(username+"==> Created");
    		return json;
    	}
        return null;
    }

    @RequestMapping(value="/login", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject logIn(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String password=(String) json.get("password");
    	System.out.println("Preparing for login...");
    	if(storage.verifyUser(username, password)){
    		System.out.println(username+"==> Login");
    		String sessionid=storage.giveSessionID(username, password);
    		json.put("sessionid", sessionid);
    		System.out.println("sessionID: "+json.get("sessionid"));
    		return json;
    	}
        return null;
    }
    
    @RequestMapping(value="/logout", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject logOut(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String sessionid=(String) json.get("sessionid");
    	System.out.println("Preparing for logout...");
    	if(storage.logout(username, sessionid)){
    		System.out.println(username+"==> Logout");
    		System.out.println("sessionID: "+json.get("sessionid"));
    		return json;
    	}
        return null;
    }
    
    
    //TODO, don't know if it's necessary
    @RequestMapping(value="/userlocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject userLocation(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String sessionid=(String) json.get("sessionid");
    	String lat=(String) json.get("latitude");
    	String lon=(String) json.get("longitude");
    	System.out.println("Preparing for adding location...");
    	if(storage.userLocation(username, lat, lon, sessionid)){
    		System.out.println("User: "+username+"\nsessionid: "+sessionid+"\nlatitude: "+lat+"\nlongitude: "+lon);
    		return json;
    	}
		return null;
    }
    
    @RequestMapping(value="/createlocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject addLocation(@RequestBody JSONObject json)
    {
    	String name=(String) json.get("name");
    	String lat=(String) json.get("latitude");
    	String lon=(String) json.get("longitude");
    	String radius=(String) json.get("radius");
		System.out.println("name: "+name+
				"latitude: "+lat+
					"longitude: "+lon+
				"radius: "+radius);
    	System.out.println("Preparing for adding location...");

    	if(storage.addLocation(name, lat, lon, radius)){
    		return json;
    	}
		return null;
    }
    @RequestMapping(value="/addwifiLocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject addWifiLocation(@RequestBody JSONObject json)
    {
    	String name=(String) json.get("name");
    	String ssid=(String) json.get("ssid");
		System.out.println("name: "+name+
				" ssid: "+ssid);
    	System.out.println("Preparing for adding wifi location...");

    	if(storage.addWifiLocation(name, ssid)){
    		return json;
    	}
		return null;
    }
    
    @RequestMapping(value="/removewifiLocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject removeWifiLocation(@RequestBody JSONObject json)
    {
    	String name=(String) json.get("name");
    	String ssid=(String) json.get("ssid");
		System.out.println("name: "+name+
				"ssid: "+ssid);
    	System.out.println("Preparing for removing wifi location...");

    	if(storage.removeWifiLocation(name, ssid)){
    		return json;
    	}
		return null;
    }
    
    @RequestMapping(value="/removelocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject removeLocation(@RequestBody JSONObject json)
    {
    	String name=(String) json.get("name");
    	String lat=(String) json.get("latitude");
    	String lon=(String) json.get("longitude");
    	System.out.println("Preparing for removing location...");
    	if(storage.removeLocation(name, lat, lon)){
    		System.out.println("Location "+name+" removed");
    		return json;
    	}
		return null;
    }

  //maybe it's not necessary-1 (there is 2)
    @RequestMapping(value="/checklocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getLocation(@RequestBody JSONObject json)
    {
    	System.out.println("Preparing for get location...");
		return storage.getLocation();
    }
    
    @RequestMapping(value="/checkwifilocation", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getWifiLocation(@RequestBody JSONObject json)
    {
    	
    	System.out.println("Preparing for get location...");
		return storage.getWifiLocation();
    }
    
    @RequestMapping(value="/sendpost", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject sendPost(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String name=(String) json.get("title");
    	String message=(String) json.get("message");
    	String startDate=(String) json.get("startDate");
    	String endDate=(String) json.get("endDate");
    	String location=(String) json.get("location");
    	String filter=(String) json.get("filter");
    	String mode=(String) json.get("mode");
    	String property=(String) json.get("property");
    	System.out.println("Preparing for adding post...");
    	System.out.println("SendPost: "+username+
    			"\n"+name+
    			"\n"+startDate+
    			"\n"+endDate+
    			"\n"+location+
    			"\n"+filter+
    			"\n"+mode+
    			"\n"+property);
    	if(storage.sendPost(name, message, username, startDate, endDate, location, filter, mode, property)){
    		return json;
    	}
		return null;
    }
    
    @RequestMapping(value="/checkpost", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getPost(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String latitude=(String) json.get("latitude");
    	String longitude=(String) json.get("longitude");
    	System.out.println("Preparing for get post... "+username+":"+latitude+":"+longitude);
    	if(username!=null&&latitude!=null&&longitude!=null){
    		return storage.getPost(username, latitude, longitude);
    	}
    	return null;
    }
    
    @RequestMapping(value="/deletepost", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject deletePost(@RequestBody JSONObject json)
    {
    	String title=(String) json.get("title");
    	String message=(String) json.get("message");
    	String username=(String) json.get("username");
    	String location=(String) json.get("location");
    	System.out.println("Preparing for deleting post...");
    	if(storage.deletePost(title, message, username, location)){
    		return json;
    	}
		return null;
    }
    
    @RequestMapping(value="/checkwifipost", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getWifiPost(@RequestBody JSONObject json)
    {
    	String ssid=(String) json.get("ssid");
    	String username=(String) json.get("username");
    	System.out.println("Preparing for get post... "+ssid+":"+username);
    	if(ssid!=null){
    		return storage.getWifiPost(ssid, username);
    	}
    	return null;
    }

    @RequestMapping(value="/getproperty", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getProperty(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
    	String sessionid=(String) json.get("sessionid");
    	System.out.println("Preparing for get property...");
		return storage.getProperty(username, sessionid);
    }
    
    @RequestMapping(value="/getallproperties", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject getAllProperties(@RequestBody JSONObject json)
    {
		return storage.getAllProperties();
    }
    
    @RequestMapping(value="/sendproperty", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject sendProperty(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
		String sessionid=(String) json.get("sessionid");
		String key=(String) json.get("key");
		String value=(String) json.get("value");
		System.out.println("Preparing for add property...");
		if(storage.addProperty(username, sessionid, key, value)){
			System.out.println(key+":"+value+ " added to: "+username);
			return json;
		}
        return null;
    }
    
    @RequestMapping(value="/deleteproperty", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject deleteProperty(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
		String sessionid=(String) json.get("sessionid");
		String key=(String) json.get("key");
		String value=(String) json.get("value");
		System.out.println("Preparing for delete property... "+key+":"+value);
		if(storage.removeProperty(username, sessionid, key, value)){
			System.out.println(key+":"+value+ " removed from: "+username);
			return json;
		}
        return null;
    }
    
    @RequestMapping(value="/editproperty", method={ RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public JSONObject editProperty(@RequestBody JSONObject json)
    {
    	String username=(String) json.get("username");
		String sessionid=(String) json.get("sessionid");
		String oldkey=(String) json.get("oldkey");
		String oldvalue=(String) json.get("oldvalue");
		String newkey=(String) json.get("newkey");
		String newvalue=(String) json.get("newvalue");
		System.out.println("Preparing for edit property..."+oldkey+":"+oldvalue+"\nto new: "+newkey+":"+newvalue);
		if(storage.editProperty(username, sessionid, oldkey, oldvalue, newkey, newvalue)){
			System.out.println(oldkey+":"+oldvalue+ " edited from: "+username+" to :"+newkey+":"+newvalue);
			return json;
		}
        return null;
    }
	
    /*
	@RequestMapping("/")
	@SuppressWarnings("unchecked")
    public JSONObject getArlindo()
    {
    	JSONObject j=new JSONObject();
    	j.put("Tiago", "Cruz");
    	j.put("estupidamente", "lindo");
    	j.put("dava-lhe de 0 a 5", 1);
        return j;
    }
	
	@RequestMapping("/jonhypeter")
	@SuppressWarnings("unchecked")
    public String forJokes()
    {
		File htmlTemplateFile = new File("page.html");
    	String page="<!DOCTYPE html>"+
    	"<html>"+
    	"<head>"+
    	"<style>"+
    	"code { "+
    	"   font-family: monospace; }"+
    	"</style>"+
    	"</head>"+
    	"<body>"+
    	"<p>I'm looking for a chicken! :)</p>"+
    	"<img src=\"https://scontent.flis7-1.fna.fbcdn.net/v/t1.0-9/14639631_1122255697829888_4999803540062379478_n.jpg?oh=640c8c0a8e3d7512488f3d89a1778910&oe=59745E1D\"height=\"300\" width=\"300\">"+
    	"<p>Hello! My name is Jonhy Peter, I'm such a badboy, I like to get some chickens to eat.</p>"+
    	"<p>If you are a chicken, just call me maybe.</p>"+
    	"<p>Thanks.</p>"+
    	"<p>Oh! By the way! You can just add me on my facebook: </p>"+
    	"<p>https://www.facebook.com/profile.php?id=100001364065596&fref=hovercard </p>"+
    	"</body>"+
    	"</html>";
        return page;
    }
	*/
    
}
