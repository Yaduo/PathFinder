package com.test.PathFind;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;

public class PathFind extends Activity
{
	// Declare GameView and Game
	GameView gameView;
	Game game;
	
	// Declare drop-down list
	Spinner searchMethod;
	Spinner targetSpinner;
	
	// Declare drop-down list model
	private ArrayAdapter<String> adapter;
	private ArrayAdapter<String> adapter2;
		
	// Declare buttons
	Button goButton;
	Button cleanMapButton;
	Button cleanPathButton;
	Button decelerateButton;
	Button accelerateButton;
	
	// Declare TextView 
	TextView StepsTextView;
	TextView pathLengthTextView;

	// main entrance 
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
    	Log.d("yaduo","onCreate");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		// Quote GameView
        gameView = (GameView)findViewById(R.id.gameView);
        // inti the Algorithm Class -- Game
        game = new Game();
        
		// -------------------- Quote TextView -------------------------
        StepsTextView = (TextView)findViewById(R.id.steps);
        pathLengthTextView = (TextView)findViewById(R.id.pathLength);

        // ------------------------- Spinner -------------------------------
		// Quote the drop-down list
        searchMethod = (Spinner)findViewById(R.id.searchMethod);
        targetSpinner = (Spinner)findViewById(R.id.target);
        
		// create the drop-down list model
        String strDF = this.getString(R.string.depth_first);
        String strBF = this.getString(R.string.breadth_first);  
        String strABF = this.getString(R.string.a_star_BF);  
        String strDijkstra = this.getString(R.string.dijkstra);  
        String[] searchMethod_str = {
        		strDF,strBF,strABF,strDijkstra
        }; 
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, searchMethod_str);
		
        // create an array depends on the target number of points 
		// choose a target
        String strTarget = this.getString(R.string.target);  
        String[] targetNum = new String[Map.target.length];
        for(int i = 0; i<Map.target.length; i++){
        	targetNum[i] = strTarget + i;
        }
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, targetNum);
        
        // set the model
		searchMethod.setAdapter(adapter);
        targetSpinner.setAdapter(adapter2);
		
		// algorithm choosing Listener
        searchMethod.setOnItemSelectedListener(
            	new Spinner.OnItemSelectedListener(){
            		// on Select Item 
    				public void onItemSelected(AdapterView<?> adapter, View v ,int i, long l){
    					// clear the game state
						game.clearState();
						// get the algorithm ID
    					game.algorithmId = (int) adapter.getSelectedItemId();
						// repaint the GameView
    					gameView.postInvalidate();
    				}
    				public void onNothingSelected(AdapterView<?> arg0) {
    				}
            	}
         );
        
		// Target drop-down list Listener
        targetSpinner.setOnItemSelectedListener(
        	new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> adapter, View v,int i, long l){
					game.target = Map.target[i];
					// clear the game state
					game.clearState();
					// repaint the GameView
					gameView.postInvalidate();
				}
				public void onNothingSelected(AdapterView<?> arg0){
				}
        	}
        );
        
		// ---------------------------- buttons ------------------------
        // Quote the buttons
        goButton = (Button) findViewById(R.id.go);
        cleanMapButton = (Button) findViewById(R.id.clean_map);
        cleanPathButton = (Button) findViewById(R.id.clean_path);
        decelerateButton = (Button) findViewById(R.id.decelerate);
    	accelerateButton = (Button) findViewById(R.id.accelerate);
        
    	// button listener
    	// Go
    	goButton.setOnClickListener(
    		new Button.OnClickListener(){
    			public void onClick(View v) {
					// run the algorithm
					game.runAlgorithm();
					goButton.setEnabled(false);
					cleanMapButton.setEnabled(false);
					cleanPathButton.setEnabled(false);
				}});
        
    	// clean Map
        cleanMapButton.setOnClickListener(
        	new Button.OnClickListener(){
    			public void onClick(View v) {
    				game.clearState();
    				game.clearMap();
    			}});
        
        //clean Path
        cleanPathButton.setOnClickListener(
            new Button.OnClickListener(){
    			public void onClick(View v) {
    				game.clearState();
    			}});
        
        // slowDown
        decelerateButton.setOnClickListener(
            new Button.OnClickListener(){
    			public void onClick(View v) {
    				game.slowDown();
    			}});
        
        // speedUp
        accelerateButton.setOnClickListener(
            new Button.OnClickListener(){
    			public void onClick(View v) {
    				game.speedUp();
    			}});
        
        // --------------------- dependency injection ---------------------- 
		// Call the dependency injection method
        this.initIoc();
    }
	
	//dependency injection method
    public void initIoc()
    {
    	gameView.game = this.game;
    	gameView.searchMethod = this.searchMethod;
    	gameView.pathLengthTextView = this.pathLengthTextView;
    	game.gameView = this.gameView;
    	game.goButton = this.goButton;
    	game.cleanMapButton = this.cleanMapButton;
    	game.cleanPathButton = this.cleanPathButton;	
    	game.StepsTextView = this.StepsTextView;
    }
}
