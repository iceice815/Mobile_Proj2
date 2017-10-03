package proj2.mobile.melbourne.elderfitness;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import proj2.mobile.melbourne.elderfitness.data.RecordTrack;
import proj2.mobile.melbourne.elderfitness.data.UserInfo;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Created by rionay on 2017/9/28.
 */

public class MainMenu extends AppCompatActivity {
    private Button run_button;
    private Button virtialization_button;
    private String username;
    private String emgergency_number;
    private TextView total_Calori;
    private TextView avg_Calori;

    private MobileServiceClient mClient;
    private MobileServiceTable<RecordTrack> mUserInfoTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        run_button = (Button)findViewById(R.id.ButtonRun);
        virtialization_button = (Button)findViewById(R.id.ButtonVir);
        total_Calori = (TextView) findViewById(R.id.totdistance);
        avg_Calori = (TextView) findViewById(R.id.avgdistance);

        Intent rec_intent = getIntent();
        emgergency_number = rec_intent.getStringExtra("Emergency_Number");
        username = rec_intent.getStringExtra("username");




        init_table();

        Calori_show();


        run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MainMenu.this, RunningTrack.class);
                intent1.putExtra("username",username);
                intent1.putExtra("Emergency_Number",emgergency_number);
                startActivity(intent1);

            }
        });

        virtialization_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainMenu.this, DataVirtualization.class);
                intent2.putExtra("username",username);
                startActivity(intent2);

            }
        });

    }

    public void Calori_show(){
        final List<Float> calorie = new ArrayList<>();

        final String username;
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");

        final float[] total_calori = {0};
        final float[] avg_calori = {0};
        final String[] total = {""};
        final String[] avg = {""};

        new AsyncTask<Object, Object, Void>(){

            @Override
            protected Void doInBackground(Object... params) {
                try {
                    final List<RecordTrack> results = get_items_from_table(username);

                    ArrayList<String> date_list = get_week_list();

                    Map<String,Float> map=new HashMap<String,Float>();
                    for(RecordTrack infor:results){
                        if(map.containsKey(infor.getmDate())){
                            double temp_distance = map.get(infor.getmDate());
                            map.put(infor.getmDate(), (float) (temp_distance+infor.getmColories()));
                        }
                        else
                            map.put(infor.getmDate(), (float) infor.getmColories());
                    }

                    Map<Integer,Float> map_this_week = new TreeMap<Integer, Float>();


                    for(int i = 0;i<date_list.size();i++){
                        if(map.containsKey(date_list.get(i))){
                            map_this_week.put(i,map.get(date_list.get(i)));
                        }
                        else
                            map_this_week.put(i, (float) 0.0);
                    }


                    for (int i = 0;i<date_list.size();i++){
                        calorie.add(map_this_week.get(i));
                    }

                    for(int i=0;i<calorie.size();i++){
                        total_calori[0] = calorie.get(i)+ total_calori[0];
//                        total_calori = (int) (calorie.get(i)+ total_calori);
                    }

                    avg_calori[0] = total_calori[0]/get_week_days();

                    total[0] = String.valueOf(total_calori[0]);
                    avg[0] = String.valueOf(avg_calori[0]);

                    show_total(total);
                    show_avg(avg);





                    Log.i("total",total_calori[0]+"total");
                    Log.i("total",avg_calori[0]+"avg");

//                    total_Calori.setText(total_calori[0]);


                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MobileServiceException e) {
                    e.printStackTrace();
                }
                return null;
            }
            }.execute();

        }

        private void show_total(final String[] total) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            total_Calori.setText(total[0]);
                        }
                    });
                    return null;
                }
            }.execute();
        }

    private void show_avg(final String[] avg) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avg_Calori.setText(avg[0]);
                    }
                });
                return null;
            }
        }.execute();
    }

        private int get_week_days(){
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime(date);
            switch (cal.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    return 7;
                case 2:
                    return 1;
                case 3:
                    return 2;
                case 4:
                    return 3;
                case 5:
                    return 4;
                case 6:
                    return 5;
                case 7:
                    return 6;
            }
            return 0;
        }



    private ArrayList<String> get_week_list(){
        ArrayList<String> list = new ArrayList<>();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            //Sunday
            case 1:

                cal.add(Calendar.DAY_OF_MONTH,-6);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }
                break;
            //Monday
            case 2:
                for(int i=0;i<7;i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }

                break;
            //Tuesday
            case 3:
                cal.add(Calendar.DAY_OF_MONTH,-1);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }
                break;
            //Wednesday
            case 4:
                cal.add(Calendar.DAY_OF_MONTH,-2);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }

                break;
            //Thursday
            case 5:
                cal.add(Calendar.DAY_OF_MONTH,-3);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }

                break;
            //Friday
            case 6:
                cal.add(Calendar.DAY_OF_MONTH,-4);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }


                break;
            //Saturday
            case 7:
                cal.add(Calendar.DAY_OF_MONTH,-5);
                for (int i = 0 ; i <7 ; i++){
                    list.add(getDate(cal));
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }

                break;

        }
        return list;
    }


    private String getDate(Calendar cld){
        String curDate = cld.get(Calendar.YEAR)+"/"+(cld.get(Calendar.MONTH)+1)+"/"
                +cld.get(Calendar.DAY_OF_MONTH);
        try{
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }


    private List<RecordTrack> get_items_from_table(String Username) throws ExecutionException, InterruptedException, MobileServiceException {
        //return a item list which has the same username with the user input
        return mUserInfoTable.where().field("username").eq(val(Username)).execute().get();
    }

    public void init_table() {
        try {
            //get a client of the referred database
            mClient = new MobileServiceClient("https://elderfitness.azurewebsites.net", this);
            //get the table of the database
            mUserInfoTable = mClient.getTable(RecordTrack.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }





}
