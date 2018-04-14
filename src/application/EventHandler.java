package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

public class EventHandler
{

    @FXML
    private ResourceBundle resources;

    @FXML
    private Button emptyPreviewB;

    @FXML
    private URL location;

    @FXML
    private ListView<String> CurrentFilesList;

    @FXML
    private ListView<String> PreviewFilesList;

    @FXML
    private Font x1;

    @FXML
    private Font      x2;
    @FXML
    private TextField begDelete;

    @FXML
    private TextField endDelete;

    @FXML
    private TextField kTextField;

    @FXML
    private AnchorPane frame;

    @FXML
    private Button getPreviewButton;

    @FXML
    private Button renameFilesButton;

    @FXML
    private Button setDirectoryButton;

    @FXML
    private Button setDirectoriesButton;

    @FXML
    private CheckBox addFileNumbers;

    File	 directory;
    List<File>	 files;
    List<String> newFilenames;

    public void disableButtons(Boolean isDisabled)
    {
	getPreviewButton.setDisable(isDisabled);
	addFileNumbers.setDisable(isDisabled);
	emptyPreviewB.setDisable(isDisabled);
    }

    public List<File> getFilesInDirectory(File dir)
    {
	File[] tfiles = dir.listFiles();
	List<File> file = new ArrayList<File>();

	for (File f : tfiles)
	{
	    if (f.isFile())
	    {
		file.add(f);
	    }
	}
	return file;
    }

    public File chooseDirectory(File dir, String title)
    {
	DirectoryChooser dc = new DirectoryChooser();
	dc.setTitle(title);
	File temp;

	if (dir == null)
	{
	    temp = chooseDirectory(dc);
	}
	else
	{
	    dc.setInitialDirectory(new File(dir.getParent()));
	    temp = dc.showDialog(null);
	}
	return temp;
    }

    public static File chooseDirectory(DirectoryChooser dc)
    {
	File file;
	try
	{
	    dc.setInitialDirectory(new File(System.getProperty("java.class.path")).getParentFile());
	    file = dc.showDialog(null);
	}
	catch (Exception e)
	{
	    dc.setInitialDirectory(new File("."));
	    file = dc.showDialog(null);
	}
	return file;
    }

    @FXML
    void setDirectory(MouseEvent event)
    {
	directory = this.chooseDirectory(directory, "Choose directory to edit");
	CurrentFilesList.getItems().clear();
	if (directory == null) return;
	files = this.getFilesInDirectory(directory);

	for (File f : files)
	{
	    CurrentFilesList.getItems().add(f.getName());
	}
	CurrentFilesList.getItems().sort(null);
	this.disableButtons(false);
	renameFilesButton.setDisable(true);
    }

    @FXML
    void makePreviewList(MouseEvent event)
    {
	List<String> previewFilenames = this.createModifiedFilenameList();
	PreviewFilesList.getItems().clear();
	PreviewFilesList.getItems().addAll(previewFilenames);

	newFilenames = PreviewFilesList.getItems();
	renameFilesButton.setDisable(false);
    }

    @FXML
    void makeEmptyPreviewList()
    {
	PreviewFilesList.getItems().clear();
	int fileNumber = Integer.parseInt(kTextField.getText());

	List<String> modifiedFilenames = new ArrayList<String>();

	files.sort(null);

	for (int i = 0; i < files.size(); i++)
	{
	    StringBuilder filename = new StringBuilder(files.get(i).getName());
	    int lastIndex = this.calculateLastIndex(filename);

	    for (int k = 0; k < lastIndex + 1; k++)
	    {
		filename.deleteCharAt(0);
	    }

	    this.addFileNumbers(filename, fileNumber);
	    fileNumber++;

	    modifiedFilenames.add(filename.toString());
	}
	PreviewFilesList.getItems().addAll(modifiedFilenames);

	newFilenames = PreviewFilesList.getItems();
	renameFilesButton.setDisable(false);

    }

    @FXML
    void renameFiles(MouseEvent event)
    {
	for (int i = 0; i < files.size(); i++)
	{
	    File newFile = new File(directory.getAbsolutePath() + "/" + newFilenames.get(i));
	    files.get(i).renameTo(newFile);
	}

	CurrentFilesList.getItems().clear();
	PreviewFilesList.getItems().clear();

	files = this.getFilesInDirectory(directory);
	List<String> tempFilenames = new ArrayList<String>();
	for (File f : files)
	{
	    tempFilenames.add(f.getName());
	}
	tempFilenames.sort(null);
	CurrentFilesList.getItems().addAll(tempFilenames);
    }

    public int calculateLastIndex(StringBuilder filename)
    {
	for (int i = filename.length() - 1; i > 0; i--) // loop calculates index
							// before fileExtentsion
	{
	    if (filename.charAt(i) == '.')
	    {
		int lastIndex = i - 1;
		return lastIndex;
	    }
	}

	return -1;
    }

    public int calculateLastIndex(String filename)
    {
	for (int i = filename.length() - 1; i > 0; i--) // loop calculates index
							// before fileExtentsion
	{
	    if (filename.charAt(i) == '.')
	    {
		int lastIndex = i - 1;
		return lastIndex;
	    }
	}

	return -1;
    }

    @SuppressWarnings("static-access")
    public StringBuilder deleteCharsFromFilename(int lastIndex, StringBuilder filename)
    {
	int length = lastIndex + 1;
	int begCharsToDelete = Integer.parseInt(begDelete.getText());
	int endCharsToDelete = Integer.parseInt(endDelete.getText());

	if (begCharsToDelete + endCharsToDelete >= length && !addFileNumbers.isSelected()
		|| addFileNumbers.isSelected() && begCharsToDelete + endCharsToDelete > length)
	{

	    JOptionPane error = new JOptionPane();
	    error.showMessageDialog(null, "Error: one or more filenames have been completely removed." + "\n" + "Try Again", "Error",
		    JOptionPane.ERROR_MESSAGE);
	    PreviewFilesList.getItems().clear();
	    return null;
	}

	for (int i = 0; i < begCharsToDelete; i++) // delete beginning chars
	{
	    filename.deleteCharAt(0);
	    lastIndex--;
	}

	for (int i = 0; i < endCharsToDelete; i++) // delete ending chars
	{
	    filename.deleteCharAt(lastIndex);
	    lastIndex--;
	}

	return filename;
    }

    public void addFileNumbers(StringBuilder filename, int fileNumber)
    {
	filename.insert(0, Integer.toString(fileNumber) + " - ");

	if (fileNumber < 10)
	{
	    filename.insert(0, 0);
	}

	if (files.size() >= 100 && fileNumber < 100)
	{
	    filename.insert(0, '0');
	}

    }

    public List<String> createModifiedFilenameList()
    {
	PreviewFilesList.getItems().clear();
	int fileNumber = Integer.parseInt(kTextField.getText());

	List<String> modifiedFilenames = new ArrayList<String>();

	files.sort(null);

	for (int i = 0; i < files.size(); i++)
	{
	    StringBuilder filename = new StringBuilder(files.get(i).getName());
	    int lastIndex = this.calculateLastIndex(filename);
	    filename = this.deleteCharsFromFilename(lastIndex, filename);

	    if (filename == null) return null;

	    if (addFileNumbers.isSelected()) this.addFileNumbers(filename, fileNumber);
	    fileNumber++;

	    modifiedFilenames.add(filename.toString());
	}

	return modifiedFilenames;
    }

    @SuppressWarnings("static-access")
    @FXML
    void setDirectories(MouseEvent event)
    {
	File cpyFromDirectory = null;
	cpyFromDirectory = this.chooseDirectory(cpyFromDirectory, "Choose directory to copy filenames from");
	if (cpyFromDirectory == null) return;

	directory = this.chooseDirectory(directory, "Choose directory to copy filnames to");
	if (directory == null) return;

	CurrentFilesList.getItems().clear();
	PreviewFilesList.getItems().clear();
	newFilenames = new ArrayList<String>();

	files = this.getFilesInDirectory(directory);
	// List<File> files2 = this.getFilesInDirectory(cpyFromDirectory);
	List<File> files2 = new ArrayList<File>();

	for (int i = 0; i < cpyFromDirectory.listFiles().length; i++)
	{
	    File f = cpyFromDirectory.listFiles()[i];
	    File cpyFrom = directory.listFiles()[i];
	    String fileType = cpyFrom.getName().substring(calculateLastIndex(cpyFrom.getName()) + 1);
	    System.out.println("FileType is: " + fileType + "\n");
	    files2.add(new File(f.getName().substring(0, calculateLastIndex(f.getName()) + 1) + fileType));
	}

	if (files.size() != files2.size())
	{
	    JOptionPane error = new JOptionPane();
	    error.showMessageDialog(null, "The number in files in both directories is not equal, try again", "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	for (File f : files)
	{
	    CurrentFilesList.getItems().add(f.getName());
	}

	for (File f : files2)
	{
	    PreviewFilesList.getItems().add(f.getName());
	    newFilenames.add(f.getName());
	}
	this.disableButtons(true);
	renameFilesButton.setDisable(false);
    }

    @FXML
    void initialize()
    {
	assert CurrentFilesList != null : "fx:id=\"CurrentFilesList\" was not injected: check your FXML file 'Sample.fxml'.";
	assert setDirectoryButton != null : "fx:id=\"setDirectoryButton\" was not injected: check your FXML file 'Sample.fxml'.";
	assert kTextField != null : "fx:id=\"kTextField\" was not injected: check your FXML file 'Sample.fxml'.";
	assert setDirectoriesButton != null : "fx:id=\"setDirectoriesButton\" was not injected: check your FXML file 'Sample.fxml'.";
	assert getPreviewButton != null : "fx:id=\"getPreviewButton\" was not injected: check your FXML file 'Sample.fxml'.";
	assert renameFilesButton != null : "fx:id=\"renameFilesButton\" was not injected: check your FXML file 'Sample.fxml'.";
	assert addFileNumbers != null : "fx:id=\"addFileNumbers\" was not injected: check your FXML file 'Sample.fxml'.";
	assert x1 != null : "fx:id=\"x1\" was not injected: check your FXML file 'Sample.fxml'.";
	assert begDelete != null : "fx:id=\"begDelete\" was not injected: check your FXML file 'Sample.fxml'.";
	assert x2 != null : "fx:id=\"x2\" was not injected: check your FXML file 'Sample.fxml'.";
	assert PreviewFilesList != null : "fx:id=\"PreviewFilesList\" was not injected: check your FXML file 'Sample.fxml'.";
	assert endDelete != null : "fx:id=\"endDelete\" was not injected: check your FXML file 'Sample.fxml'.";
	assert frame != null : "fx:id=\"frame\" was not injected: check your FXML file 'Sample.fxml'.";
	renameFilesButton.setDisable(true);
	this.disableButtons(true);
    }

}
