package viewmodel;

import dao.DbConnectivityClass;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Person;
import service.MyLogger;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, department, email, imageURL;
    private boolean fnValid, lnValid, deptValid, emailValid;
    @FXML
    ComboBox majorField;
    @FXML
    ImageView img_view;
    @FXML
    Label userMessage;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addBtn;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public synchronized void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addBtn.setDisable(true);
        Pattern emailPattern = Pattern.compile("@farmingdale.edu", Pattern.CASE_INSENSITIVE);
        Pattern fnPattern = Pattern.compile("\s");
        Pattern lnPattern = Pattern.compile("\s");
        Pattern deptPattern = Pattern.compile("\s");
        Pattern majorBusinessPattern = Pattern.compile("business", Pattern.CASE_INSENSITIVE);
        Pattern majorCSCPattern = Pattern.compile("csc", Pattern.CASE_INSENSITIVE);
        Pattern majorCPISPattern = Pattern.compile("cpis", Pattern.CASE_INSENSITIVE);
        Pattern imagePattern = Pattern.compile(""); // add the url base requirements, find later

        first_name.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> validateFn(fnPattern));
        last_name.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> validateLn(lnPattern));
        department.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> validateDept(deptPattern));
        email.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> validateEmail(emailPattern));

        AnimationTimer buttonCheckTimer = new AnimationTimer(){
            @Override
            public void handle(long currently) {
                editButtonCheck();
                deleteButtonCheck();
            }
        };
        buttonCheckTimer.start();
    }
    public synchronized void validateFn(Pattern fnPattern){
        Matcher fnMatcher = fnPattern.matcher(first_name.getText());
        fnValid = fnMatcher.find();
        addButtonCheck();
    }
    public synchronized void validateLn(Pattern lnPattern){
        Matcher lnMatcher = lnPattern.matcher(last_name.getText());
        lnValid = lnMatcher.find();
        addButtonCheck();
    }
    public synchronized void validateDept(Pattern deptPattern){
        Matcher deptMatcher = deptPattern.matcher(department.getText());
        deptValid = deptMatcher.find();
        addButtonCheck();
    }

    public synchronized void validateEmail(Pattern emailPattern){
        Matcher emailMatcher = emailPattern.matcher(email.getText());
        emailValid = emailMatcher.find();
        addButtonCheck();
    }

    public synchronized void editButtonCheck(){
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue != null) {
                editBtn.setDisable(false);
            } else {
                editBtn.setDisable(true);
            }
        });
    }
    public synchronized void deleteButtonCheck(){
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue != null) {
                editBtn.setDisable(false);
            } else {
                editBtn.setDisable(true);
            }
        });
    }
    public synchronized void addButtonCheck(){
        boolean shouldEnable = fnValid && lnValid && deptValid && emailValid;
        addBtn.setDisable(!shouldEnable);

        userMessage.setText("Information added!");
        PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
        messageTransition.setOnFinished(event -> {
            userMessage.setText("Welcome to the Database!");
        });
        messageTransition.play();
    }

    @FXML
    protected synchronized void addNewRecord() {
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    (String) majorField.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
    }

    @FXML
    protected synchronized void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        majorField.setValue(null);
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected synchronized void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected synchronized void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected synchronized void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected synchronized void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                (String) majorField.getValue(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);

        userMessage.setText("Updated information!");
        PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
        messageTransition.setOnFinished(event -> {
            userMessage.setText("Welcome to the Database!");
        });
        messageTransition.play();
    }

    @FXML
    protected synchronized void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);

        userMessage.setText("Deleted information!");
        PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
        messageTransition.setOnFinished(event -> {
            userMessage.setText("Welcome to the Database!");
        });
        messageTransition.play();
    }

    @FXML
    protected synchronized void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected synchronized void addRecord() {
        showSomeone();
    }

    @FXML
    protected synchronized void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        majorField.setValue(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public synchronized void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

    public synchronized void exportToCSV(){
        FileChooser fileExporter = new FileChooser();
        //restricts the file type to just csv
        fileExporter.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        //prompts user with the file explorer window to select a valid csv file
        File exportedFile = fileExporter.showSaveDialog(menuBar.getScene().getWindow());

        //if the file is found successfully
        if(exportedFile != null){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportedFile))) {
                //sets up categories
                writer.write("ID, First Name, Last Name, Department, Major, Email, Image URL");
                writer.newLine();
                for (Person person : data){
                    //takes all variables from each person in the TableView and puts it one at a time as a line into the file
                    writer.write(person.getId() + "," +
                            person.getFirstName() + "," +
                            person.getLastName() + "," +
                            person.getDepartment() + "," +
                            person.getMajor() + "," +
                            person.getEmail() + "," +
                            person.getImageURL());
                    writer.newLine();
                }
                //status report
                userMessage.setText("Data exported!");
                PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
                messageTransition.setOnFinished(event -> {
                    userMessage.setText("Welcome to the Database!");
                });
                messageTransition.play();
            } catch(IOException e){
                //if they fail to select a file / the file they chose wasn't a CSV file
                e.printStackTrace();
                userMessage.setText("Error exporting data...");
                PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
                messageTransition.setOnFinished(event -> {
                    userMessage.setText("Welcome to the Database!");
                });
            }
        }
    }

    public synchronized void importCSVFile(){
        FileChooser fileImporter = new FileChooser();
        fileImporter.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File importedFile = fileImporter.showOpenDialog(menuBar.getScene().getWindow());
        if(importedFile != null){
            try(BufferedReader reader = new BufferedReader(new FileReader(importedFile))) {
                String line;
                ObservableList<Person> importedData = FXCollections.observableArrayList();
                reader.readLine();
                //reads each line of the file, every 6 segments that are separated by comma are then put into an array -> Person
                while((line = reader.readLine()) != null){
                    String[] values = line.split(",");
                    if(values.length == 6){
                        Person importedPerson = new Person(values[0], values[1], values[2], values[3], values[4], values[5]);
                        importedData.add(importedPerson);
                    }
                }
                //clears and inputs all the imported data
                data.clear();
                data.addAll(importedData);

                userMessage.setText("Data imported!");
                PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
                messageTransition.setOnFinished(event -> {
                    userMessage.setText("Welcome to the Database!");
                });
            } catch(IOException e){
                e.printStackTrace();
                userMessage.setText("Data failed to import...");
                PauseTransition messageTransition = new PauseTransition(Duration.seconds(5));
                messageTransition.setOnFinished(event -> {
                    userMessage.setText("Welcome to the Database!");
                });
            }
        }
    }

}