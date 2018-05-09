package pt.ulisboa.tecnico.meic.cmu.locmess.storage;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.post.Post;

/**
 * Created by Sheng on 16/05/2017.
 */

public class FileStorage {


    private static final String filenamePosts="posts";
    private static final String filenameLocations="locations";
    private Context context;
    private HashMap<String, Post> postHashMap;
    public FileStorage(Context context){
        this.context=context;
        postHashMap=new HashMap<String, Post>();
        readPosts();
    }

    public void writePost(Post post) {

        File directory = new File(context.getFilesDir().getAbsolutePath() + File.separator + "serialization");

        if(!directory.exists()){
            directory.mkdir();
        }
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(directory + File.separator + filenamePosts);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            //Maybe it's necessary
            for(String p: postHashMap.keySet()){
                if(p.equals(post.getTitle().toString() +","+ post.getMessage().toString()))
                    return;

            }
            postHashMap.put((post.getTitle().toString() +","+ post.getMessage().toString()), post);
            objectOutputStream.writeObject(postHashMap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fileOutputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void readPosts(){

        File directory = new File(context.getFilesDir().getAbsolutePath() + File.separator + "serialization");
        FileInputStream fileInputStream=null;
        ObjectInputStream objectInputStream=null;
        if(directory.exists()){
            try {
                fileInputStream = new FileInputStream(directory + File.separator + filenamePosts);
                objectInputStream = new ObjectInputStream(fileInputStream);
                postHashMap = (HashMap) objectInputStream.readObject();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    objectInputStream.close();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String getPost(){
        String result="";
        for (Post post : postHashMap.values()) {
            result+=post.getTitle()+","+post.getMessage()+","+post.getLocation()+","+post.getUser()+","+post.getMode()+";";
        }
        Log.d("PostMap", result);
        return result;
    }
    public void receivePost(String received){
        String[] receivedPost=received.split(";");
        for(int i=0; i<receivedPost.length;i++){
            String[] valuesPost=receivedPost[i].split(",");
            try {
                writePost(new Post(valuesPost[0],valuesPost[1],valuesPost[2],valuesPost[3],valuesPost[4]));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
