/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;


import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import javax.swing.filechooser.FileNameExtensionFilter;
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
    final String VERSION = "1.0.2";
    
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
                newFileBlock.fcb.hasChild = true;
            }else{
                newFileBlock.fcb.size = text.length();
                newFileBlock.fcb.hasChild = false;
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
               if (!childNode.getAllowsChildren()) {
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
        int childCount = fileBlock.fcb.itemNumber;
        
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
        if (newFolderName.equals("") || newFolderName == null) {
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
            
            int itemNumber = node.getChildCount();
            int blockPosition = getBlockPosition(path);
            FileBlock parentBlock = (FileBlock)memory.fileMap.get(blockPosition);
            parentBlock.fcb.itemNumber++;
            memory.fileMap.put(blockPosition, parentBlock);
            
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
        if (newFileName.equals("") || newFileName == null) {
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
            
            int itemNumber = node.getChildCount();
            int blockPosition = getBlockPosition(path);
            FileBlock parentBlock = (FileBlock)memory.fileMap.get(blockPosition);
            parentBlock.fcb.itemNumber++;
            memory.fileMap.put(blockPosition, parentBlock);
            
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
        path = new TreePath(treeModel.getPathToRoot(parentNode));
        blockPosition = getBlockPosition(path);
        fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
        fileBlock.fcb.itemNumber--;
        memory.fileMap.put(blockPosition, fileBlock);
    }
    
    public void formatFile(DefaultMutableTreeNode node,TreePath path){
        int showConfirmDialog = JOptionPane.showConfirmDialog(null, "Are you sure?");
        if (showConfirmDialog == 0) {
             DefaultTreeModel treeModel = FileSystemGUI.getTreeModel();
            int blockPosition = getBlockPosition(path);
            FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
            if(node.getAllowsChildren()){
                for(int i = 0;i < node.getChildCount();){
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)node.getChildAt(i);
                    path = new TreePath(treeModel.getPathToRoot(childNode));
                    deleteFile(childNode, path);
                }
            }
        }   
    }
    
    public void quitFile(DefaultMutableTreeNode node,String diskFileName){
        int save = JOptionPane.showConfirmDialog(null, "Save file?");
        if(save == 0){
            saveFileToDisk(node,diskFileName);
            System.exit(0);
        }else if(save == 1){
            System.exit(0);
        }else{
            return;
        }
    }
    
    public void saveFileToDisk(DefaultMutableTreeNode node,String diskFileName){
        try {
            FileWriter writer = new FileWriter(diskFileName);
            DefaultTreeModel model = FileSystemGUI.getTreeModel();
            
            Enumeration allNodes = node.preorderEnumeration();
            while (allNodes.hasMoreElements()) {
                String data = "";
                DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode)allNodes.nextElement();
                TreePath path = new TreePath(model.getPathToRoot(nextNode));
                int blockPosition = getBlockPosition(path);
                FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
                data = generateData(fileBlock);
                writer.write(data);
            }
            JOptionPane.showMessageDialog(null, "File has been created, filename is " + diskFileName);
            writer.close();
        } catch (IOException e) {
        }     
    }
    
    public void reloadSource(String diskFileName){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(diskFileName));          
            String readLine = reader.readLine();
            FileBlock fileBlock = new FileBlock();     
            DefaultTreeModel model = FileSystemGUI.getTreeModel();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)model.getRoot();  
            fileBlock = recoverData(readLine, model);
            while ((readLine = reader.readLine()) != null){
                fileBlock = recoverData(readLine, model);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileBlock.fcb.fileName);
                newNode.setAllowsChildren(fileBlock.fcb.hasChild);

                TreePath parentPath = new TreePath(model.getPathToRoot(rootNode));
                int parentBlockNumber = getBlockPosition(parentPath);
                FileBlock parentBlock = (FileBlock)memory.fileMap.get(parentBlockNumber);

                FileSystemGUI.insertNode(rootNode, newNode);

                if(newNode.getAllowsChildren() && newNode.getChildCount() != fileBlock.fcb.itemNumber){
                    recoverAllPoint(reader, model, newNode);
                }
            }
        } catch (IOException e) {
        }
    }
    
    public void recoverAllPoint(BufferedReader reader,DefaultTreeModel model,DefaultMutableTreeNode node){
        try {     
            FileBlock fileBlock = new FileBlock();
            String readLine;
            while ((readLine = reader.readLine()) != null) {                
                fileBlock = recoverData(readLine, model);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileBlock.fcb.fileName);
                newNode.setAllowsChildren(fileBlock.fcb.hasChild);

                TreePath parentPath = new TreePath(model.getPathToRoot(node));
                int parentBlockNumber = getBlockPosition(parentPath);
                FileBlock parentBlock = (FileBlock)memory.fileMap.get(parentBlockNumber);

                FileSystemGUI.insertNode(node, newNode);

                if(newNode.getAllowsChildren() && newNode.getChildCount() != fileBlock.fcb.itemNumber){
                    recoverAllPoint(reader, model, newNode);
                }
                if(node.getChildCount() == parentBlock.fcb.itemNumber){           
                    return;
                }
            }       
        } catch (IOException e) {
        }
        
    }
    
    private FileBlock recoverData(String readLine,DefaultTreeModel model){
        FileBlock fileBlock = new FileBlock();
        String fcbArray[] = readLine.split("┬");
        int blockNumber = Integer.valueOf(fcbArray[0]);
        pathArray[blockNumber] = fcbArray[5];
        fileBlock.data.text = fcbArray[7];
        fileBlock.fcb.createTime = fcbArray[4];
        fileBlock.fcb.fileName = fcbArray[3];
        fileBlock.fcb.filePath = fcbArray[5];
        fileBlock.fcb.hasChild = fcbArray[1].equals("true") ? true : false;
        fileBlock.fcb.modifiedTime = fcbArray[6];
        fileBlock.fcb.size = Integer.valueOf(fcbArray[2]);
        fileBlock.fcb.startBlock = blockNumber;
        fileBlock.fcb.itemNumber = Integer.valueOf(fcbArray[8]);
        memory.fat[blockNumber].isFATUsed = true;
        memory.fat[blockNumber].currentBlock = blockNumber;
        memory.fileMap.remove(blockNumber);
        memory.fileMap.put(blockNumber,fileBlock);
        return fileBlock;
    }
    
    private String generateData(FileBlock block){
        String data = "";
        data = appendString(data, String.valueOf(block.fcb.startBlock));
        data = appendString(data, "┬");
        data = appendString(data, String.valueOf(block.fcb.hasChild));
        data = appendString(data, "┬");
        data = appendString(data, String.valueOf(block.fcb.size));
        data = appendString(data, "┬");
        data = appendString(data, block.fcb.fileName);
        data = appendString(data, "┬");
        data = appendString(data, block.fcb.createTime);
        data = appendString(data, "┬");
        data = appendString(data, block.fcb.filePath);
        data = appendString(data, "┬");
        data = appendString(data, block.fcb.modifiedTime);
        data = appendString(data, "┬");
        data = appendString(data, block.data.text);
        data = appendString(data, "┬");
        data = appendString(data, String.valueOf(block.fcb.itemNumber));
        data = appendString(data, "┬");
        data = appendString(data, "\n");
        return data;
    }
    
    private String appendString(String des,String src){
        des = (new StringBuilder(des).append(src)).toString();
        return des;
    }
}
