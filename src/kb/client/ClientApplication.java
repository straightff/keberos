package kb.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import kb.constant.KbConstants;
import kb.ToolClass.MD5Tool;

import javax.swing.*;

/**
 * @author han
 */
public class ClientApplication extends Application {
    private Socket socket;
    private ArrayList<Thread> cThreads;
    private ObservableList<String> loglist;// 存储服务器收到的信息

    private SimpleDateFormat sdf;
    private final static String TGS_ID = "40501";
    private final static String VSERVER_ID = "40502";
    private final static String AS_ID = "40401";
    private static String Client_ID; // 客户端生成时随机生成客户端ID,取system当前时间的前5位
    private static String PACK;
    private static String reqPACK;
    private  String TgsTicket; // TGT
    private  String VTicket; // VT
    private static String hashKey; // client hash过后的key

    private  String Kctgs; // AS生成的client与tgs之间的session-key
    private  String Kcv;    //TGS生成的client与Vserver 之间的session-key

    private static String hostName;

    @Override
    public void init() throws Exception {
        Client_ID = String.valueOf(System.currentTimeMillis()).substring(9, 13);
        loglist = FXCollections.observableArrayList();
        super.init();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {

        for (Thread thread : cThreads) {
            thread.interrupt();
        }
        if (socket != null && !socket.isClosed())
            socket.close();

        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

//		ClientThread client = new ClientThread(hostName, port, name)
        cThreads = new ArrayList<Thread>();
        primaryStage.setScene(loginStage(primaryStage));
        primaryStage.setIconified(false);
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);
        primaryStage.setTitle("客户端 ID:" + Client_ID);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public Scene loginStage(Stage primaryStage) {
        Label lname = new Label("用	户：");
        Label lpass = new Label("密	码：");
        Label lhostName = new Label("地	址：");
        Label lport = new Label("端	口：");

        lname.setFont(new Font(16));
        lpass.setFont(new Font(16));
        lhostName.setFont(new Font(16));
        lport.setFont(new Font(16));

        Button regist = new Button("注册");
        Button login = new Button("登录");

        regist.setPrefSize(60, 40);
        login.setPrefSize(60, 40);

        // 用户名框
        TextField tname = new TextField();
        TextField port = new TextField();
        TextField hostname = new TextField();

        port.setPromptText("请输入端口号");
        port.setText("8888");

        // 格式控制
        port.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(newValue);
                String mess = m.replaceAll("");
                if (mess.length() != newValue.length()) {
                    port.setText(oldValue);
                    Tooltip tp = new Tooltip("请输入数字");
                    port.setTooltip(tp);
                } else if (newValue.length() > 6) {
                    port.setText(oldValue);
                }

            }
        });

        port.setPrefHeight(40);
        port.setPrefWidth(200);

        hostname.setPromptText("请输入服务器地址");
        hostname.setText("localhost");
        hostname.setPrefHeight(40);
        hostname.setPrefWidth(200);

        tname.setPromptText("请输入聊天用户名");
        tname.setPrefHeight(40);
        tname.setPrefWidth(200);

        // 密码框
        PasswordField pass = new PasswordField();
        pass.setPrefHeight(40);
        pass.setPrefWidth(200);
        pass.setPromptText("请输入密码");

        // 网格布局
        GridPane gridp = new GridPane();
        gridp.add(lhostName, 0, 0);
        gridp.add(hostname, 1, 0);
        gridp.add(lport, 0, 1);
        gridp.add(port, 1, 1);
        gridp.add(lname, 0, 2);
        gridp.add(tname, 1, 2);
        gridp.add(lpass, 0, 3);
        gridp.add(pass, 1, 3);
        gridp.add(login, 0, 4);
        gridp.add(regist, 1, 4);
        gridp.setVgap(20);
        gridp.setHgap(30);
        gridp.setAlignment(Pos.BOTTOM_CENTER);
        gridp.setPadding(new Insets(30));

        regist.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (tname.getText().length() < 1) {
                    Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                    nameAlert.setHeaderText("请输入用户名");
                    nameAlert.initModality(Modality.APPLICATION_MODAL);
                    nameAlert.showAndWait();
                } else if (pass.getText().length() < 1) {
                    Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                    nameAlert.setHeaderText("请输入密码");
                    nameAlert.initModality(Modality.APPLICATION_MODAL);
                    nameAlert.showAndWait();
                    System.out.println("请输入密码");
                } else {
                    ClientApplication.setHashKey(MD5Tool.getMD5(pass.getText(), 16));
                    hostName = hostname.getText();
                    try {

                        Stage logStage = new Stage();
                        logStage.setWidth(800);
                        logStage.setHeight(800);
                        logStage.setTitle("authlog");
                        logStage.setScene(setLogField());
                        logStage.show();

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        login.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ClientThread client;
                if (ClientApplication.this.getVTicket() == null) {
                    Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                    nameAlert.setHeaderText("请先注册");
                    nameAlert.initModality(Modality.APPLICATION_MODAL);
                    nameAlert.show();
                } else {
                    if (tname.getText().length() < 1) {
                        Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                        nameAlert.setHeaderText("请输入用户名");
                        nameAlert.initModality(Modality.APPLICATION_MODAL);
                        nameAlert.showAndWait();
                    } else if (pass.getText().length() < 1) {
                        Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                        nameAlert.setHeaderText("请输入密码");
                        nameAlert.initModality(Modality.APPLICATION_MODAL);
                        nameAlert.showAndWait();
                        System.out.println("请输入密码");
                    } else {
                        // 建立socket连接
                        try {
                            client = new ClientThread(hostname.getText(), Integer.parseInt(port.getText()), tname.getText(),
                                    KbConstants.VSERVER_MODE, ClientApplication.this);
                            System.out.println(Integer.parseInt(port.getText()));
                            Thread clientThread = new Thread(client);
                            cThreads.add(clientThread);
                            clientThread.start();
                            Stage chatStage = new Stage();

                            chatStage.setWidth(800);
                            chatStage.setHeight(700);
                            chatStage.setTitle("当前用户" + tname.getText());
                            chatStage.setScene(setChatStage(client));
                            chatStage.show();
                            primaryStage.close();
                        } catch (java.net.ConnectException e) {
                            Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                            nameAlert.setHeaderText("服务器未开启");
                            nameAlert.showAndWait();
                            System.out.println("服务器未开启");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        return new Scene(gridp);
    }

    //注册获取认证信息时的二级界面
    public Scene setLogField() {

        GridPane grid = new GridPane();

        ListView<String> logArea = new ListView<String>();
        logArea.setPrefSize(600, 600);
//        logArea.setFixedCellSize(20);
//        logArea.autosize();
        logArea.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if(!empty&&item!=null&&item!="\r|\n|"&&item!="") {
                            Label la = new Label();
                            la.setWrapText(true);
                            la.setText(item);
                            la.setPadding(new Insets(10));
                            la.setStyle("-fx-border-color:green;"+"-fx-border-radius:10px;");
                            this.setGraphic(la);
                        }
                    }
                };
                cell.prefWidthProperty().bind(logArea.prefWidthProperty().divide(10));
                return  cell;
            }
        });
        logArea.setItems(loglist);
        logArea.refresh();
        Button start = new Button("start");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ClientThread client;
                try {
                    client = new ClientThread(KbConstants.AsAddr, Integer.parseInt(KbConstants.AS_Port),
                            KbConstants.Auth_MODE,ClientApplication.this);
                    Thread clientThread = new Thread(client);
                    clientThread.setName("AS_SOCKET");
                    cThreads.add(clientThread);
                    clientThread.start();
                }catch (java.net.ConnectException e)
                {
                    Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                    nameAlert.setHeaderText("服务器未开启");
                    nameAlert.initModality(Modality.APPLICATION_MODAL);
                    nameAlert.show();
//                    System.out.println("请输入密码");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        grid.add(logArea, 0, 0);
        grid.add(start, 0, 1);
        grid.setAlignment(Pos.CENTER);
        Scene scene = new Scene(grid);
        return scene;
    }

    // 二级聊天界面
    public Scene setChatStage(ClientThread client) {

        Button sendBtn = new Button("发送");
        sendBtn.setPrefSize(80, 40);

        Label ipAdress = new Label();

        ipAdress.setPrefSize(50, 20);
//        ipAdress.setStyle("-fx-background-color:red");
        TextArea ta = new TextArea();
        ta.setPrefSize(100,20);

        ListView<String> tArea = new ListView<>();
        tArea.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty&&item!=null&&item!="\r|\n|"&&item!="") {
                            Label la = new Label();
                            la.setWrapText(true);
                            la.setText(item);
                            la.setPadding(new Insets(10));
                            la.setStyle("-fx-border-color:green;"+"-fx-border-radius:10px;");
                            this.setGraphic(la);
                        }
                    }
                };
                cell.prefWidthProperty().bind(tArea.prefWidthProperty().divide(10));
                return  cell;
            }
        });
        tArea.setItems(client.loglist);
        tArea.refresh();
        TextArea sendArea = new TextArea();
        ListView<String> recvArea = new ListView<>();
        recvArea.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if(empty==false) {
                            Label la = new Label();
                            la.setWrapText(true);
                            la.setText(item);
                            la.setPadding(new Insets(10));
                            la.setStyle("-fx-border-color:green;"+"-fx-border-radius:10px;");
                            this.setGraphic(la);
                        }

                    }
                };

                cell.prefWidthProperty().bind(recvArea.prefWidthProperty().divide(10));
                return  cell;
            }
        });
        tArea.setPrefSize(200, 500);
        sendArea.setPrefSize(500, 100);
        recvArea.setPrefSize(500, 400);

        VBox vbox = new VBox();
        vbox.getChildren().add(recvArea);
        vbox.getChildren().add(sendArea);
        vbox.getChildren().add(sendBtn);
        vbox.setPadding(new Insets(10));
        VBox.setMargin(sendArea, new Insets(10, 0, 0, 0));
        VBox.setMargin(sendBtn, new Insets(10, 0, 0, 480));
        HBox hb = new HBox();
        hb.getChildren().add(ipAdress);
        hb.getChildren().add(ta);
        BorderPane borderPane = new BorderPane();
        borderPane.setRight(tArea);
//        borderPane.setTop(hb);
        borderPane.setCenter(vbox);
        BorderPane.setMargin(tArea, new Insets(10, 10, 10, 0));

        recvArea.setItems(client.messList);

        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(sendArea.getText()==""||sendArea.getText()==null) {
                    Alert nameAlert = new Alert(Alert.AlertType.ERROR);
                    nameAlert.setHeaderText("服务器未开启");
                    nameAlert.initModality(Modality.APPLICATION_MODAL);
                    nameAlert.show();
                }else {
                    try {
                        client.writeMessage(sendArea.getText(), ClientApplication.getClient_ID(), client.getName());
                        System.out.println(sendArea.getText());
                        sendArea.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return new Scene(borderPane);
    }

    // 获取当前时间
    public static String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return sdf.format(new Date());
    }


    public ObservableList<String> getLoglist() {
        return loglist;
    }

    public  String getKctgs() {
        return Kctgs;
    }

    public  void setKctgs(String kctgs) {
        Kctgs = kctgs;
    }

    public  String getKcv() {
        return Kcv;
    }

    public  void setKcv(String kcv) {
        Kcv = kcv;
    }

    public  String getVTicket() {
        return VTicket;
    }

    public  void setVTicket(String VTicket) {
        this.VTicket = VTicket;
    }

    public static String getReqPACK() {
        return reqPACK;
    }

    public static void setReqPACK(String reqPACK) {
        ClientApplication.reqPACK = reqPACK;
    }

    public static String getPACK() {
        return PACK;
    }

    public  String getTgsTicket() {
        return TgsTicket;
    }

    public  void setTgsTicket(String tgsTicket) {
        TgsTicket = tgsTicket;
    }

    public static void setHashKey(String hashKey) {
        ClientApplication.hashKey = hashKey;
    }

    public static String getVserverId() {
        return VSERVER_ID;
    }

    public static String getTgsId() {
        return TGS_ID;
    }

    public static String getClient_ID() {
        return Client_ID;
    }

    public static String getHashKey() {
        return hashKey;
    }

    public static String getHostName() {
        return hostName;
    }

    public static void setHostName(String hostName) {
        ClientApplication.hostName = hostName;
    }
}
