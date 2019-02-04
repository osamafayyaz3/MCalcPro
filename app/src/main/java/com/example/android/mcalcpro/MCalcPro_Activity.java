package com.example.android.mcalcpro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
        this.tts = new TextToSpeech(this,this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if(a > 20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }

    public void onInit(int initStatus)
    {
        this.tts.setLanguage(Locale.US);
    }

    public void buttonClicked(View v)
    {
        EditText x = (EditText) findViewById(R.id.pBox);
        String x1 = x.getText().toString();

        EditText y = (EditText) findViewById(R.id.aBox);
        String y1 = y.getText().toString();

        EditText z = (EditText) findViewById(R.id.iBox);
        String z1 = z.getText().toString();
      try {
          MPro myModel = new MPro();
          myModel.setPrinciple(x1);
          myModel.setAmortization(y1);
          myModel.setInterest(z1);
          String toSpeak = "Monthly Payment = " + myModel.computePayment("%.2f");

          String s = "Monthly Payment = " + "$" +myModel.computePayment("%,.2f");
          s += "\n\n";
          s += "By making this payments monthly for 20 years, the mortgage will be paid in full." +
                  " But if you terminate the mortgage on its nth anniversary, the balance still owing depends " +
                  "on n as shown below: ";
          s += "\n\n";
          s += String.format("%8s", "n") + String.format("%16s", "Balance");
          s += "\n\n";

          for(int i = 0; i < 6; i++)
          {
              s += String.format("%8d",i) + myModel.outstandingAfter(i,"%,16.0f");
              s += "\n\n";
          }

          for(int an = 10; an < 21; an += 5 )
          {
              s += String.format("%8d",an)  + myModel.outstandingAfter(an,"%,16.0f");
              s += "\n\n";
          }

          ((TextView) findViewById(R.id.output)).setText(s);
          tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
      }

      catch (Exception e)
      {
          Toast label = Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
          label.show();
      }

    }


}
