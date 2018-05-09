package ist.meic.cmu.server.tool;

public class StringParser {
    public static String[] getProperty(String input){
        String tmp=input.substring(input.indexOf("[") + 1, input.indexOf("]"));
        String[] output=tmp.split(",");
        return output;
    }
}
