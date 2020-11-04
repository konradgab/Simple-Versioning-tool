import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Commands {
    static String GVT = ".gvt" + File.separator;
    static String HISTORY = "history.txt";
    static String INFO = "info.txt";
    static String VERSION = "version.txt";

    public static void Init() {
        if (Files.exists(Paths.get(".gvt"))) {
            System.out.print("Current directory is already initialized.\n");
            System.exit(10);
        } else {
            try {
                Files.createDirectory(Paths.get(".gvt"));
            } catch (IOException e) {
                System.out.print("Underlying system problem. See ERR for details.\n");
                e.printStackTrace();
                System.exit(-3);
            }
            System.out.print("Current directory initialized successfully.\n");
            try {
                FileFunctions.addMessageToTxt(".gvt", HISTORY, "0: " + "GVT initialized.\n");
                FileFunctions.addMessageToTxt(".gvt", INFO, "curr: 0\n" + "last: 0");
                FileFunctions.createDirectory(0);
                FileFunctions.addMessageToVersion(FileFunctions.createFilePath(0), VERSION, """
                        Version: 0
                        GVT initialized.
                        """);
                FileFunctions.addHistory(0);

            } catch (IOException e) {
                System.out.print("Underlying system problem. See ERR for details.\n");
                e.printStackTrace();
                System.exit(-3);
            }
        }

    }

    public static void Add(String[] args) {
        if (args.length == 1) {
            System.out.println("Please specify file to add.\n");
            System.exit(20);
        }
        if (!(Files.exists(Paths.get(args[1])))) {
            System.out.print("File " + args[1] + " not found.\n");
            System.exit(21);
        }
        if ((Files.exists(Paths.get(FileFunctions.createFilePath(FileFunctions.takeCurrVersion()) + File.separator + args[1])))) {
            System.out.println("File " + args[1] + " already added.\n");
            return;
        }
        try {
            FileFunctions.addMessageToTxt(GVT, HISTORY, (FileFunctions.takeCurrVersion() + 1) + ": " + FileFunctions.createMessageWithoutComment(args));
            FileFunctions.createVersionDirectory(FileFunctions.takeCurrVersion(), FileFunctions.takeLastVersion() + 1, args[1], "add");
            FileFunctions.addMessageToVersion(FileFunctions.createFilePath(FileFunctions.takeLastVersion() + 1), VERSION, "Version: " + (FileFunctions.takeCurrVersion() + 1) + "\n" + FileFunctions.createMessage(args));
            FileFunctions.addHistory(FileFunctions.takeLastVersion() + 1);
            FileFunctions.updateAddedInfo();
            System.out.print("File " + args[1] + " added successfully.\n");
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void Detach(String[] args) {
        if (args.length == 1) {
            System.out.print("Please specify file to detach.\n");
            System.exit(30);
        }
        if (!(Files.exists(Paths.get(FileFunctions.createFilePath(FileFunctions.takeLastVersion()) + File.separator + args[1])))) {
            System.out.print("File " + args[1] + " is not added to gvt.\n");
            return;
        }
        try {
            FileFunctions.addMessageToTxt(GVT, HISTORY, (FileFunctions.takeLastVersion() + 1) + ": " + FileFunctions.createMessageWithoutComment(args));
            FileFunctions.createVersionDirectory(FileFunctions.takeCurrVersion(), FileFunctions.takeLastVersion() + 1, args[1], "detach");
            FileFunctions.addMessageToVersion(FileFunctions.createFilePath(FileFunctions.takeLastVersion() + 1),
                    "version.txt",
                    "Version: " + (FileFunctions.takeCurrVersion() + 1) +
                            "\n" + FileFunctions.createMessage(args));

            FileFunctions.addHistory(FileFunctions.takeLastVersion() + 1);
            FileFunctions.updateAddedInfo();
            FileFunctions.deleteFile(FileFunctions.createFilePath(FileFunctions.takeCurrVersion()) + File.separator + args[1]);
            System.out.print("File " + args[1] + " detached successfully.\n");
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }

    }

    public static void Checkout(String[] args) {
        long version = -1;
        try {
            version = Long.parseLong(args[1]);
            if (version > FileFunctions.takeLastVersion()) {
                System.out.print("Invalid version number: " + args[1] + ".\n");
                System.exit(40);
            }
        } catch (Exception e) {
            System.out.print("Invalid version number: " + args[1] + ".\n");
            System.exit(40);
        }
        FileFunctions.toPreviousVersion(version);
        System.out.print("Version " + args[1] + " checked out successfully.\n");

    }

    public static void Commit(String[] args) {
        if (args.length == 1) {
            System.out.print("Please specify file to commit.\n");
            System.exit(50);
        }
        if (!Files.exists(Paths.get(FileFunctions.createFilePath(FileFunctions.takeCurrVersion()) + File.separator + args[1]))) {
            System.out.print("File " + args[1] + " does not exist.\n");
            System.exit(51);
        }
        try {
            FileFunctions.addMessageToTxt(GVT, HISTORY, (FileFunctions.takeLastVersion() + 1) + ": " + FileFunctions.createMessageWithoutComment(args));
            FileFunctions.createVersionDirectory(FileFunctions.takeCurrVersion(), FileFunctions.takeLastVersion() + 1, args[1], "commit");
            FileFunctions.addMessageToVersion(FileFunctions.createFilePath(FileFunctions.takeLastVersion() + 1), "version.txt", "Version: " + (FileFunctions.takeCurrVersion() + 1) + "\n" + FileFunctions.createMessage(args));
            FileFunctions.addHistory(FileFunctions.takeLastVersion() + 1);
            FileFunctions.updateAddedInfo();
            System.out.print("File " + args[1] + " committed successfully.\n");
        } catch (IOException e) {
            System.out.print("File " + args[1] + " cannot be committed, see ERR for details.\n");
            e.printStackTrace();
            System.exit(-52);
        }
    }

    public static void History(String[] args) {
        if (args.length > 1) {
            long number;
            try {
                number = Long.parseLong(args[2]);
                if (FileFunctions.takeLastVersion() - number < 0) {
                    FileFunctions.displayHistory();
                } else {
                    FileFunctions.displayLastHistory(number);
                }
            } catch (Exception e) {
                FileFunctions.displayHistory();
            }
        } else {
            FileFunctions.displayHistory();
        }
    }

    public static void Version(String[] args) {
        if (args.length > 1) {
            long number;
            try {
                number = Long.parseLong(args[1]);
                if ((number >= 0 && number <= FileFunctions.takeLastVersion())) {
                    FileFunctions.displayVersion(number);
                } else {
                    System.out.print("Invalid version number: " + args[1] + ".\n");
                    System.exit(60);
                }
            } catch (Exception e) {
                System.out.print("Invalid version number: " + args[1] + ".\n");
                System.exit(60);
            }
        } else {
            FileFunctions.displayVersion(FileFunctions.takeLastVersion());
        }
    }

}


