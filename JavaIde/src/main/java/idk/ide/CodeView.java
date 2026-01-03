package idk.ide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeView extends EditText {

    private static final Pattern PATTERN_SINGLE_LINE_COMMENT = Pattern.compile("//[^\\n]*");
    private static final Pattern PATTERN_MULTI_LINE_COMMENT = Pattern.compile("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/");
    private static final Pattern PATTERN_ANNOTATION = Pattern.compile("@.[a-zA-Z0-9]+");
    private static final Pattern PATTERN_KEYWORDS = Pattern.compile("\\b(extends|implements|instanceof|null|strictfp|this)\\b");
    private static final Pattern PATTERN_MODIFIER = Pattern.compile("\\b(abstract|default|private|native|public|protected|final|static|volatile|transient|synchronized)\\b");
    private static final Pattern PATTERN_STATEMENT = Pattern.compile("\\b(if|continue|try|throw|else|catch|finally|for|while|do|import|switch|case|new|package|super)\\b");
    private static final Pattern PATTERN_PRIMITIVE= Pattern.compile("\\b(enum|int|short|char|boolean|float|double|void|class|byte|long|interface)\\b");
    private static final Pattern PATTERN_STRING = Pattern.compile("[\"](.*?)[\"]");
    private static final Pattern PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b");
    private static final Pattern PATTERN_CHAR = Pattern.compile("['](.*?)[']");
    private static final Pattern PATTERN_BUILTINS = Pattern.compile("[,:;[->]{}()]");
    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\.[a-zA-Z0-9_]+");
    private static final Pattern PATTERN_OPERATION =Pattern.compile( ":|==|>|<|!=|>=|<=|->|=|>|<|%|-|-=|%=|\\+|\\-|\\-=|\\+=|\\^|\\&|\\|::|\\?|\\*");
    private static final Pattern PATTERN_GENERIC = Pattern.compile("<[a-zA-Z0-9,<>]+>");
    private List<Integer> errorNumber = new ArrayList<>();
    private List<Integer> warnNumber = new ArrayList<>();
    private Handler handler;
    private Sythx[] sythxes;
    public String name="";
    private TextPaint linePaint;
    private boolean complete = true;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float minTextSize = 12f;
    private float maxTextSize = 100f;
    private float originalTextSize;
    public CodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inti();
    }

    public CodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti();
    }

    public CodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    public CodeView(Context context) {
        super(context);
        inti();
    }
    private int lines =0;
    @Override
    protected void onTextChanged(CharSequence text, int stt, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, stt, lengthBefore, lengthAfter);
        try {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateSpan();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Editable editable = getEditableText();

            if(complete) {
                complete = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void inti(){
        sythxes = new Sythx[]{
                sythx(PATTERN_KEYWORDS,Color.rgb(1,1,150)),
                sythx(PATTERN_BUILTINS,Color.rgb(150,10,10)),
                sythx(PATTERN_STATEMENT,Color.rgb(51,51,150)),
                sythx(PATTERN_MODIFIER,Color.rgb(1,50,100)),
                sythx(PATTERN_NUMBERS,Color.rgb(50,100,150)),
                sythx(PATTERN_PRIMITIVE,Color.rgb(150,51,51)),
                sythx(PATTERN_GENERIC,Color.rgb(10,100,10))
        };
        setBackgroundColor(Color.TRANSPARENT);
        linePaint = new TextPaint();
        originalTextSize = getTextSize();
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
                float newTextSize = originalTextSize * scaleFactor;
                newTextSize = Math.max(minTextSize, Math.min(newTextSize, maxTextSize));

                setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
                return true;
            }
        });
    }

    public List<Integer> getWarningNumber() {
        return warnNumber;
    }

    public List<Integer> getErrorNumber() {
        return errorNumber;
    }

    public void updateSpan(){
        Editable editable = getEditableText();
        StyleSpan[] spans =  editable.getSpans(0,editable.length(), StyleSpan.class);
        for(StyleSpan span : spans){
            editable.removeSpan(span);
        }
        ForegroundColorSpan[] fspans =  editable.getSpans(0,editable.length(), ForegroundColorSpan.class);
        for(ForegroundColorSpan span : fspans){
            editable.removeSpan(span);
        }
        Vector<SythxSpan> spanVector = new Vector<>();
        AtomicInteger integer = new AtomicInteger(0);
        Thread t1 = new Thread(()->{
            int i=0;
            try {
                while ((i = integer.getAndIncrement()) < sythxes.length) {
                    SythxSpan[] spans1 = sythxes[i].perform(editable.toString());
                    for (SythxSpan sythxSpan : spans1) {
                        spanVector.add(sythxSpan);
                    }
                }
            }catch (Exception e){e.printStackTrace();}
        });
        t1.start();

        Thread t2 = new Thread(()->{
            int i=0;
            try{
                while((i=integer.getAndIncrement()) < sythxes.length){
                    SythxSpan[] spans1 = sythxes[i].perform(editable.toString());
                    for(SythxSpan sythxSpan : spans1){
                        spanVector.add(sythxSpan);
                    }
                }
            }catch (Exception e){e.printStackTrace();}
        });
        t2.start();

        Thread t3 = new Thread(()-> {
            Matcher matcher1;
            matcher1 = PATTERN_ANNOTATION.matcher(editable);
            while (matcher1.find()) {
                int start = matcher1.start();
                int end = matcher1.end();
                if (end > 0 && start > 0 && start != end) {
                    StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
                    spanVector.add(SythxSpan.create(span, start, end, 33));
                    spanVector.add(SythxSpan.create(new ForegroundColorSpan(Color.rgb(130, 100, 0)), start, end, 33));
                }
            }
            matcher1 = PATTERN_STRING.matcher(editable);
            while (matcher1.find()) {
                int start = matcher1.start();
                int end = matcher1.end();
                StyleSpan span = new StyleSpan(Typeface.BOLD);
                if (end > 0 && start > 0 && start != end) {
                    spanVector.add(SythxSpan.create(new ForegroundColorSpan(Color.rgb(30, 100, 30)), start, end, 0));
                    spanVector.add(SythxSpan.create(span, start, end, 33));
                }
            }
            matcher1 = PATTERN_CHAR.matcher(editable);
            while (matcher1.find()) {
                int start = matcher1.start();
                int end = matcher1.end();
                if (end > 0 && start > 0 && start != end) {
                    StyleSpan span = new StyleSpan(Typeface.BOLD);
                    spanVector.add(SythxSpan.create(new ForegroundColorSpan(Color.rgb(30, 130, 30)), start, end, 0));
                    spanVector.add(SythxSpan.create(span, start, end, 33));
                }
            }
            matcher1 = PATTERN_SINGLE_LINE_COMMENT.matcher(editable);
            while (matcher1.find()) {
                int strt = matcher1.start();
                int end = matcher1.end();
                StyleSpan span = new StyleSpan(Typeface.ITALIC);
                if (end > 0 && strt > 0 && strt != end) {
                    spanVector.add(SythxSpan.create(new ForegroundColorSpan(Color.rgb(100, 100, 100)), strt, end, 0));
                    spanVector.add(SythxSpan.create(span, strt, end, 0));
                }
            }
            matcher1 = PATTERN_MULTI_LINE_COMMENT.matcher(editable);
            while (matcher1.find()) {
                int strt = matcher1.start();
                int end = matcher1.end();
                StyleSpan span = new StyleSpan(Typeface.ITALIC);
                if (end > 0 && strt > 0 && strt != end) {
                    spanVector.add(SythxSpan.create(new ForegroundColorSpan(Color.rgb(100, 100, 100)), strt, end, 0));
                    spanVector.add(SythxSpan.create(span, strt, end, 0));
                }
            }
        });
        t3.start();
        try {
            t1.join();
        }catch (Exception e){ }
        try {
            t2.join();
        }catch (Exception e){ }
        try {
            t3.join();
        }catch (Exception e){ }
        for(SythxSpan span : spanVector){
            span.set(editable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Layout lay = getLayout();
        setPadding((int)getPaint().getTextSize()+30,0,0,0);
        if(lines != lay.getLineCount()){
            lines = lay.getLineCount();
            setMinLines(lines+100);
        }
        int lineHeight = getLineHeight() + ((int) getLineSpacingExtra());
        int lineCount = lay.getLineForVertical(getScrollY() + getHeight()) + 1; //getLineCount();
        int visible_count = getHeight() / lineHeight;
        if (lineCount < visible_count) {
            lineCount = visible_count;
        }
        for (int i = lay.getLineForVertical(getScrollY()); i < lineCount; i++) {
            int line = i + 1;
            int baseline = (lineHeight * line);
            TextPaint paint = getPaint();
            int color = paint.getColor();
            if(errorNumber.contains(line)) {
                paint.setColor(Color.rgb(200, 0, 0));
                canvas.drawText(String.valueOf(line), 10 - getTranslationX(), baseline, paint);
                paint.setColor(color);
            }else if(warnNumber.contains(line)){
                paint.setColor(Color.rgb(130, 100, 0));
                canvas.drawText(String.valueOf(line), 10 - getTranslationX(), baseline, paint);
                paint.setColor(color);
            }else {
                canvas.drawText(String.valueOf(line), 10-getTranslationX(), baseline, paint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    private Sythx sythx(Pattern pat,int c){
        Sythx sythx = new Sythx();
        sythx.pattern = pat;
        sythx.spanColor = c;
        return sythx;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
    }

    class Sythx{
        Pattern pattern;
        int spanColor;
        SythxSpan[] perform(CharSequence data){
            Matcher matcher = pattern.matcher(data);
            List<SythxSpan> sythxSpanList = new ArrayList<>();
            while (matcher.find()){
                SythxSpan span = new SythxSpan();
                span.span = new ForegroundColorSpan(spanColor);
                span.start = matcher.start();
                span.end = matcher.end();
                if(span.end > 0 && span.start > 0 && span.start != span.end) {
                    sythxSpanList.add(span);
                }
            }
            return sythxSpanList.toArray(new SythxSpan[0]);
        }
    }
    static class SythxSpan{
        Object span;
        int start;
        int end;
        int flag=33;
        void set(Editable editable){
            editable.setSpan(span,start,end,flag);
        }
        static SythxSpan create(Object obj,int st, int en,int fl){
            SythxSpan sythxSpan = new SythxSpan();
            sythxSpan.end =en;
            sythxSpan.start = st;
            sythxSpan.span = obj;
            sythxSpan.flag = fl;
            return sythxSpan;
        }
    }
}
