package idk.core.engine.drawers;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import idk.core.engine.HomeActivity;
import idk.core.engine.R;

public abstract class FileManager extends ArrayAdapter<File> {
    private String path;
    private File[] list;
    private ListView listView;
    private View root;
    public FileManager(@NonNull Context context,String path) {
        super(context, android.R.layout.simple_list_item_1);
        this.path = path;
        update();
        root = ViewGroup.inflate(context, R.layout.file_manager, null);
        listView = root.findViewById(R.id.file_manager_list);
        listView.setAdapter(this);
        root.findViewById(R.id.file_manager_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p = getParentPath();
                if(p != null){

                    refresh(p);
                }
            }
        });
    }

    public View getListView() {
        return root;
    }

    public String getPath() {
        return path;
    }
    public String getParentPath(){
        return new File(path).getParent().toString();
    }

    private void update(){
        list = new File(path).listFiles();
        List<File> files = new ArrayList<>();
        int g =0;
        for(int i=0;i < list.length;i++){
            if(list[i].isDirectory()){
                files.add(list[i]);
            }
        }
        for(int i=0;i < list.length;i++){
            if(!list[i].isDirectory()){
                if(filter(list[i])){
                    files.add(list[i]);
                }

            }
        }
        list = files.toArray(new File[0]);
    }
    public void refresh(String path){
        this.path = path;
        update();
        notifyDataSetChanged();
    }
    public abstract void onFileSelect(File file);
    public abstract boolean filter(File file);
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        File file = getItem(position);
        View view = null;
        if(file.isDirectory()){
            if(convertView != null && convertView.getTag() != null && convertView.getTag().equals("dir")){
                view = convertView;
            }else {
                view = ViewGroup.inflate(getContext(), R.layout.folder_item, null);
                view.setTag("dir");
            }
            TextView name  = view.findViewById(R.id.folder_item_name);
            TextView size  = view.findViewById(R.id.folder_num_item);
            name.setText(file.getName());
            size.setText(file.list().length+" items");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh(file.getAbsolutePath());
                }
            });

        }else {
            if(convertView != null && convertView.getTag() != null && convertView.getTag().equals("file")) {
                view = convertView;
            }else {
                view = ViewGroup.inflate(getContext(), R.layout.file_item, null);
                view.setTag("file");
            }
            TextView name  = view.findViewById(R.id.file_item_name);
            TextView size  = view.findViewById(R.id.file_size);
            ImageView img = view.findViewById(R.id.file_item_icon);
            String shortName = file.getName().toLowerCase();
            if(shortName.length() > 5){
                shortName.substring(shortName.length() - 5);
            }
            img.setImageResource(R.drawable.unknow_file);
            if(shortName.endsWith(".jpg")||shortName.endsWith(".png")||shortName.endsWith(".jpeg")){
                loadImag(file,img);
            }
            name.setText(file.getName());
            size.setText(formatFileSize(file.length()));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFileSelect(file);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    view.startDragAndDrop(ClipData.newRawUri("",null), new View.DragShadowBuilder(view), file, 0);
                    return true;
                }
            });
        }

        return view;
    }
    private void loadImag(File file, ImageView imf){
        HomeActivity.getCurrent().demonThread.tasks.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(),50,50);
                    HomeActivity.current.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imf.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e){

                }
            }
        });
    }

    @Override
    public int getCount() {
        return list.length;
    }
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return list[position];
    }
    public static String formatFileSize(long sizeInBytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        double size = sizeInBytes;
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format(Locale.ENGLISH, "%.1f %s", size, units[unitIndex]);
    }
}
