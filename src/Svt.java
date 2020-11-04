import java.nio.file.Files;
import java.nio.file.Paths;

public class Svt {

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.print("Please specify command.\n");
            System.exit(1);
        } else if (args[0].equals("init")) {
            Commands.Init();
        } else if (Files.exists(Paths.get(".gvt"))) {
            switch (args[0]) {
                case "add" -> Commands.Add(args);
                case "detach" -> Commands.Detach(args);
                case "checkout" -> Commands.Checkout(args);
                case "history" -> Commands.History(args);
                case "version" -> Commands.Version(args);
                case "commit" -> Commands.Commit(args);
            }
        } else {
            System.out.print("Current directory is not initialized. Please use \"init\" command to initialize.\n");
            System.exit(-2);
        }
    }
}