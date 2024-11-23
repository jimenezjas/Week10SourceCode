package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.UserSession;

public class SignUpController {

    @FXML
    TextField newUsername, newPassword, newPassConfirm;

    public void createNewAccount(ActionEvent actionEvent) {
        String username = newUsername.getText();
        String password = newPassword.getText();
        String passConfirm = newPassConfirm.getText();

        if(username.isEmpty() || password.isEmpty() || passConfirm.isEmpty()){
            showAlert("Please input information into all fields.");
            return;
        }
        if(!password.equals(passConfirm)){
            showAlert("Passwords do not match.");
            return;
        }

        UserSession newSession = UserSession.getInstance(username, password, "USER");

        showAlert("Account created!");
        goBack(actionEvent);
    }



    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
