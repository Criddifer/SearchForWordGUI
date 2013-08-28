//import external libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
//import specific libraries where conflicts occur
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This program opens a simple window, waits for a Text Document to be selected from the user's computer.<br/>
 * Stores the contents in an ArrayList and then provides a search functionality for different words.<br/>
 * Prints line in which word appears along with line number to areaOutput. After searching, shows 'statistics'.<br/>
 * Allows for the user to copy the output in the areaOutput to their clipboard.
 *
 * @author Matt Christopher
 * A slightly edited copy of this work can be found on https://www.github.com/Criddifer/SearchWordGUI
 * @version 1.0
 */
public class SearchWordGUI extends JFrame {

    //Create global variables
    private ArrayList<String> lineArray = new ArrayList<String>();
    private JLabel labelCopy;
    private JTextField textFile, textWord;
    private JTextArea areaOutput;
    private JButton buttonRun, buttonCopy, buttonSave;
    private JCheckBox chkIgnoreCase, chkWholeWord;
    private JPanel panel;
    private JFrame frame;
    private JFileChooser fileChooser = new JFileChooser(".");
    private int lineCount, wordMatchCount;
    /**
     * Constructor for program, runs methods to create window
     */
    public SearchWordGUI(){
        createGUI();
        addContent();

        frame.add(panel);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Creates the window frame and defines certain attributes
     */
    //PASS IN VARIABLES
    public void createGUI(){
        frame = new JFrame();
        frame.setTitle("Search File for Word");
        frame.setSize(575,460);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
    }

    /**
     * Clears stored values
     */
    public void resetValues(){
        wordMatchCount = 0;
        lineCount = 0;
        lineArray.clear();
        areaOutput.setText("");
        textWord.setText("");
        textFile.setText("");
        buttonRun.setEnabled(false);
        buttonCopy.setEnabled(false);
    }

    /**
     * Creates a JLabel based on what's passed into it
     * @param text text to display
     * @param x label X location
     * @param y label Y location
     * @param w label Width dimension
     * @param h label Height dimension
     * @param visible visible by default?
     * @return JLabel with defined attributes
     */
    public JLabel configureJLabel(String text, int x, int y, int w, int h, boolean visible){
        JLabel label = new JLabel(text);
        label.setBounds(x,y,w,h);
        label.setVisible(visible);
        panel.add(label);
        return label;
    }

    /**
     * Creates JTextField based on what's passed into it
     * @param x textField X location
     * @param y textField Y location
     * @param w textField Width dimension
     * @param h textField Height dimension
     * @param editable editable by default?
     * @return JTextField with defined attributes
     */
    public JTextField configureJTextField(int x, int y, int w, int h, boolean editable){
        JTextField textField = new JTextField();
        textField.setBounds(x,y,w,h);
        textField.setEditable(editable);
        panel.add(textField);
        return textField;
    }

    /**
     * Creates JButton based on what's passed into it
     * @param text button text to display
     * @param actionListener required action listener
     * @param x button X location
     * @param y button Y location
     * @param w button Width attribute
     * @param h button Height attribute
     * @param enabled enabled by default?
     * @return JButton with defined attributes
     */
    public JButton configureJButton(String text, ActionListener actionListener, int x, int y, int w, int h, boolean enabled){
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBounds(x,y,w,h);
        button.setEnabled(enabled);
        panel.add(button);
        return button;
    }

    public JCheckBox configureJCheckBox(int x, int y, int w, int h){
        JCheckBox checkBox = new JCheckBox();
        checkBox.setBounds(x,y,w,h);
        panel.add(checkBox);
        return checkBox;
    }

    /**
     * Populate window with content
     */
    public void addContent(){
        panel = new JPanel();
        panel.setLayout(null);

        configureJLabel("File open:",10,10,150,20,true);
        configureJLabel("Word to Search:",10,35,150,20,true);
        configureJLabel("Ignore Case",30,55,150,20,true);
        configureJLabel("Word Contains",30,72,150,20,true);
        labelCopy = configureJLabel("Results copied to clipboard",250,400,175,20,false);

        textFile = configureJTextField(150,10,300,20,false);
        textWord = configureJTextField(150,35,300,20,true);

        configureJButton("Browse",new OpenFile(),460,10,100,20,true);
        configureJButton("Quit",new QuitApplication(),460,60,100,20,true);
        buttonRun = configureJButton("Run Search",new ExecuteSearch(),460,35,100,20,false);
        buttonCopy = configureJButton("Copy to clipboard",new CopyContent(),410,400,150,20,false);
        buttonSave = configureJButton("Save to file", new SaveOutput(),10,400,100,20,false);

        chkIgnoreCase = configureJCheckBox(10,55,20,20);
        chkWholeWord = configureJCheckBox(10,72,20,20);

        frame.getRootPane().setDefaultButton(buttonRun);

        areaOutput = new JTextArea();
        areaOutput.setLineWrap(true);
        areaOutput.setWrapStyleWord(true);
        areaOutput.setEditable(false);
        areaOutput.setBackground(Color.DARK_GRAY);
        areaOutput.setForeground(Color.GREEN);

        JScrollPane scrollPane = new JScrollPane(areaOutput);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10,95,550,300);
        panel.add(scrollPane);
    }

    class OpenFile implements ActionListener {
        public void actionPerformed(ActionEvent e){
            resetValues();

            fileChooser.setAcceptAllFileFilterUsed(false);
            FileFilter fileFilter = new FileNameExtensionFilter("Text Document (.txt)", "txt");
            fileChooser.addChoosableFileFilter(fileFilter);
            fileChooser.setFileFilter(fileFilter);

            int openDialog = fileChooser.showOpenDialog(SearchWordGUI.this);
            if (openDialog == JFileChooser.APPROVE_OPTION){
                textFile.setText(fileChooser.getCurrentDirectory().toString() + "\\" + fileChooser.getSelectedFile().getName());
                buttonRun.setEnabled(true);
                try{
                    Scanner scan = new Scanner(new File(textFile.getText()));
                    while(scan.hasNextLine()){
                        lineArray.add(scan.nextLine());
                        lineCount++;
                    }
                    scan.close();
                    if(lineCount == 0){
                        JOptionPane.showMessageDialog(frame,"Selected file contains no lines.","File read error",JOptionPane.ERROR_MESSAGE);
                        resetValues();
                    }
                } catch (FileNotFoundException e1){
                    System.err.print("You should never see this error.");
                    System.exit(2);
                }
            }
        }
    }

    class ExecuteSearch implements ActionListener {
        public void actionPerformed(ActionEvent e){
            wordMatchCount = 0;
            areaOutput.setText("");
            labelCopy.setVisible(false);
            if(textWord.getText().equals("")){
                JOptionPane.showMessageDialog(frame,"Please enter a search query.","Invalid search parameters",JOptionPane.ERROR_MESSAGE);
                buttonCopy.setEnabled(false);
                buttonSave.setEnabled(false);
            } else {
                for(String line:lineArray){
                    String words[] = line.split("[\\W]+");
                    for(String word:words){
                        if(chkIgnoreCase.isSelected()){
                            if(chkWholeWord.isSelected()){
                                if(word.contains(textWord.getText())){
                                    areaOutput.append("Line " + (lineArray.indexOf(line)+1) + ": " + line + "\n");
                                    wordMatchCount++;
                                }
                            } else {
                                if(word.equalsIgnoreCase(textWord.getText())){
                                    areaOutput.append("Line " + (lineArray.indexOf(line)+1) + ": " + line + "\n");
                                    wordMatchCount++;
                                }
                            }
                        } else {
                            if(chkWholeWord.isSelected()){
                                if(word.contains(textWord.getText())){
                                    areaOutput.append("Line " + (lineArray.indexOf(line)+1) + ": " + line + "\n");
                                    wordMatchCount++;
                                }
                            } else {
                                if(word.equals(textWord.getText())){
                                    areaOutput.append("Line " + (lineArray.indexOf(line)+1) + ": " + line + "\n");
                                    wordMatchCount++;
                                }
                            }
                        }
                    }
                }
                buttonCopy.setEnabled(true);
                buttonSave.setEnabled(true);
                if(wordMatchCount == 0){
                    JOptionPane.showMessageDialog(frame,"\"" + textWord.getText() + "\" was not found in file.","Word not found",JOptionPane.INFORMATION_MESSAGE);
                } else {
                    areaOutput.append("\nResults: " + wordMatchCount + " instances of \"" + textWord.getText() + "\" in " + lineCount + " lines.");
                }
            }
        }
    }


    class QuitApplication implements ActionListener{
        public void actionPerformed(ActionEvent e){
            int exitDialog = JOptionPane.showConfirmDialog(frame,"Are you sure you want to exit?","Exit",JOptionPane.YES_NO_OPTION);
            if(exitDialog == JOptionPane.YES_OPTION){
                lineArray.clear();
                System.exit(1);
            }
        }
    }

    class SaveOutput implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try{
                String output = areaOutput.getText();
                String[] splitString = output.split("\\n");
                FileWriter fileWriter;

                int saveDialog = fileChooser.showSaveDialog(SearchWordGUI.this);
                if(saveDialog == JFileChooser.APPROVE_OPTION){
                    if(fileChooser.getSelectedFile().exists()){
                        int saveWarningDialog = JOptionPane.showConfirmDialog(frame,"File \"" + fileChooser.getSelectedFile().getName() + "\" already exists, overwrite?","File exists",JOptionPane.YES_NO_OPTION);
                        if(saveWarningDialog == JOptionPane.YES_OPTION){

                            File file = fileChooser.getSelectedFile();
                            fileWriter = new FileWriter(file);
                            for(String string:splitString){
                                fileWriter.write(string + "\r\n");
                            }
                            fileWriter.close();
                        }
                    }
                }

                if(!fileChooser.getSelectedFile().exists()){
                    System.out.print("File not found, attempting to save");
                    File file = fileChooser.getSelectedFile();
                    fileWriter = new FileWriter(file);
                    for(String string:splitString){
                        fileWriter.write(string + "\r\n");
                    }
                    fileWriter.close();
                    System.out.print("Save complete");
                }

            } catch(IOException e1){
                JOptionPane.showMessageDialog(frame,"File does not exist","File not found",JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    class CopyContent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            areaOutput.selectAll();
            areaOutput.copy();
            labelCopy.setVisible(true);
            buttonCopy.setEnabled(false);
        }
    }

    public static void main(String[] args){
        new SearchWordGUI();
    }
}