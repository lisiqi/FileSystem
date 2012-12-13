/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import java.util.Date;


/**
 *
 * @author lenovo
 */
public class Memory {
    final int MAX_SIZE = 128;
    Data block[];
    FAT fat[];

    public Memory() {
        block = new Data[MAX_SIZE];
        fat = new FAT[MAX_SIZE];
        for(int i = 0;i < MAX_SIZE;i++){
            block[i] = new Data();
            fat[i] = new FAT();
        }
    }
    
}
class Data{
    final int MAX_FILE_LENGTH  = 256;
    char data[];

    public Data() {
        data = new char[MAX_FILE_LENGTH];
    }    
}

class FAT{
    boolean isFATUsed;
    int nextDataPosition;
    int currentDataPosition;

    public FAT() {
        
    }
    
    public void clear(){
        isFATUsed = false;
        nextDataPosition = -1;
        currentDataPosition = 0;
    }
}


class FCB{
    String fileName;
    String filePath;
    int size;
    String createTime;
    String modifiedTime;
    int startBlock;

    public FCB() {
        startBlock = -1;
        fileName = null;
        filePath = null;
        size = 0;
        createTime = null;
        modifiedTime = null;
    }
    
    public FCB(int sb,String ct,String fp,String fn,String mt){
        startBlock = sb;
        createTime = ct;
        filePath = fp;
        fileName = fn;
        modifiedTime = mt;
    }
    
    public void setModifiedTime(String mt){
        modifiedTime = mt;
    }
    
    public void setSize(int sz){
        size = sz;
    }
    
}

class TimeFormat{
    static String timeFormat;
    static Date date;  
    
    static public String getTimeFormat(){
        date = new Date();
        timeFormat = date.toString();
        return timeFormat;
    }
}
