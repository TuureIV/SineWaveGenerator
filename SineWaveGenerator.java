/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package sinewavegenerator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.* ;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*; 
import javafx.animation.*; 
import static javafx.application.Application.launch;
import javafx.util.Duration;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.collections.*; 


/**
 *
 * @author Tuure Vairio
 */
public class SineWaveGenerator extends Application
{   
    KeyCode key_press = KeyCode.EXCLAMATION_MARK;
    public double radius = 60;
    public double offset = 0;
    public int phases = 360;
    public int phase = 5;
    public double xPos = 0;
    public double yPos = 0;

 @Override

    public void start(Stage stage)
    {   
        //Määritetään scene ja pane
        StackPane stack_pane = new StackPane();
        Scene scene = new Scene(stack_pane, 900, 800);

        //Yksikköympyrän määritys
        Circle yksikkoCircle = new Circle (0, 0, radius, Color.ORANGE);
        yksikkoCircle.setStroke(Color.BLACK);
        yksikkoCircle.setOpacity(20);
        Circle siniPointer = new Circle (5);
        siniPointer.setStroke(Color.BLACK);
        siniPointer.setFill(Color.ORANGE);
        
        Path pathSine = new Path();
        
        //Säteen muodostus
        Line radLine = new Line();
        radLine.setStroke(Color.BLACK);
        //Nappuloiden muodostus
        Button reset_button = new Button ("Reset Pointer");
        Button start_button = new Button ("Start");
        Button stop_button = new Button ("Pause");
        ScrollBar radius_scrollbar = new ScrollBar() ;
        
        Image origo = new Image( "koordinaatisto.png" ) ;

        ImageView viewPort = new ImageView( origo ) ;
        viewPort.setX( 142 ) ;
        viewPort.setY( 3.5 ) ;

        //värilistan määritys
        ObservableList<String> color_names = FXCollections.observableArrayList
                                            (
                                             "Orange", "Yellow", "Green",
                                             "DeepPink", "Crimson", "Olive",
                                             "MediumSeaGreen", "Teal","Indigo"
                                            );
        ChoiceBox<String> color_selection_box = new ChoiceBox<String>();     
        color_selection_box.setItems(color_names);
        color_selection_box.setValue("Orange");
        Color[] selectable_colors =
                             {
                              Color.ORANGE, Color.YELLOW, Color.GREEN,
                              Color.DEEPPINK, Color.CRIMSON, Color.OLIVE,
                              Color.MEDIUMSEAGREEN, Color.TEAL, Color.INDIGO
                             };
        color_selection_box.getSelectionModel().selectedIndexProperty().addListener
        (
            (observable_value, value, new_value) ->
            {
                yksikkoCircle.setFill(selectable_colors[new_value.intValue()]);
                siniPointer.setFill(selectable_colors[new_value.intValue()]);
            }
        );
        
        //Nappuloiden hitbox
        HBox operations_pane = new HBox();
        operations_pane.getChildren().addAll(start_button, stop_button, reset_button, 
                                            color_selection_box);
        operations_pane.setAlignment(Pos.CENTER);
        operations_pane.setSpacing(15);
        operations_pane.setPadding ( new Insets(0,0,20,0));
        BorderPane border_pane = new BorderPane();
        border_pane.setBottom(operations_pane);
        border_pane.setLeft(radius_scrollbar);

        
        //määritetään komponentit pysymään paikallaan
        yksikkoCircle.setManaged( false );
        radLine.setManaged( false );
        viewPort.setManaged( false );
        siniPointer.setManaged(false);
        pathSine.setManaged(false);
        

        //Lisätään taustaväri
        stack_pane.setBackground(new Background(new BackgroundFill(Color.BEIGE,CornerRadii.EMPTY, Insets.EMPTY)));

        //Määritetään yksikköympyrän sijainti
        yksikkoCircle.setCenterX( scene.getWidth()/5-21); //hienosäätöä origolle
        yksikkoCircle.setCenterY( scene.getHeight()/2 + offset); //offset liikuttaa ympyrää Y-akselilla
        
        //Säteen sijainnin määritys
        double  lineX = yksikkoCircle.getCenterX();
        double  lineY = yksikkoCircle.getCenterY();
        radLine.setStartX(lineX-radius);
        radLine.setStartY(lineY);
        radLine.setEndX(lineX+radius);
        radLine.setEndY(lineY);
    
        //Siniaaltopolku
        pathSine.getElements().add(new MoveTo(lineX,lineY));
        for (int i = 0; i <phases*phase; i++)
                {
                    yPos = radius * Math.sin(2*Math.PI*i/phases)+offset+lineY;
                    xPos = 0.3*i+lineX;
                    pathSine.getElements().add(new LineTo(xPos,yPos));   
                 }
        //Aallon muodostaminen
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(6000*phase));
        pathTransition.setPath(pathSine);
        pathTransition.setNode(siniPointer);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(false);

        //Säteen Kääntäminen
        RotateTransition rt = new RotateTransition(Duration.millis(6000*phase),radLine);
        rt.setByAngle(360*phase);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.setAutoReverse(false);
        rt.setCycleCount(Timeline.INDEFINITE);
    
        
        //Määritetään liukusäätimen toiminnat ja sijainti
        radius_scrollbar.setOrientation( Orientation.VERTICAL );
        radius_scrollbar.setValue(yksikkoCircle.getRadius());
        radius_scrollbar.setValue(radius);
        radius_scrollbar.setBlockIncrement(10) ;
        radius_scrollbar.setMin(10);
        radius_scrollbar.setMax(250);
        radius_scrollbar.valueProperty().addListener
        (
            ( observable_value, value, new_value ) ->
            {
                yksikkoCircle.setRadius( radius_scrollbar.getValue());
                radLine.setStartX(lineX-radius_scrollbar.getValue());
                radLine.setEndX(lineX+radius_scrollbar.getValue());
                //Nollataan transisitionit
                rt.stop();
                rt.getNode().setRotate(0);
                pathTransition.stop();
                //lasketaan polku uudelleen
                pathSine.getElements().clear();
                pathSine.getElements().add(new MoveTo(lineX,lineY));
                for (int i = 0; i <phases*phase; i++)
                {   
                    yPos = radius_scrollbar.getValue() * Math.sin(2*Math.PI*i/phases)+lineY;
                    xPos = 0.3*i+lineX;
                    pathSine.getElements().add(new LineTo(xPos,yPos));   
                }
                pathTransition.play();
                rt.playFromStart();
                rt.play();
            }
        );
        //Näppäimistötapahtumat
        scene.setOnKeyPressed( ( KeyEvent event ) ->
        {
            if ( key_press == KeyCode.SPACE ){ /*STOP/START OR SOMETHING */}
            else if ( key_press  ==  KeyCode.UP ) {offset --;}
            else if ( key_press  ==  KeyCode.DOWN ) {offset ++;}
            
            key_press = event.getCode() ;
            yksikkoCircle.setCenterY(scene.getHeight()/2 + offset);
            pathSine.setTranslateY(offset);
            radLine.setEndY(scene.getHeight()/2 + offset);
            radLine.setStartY(scene.getHeight()/2 + offset);
        });
        //Nappien tapahtumat
        reset_button.setOnAction( ( ActionEvent event ) ->
        {   rt.getNode().setRotate(0);
            pathTransition.stop();
            pathTransition.play();
            rt.stop();
            rt.play();
            
                rt.getNode().setRotate(0);
        });
        start_button.setOnAction( ( ActionEvent event ) ->
        {
            pathTransition.play();
            rt.play();
        });
        stop_button.setOnAction( ( ActionEvent event ) ->
        {
            pathTransition.pause();
            rt.pause();
        });
        //Pakataan kaikki kasaan
        stack_pane.getChildren().addAll(yksikkoCircle,viewPort, border_pane, pathSine, radLine,siniPointer);
     
        stage.setTitle( "Yksikkoympyra" );
        stage.setScene(scene);
        stage.show();
      
   }

   public static void main( String[] command_line_parameters )
   {
      launch( command_line_parameters ) ;
   }
    
}
