package com.suka.controller;

import com.suka.model.User;
import com.suka.repository.UserRepository;
import com.suka.session.Session;
import com.suka.util.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ManageUsersController {
    @FXML
    public TableColumn usernameColumn;
    @FXML
    public TableColumn idColumn;
    @FXML
    public TableColumn emailColumn;
    @FXML
    public TableColumn roleColumn;

    @FXML
    public TableView userTable;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField searchField;

    @FXML
    private Label loadingLabel;

    private UserRepository userRepository = new UserRepository();

    /**
     * ObservableList = danh sách có cơ chế "quan sát thay đổi".
     *
     * JavaFX TableView rất hợp với kiểu list này:
     * khi dữ liệu bên trong list đổi, UI có thể tự cập nhật lại.
     *
     * Khởi tạo sẵn từ đầu để:
     * 1. tránh users == null
     * 2. FilteredList và SortedList có thể bám vào cùng một list cố định
     */
    private ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    public void initialize(){

        User currentUser = Session.getCurrentUser();

        //route guard:
        if (currentUser == null) {
            Navigator.switchScene("login.fxml");
            return;
        }
        if (!currentUser.getRole().equals("ADMIN")){
            Navigator.switchScene("dashboard.fxml");
            return;
        }

        // mapping column = getter : idColumn -> user.getId(), usernameColumn -> user.getUsername(), ...
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));


        /**
         * Task = công việc chạy nền.
         *
         * Query database là việc tương đối chậm. Nếu chạy trực tiếp trên JavaFX UI thread
         * thì cửa sổ có thể bị khựng hoặc trắng trong lúc chờ dữ liệu.
         *
         * Vì vậy ta đưa phần load user sang Task và chạy ở thread khác.
         */
        loadingLabel.setVisible(true);
        Task<ObservableList<User>> loadUsersTask =new Task<ObservableList<User>>() {
            @Override
            protected ObservableList<User> call() throws Exception {

                Thread.sleep(2000);
                return  FXCollections.observableArrayList(userRepository.getAllUsers());
            }
        };

        loadUsersTask.setOnSucceeded(event ->{
            /**
             * Không gán users = loadUsersTask.getValue()
             *
             * Lý do:
             * FilteredList và SortedList bên dưới đang bám vào object users hiện tại.
             * Nếu đổi sang object list mới, các wrapper đó không còn theo list mới nữa.
             *
             * setAll(...) giữ nguyên object users, chỉ thay nội dung bên trong.
             * Nhờ vậy filter, sort và TableView vẫn còn liên kết đúng.
             */
            users.setAll(loadUsersTask.getValue());
            loadingLabel.setVisible(false);
        });
        loadUsersTask.setOnFailed(event -> {
            loadingLabel.setVisible(false);
            loadUsersTask.getException().printStackTrace();
        });
        Thread thread = new Thread(loadUsersTask);
        thread.setDaemon(true);
        thread.start();

        /**
         * FilteredList = một lớp bọc quanh users để lọc dữ liệu.
         *
         * Nó không copy dữ liệu ra list khác, mà chỉ tạo ra một "view"
         * gồm các phần tử thỏa điều kiện predicate.
         *
         * b -> true nghĩa là mặc định ban đầu: cho hiện tất cả user.
         */
        FilteredList<User> filteredData = new FilteredList<>(users, b-> true);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    filteredData.setPredicate(user -> {

                        // show all if empty
                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }

                        //key word duoc nhap vao
                        String keyword = newValue.toLowerCase();

                        // search username
                        if (user.getUsername().toLowerCase().contains(keyword)) {
                            return true;
                        }

                        // search email
                        if (user.getEmail().toLowerCase().contains(keyword)) {
                            return true;
                        }

                        // search role
                        return user.getRole().toLowerCase().contains(keyword);
                    });
                });

        /**
         * SortedList = một lớp bọc tiếp theo quanh filteredData để sắp xếp dữ liệu.
         *
         * Luồng dữ liệu tổng thể:
         * users -> filteredData -> sortedData -> userTable
         *
         * Nghĩa là:
         * - users giữ dữ liệu gốc
         * - filteredData lọc theo ô search
         * - sortedData sắp xếp phần dữ liệu đã lọc
         * - userTable hiển thị kết quả cuối cùng
         */
        SortedList<User> sortedData = new SortedList<>(filteredData);

        /**
         * bind comparator của sortedData với comparator của TableView.
         *
         * Khi người dùng click vào header cột trong table, JavaFX sẽ tạo/comparator mới
         * tương ứng với cách sort hiện tại (A-Z, Z-A, theo cột nào...).
         *
         * Dòng bind này làm cho sortedData luôn dùng đúng "quy tắc sort"
         * mà user đang chọn trên giao diện.
         */
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());

        /**
         * setItems(...) để TableView biết nguồn dữ liệu cần hiển thị.
         *
         * Ở đây phải set sortedData, không set trực tiếp users:
         * - set users: chỉ có dữ liệu thô, không có filter/sort
         * - set sortedData: vừa search được, vừa click sort cột được
         */
        userTable.setItems(sortedData);
    }

    @FXML
    private void handleAdd() {

        User user = new User(
                usernameField.getText(),
                emailField.getText(),
                roleField.getText(),
                ""
        );

        userRepository.addUser(user);//userRepository se modify database

        refreshTable();
    }

    @FXML
    private void handleUpdate() {
        /**
         * chi update object user
         * Khong chinh sua database (trong user repository se lam )
         */

        User selectedUser = (User) userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            return;
        }

        selectedUser.setUsername(usernameField.getText()
        );

        selectedUser.setEmail(emailField.getText());

        selectedUser.setRole(roleField.getText());

        userRepository.updateUser(selectedUser); //userRepository se modify database

        refreshTable();
    }
    @FXML
    private void handleDelete() {

        User selectedUser = (User) userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            return;
        }

        userRepository.deleteUser(selectedUser.getId());//userRepository se modify database

        refreshTable();
    }
    private void refreshTable() {
        /**
         * Sau khi add/update/delete, nạp lại dữ liệu mới từ database vào chính object users cũ.
         *
         * Vì filteredData, sortedData và userTable đều đang nối với users này,
         * nên UI sẽ cập nhật theo mà không cần setItems(...) lại.
         */
        users.setAll(userRepository.getAllUsers());
    }
}
