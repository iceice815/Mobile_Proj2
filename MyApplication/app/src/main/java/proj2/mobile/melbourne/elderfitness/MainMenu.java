package proj2.mobile.melbourne.elderfitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by rionay on 2017/9/28.
 */

public class MainMenu extends AppCompatActivity {
    private Button run_button;
    private Button virtialization_button;
    private String username;
    private String emgergency_number;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        run_button = (Button)findViewById(R.id.ButtonRun);
        virtialization_button = (Button)findViewById(R.id.ButtonVir);

        Intent rec_intent = getIntent();
        emgergency_number = rec_intent.getStringExtra("Emergency_Number");
        username = rec_intent.getStringExtra("username");
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
}
