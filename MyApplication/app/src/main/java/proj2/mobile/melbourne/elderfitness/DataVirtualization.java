package proj2.mobile.melbourne.elderfitness;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

import proj2.mobile.melbourne.elderfitness.dto.RecordTrack;
import proj2.mobile.melbourne.elderfitness.util.InitializeTable;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class DataVirtualization extends AppCompatActivity implements InitializeTable {
    private PieChart pie_chart1;
    private PieChart pie_chart2;
    private BarChart bar_distance_chart;
    private BarChart bar_flight_chart;
    private BarChart bar_calorie_chart;

    private MobileServiceClient mClient;
    private MobileServiceTable<RecordTrack> mUserInfoTable;

    private String username;
    private ArrayList<String> dates=new ArrayList<>();
    private ArrayList<String> mon_to_sun=new ArrayList<>();
    private ArrayList<Integer> mon_to_sun_colors=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_virtualization);
        pie_chart1 = (PieChart)findViewById(R.id.piechartID);
        pie_chart2 = (PieChart)findViewById(R.id.piechartID2);
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");
        init_table();
        //get current week date
        dates = get_week_list();
        //get mon to sun
        mon_to_sun = get_mon_to_sun();
        //get each day's color
        mon_to_sun_colors = get_mon_to_sun_colors();
        //vitualize two piecharts
        virtualize_pieChart();
        //set their property
        setPieChartProperty(pie_chart1);
        setPieChartProperty(pie_chart2);
        print_distance_bar_chart();
        print_flight_bar_chart();
        print_calorie_bar_chart();

    }
    /**
     * set pie chart property
     * @param pieChart
     */
    public void setPieChartProperty(PieChart pieChart){
        pieChart.setDescription(null);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterTextSize(10);
    }
    /**
     * virtualize pieChart1
     */
    private void virtualize_pieChart(){


        new AsyncTask<Object, Object, Void>() {
            PieDataSet pieDataSet1;
            PieData pieData1;
            PieDataSet pieDataSet2;
            PieData pieData2;
            @Override
            protected Void doInBackground(Object... params) {

                try {
                    /**
                     *transaction logic for piechart1
                     */
                    ArrayList<PieEntry> yEntrys1 = new ArrayList<>();
                    ArrayList<String> xEntrys1 =new ArrayList<>();
                    ArrayList<Integer> colors1 = new ArrayList<>();
                    final List<RecordTrack> results1 = get_items_from_table(username);
                    final List<RecordTrack> this_week_results = get_this_week_results(results1);
                    for(int i = 0; i < dates.size(); i++) {
                        double temp_colories = 0;
                        for (RecordTrack this_week_result : this_week_results) {
                                if(dates.get(i).equals(this_week_result.getmDate())){
                                    temp_colories = temp_colories + this_week_result.getmColories();
                                }
                        }
                        if(temp_colories>0) {
                            xEntrys1.add(mon_to_sun.get(i));
                            yEntrys1.add(new PieEntry((float) temp_colories, i));
                            colors1.add(mon_to_sun_colors.get(i));
                        }

                    }
                    pieDataSet1 = new PieDataSet(yEntrys1,"Weekly Calories");
                    pieDataSet1.setSliceSpace(2);
                    pieDataSet1.setValueTextSize(12);
                    pieDataSet1.setColors(colors1);
                    pieData1 = new PieData(pieDataSet1);
                    /**
                     *transaction logic for piechart1  END
                     */
                    /**
                     *transaction logic for piechart2  BEGIN
                     */
                    ArrayList<PieEntry> yEntrys2 = new ArrayList<>();
                    ArrayList<String> xEntrys2 =new ArrayList<>();
                    ArrayList<Integer> colors2 = new ArrayList<>();
                    final List<RecordTrack> results2 = get_items_from_table(username);
                    final List<RecordTrack> today_results = get_today_results(results2);
                    double today_distance_colories = 0;
                    double today_filightsclimbed_colories = 0;
                    for (RecordTrack today_result : today_results){
                        today_distance_colories = today_distance_colories + today_result.getmDistance() * 0.065;
                        today_filightsclimbed_colories = today_filightsclimbed_colories + today_result.getmFlightsClimbed() * 1.1;
                    }
                    xEntrys2.add("Running");
                    xEntrys2.add("Flights Climbed");
                    yEntrys2.add(new PieEntry((float)today_distance_colories,0));
                    yEntrys2.add(new PieEntry((float)today_filightsclimbed_colories,1));
                    pieDataSet2 = new PieDataSet(yEntrys2,"Today's Colories");
                    colors2.add(Color.YELLOW);
                    colors2.add(Color.GREEN);
                    pieDataSet2.setColors(colors2);
                    pieDataSet2.setSliceSpace(2);
                    pieDataSet2.setValueTextSize(12);
                    pieData2 = new PieData(pieDataSet2);
                    /**
                     *transaction logic for piechart2  END
                     */

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /**
                             * refresh UI thead
                             */
                            Legend legend1 = pie_chart1.getLegend();
                            legend1.setForm(Legend.LegendForm.CIRCLE);
                            legend1.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                            pie_chart1.setData(pieData1);
                            pie_chart1.invalidate();

                            Legend legend2 = pie_chart2.getLegend();
                            legend2.setForm(Legend.LegendForm.CIRCLE);
                            legend2.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
                            pie_chart2.setData(pieData2);
                            pie_chart2.invalidate();
                        }
                    });


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

    /**
     * A filter for getting today's results
     * @param results
     * @return
     */
    private List<RecordTrack> get_today_results(List<RecordTrack> results){
        List<RecordTrack> today_results =new ArrayList<>();
        String today = get_today_date();
        for(RecordTrack result: results){
            if(today.equals(result.getmDate())){
                today_results.add(result);
            }
        }
        return today_results;

    }
    private List<RecordTrack> get_this_week_results(List<RecordTrack> results){
        List<RecordTrack> this_week_results =new ArrayList<>();
        for(RecordTrack result: results){
            if(dates.contains(result.getmDate())){
                this_week_results.add(result);
            }
        }
        return this_week_results;
    }
    /**
     *
     * @return an Arraylist containing current week date information
     */

    public String get_today_date(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String today_to_string =simpleDateFormat.format(date);
        return today_to_string;
    }

    /**
     * initilize an ArrayList contains dates
     * @return
     */
    private ArrayList<String> get_mon_to_sun(){
        ArrayList<String> theDates=new ArrayList<>();
        theDates.add("Mon");
        theDates.add("Tue");
        theDates.add("Wed");
        theDates.add("Thu");
        theDates.add("Fri");
        theDates.add("Sat");
        theDates.add("Sun");
        return theDates;
    }

    /**
     * initilize table
     */
    @Override
    public void init_table(){
        try {
            mClient = new MobileServiceClient("https://elderfitness.azurewebsites.net", this);
            mUserInfoTable = mClient.getTable(RecordTrack.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * A method that retrieve the distance data from the database by
     * using the username input by the user
     */


    public void print_distance_bar_chart(){
        final List<Integer> run_distance = new ArrayList<>();

        final String username;
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");

        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {


                try {
                    final List<RecordTrack> results = get_items_from_table(username);
                    //get all the data of the same user.

                    ArrayList<String> date_list = get_week_list();
                    //get all the date of this week

                    Map<String,Integer> map=new HashMap<String,Integer>();
                    for(RecordTrack infor:results){
                        if(map.containsKey(infor.getmDate())){
                            int temp_distance = map.get(infor.getmDate());
                            map.put(infor.getmDate(),temp_distance+infor.getmDistance());
                            //put all the distance data of this week into map
                        }
                        else {
                            map.put(infor.getmDate(), infor.getmDistance());
                        }

                    }



                    Map<Integer,Integer> map_this_week = new TreeMap<Integer, Integer>();


                    for(int i = 0;i<date_list.size();i++){
                        if(map.containsKey(date_list.get(i))){
                            map_this_week.put(i,map.get(date_list.get(i)));
                            //sort the data of this week by the week days
                        }
                        else
                            map_this_week.put(i,0);
                    }



                    for (int i = 0;i<date_list.size();i++){
                        run_distance.add(map_this_week.get(i));
                        //add all the data of the map into the distance list
                    }



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


        bar_distance_chart = (BarChart)findViewById(R.id.DistanceBarID);
        for(int i = 0;i<7;i++){
            barEntries.add(new BarEntry(i, 0));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        BarData theData = new BarData(barDataSet);
        bar_distance_chart.setData(theData);
        bar_distance_chart.setTouchEnabled(true);
        bar_distance_chart.setDragEnabled(true);
        bar_distance_chart.setScaleEnabled(true);
        XAxis xAxis =bar_distance_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));
        //initial the barchart

        Refresh_distance_Chart(run_distance);

    }

    /**
     * A method that use the distance data retrieve from the database
     * and show them in the barchart
     * @param run_distance
     */

    public void Refresh_distance_Chart(final List<Integer> run_distance){
        bar_distance_chart = (BarChart)findViewById(R.id.DistanceBarID);
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i<run_distance.size();i++){
                            barEntries.add(new BarEntry(i, run_distance.get(i)));
                        }
                        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
                        barDataSet.setColor(Color.GRAY);
                        BarData theData = new BarData(barDataSet);
                        bar_distance_chart.setData(theData);
                        bar_distance_chart.setTouchEnabled(true);
                        bar_distance_chart.setDragEnabled(true);
                        bar_distance_chart.setScaleEnabled(true);
                        bar_distance_chart.setActivated(true);
                        Description ds = new Description();
                        ds.setText("Weekly Running Distance");
                        bar_distance_chart.setDescription(ds);

                    }
                });
                return null;
            }
        }.execute();

        XAxis xAxis =bar_distance_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));

    }

    /**
     * A method that retrieve the flight data from the database by
     * using the username input by the user
     */

    public void print_flight_bar_chart(){
        final List<Integer> climb_distance = new ArrayList<>();

        final String username;
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");

        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {


                try {
                    final List<RecordTrack> results = get_items_from_table(username);

                    ArrayList<String> date_list = get_week_list();

                    Map<String,Integer> map=new HashMap<String,Integer>();
                    for(RecordTrack infor:results){
                        if(map.containsKey(infor.getmDate())){
                            int temp_distance = map.get(infor.getmDate());
                            map.put(infor.getmDate(),temp_distance+infor.getmFlightsClimbed());
                        }
                        else
                            map.put(infor.getmDate(),infor.getmFlightsClimbed());
                    }

                    Map<Integer,Integer> map_this_week = new TreeMap<Integer, Integer>();


                    for(int i = 0;i<date_list.size();i++){
                        if(map.containsKey(date_list.get(i))){
                            map_this_week.put(i,map.get(date_list.get(i)));
                        }
                        else
                            map_this_week.put(i,0);
                    }


                    for (int i = 0;i<date_list.size();i++){
                        climb_distance.add(map_this_week.get(i));
                    }




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


        bar_flight_chart = (BarChart)findViewById(R.id.FightsClimbedBarID);
        for(int i = 0;i<7;i++){
            barEntries.add(new BarEntry(i, 0));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        BarData theData = new BarData(barDataSet);
        bar_flight_chart.setData(theData);
        bar_flight_chart.setTouchEnabled(true);
        bar_flight_chart.setDragEnabled(true);
        bar_flight_chart.setScaleEnabled(true);

        XAxis xAxis =bar_flight_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));

        Refresh_flight_Chart(climb_distance);

    }

    /**
     * A method that use the flight data retrieve from the database
     * and show them in the barchart
     * @param run_distance
     */

    public void Refresh_flight_Chart(final List<Integer> run_distance){
        bar_flight_chart = (BarChart)findViewById(R.id.FightsClimbedBarID);
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i<run_distance.size();i++){
                            barEntries.add(new BarEntry(i, run_distance.get(i)));
                            //add data into the barchart
                        }
                        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
                        barDataSet.setColor(Color.GRAY);
                        BarData theData = new BarData(barDataSet);
                        bar_flight_chart.setData(theData);
                        bar_flight_chart.setTouchEnabled(true);
                        bar_flight_chart.setDragEnabled(true);
                        bar_flight_chart.setScaleEnabled(true);
                        bar_flight_chart.setActivated(true);

                        Description ds = new Description();
                        ds.setText("Weekly Flights Climbed");
                        bar_flight_chart.setDescription(ds);
                    }
                });
                return null;
            }
        }.execute();
        XAxis xAxis =bar_flight_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));

    }


    /**
     * A method that retrieve the calori data from the database by
     * using the username input by the user
     */
    public void print_calorie_bar_chart(){
        final List<Float> calorie = new ArrayList<>();

        final String username;
        Intent rec_intent = getIntent();
        username = rec_intent.getStringExtra("username");

        final ArrayList<BarEntry> barEntries = new ArrayList<>();

        new AsyncTask<Object, Object, Void>() {
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


        bar_calorie_chart = (BarChart)findViewById(R.id.CaloriesBarID);
        for(int i = 0;i<7;i++){
            barEntries.add(new BarEntry(i, 0));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        BarData theData = new BarData(barDataSet);
        bar_calorie_chart.setData(theData);
        bar_calorie_chart.setTouchEnabled(true);
        bar_calorie_chart.setDragEnabled(true);
        bar_calorie_chart.setScaleEnabled(true);
        XAxis xAxis =bar_calorie_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));

        Refresh_calorie_Chart(calorie);

    }

    /**
     * A method that use the calori data retrieve from the database
     * and show them in the barchart
     * @param run_distance
     */
    public void Refresh_calorie_Chart(final List<Float> run_distance){
        bar_calorie_chart = (BarChart)findViewById(R.id.CaloriesBarID);
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i<run_distance.size();i++){
                            float calories = (float)run_distance.get(i);
                            barEntries.add(new BarEntry(i, run_distance.get(i)));
                        }
                        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
                        barDataSet.setColor(Color.GRAY);
                        BarData theData = new BarData(barDataSet);
                        bar_calorie_chart.setData(theData);
                        bar_calorie_chart.setTouchEnabled(true);
                        bar_calorie_chart.setDragEnabled(true);
                        bar_calorie_chart.setScaleEnabled(true);
                        bar_calorie_chart.setActivated(true);
                        Description ds = new Description();
                        ds.setText("Weekly Calories");
                        bar_calorie_chart.setDescription(ds);
                    }
                });
                return null;
            }
        }.execute();
        XAxis xAxis =bar_calorie_chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mon_to_sun));

    }

    /**
     * A method that given today's date and then get this week's date,
     * which has an significant function in realize data virtualization
     * @return
     */
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

    /**
     * get today's date
     * @param cld
     * @return
     */
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

    /**
     * give each date with a color for better data virtualization
     * @return
     */
    private ArrayList<Integer> get_mon_to_sun_colors() {
        ArrayList<Integer> theColors = new ArrayList<>();
        theColors.add(Color.RED);
        theColors.add(Color.GREEN);
        theColors.add(Color.GRAY);
        theColors.add(Color.BLUE);
        theColors.add(Color.BLACK);
        theColors.add(Color.YELLOW);
        theColors.add(Color.DKGRAY);
        return theColors;
    }

    /**
     * Similar to Select*From ***.table where username ="xxxx"
     * @param Username
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws MobileServiceException
     */
    private List<RecordTrack> get_items_from_table(String Username) throws ExecutionException, InterruptedException, MobileServiceException {
        //return a item list which has the same username with the user input
        return mUserInfoTable.where().field("username").eq(val(Username)).execute().get();
    }

}
