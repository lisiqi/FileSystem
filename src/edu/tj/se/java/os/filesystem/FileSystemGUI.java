/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author lenovo
 */
public class FileSystemGUI extends JFrame implements ActionListener,TreeSelectionListener,MouseListener{
    
    final int WINDOW_SIZE_WIDTH = 500;
    final int WINDOW_SIZE_HEIGHT = 500;
    final int TREE_SIZE_WIDTH = 200;
    final int TREE_SIZE_HEIGHT = 400;

    //Upper Component
    static JPanel upperPanel = new JPanel();
    static JLabel pathLabel = new JLabel("Path:");
    static JTextField pathField = new JTextField("[X:]");
    
    //Center Component
    static JPanel middlePanel = new JPanel();   
    static JTextArea editArea = new JTextArea();
    static DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("X:");
    static DefaultTreeModel treeModel = new DefaultTreeModel(rootNode,true);
    static TreePath treePath;
    static JTree jTree = new JTree(rootNode);
    static JScrollPane treeScrollPane = new JScrollPane(jTree);
    static JScrollPane editAreaScrollPane = new JScrollPane(editArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    static JPopupMenu popupMenuFile = new JPopupMenu();
    static JPopupMenu popupMenuFolder = new JPopupMenu();
    static JMenuItem propertyItem = new JMenuItem("Property->");
    static JMenuItem newFileItem = new JMenuItem("New File->");
    static JMenuItem newFolderItem = new JMenuItem("New Folder->");
    
    //Lower Component
    static JPanel lowerPanel = new JPanel();
    static JButton propertyButton = new JButton("Property");
    static JButton saveButton = new JButton("Save");
    static JButton newFolderButton = new JButton("New Folder");
    static JButton newFileButton = new JButton("New File");
    static JButton aboutButton = new JButton("About");
    static JButton deleteButton = new JButton("Delete");
    static JButton formatButton = new JButton("Format");
    static JButton quitButton = new JButton("Quit");
    
    static FileSystem fileSystem = new FileSystem();
    
    public FileSystemGUI() {
        super("File System");
        drawGUI();
        addListener();
        fileSystem.newFileSystem();
    }

    private void drawGUI(){
        //General Setting
        setLookAndFeel();
        setSize(WINDOW_SIZE_HEIGHT,WINDOW_SIZE_WIDTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        //General Layout Setting
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);
        
        BorderLayout upperBorderLayout = new BorderLayout();
        upperPanel.setLayout(upperBorderLayout);
        upperPanel.add(pathLabel,BorderLayout.WEST);
        upperPanel.add(pathField,BorderLayout.CENTER);
        pathField.setEditable(false);
        add(upperPanel,BorderLayout.NORTH);
        
        BorderLayout middleBorderLayout = new BorderLayout();
        middlePanel.setLayout(middleBorderLayout);
        jTree.setAutoscrolls(true);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setCellEditor(new DefaultTreeCellEditor(jTree, new DefaultTreeCellRenderer()));
        jTree.setPreferredSize(new Dimension(TREE_SIZE_WIDTH, TREE_SIZE_HEIGHT));
        jTree.setEditable(false);
        rootNode.setAllowsChildren(true);
        treePath = new TreePath(treeModel.getPathToRoot(rootNode));
        fileSystem.addToMemory(rootNode, treePath);
        treeModel.setRoot(rootNode);
        jTree.setModel(treeModel);
        jTree.setShowsRootHandles(rootPaneCheckingEnabled);
        jTree.addTreeSelectionListener(this);
        //treeScrollPane.getViewport().add(jTree,null);
        middlePanel.add(jTree,BorderLayout.WEST);
        middlePanel.add(editAreaScrollPane,BorderLayout.CENTER);
        editArea.setLineWrap(true);
        editArea.setWrapStyleWord(true);
        editArea.setEditable(false);
        add(middlePanel,BorderLayout.CENTER);
        
        GridLayout gridLayout = new GridLayout(2, 4);
        lowerPanel.setLayout(gridLayout);
        lowerPanel.add(propertyButton);
        lowerPanel.add(saveButton);
        lowerPanel.add(newFolderButton);
        lowerPanel.add(newFileButton);   
        lowerPanel.add(deleteButton);
        lowerPanel.add(formatButton);
        lowerPanel.add(aboutButton);
        lowerPanel.add(quitButton);
        add(lowerPanel,BorderLayout.SOUTH);
        
        popupMenuFolder.add(propertyItem);
        popupMenuFolder.add(newFileItem);
        popupMenuFolder.add(newFolderItem);
             
        popupMenuFile.add(propertyItem);
    }
    
    
    private void setLookAndFeel(){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");    
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
        // ignore error
        }
    }
    
    private void addListener(){
        propertyButton.addActionListener(this);
        saveButton.addActionListener(this);
        newFolderButton.addActionListener(this);
        newFileButton.addActionListener(this);
        aboutButton.addActionListener(this);
        deleteButton.addActionListener(this);
        formatButton.addActionListener(this);
        quitButton.addActionListener(this);
        propertyItem.addActionListener(this);
        newFileItem.addActionListener(this);
        newFolderItem.addActionListener(this);
        jTree.addMouseListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        String actionCommand = event.getActionCommand();
        switch(actionCommand){
            case "About":
                fileSystem.aboutFileSystem();
                break;
            case "Quit":
                fileSystem.quitFile();
                break;   
        }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        treePath = new TreePath(treeModel.getPathToRoot(node));
        
        switch(actionCommand){
            case "Property":
                fileSystem.fileProperty(node,treePath);
                break;
            case "Save":
                fileSystem.saveFile(node,treePath);
                break;
            case "New Folder":
                fileSystem.newFolder(node,treePath);
                break;
            case "New File":
                fileSystem.newFile(node,treePath);
                break;           
            case "Delete":
                fileSystem.deleteFile(node,treePath);
                break;
            case "Format":
                fileSystem.formatFile(rootNode,treePath);
                break;           
        }
        if(event.getSource() == propertyItem){
            fileSystem.fileProperty(node,treePath);
        }else if(event.getSource() == newFileItem){
            fileSystem.newFile(node, treePath);
        }else if(event.getSource() == newFolderItem){
            fileSystem.newFolder(node, treePath);
        }
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent se){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree.getLastSelectedPathComponent();
        Memory memory = fileSystem.getMemory();
        
        try {
            if(!node.getAllowsChildren()){
                newFileButton.setEnabled(false);
                newFolderButton.setEnabled(false);
                editArea.setEditable(true);
            }else{
                newFileButton.setEnabled(true);
                newFolderButton.setEnabled(true);
                editArea.setEditable(false);
            }
            treePath = new TreePath(treeModel.getPathToRoot(node));
            int blockPosition = fileSystem.getBlockPosition(treePath);  
            FileBlock fileBlock = (FileBlock)memory.fileMap.get(blockPosition);
            String fileBlocktext = fileBlock.data.text;
            String treePathToString = treePath.toString();
            showPath(treePathToString);         
            editArea.setText(fileBlocktext);
        } catch (NullPointerException e) {
        }   
        
        
    }

    @Override
    public void mousePressed(MouseEvent event){
        try {
            TreePath path = jTree.getPathForLocation(event.getX(), event.getY());
        jTree.setSelectionPath(path);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (event.getButton() == 3 ) {
            if (node.getAllowsChildren()) {
                popupMenuFolder.show(jTree, event.getX(), event.getY());
            }else{
                popupMenuFile.show(jTree, event.getX(), event.getY());
            }
        }
        } catch (NullPointerException e) {
        }   
    }
    
    @Override
    public void mouseExited(MouseEvent event){
        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }
    
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    
    private void showPath(String path){
        path = path.replace(", ", "\\");
        pathField.setText(path);
    }
    
    static String getScreenText() {
        String text = editArea.getText();
        return text;
    }
    static DefaultTreeModel getTreeModel(){
        return treeModel;
    }
    
    static void insertNode(DefaultMutableTreeNode parent,DefaultMutableTreeNode children){
        treeModel.insertNodeInto(children, parent, parent.getChildCount());
    }
    
    public static void main(String[] args) {
        FileSystemGUI fileSystemGUI = new FileSystemGUI();      
    }
}
