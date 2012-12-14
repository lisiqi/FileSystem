/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tj.se.java.os.filesystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author lenovo
 */
public class FileSystemGUI extends JFrame implements ActionListener{
    
    final int WINDOW_SIZE_WIDTH = 500;
    final int WINDOW_SIZE_HEIGHT = 500;
    final int TREE_SIZE_WIDTH = 100;
    final int TREE_SIZE_HEIGHT = 400;

    //Upper Component
    static JPanel upperPanel = new JPanel();
    static JLabel pathLabel = new JLabel("Path:");
    static JTextField pathField = new JTextField("X:\\");
    
    //Center Component
    static JPanel middlePanel = new JPanel();   
    static JTextArea editArea = new JTextArea();
    static DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
    static DefaultTreeModel treeModel = new DefaultTreeModel(rootNode,true);
    static TreePath treePath;
    static JTree jTree = new JTree(rootNode);
    static JScrollPane treeScrollPane = new JScrollPane(jTree);
    static JScrollPane editAreaScrollPane = new JScrollPane(editArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    //Lower Component
    static JPanel lowerPanel = new JPanel();
    static JButton propertyButton = new JButton("Property");
    static JButton saveButton = new JButton("Save");
    static JButton newFolderButton = new JButton("New Folder");
    static JButton newFileButton = new JButton("New File");
    static JButton renameButton = new JButton("Rename");
    static JButton deleteButton = new JButton("Delete");
    static JButton formatButton = new JButton("Format");
    static JButton quitButton = new JButton("Quit");
    
    FileSystem fileSystem = new FileSystem();
    
    public FileSystemGUI() {
        super("File System By Lazy");
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
        
        //Partical Setting
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
        //pathTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setPreferredSize(new Dimension(TREE_SIZE_WIDTH, TREE_SIZE_HEIGHT));
        jTree.setEditable(false);
        rootNode.setAllowsChildren(true);
        DefaultMutableTreeNode exampleNode = new DefaultMutableTreeNode("example");
        exampleNode.setAllowsChildren(false);
        rootNode.add(exampleNode);
        jTree.setModel(treeModel);
        jTree.setShowsRootHandles(rootPaneCheckingEnabled);
        //treeScrollPane.getViewport().add(jTree,null);
        middlePanel.add(jTree,BorderLayout.WEST);
        middlePanel.add(editAreaScrollPane,BorderLayout.CENTER);
        editArea.setLineWrap(true);
        editArea.setWrapStyleWord(true);
        editArea.setEditable(true);
        add(middlePanel,BorderLayout.CENTER);
        
        GridLayout gridLayout = new GridLayout(2, 4);
        lowerPanel.setLayout(gridLayout);
        lowerPanel.add(propertyButton);
        lowerPanel.add(saveButton);
        lowerPanel.add(newFolderButton);
        lowerPanel.add(newFileButton);
        lowerPanel.add(renameButton);
        lowerPanel.add(deleteButton);
        lowerPanel.add(formatButton);
        lowerPanel.add(quitButton);
        add(lowerPanel,BorderLayout.SOUTH);
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
        renameButton.addActionListener(this);
        deleteButton.addActionListener(this);
        formatButton.addActionListener(this);
        quitButton.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        String actionCommand = event.getActionCommand();
        switch(actionCommand){
            case "Property":
                fileSystem.fileProperty();
                break;
            case "Save":
                fileSystem.saveFile();
                break;
            case "New Folder":
                fileSystem.newFolder();
                break;
            case "New File":
                fileSystem.newFile();
                break;
            case "Rename":
                fileSystem.renameFile();
                break;
            case "Delete":
                fileSystem.deleteFile();
                break;
            case "Format":
                fileSystem.formatFile();
                break;
            case "Quit":
                fileSystem.quitFile();
                break;
        }
    }
    
    public static void main(String[] args) {
        FileSystemGUI fileSystemGUI = new FileSystemGUI();      
    }
}