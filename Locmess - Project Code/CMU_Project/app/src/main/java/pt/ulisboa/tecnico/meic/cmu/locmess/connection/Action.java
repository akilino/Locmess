package pt.ulisboa.tecnico.meic.cmu.locmess.connection;

import org.json.simple.JSONObject;

public class Action {
    private MessageType type;
    private JSONObject json;
    public Action(MessageType type, JSONObject json) {
        this.type = type;
        this.json=json;
    }
    public MessageType getType() {
        return type;
    }
    public JSONObject getJSON(){ return json; }
}