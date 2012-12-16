/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 *
 * @author lenovo
 */
public class Memory {
    final int MAX_SIZE = 128;
    public FAT fat[];
    public Map fileMap;
    
    public Memory() {
        fileMap = new HashMap();
        fat = new FAT[MAX_SIZE];
        for(int i = 0;i < MAX_SIZE;i++){
            fat[i] = new FAT();
        }
    }
    
}
class Data{
    final int MAX_FILE_LENGTH  = 128;
    public String text;

    public Data() {
       text = new String();
    }  
    
    public void clear(){
        text = "";
    }
}

class FAT{
    public boolean isFATUsed;
    public int nextBlock;
    public int currentBlock;

    public FAT() {
        isFATUsed = false;
        nextBlock = -1;
        currentBlock = -1;
    }
    
    public void clear(){
        isFATUsed = false;
    }
    
    public void setNextBlock(int block){
        nextBlock = block;
    }
    
    public void setCurrentBlock(int block){
        currentBlock = block;
    }
}


class FCB{
    public String fileName;
    public String filePath;
    public int size;
    public String createTime;
    public String modifiedTime;
    public int startBlock;

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


class FileBlock{
    FCB fcb;
    Data data;

    public FileBlock() {
        fcb = new FCB();
        data = new Data();
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
