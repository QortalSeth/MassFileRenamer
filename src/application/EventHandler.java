package application;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

public class EventHandler
{

    @FXML
    private ResourceBundle resources;

	@FXML
	private TextField customFileTypes;

	@FXML
	private ComboBox<String> fileTypesC;

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

	@FXML
	private CheckBox replaceDelimitersWithSpaces;

	@FXML
	private Button mergeDirectoriesButton;

	private boolean disableFilter = false;
    File	directory;



	List<String> unfilteredCurrentFiles = new ArrayList<>();
	ObservableList<String> filteredCurrentFiles = FXCollections.observableArrayList();
	ObservableList<String> filteredPreviewFiles = FXCollections.observableArrayList();
	Map<String,List<String>> fileTypes = new LinkedHashMap<>();

    public void disableButtons(Boolean isDisabled)
    {
	getPreviewButton.setDisable(isDisabled);
	addFileNumbers.setDisable(isDisabled);
	replaceDelimitersWithSpaces.setDisable(isDisabled);
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
	filteredCurrentFiles.clear();
	unfilteredCurrentFiles.clear();
	filteredCurrentFiles.clear();
	if (directory == null) return;
	List<File> files = this.getFilesInDirectory(directory);

	for (File f : files)
	{
	    filteredCurrentFiles.add(f.getName());
		unfilteredCurrentFiles.add(f.getName());
	}
	filteredCurrentFiles.sort(null);
	this.disableButtons(false);
	renameFilesButton.setDisable(true);
	filteredPreviewFiles.clear();

	disableFilter = false;
	filterFiles();
    }

	@FXML
	void mergeDirectories(MouseEvent event)
	{
		/*
		directory = this.chooseDirectory(directory, "Choose directory to edit");
		filteredCurrentFiles.clear();
		unfilteredCurrentFiles.clear();
		filteredCurrentFiles.clear();

		FileFilter directoryFilter = new FileFilter(){
			public boolean accept(File dir) {
				if (dir.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}
		};

				File[] subDirectories = directory.listFiles(directoryFilter);
		for(File dir : subDirectories){
			List<File> files = this.getFilesInDirectory(dir);
			for(File f: files)
			unfilteredCurrentFiles.add(f.getName());
		}

		 */
	}

    @FXML
    void makePreviewList()
    {
	List<String> previewFilenames = this.createModifiedFilenameList();
	filteredPreviewFiles.clear();
	filteredPreviewFiles.addAll(previewFilenames);

	renameFilesButton.setDisable(false);
    }

    @FXML
    void makeEmptyPreviewList()
    {
	filteredPreviewFiles.clear();
	int fileNumber = Integer.parseInt(kTextField.getText());

	List<String> modifiedFilenames = new ArrayList<String>();

	for (int i = 0; i < filteredCurrentFiles.size(); i++)
	{
	    StringBuilder filename = new StringBuilder(filteredCurrentFiles.get(i));
	    int lastIndex = this.calculateLastIndex(filename);

	    for (int k = 0; k < lastIndex + 1; k++)
	    {
		filename.deleteCharAt(0);
	    }

	    this.addFileNumbers(filename, fileNumber);
	    fileNumber++;

	    modifiedFilenames.add(filename.toString());
	}
	filteredPreviewFiles.addAll(modifiedFilenames);


	renameFilesButton.setDisable(false);
    }

	String getFileExtension(String file){
		int fileExtensionStart = file.lastIndexOf(".")+1;
		return file.substring(fileExtensionStart, file.length()).toLowerCase();
	}

	boolean checkFilter(String file)
	{
		String fileExtension = getFileExtension(file);
		String selectedFileText = fileTypesC.getSelectionModel().getSelectedItem();
		List<String> selectedFileTypes = fileTypes.get(selectedFileText);
		if(selectedFileText == "Any") return true;
		else if (selectedFileText == "Custom")
		{
			String extensionsString = customFileTypes.getText().trim().replaceAll("\\.","");
			System.out.println("Custom Extensions Text: " + extensionsString);
			String[] splitExtensions = extensionsString.split(" ");
			System.out.println("Custom Extensions Array: " + Arrays.toString(splitExtensions));
			List<String> extensionsList = new ArrayList<>(List.of(splitExtensions));
			return extensionsList.contains(fileExtension);
		}


		return selectedFileTypes.contains(fileExtension);
	}

	void filterList(List<String> unfiltered, List<String> filtered)
	{
		filtered.clear();
		unfiltered.forEach((k) ->
				{
					//System.out.println("Selected File Types: "+ selectedFileTypes);
					if (checkFilter(k)) {
						filtered.add(k);
					}
				}
		);
	}

	void filterList(ArrayList<File> unfiltered, ArrayList<File> filtered)
	{
		filtered.clear();
		unfiltered.forEach((k) ->
				{
					//System.out.println("Selected File Types: "+ selectedFileTypes);
					if (checkFilter(k.getName())) {
						filtered.add(k);
					}
				}
		);
	}
	@FXML
	void filterFiles()
	{
		if(!disableFilter) {
			filterList(unfilteredCurrentFiles, filteredCurrentFiles);
			makePreviewList();

			System.out.println("Filtered Files Count");
			System.out.println("Current: " + filteredCurrentFiles.size() + "     Preview: " + filteredPreviewFiles.size());
			sortFiles();
		}
	}

	void sortFiles(){
		unfilteredCurrentFiles.sort(null);
		filteredCurrentFiles.sort(null);
		filteredPreviewFiles.sort(null);
	}

	@FXML
	void checkFilterOnType(){
		if (fileTypesC.getSelectionModel().getSelectedItem() == "Custom"){
			filterFiles();
		}
	}

    @FXML
    void renameFiles(MouseEvent event)
    {
		if(filteredCurrentFiles.size() != filteredPreviewFiles.size()) {
			JOptionPane error = new JOptionPane();
			error.showMessageDialog(null, "The number in files in both directories is not equal, try again", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

	for (int i = 0; i < filteredCurrentFiles.size(); i++)
	{
		File oldFile = new File(directory.getAbsolutePath() + "/" + filteredCurrentFiles.get(i));
	    File newFile = new File(directory.getAbsolutePath() + "/" + filteredPreviewFiles.get(i));

	oldFile.renameTo(newFile);
	}

	filteredCurrentFiles.clear();
	filteredCurrentFiles.addAll(filteredPreviewFiles);


	/*
	files = this.getFilesInDirectory(directory);
	List<String> tempFilenames = new ArrayList<String>();
	for (File f : files)
	{
	    tempFilenames.add(f.getName());
	}
	tempFilenames.sort(null);
	filteredCurrentFiles.addAll(tempFilenames);

	 */
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
	    filteredPreviewFiles.clear();
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

	if (filteredCurrentFiles.size() >= 100 && fileNumber < 100)
	{
	    filename.insert(0, '0');
	}

    }

    public List<String> createModifiedFilenameList()
    {
	filteredPreviewFiles.clear();
	int fileNumber = Integer.parseInt(kTextField.getText());

	List<String> modifiedFilenames = new ArrayList<String>();

	filteredCurrentFiles.sort(null);

	for (int i = 0; i < filteredCurrentFiles.size(); i++)
	{
	    StringBuilder filename = new StringBuilder(filteredCurrentFiles.get(i));
	    int lastIndex = this.calculateLastIndex(filename);
	    filename = this.deleteCharsFromFilename(lastIndex, filename);

	    if (filename == null) return null;

	    if (addFileNumbers.isSelected()) this.addFileNumbers(filename, fileNumber);
	    fileNumber++;


	    int fileExtensionStart = filename.lastIndexOf(".")+1;

	    if(replaceDelimitersWithSpaces.isSelected())
		{
			replaceAll(filename, Pattern.compile("[._]"), " ");
			filename.replace(fileExtensionStart-1,fileExtensionStart,".");

		}

	    String fileExtension = filename.substring(fileExtensionStart, filename.length()).toLowerCase();
	    filename.replace(fileExtensionStart, filename.length(), fileExtension );


	    modifiedFilenames.add(filename.toString().trim());
	}

	return modifiedFilenames;
    }

	public static void replaceAll(StringBuilder sb, Pattern pattern, String replacement) {
		Matcher m = pattern.matcher(sb);
		while(m.find()) {
			sb.replace(m.start(), m.end(), replacement);
		}
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

	filteredCurrentFiles.clear();
	filteredPreviewFiles.clear();

	ArrayList<File> unFilteredFiles = (ArrayList<File>) this.getFilesInDirectory(directory);

	unfilteredCurrentFiles.clear();
	for(File f: unFilteredFiles){unfilteredCurrentFiles.add(f.getName());}
	// List<File> files2 = this.getFilesInDirectory(cpyFromDirectory);
	ArrayList<File> unFilteredFiles2 = (ArrayList<File>) this.getFilesInDirectory(cpyFromDirectory);
	ArrayList<File> files = new ArrayList<File>();
			filterList(unFilteredFiles, files);
		ArrayList<File> files2 = new ArrayList<File>();
		filterList(unFilteredFiles2, files2);

	try {
		for (int i = 0; i < files.size(); i++) {
			File f = files.get(i);
			File cpyFrom = files2.get(i);
			String fileType = cpyFrom.getName().substring(calculateLastIndex(cpyFrom.getName()) + 1);
			System.out.println("FileType is: " + fileType + "\n");
			// files2.add(new File(f.getName().substring(0, calculateLastIndex(f.getName()) + 1) + fileType));
		}
	}
	catch(ArrayIndexOutOfBoundsException e)
	{
	    JOptionPane error = new JOptionPane();
	    error.showMessageDialog(null, "The number in files in both directories is not equal, try again", "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	for (File f : files)
	{
	    filteredCurrentFiles.add(f.getName());
	}

	for (File f : files2)
	{
	    filteredPreviewFiles.add(f.getName());
	}
	this.disableButtons(true);
	renameFilesButton.setDisable(false);
	disableFilter = true;
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
	assert fileTypesC != null;

	fileTypes.put("Music", List.of("mp3","flac","ogg", "pcm", "wav", "aac", "wma", "m4a"));
	fileTypes.put("Video", List.of("mkv", "webm", "flv","avi", "wmv", "mp4", "m4v", "av1"));
	fileTypes.put("Any", List.of());
	fileTypes.put("Custom", new ArrayList<>());

	fileTypesC.getItems().addAll(fileTypes.keySet());
	fileTypesC.getSelectionModel().select(0);

CurrentFilesList.setItems(filteredCurrentFiles);
PreviewFilesList.setItems(filteredPreviewFiles);
mergeDirectoriesButton.setDisable(true);
		
	assert customFileTypes != null;


    }

}
