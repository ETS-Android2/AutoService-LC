package kk.techbytecare.simplechatbot;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import kk.techbytecare.simplechatbot.Adapter.ChatMessageAdapter;
import kk.techbytecare.simplechatbot.Model.ChatMessage;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMsg;
    ImageView imageView;

    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        edtTextMsg = findViewById(R.id.edtTextMsg);
        imageView = findViewById(R.id.imageView);

        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);
/*
* This button is in charge of sending messages to the chatbot
* and that the chatbot responds with a message
*/
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtTextMsg.getText().toString();

                String response = chat.multisentenceRespond(edtTextMsg.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this, "Please enter a query..", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(message);
                botsReply(response);
                edtTextMsg.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        boolean available = isSDCARDAvailable();

        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/TBC/bots/TBC");

        boolean makeFile = fileName.mkdirs();

/*
* In the section below you will find what makes the app find where the bot's brain
* is stored, which is located on the sd card in a folder called TBC \ bots \ TBC \ ...
* Within these folders is every possible question and answer to user interaction.
*/

        if (fileName.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("TBC")) {

                    File subDir = new File(fileName.getPath() + "/" + dir);
                    boolean subDir_check = subDir.mkdirs();

                    for (String file : assets.list("TBC/" + dir)) {
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);
                        if (newFile.exists()) {
                            continue;
                        }
                        InputStream in;
                        OutputStream out;
                        in = assets.open("TBC/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                        /*
                        copy file from assets to the mobile's SD card or any secondary memory
                         */
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        get the working directory
        from here you get what you need for the chatbot, and show it on the screen
         */
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC";
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();

        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);

        /*
         Welcome message
         Here is the code that shows the first message when the application is opened and asks us
         for the name
         */
        String message = "Hi, Welcome to the Taller cedeno Auto Service \nwhat is your name?";

        botsReply(message);
        listView.setSelection(adapter.getCount() - 1);

    }
    /*
    here we find the code that is responsible for choosing the side on which
    the messages go, the bot goes to the left and the user goes to the right
     */
    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        adapter.add(chatMessage);
    }

    private void botsReply(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        adapter.add(chatMessage);
    }

    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}