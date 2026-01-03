package idk.core.engine.drawers;

import android.app.Activity;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class LogCatView {
    private ListView loglist;
    private ArrayAdapter<String> adapter;
    private ParcelFileDescriptor in;
    private ParcelFileDescriptor out;
    private LinearLayout l;
    private Activity activity;
    private Thread thread;
    public LogCatView(Activity context){
        activity = context;
        l = new LinearLayout(context);
        loglist = new ListView(context);
        l.addView(loglist, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        loglist.setAdapter(adapter);

        try {
            ParcelFileDescriptor[] tmp = ParcelFileDescriptor.createPipe();
            in = tmp[0];
            out = tmp[1];
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
        thread = new Thread(){
            @Override
            public void run() {
                try {
                    if(in == null || out == null){
                        return;
                    }
                    System.setOut(new PrintStream(new ParcelFileDescriptor.AutoCloseOutputStream(out),true));
                    BufferedReader dis = new BufferedReader(new InputStreamReader(
                            new ParcelFileDescriptor.AutoCloseInputStream(in)));
                    String str =null;
                    while((str = dis.readLine()) !=null){
                        addConsoleLine(str);
                    }
                    //Log.e("status","Close");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    public View getView(){
        return l;
    }
    public void addConsoleLine(String str){
        Log.v("System.out",str);
          activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter.getCount() >= 500) {
                        adapter.remove(adapter.getItem(0));
                    }
                    adapter.add(str);
                    adapter.notifyDataSetChanged();
                    loglist.smoothScrollToPosition(adapter.getCount());
                }
            });

    }

   private void stop(){
        thread.interrupt();
   }
}
