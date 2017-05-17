package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ich on 16.05.2017.
 */
public class EfbHelperConnectionClass {

    Context context;

    public EfbHelperConnectionClass (Context tmp_context) {

        context = tmp_context;

    }



    public boolean internetAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }





    /*

    public class MainActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    TextView tv;

    final String scripturlstring = "http://derandroidpro.esy.es/sendtexttoserver_tutorial/receive_script.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.editText);
        tv = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetAvailable()){
                    sendToServer(et.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Internet ist nicht verfügbar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendToServer(final String text){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String textparam = "text1=" + URLEncoder.encode(text, "UTF-8");

                    URL scripturl = new URL(scripturlstring);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();
                    contentWriter.close();

                    InputStream answerInputStream = connection.getInputStream();
                    final String answer = getTextFromInputStream(answerInputStream);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(answer);
                        }
                    });
                    answerInputStream.close();
                    connection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public String getTextFromInputStream(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String aktuelleZeile;
        try {
            while ((aktuelleZeile = reader.readLine()) != null){
                stringBuilder.append(aktuelleZeile);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString().trim();
    }

    public boolean internetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}


     */




}
