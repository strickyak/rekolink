package net.yak.rekolink;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Etc etc;
    private Engine engine;

    private TextView titleView;
    static Handler handler = new Handler();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    titleView.setText("REKolink");
                    return true;
                case R.id.navigation_dashboard:
                    titleView.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    titleView.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("PTT", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When the volume keys will be pressed the audio stream volume will be changed.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        etc = new Etc(this, new Config());
        engine = new Engine(this, etc, handler);
        engine.start();

        etc.feedback = new MainFeedback();

        ////////  new Thread(new EchoServer(etc)).start();

        titleView = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ConfigurePttHandlers();
    }

    Thread machineThread;

    @Override
    public void onStart() {
        Log.e("PTT", "onStart");
        super.onStart();
        // pttInit();

    }

    // activity comes to the foreground
    @Override
    public void onResume() {
        Log.e("PTT", "onResume");
        super.onResume();

        engine.setGoal(Engine.SPEAKER_LEVEL);
        //engine.setGoal(Engine.MICRPOPHONE_LEVEL);
    }

    @Override
    public void onStop() {
        Log.e("PTT", "onStop");
        super.onStop();
        // pttPause();
        engine.setGoal(Engine.DNS_LEVEL);
    }

    @Override
    public void onDestroy() {
        Log.e("PTT", "onDestroy");
        super.onDestroy();
        engine.setGoal(Engine.DNS_LEVEL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        engine.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

//    int timesPttWasPushed;

    void ConfigurePttHandlers() {
        final View b = findViewById(R.id.PttButton);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        engine.setGoal(Engine.TALK_LEVEL);
                        return true;
                    case MotionEvent.ACTION_UP:
                        engine.setGoal(Engine.MICRPOPHONE_LEVEL);
//                        ++timesPttWasPushed;
//                        if (timesPttWasPushed > 3) {
//                            engine.setGoal(0);
//                        }
                        return true;
                }
                return false;
            }
        });
    }

    class MainFeedback implements Etc.Feedback {
        final View b = findViewById(R.id.PttButton);

        @Override
        public void Busy() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("Rx: BUSY");
                    b.setBackgroundColor(0xFFFFFF00);  // yellow
                }
            });
        }

        @Override
        public void Ready() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("READY TO Tx or Rx");
                    b.setBackgroundColor(0xFF00FF00);  // green
                }
            });
        }

        @Override
        public void Disabled() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("READY TO Tx or Rx");
                    b.setBackgroundColor(0xFF888888);  // medium gray
                }
            });
        }

        @Override
        public void PacketSendSuccessful() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("Tx: SENDING");
                    b.setBackgroundColor(0xFFFF00FF);  // magenta
                }
            });
        }

        @Override
        public void PacketSendFailure() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("Tx: CANNOT SEND");
                    b.setBackgroundColor(0xFF443322);  // dark gray (orangish?)
                }
            });
        }

        @Override
        public void HeartbeatFailure() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    titleView.setText("OFFLINE");
                    b.setBackgroundColor(0xFF443322);  // dark gray (orangish?)
                }
            });
        }

        @Override
        public void HeartbeatSuccessful() {
            Disabled();
        }
//    }
    }
}
