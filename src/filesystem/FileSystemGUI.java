/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import javax.swing.*;
import java.awt.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author lenovo
 */
public class FileSystemGUI extends JFrame{
    
    final int WINDOW_SIZE_WIDTH = 500;
    final int WINDOW_SIZE_HEIGHT = 500;
    final int TREE_SIZE_WIDTH = 100;
    final int TREE_SIZE_HEIGHT = 400;

    //Upper Component
    JPanel upperPanel = new JPanel();
    JLabel pathLabel = new JLabel("Path:");
    JTextField pathField = new JTextField("X:\\");
    
    //Center Component
    JPanel middlePanel = new JPanel();   
    JTextArea editArea = new JTextArea();
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode,true);
    TreePath treePath;
    JTree jTree = new JTree(rootNode);
    JScrollPane treeScrollPane = new JScrollPane(jTree);
    JScrollPane editAreaScrollPane = new JScrollPane(editArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    //Lower Component
    JPanel lowerPanel = new JPanel();
    JButton propertyButton = new JButton("Property");
    JButton saveButton = new JButton("Save");
    JButton newFolderButton = new JButton("New Folder");
    JButton newFileButton = new JButton("New File");
    JButton renameButton = new JButton("Rename");
    JButton deleteButton = new JButton("Delete");
    JButton formatButton = new JButton("Format");
    JButton quitButton = new JButton("Quit");
    
    
    
    public FileSystemGUI() {
        super("File System By Lazy");
        drawGUI();
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
        jTree.setEditable(true);
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
    
    public static void main(String[] args) {
        FileSystemGUI fileSystemGUI = new FileSystemGUI();
    }
}
