/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author lenovo
 */
public class FileSystem {
    final int MAX_SIZE = 128;
    final double VERSION = 0.9;
    
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
    
    public boolean hasTheSameFolderNameInPath(String name,DefaultMutableTreeNode node,TreePath path){
        DefaultTreeModel treeModel = FileSystemGUI.getTreeModel();
        if (node.getChildCount() >= 0) {
           for (Enumeration e = node.children(); e.hasMoreElements(); ) {
               DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)e.nextElement();
               if (childNode.getAllowsChildren()) {
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
    
    public boolean hasTheSameFileNameInPath(String name,DefaultMutableTreeNode node,TreePath path){
        DefaultTreeModel treeModel = FileSystemGUI.getTreeModel();
        if (node.getChildCount() >= 0) {
           for (Enumeration e = node.children(); e.hasMoreElements(); ) {
               DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)e.nextElement();
               if (childNode.getAllowsChildren()) {
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
    
    public void deleteChild(DefaultMutableTreeNode node,TreePath path,DefaultTreeModel treeModel){
        for(int i = 0;i < node.getChildCount();i++){
            if(node.getChildAt(i).getAllowsChildren()){
                deleteChild((DefaultMutableTreeNode)node.getChildAt(i), path,treeModel);
            }else{
                path = new TreePath(treeModel.getPathToRoot((DefaultMutableTreeNode)node.getChildAt(i)));
                int blockPosition = getBlockPosition(path);
                
                memory.fileMap.remove(blockPosition);
                memory.fat[blockPosition].isFATUsed = false;
                memory.fat[blockPosition].currentBlock = -1;
            }
        }
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
    
    public void newFolder(DefaultMutableTreeNode node,TreePath path){
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
        }else if(hasTheSameFolderNameInPath(newFolderName, node, path)){
            JOptionPane.showMessageDialog(null, "Sorry, but the name exists in current path!");
            return;
        }else{
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFolderName);
            newNode.setAllowsChildren(true);
            FileSystemGUI.insertNode(node, newNode);
            DefaultTreeModel model = FileSystemGUI.getTreeModel();
            path = new TreePath(model.getPathToRoot(newNode));
            addToMemory(newNode, path);
        }
    }
    
    public void newFile(DefaultMutableTreeNode node,TreePath path){
        String newFileName = JOptionPane.showInputDialog(null, "Please input file name");
        if (newFileName == null) {
            JOptionPane.showMessageDialog(null, "Empty Operation!");
            return;
        }else if(newFileName.length() > MAX_SIZE){
            JOptionPane.showMessageDialog(null, "The max length of a file name is 128!");
            return;
        }else if(isIllegalName(newFileName)){
            JOptionPane.showMessageDialog(null, "Only character and numbers can be used to be a file name!");
            return;
        }else if(hasTheSameFileNameInPath(newFileName, node, path)){
            JOptionPane.showMessageDialog(null, "Sorry, but the name exists in current path!");
            return;
        }else{
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFileName);
            newNode.setAllowsChildren(false);
            FileSystemGUI.insertNode(node, newNode);
            DefaultTreeModel model = FileSystemGUI.getTreeModel();
            path = new TreePath(model.getPathToRoot(newNode));
            addToMemory(newNode, path);
        }
    }
    
    public void aboutFileSystem(){
        JFrame frame = new JFrame("About");
        frame.setSize(350,200);
        frame.setVisible(true);
        
        GridLayout gridLayout = new GridLayout(4, 1);
        frame.setLayout(gridLayout);
        
        JLabel nameLabel = new JLabel("File System© V" + VERSION);
        JLabel copyRightLabel = new JLabel("Created by Lazy at SSE.STJU");
        final JLabel urlLabel = new JLabel("Project Address: https://github.com/liil444/FileSystem");
        urlLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event){
                URI uri;
                try {
                    uri = new URI("https://github.com/liil444/FileSystem");
                    Desktop desktop = Desktop.getDesktop();
                    
                    if(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.OPEN)){
                        try {
                            desktop.browse(uri);
                        } catch (IOException e) {
                            
                        }
                    }
                } catch (URISyntaxException e) {
                }
            }
            
            public void mouseEntered(MouseEvent event){
                
            }
            
            public void mouseExited(MouseEvent event){
                
            }
        });
        
        frame.add(nameLabel);
        frame.add(copyRightLabel);
        frame.add(urlLabel);
    }
    
    public void deleteFile(DefaultMutableTreeNode node,TreePath path){
        DefaultTreeModel treeModel = FileSystemGUI.getTreeModel();
        int blockPosition = getBlockPosition(path);
        FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
        
        String rootName = "X:";
        if(fileBlock.fcb.fileName.equals(rootName)){
            JOptionPane.showMessageDialog(null, "Can't delete root folder!");
            return;
        }
        
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
        if(node.getAllowsChildren()){
            deleteChild(node,path,treeModel);
            treeModel.removeNodeFromParent(node);   
        }else{
            memory.fileMap.remove(blockPosition);
            memory.fat[blockPosition].isFATUsed = false;
            memory.fat[blockPosition].currentBlock = -1;
            treeModel.removeNodeFromParent(node);            
        }
    }
    
    public void formatFile(){
        
    }
    
    public void quitFile(){
        
    }
}
