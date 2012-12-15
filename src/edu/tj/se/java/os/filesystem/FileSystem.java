/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import java.awt.GridLayout;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.org.mozilla.javascript.internal.ast.ContinueStatement;

/**
 *
 * @author lenovo
 */
public class FileSystem{
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
            String text = FileSystemGUI.getScreenText();
            
            pathArray[newBlock] = path.toString();
            memory.fat[newBlock].isFATUsed = true;
            memory.fat[newBlock].currentBlock = newBlock;
            
            FileBlock newFileBlock = new FileBlock();     
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
//            FileBlock new1 = (FileBlock)memory.fileMap.get(newFileBlock.fcb.startBlock);
//            System.out.println(new1.fcb.createTime);
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
    
    public int getBlockPosition(TreePath path){
        String pathString = path.toString();
        for(int i = 0;i < MAX_SIZE;i++){
            if (pathArray[i].equals(pathString)) {
                return i;
            }
        }
        return -1;
    }
    
    
    
    /**
     *
     * @param name
     * @return
     */
    public boolean isIllegalName(String name){
        String regEx = "[^0-9a-zA-Z]";
        Pattern regexPattern = Pattern.compile(regEx);
        Matcher matchRslt = regexPattern.matcher(name);
        boolean match = matchRslt.find();
        return match;
    }
    
    public boolean hasTheSameNameInPath(String name,DefaultMutableTreeNode node,TreePath path,boolean isFile){
        DefaultTreeModel treeModel = FileSystemGUI.getTreeModel();
        if (node.getChildCount() >= 0) {
           for (Enumeration e = node.children(); e.hasMoreElements(); ) {
               DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)e.nextElement();
               if ((childNode.getAllowsChildren() && !isFile) || ((!childNode.getAllowsChildren() && isFile))) {
                   TreePath childPath = new TreePath(treeModel.getPathToRoot(childNode));
                   int blockPosition = getBlockPosition(childPath);
                   FileBlock block = (FileBlock)memory.fileMap.get(blockPosition);
                   if (block.fcb.fileName.equals(name)) {
                       return true;
                   }
               }
           }
       }
        return false;
    }
    
    public void fileProperty(DefaultMutableTreeNode node,TreePath path) {
        int blockPosition = getBlockPosition(path);
        FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
        String frameTitle = fileBlock.fcb.fileName;
        int childCount = node.getChildCount();
        
        JFrame frame = new JFrame(frameTitle);
        frame.setSize(500, 300);
        frame.setVisible(true);
        
        GridLayout gridLayout = new GridLayout(6, 1);
        frame.setLayout(gridLayout);
        
        JLabel general = new JLabel("[Property]");
        JLabel fileName = new JLabel("Name:" + fileBlock.fcb.fileName);
        JLabel blockNumber = new JLabel("Block Address:" + String.valueOf(fileBlock.fcb.startBlock));      
        JLabel createTime = new JLabel("Create Time:" + fileBlock.fcb.createTime);
               
        frame.add(general);
        frame.add(fileName);        
        frame.add(blockNumber);
        frame.add(createTime);
        
        if (!node.getAllowsChildren()) {
            JLabel modifiedTime = new JLabel("Modified Time:" + fileBlock.fcb.modifiedTime);
            JLabel fileSize = new JLabel("Size:" + String.valueOf(fileBlock.fcb.size));
            frame.add(modifiedTime);
            frame.add(fileSize);
        }else{
            JLabel itemCount = new JLabel("Items:" + childCount);
            frame.add(itemCount);
        }
            
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
    
    public void newItem(DefaultMutableTreeNode node,TreePath path,boolean isFile){
        String newFolderName = JOptionPane.showInputDialog(null, "Please input folder name");
        if (newFolderName == null) {
            JOptionPane.showMessageDialog(null, "Empty Operation!");
            return;
        }else if(newFolderName.length() > MAX_SIZE){
            JOptionPane.showMessageDialog(null, "The max length of a folder name is 128!");
            return;
        }else if(isIllegalName(newFolderName)){
            JOptionPane.showMessageDialog(null, "Only character and numbers can be used to be a folder name!");
            return;
        }else if(hasTheSameNameInPath(newFolderName, node, path,isFile)){
            JOptionPane.showMessageDialog(null, "Sorry, but the name exists in current path!");
            return;
        }else{
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFolderName);
            if (!isFile) {
                newNode.setAllowsChildren(true);
            }  
            FileSystemGUI.insertNode(node, newNode);
            DefaultTreeModel model = FileSystemGUI.getTreeModel();
            path = new TreePath(model.getPathToRoot(newNode));
            addToMemory(newNode, path);
        }
    }
    
    
    public void renameFile(DefaultMutableTreeNode node,TreePath path,String newName){
        boolean isFile,renameSucceed = false;
        String tryAgain;
        while(!renameSucceed){
            if (newName == null) {
                tryAgain = JOptionPane.showInputDialog(null, "Empty Operation!");
                continue;
            }else if(newName.length() > MAX_SIZE){
                tryAgain = JOptionPane.showInputDialog(null, "The max length of a folder name is 128!");
                return;
            }else if(isIllegalName(newName)){
                tryAgain = JOptionPane.showInputDialog(null, "Only character and numbers can be used to be a folder name!");
                return;
            }else if(hasTheSameNameInPath(newName, node, path,isFile)){
                tryAgain = JOptionPane.showInputDialog(null, "Sorry, but the name exists in current path!");
                return;
            }
        }
        String newName = JOptionPane.showInputDialog(null, "Please input the new name.");
        isFile = node.getAllowsChildren() ? false : true;
        JTree jTree = FileSystemGUI.getJTree();
        jTree.setEditable(true);
            
        
    }
    
    public void deleteFile(){
        
    }
    
    public void formatFile(){
        
    }
    
    public void quitFile(){
        
    }
}
