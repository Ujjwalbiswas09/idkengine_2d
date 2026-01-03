package idk.core.engine;

import java.util.Vector;

public class DemonThread extends Thread{
    public Vector<Runnable> tasks = new Vector<>();
    public boolean active =true;

    private static DemonThread instance;
    public static DemonThread getInstance(){
        if(instance == null){
            instance = new DemonThread();
            instance.start();
        }
        return instance;
    }

    @Override
    public void run() {
        while (active){
            if (!tasks.isEmpty()){
                Runnable run = tasks.remove(tasks.size()-1);
                try {
                    run.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
