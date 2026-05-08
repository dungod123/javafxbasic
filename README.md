🧠 1. Stage = CỬA SỔ (window)

👉 Đây là cái khung app

┌──────────────────────┐
│        Stage         │  ← cửa sổ
│  ┌───────────────┐   │
│  │    Scene      │   │
│  └───────────────┘   │
└──────────────────────┘
🧱 Code:
Stage stage = new Stage();
stage.setTitle("App");
stage.show();

👉 Nhưng thường anh KHÔNG tạo Stage
→ JavaFX tạo sẵn trong MainApp.start(Stage stage)

🎬 2. Scene = NỘI DUNG trong cửa sổ

👉 Stage là cái khung
👉 Scene là cái “màn hình bên trong”

Ví dụ:
Scene 1 = Login screen
Scene 2 = Dashboard
🧱 Code:
Scene scene = new Scene(root);
stage.setScene(scene);

👉 Nghĩa là:

nhét UI vào cửa sổ
🔥 Khi anh switch scene:
stage.setScene(new Scene(root));

👉 giống như:

đổi từ màn login → dashboard
🌳 3. Parent (root) = cây UI
Parent root = loader.load();

👉 root = node gốc của UI (AnchorPane của anh)

AnchorPane (root)
├── TextField
├── Button
└── Label
📦 4. FXMLLoader = người “đọc FXML”

👉 FXML chỉ là file XML (text)

👉 FXMLLoader:

đọc file đó
tạo object Java tương ứng
🧱 Code:
FXMLLoader loader = new FXMLLoader(
getClass().getResource("/dashboard.fxml")
);
Parent root = loader.load();
❓ 5. getResource() là cái gì?

👉 Đây là chỗ anh đang mù mờ nhất

🧠 Hiểu đơn giản:
getClass().getResource("/dashboard.fxml")

👉 = tìm file trong classpath

📦 Classpath là gì?

Trong Maven:

src/main/resources/

👉 tất cả file trong đây sẽ được “đưa vào runtime”

🎯 Ví dụ:

File:

src/main/resources/dashboard.fxml

Thì load bằng:

getClass().getResource("/dashboard.fxml")
❗ Dấu / rất quan trọng
Cách viết	Ý nghĩa
/dashboard.fxml	từ root resources
dashboard.fxml	từ package hiện tại
🔥 6. Ghép tất cả lại (FULL FLOW)
FXMLLoader loader = new FXMLLoader(
getClass().getResource("/dashboard.fxml")
);
// 👉 tìm file

Parent root = loader.load();
// 👉 biến FXML thành UI object

Scene scene = new Scene(root);
// 👉 tạo màn hình

stage.setScene(scene);
// 👉 hiển thị lên cửa sổ
💥 7. Vì sao Navigator giúp?

👉 vì đoạn này lặp lại mọi nơi

❌ Không dùng Navigator:
// LoginController
load dashboard

// DashboardController
load login

// SettingsController
load home
✅ Dùng Navigator:
Navigator.switchScene("dashboard.fxml");

👉 ẩn toàn bộ logic phía trên

🧠 8. Mapping siêu dễ nhớ
JavaFX	Đời thật
Stage	cửa sổ
Scene	màn hình
Parent	layout
FXMLLoader	người dựng UI
getResource	tìm file
⚠️ 9. Lỗi hay gặp (anh chắc chắn sẽ gặp)
❌ Sai path
getResource("/dashboard.fxml")

→ file không nằm trong resources

❌ NullPointerException

→ getResource() trả về null

❌ UI trắng

→ FXML lỗi

🧠 Tóm lại cực ngắn cho anh

👉 Khi anh viết:

Navigator.switchScene("dashboard.fxml");

👉 Thực chất là:

1. tìm file FXML
2. parse thành UI
3. tạo Scene
4. set vào Stage





