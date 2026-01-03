package idk.core.buildsrc;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyClass extends DefaultTask{
    @TaskAction
    public void runTask() {
        try {

            File file = new File(getProject().getProjectDir(), "\\src\\main\\java");
            ProcessBuilder builder = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add("cmd.exe");
            commands.add("/C"); // Execute the command and then terminate
            commands.add("dir");
            builder.command(commands);
            builder.start().waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("✅ Running MyTask in Android Studio (Java + Gradle KTS)");
    }
}