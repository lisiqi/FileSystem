/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filesystem;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author lenovo
 */
public class FileSystemGUI extends JFrame{

    //Upper Component
    JPanel upperPanel = new JPanel();
    JLabel pathLabel = new JLabel("Path:");
    JTextField pathField = new JTextField("X:\\");
    
    //Center Component
    JPanel middlePanel = new JPanel();
    JTree pathTree = new JTree();
    JTextArea editArea = new JTextArea();
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
        setSize(500,500);
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
        middlePanel.add(pathTree,BorderLayout.EAST);
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
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
        // ignore error
        }
    }
    
    public static void main(String[] args) {
        FileSystemGUI fileSystemGUI = new FileSystemGUI();
    }
}
