package com.arka.android.speechtotext;

import java.lang.reflect.Array;
import java.util.*;  
import java.io.*;  
 
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Parcelable;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileReader;

public class MainActivity extends Activity {
	
	protected static final int RESULT_SPEECH = 1;
	
	private ImageButton btnSpk;
	private TextView txt, txt2;
	String ipstring, opstring1, opstring2;
	ArrayList a = new ArrayList();
	
	public class CSVFile {
		InputStream inputStream;
	    public CSVFile(InputStream inputStream){
        	this.inputStream = inputStream;
	    }

	    public ArrayList read()
	    {
	    	ArrayList resultList = new ArrayList();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    	try 
	    	{
	    		String csvLine;
	    		int count = 0;
	    		while ((csvLine = reader.readLine()) != null) 
	    		{
	    			if(csvLine.contains("|,"))
	    			{
	    				//int pos = csvLine.indexOf("|,");
	    				//String row = csvLine.substring(0, pos);
	    				resultList.add(count, csvLine);
	    				count++;
	    			}
	    		}
	    	} catch (IOException ex) 
	    	{
	    		throw new RuntimeException("Error in reading CSV file: "+ex);
	    	}
	    	finally 
	    	{
	    		try 
	    		{
	    			inputStream.close();
	    		} catch (IOException e) 
	    		{
	    			throw new RuntimeException("Error while closing input stream: "+e);
	    		}
	    	}
	    	return resultList;
	    }
	}
				
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InputStream inputStream = getResources().openRawResource(R.raw.out_1);
		CSVFile csvFile = new CSVFile(inputStream);
		a = csvFile.read();
		
		txt = (TextView) findViewById(R.id.txtText);
		txt2 = (TextView) findViewById(R.id.txtText2);
		
		btnSpk = (ImageButton) findViewById(R.id.btnSpeak);

		btnSpk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try 
				{
					startActivityForResult(i, RESULT_SPEECH);
					txt.setText("");
				} catch (ActivityNotFoundException a) 
				{
					Toast toast = Toast.makeText(getApplicationContext(),
							"Device doesn't support Speech to Text Conversion",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) 
			{
				ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				ipstring = text.get(0).toString();
				
				for(int i=0; i< a.size()-1; i++)
				{
					String s = a.get(i).toString();
					int commaindex = s.indexOf(",");
					String q = s.substring(0,commaindex);
					String r = s.substring(commaindex+1);
					int opIndex = r.indexOf("|,");
					String typeString = r.substring(0,opIndex);
					String rankString = r.substring(opIndex + 2);
					
					
					String[] types = typeString.split("\\|");
					String[] ranks = rankString.split("\\|");
					System.out.println(rankString);
					for (String str : ranks)
						System.out.println(str);
					
					HashMap<Integer, String> map = new HashMap<Integer, String>();
					
					String finalString = "";
					for (int j=0;j<ranks.length;j++){
						if (!ranks[j].equals("") )
							map.put(Integer.parseInt(ranks[j]), types[j]);
					}
					
					for (int j=1;j<=5;j++){
						if (map.get(j) != null){
							finalString += map.get(j) + '\n';
						}
					}
					
//					String t = r.replace("|", "\n");
					if(ipstring.contains(q.toLowerCase()))
					{
						txt.setText(q);
						txt2.setText(finalString);
					}	
				}
			}
			break;
		}

		}
	}
}
