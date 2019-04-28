// MainActivityFragment.java
// Fragment in which the DoodleView is displayed
package com.fernando.jogo_adivinha_figura;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivityFragment extends Fragment {
   private DoodleView doodleView; // handles touch events and draws
   private boolean dialogOnScreen = false;
   private String shape;

   public TextView tituloTextView;


    //called when Fragment's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);

      View view =
         inflater.inflate(R.layout.fragment_main, container, false);



      //setHasOptionsMenu(true); // this fragment has menu items to display

      // get reference to the DoodleView
      doodleView = (DoodleView) view.findViewById(R.id.doodleView);

      tituloTextView = view.findViewById(R.id.tituloTextView);

      tituloTextView.setText(doodleView.ShapeRandom());

      Toast.makeText(getContext(), ShapeRandom(), Toast.LENGTH_LONG).show();

      return view;
   }


   public String ShapeRandom(){

      String shape = "";
      int number = (int)(Math.random()*4);

      switch (number) {

         case 0:
            shape = "square";
            break;
         case 1:
            shape = "circle";
            break;
         case 2:
            shape = "rectangle";
            break;
         case 3:
            shape = "triangle";
            break;
      }
      return shape;
   }


   // returns the DoodleView
   public DoodleView getDoodleView() {
      return doodleView;
   }

   // indicates whether a dialog is displayed
   public void setDialogOnScreen(boolean visible) {
      dialogOnScreen = visible;
   }
}

