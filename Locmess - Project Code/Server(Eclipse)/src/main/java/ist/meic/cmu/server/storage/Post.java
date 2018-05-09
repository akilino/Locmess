package ist.meic.cmu.server.storage;

import java.util.ArrayList;
import java.util.Date;

public class Post {
	private String title;
	private String message;
	private Date startDate;
	private Date endDate;
	private String username;
	private String location;
	private String filter;
	private String mode;
	private ArrayList<Property> property=new ArrayList<Property>();
	public Post(String title, String message, String username, Date startDate, Date endDate, String location, String filter, String mode, ArrayList<Property> property) {
		this.title=title;
		this.message=message;
		this.username = username;
		this.startDate=startDate;
		this.endDate=endDate;
		this.location=location;
		this.filter=filter;
		this.mode=mode;
		if(property!=null)
		this.property = property;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String name) {
		this.title = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public ArrayList<Property> getProperty() {
		return property;
	}
	public String getPropertyString(){
		String result="";
		if(property.size()==0)
			return null;
		for(Property p:property){
			result+=p.getKey()+","+p.getValue();
		}
		return result;
	}
	public boolean addProperty(Property property) {
		if(this.property.size()!=0&&this.property.contains(property)){
			return false;
		}
		this.property.add(property);
		return true;
	}
	public boolean removeProperty(Property property) {
		if(this.property.size()!=0&&this.property.contains(property)){
			this.property.remove(property);
			return true;
		}
		return false;
	}

}
