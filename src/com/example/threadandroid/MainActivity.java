// Multi-threading example using message passing 
package com.example.threadandroid;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
ProgressBar bar1;
ProgressBar bar2;
TextView msgWorking;
TextView msgReturned;

boolean isRunning = false;
final int MAX_SEC =  60; // (seconds) lifetime for background thread

String strTest = "global value seen by all threads ";
int intTest = 0;
@Override
public void onCreate(Bundle icicle) {

	super.onCreate(icicle);
	setContentView(R.layout.thread_main);

	bar1 = (ProgressBar) findViewById(R.id.progress);
	bar2 = (ProgressBar) findViewById(R.id.progress2);
	     bar1.setMax(MAX_SEC);
	     bar1.setProgress(0);

	msgWorking = (TextView)findViewById(R.id.TextView01);
	msgReturned = (TextView)findViewById(R.id.TextView02);

	strTest += "-01"; // slightly change the global string 
	intTest = 1;

}//onCreate

	Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		String returnedValue = (String)msg.obj;
		//do something with the value sent by the background thread here ...
		msgReturned.setText("returned by background thread: \n\n" 
		       + returnedValue);
		bar1.incrementProgressBy(2);
		
		//testing thread’s termination
		if (bar1.getProgress() == MAX_SEC){
			msgReturned.setText("Done \n back thread has been stopped");
			isRunning = false;
		}
		if (bar1.getProgress() == bar1.getMax()){
			msgWorking.setText("Done");
			bar1.setVisibility(View.INVISIBLE);
			bar2.setVisibility(View.INVISIBLE);
			bar1.getLayoutParams().height = 0;
			bar2.getLayoutParams().height = 0;
		}
		else {
			msgWorking.setText("Working..." +
			bar1.getProgress() );
		}
	}
	}; //handler
	



	public void onStop() {
	
		super.onStop();
		isRunning = false;
	}

	public void onStart() {
	super.onStart();
	//bar1.setProgress(0);
		Thread background = new Thread(new Runnable() {
			public void run() {
				try {
					for (int i = 0; i < MAX_SEC && isRunning; i++) {
					//try a Toast method here (will not work!)
					//fake busy busy work here
					Thread.sleep(1000);  //one second at a time
					Random rnd = new Random();
					
					//this is a locally generated value
					String data = "Thread Value: " + (int) rnd.nextInt(101);
					
					//we can see and change (global) class variables
					data += "\n" + strTest + " " + intTest;
					intTest++;
					
					//request a message token and put some data in it 
					Message msg = handler.obtainMessage(1, (String)data);
					
					//if thread is still alive send the message
					if (isRunning) {
						handler.sendMessage(msg);
					}
					}
				} catch (Throwable t) {
					//just end the background thread
				}
			}//run
		});//background
		isRunning = true;
		background.start();
	}//onStart
} //class
