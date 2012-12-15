/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author lenovo
 */
public class FileSystem {
    final int MAX_SIZE = 128;
    
    Memory memory = new Memory();
    String pathArray[];
    
    public FileSystem() {
        pathArray = new String[MAX_SIZE];
        for(int i = 0;i < MAX_SIZE;i++){
            pathArray[i] = new String();
        }
    }
    
    public Memory getMemory(){
        return memory;
    }

    public void addToMemory(DefaultMutableTreeNode node,TreePath path){
        int newBlock = findFreeBlock();
        if(newBlock == -1){
            JOptionPane.showMessageDialog(null, "Memory is full!");
        }else{
            pathArray[newBlock] = path.toString();
            memory.fat[newBlock].isFATUsed = true;
            memory.fat[newBlock].currentBlock = newBlock;
            FileBlock newFileBlock = new FileBlock();
            String text = FileSystemGUI.getScreenText();
            newFileBlock.data.text = text;
            newFileBlock.fcb.filePath = path.toString();
            newFileBlock.fcb.fileName = node.toString();
            newFileBlock.fcb.startBlock = newBlock;
            if (node.getAllowsChildren()) {
                newFileBlock.fcb.size = 0;
            }else{
                newFileBlock.fcb.size = text.length();
            }
            newFileBlock.fcb.createTime = TimeFormat.getTimeFormat();
            memory.fileMap.put(newFileBlock.fcb.startBlock, newFileBlock);
            FileBlock new1 = (FileBlock)memory.fileMap.get(newFileBlock.fcb.startBlock);
            System.out.println(new1.fcb.createTime);
        }
    }
    
    private int findFreeBlock(){
        for(int i = 0;i < MAX_SIZE;i++){
            if(!memory.fat[i].isFATUsed){
                return i;
            }
        }
        return -1;
    }
    
    public void newFileSystem(){
        
    }
    
    public void fileProperty() {
        
    }
    
    public void saveFile(DefaultMutableTreeNode node,TreePath path){
        String text = FileSystemGUI.getScreenText();
        int blockPosition = getBlockPosition(path);
        FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
        fileBlock.data.clear();
        fileBlock.data.text = text;
        fileBlock.fcb.size = text.length();
        fileBlock.fcb.modifiedTime = TimeFormat.getTimeFormat();
        memory.fileMap.put(blockPosition, fileBlock);
    }
    
    public int getBlockPosition(TreePath path){
        String pathString = path.toString();
        for(int i = 0;i < MAX_SIZE;i++){
            if (pathArray[i].equals(pathString)) {
                return i;
            }
        }
        return -1;
    }
    
    public void newFolder(){
        
    }
    
    public void newFile(){
        
    }
    
    public void renameFile(){
        
    }
    
    public void deleteFile(){
        
    }
    
    public void formatFile(){
        
    }
    
    public void quitFile(){
        
    }
}
