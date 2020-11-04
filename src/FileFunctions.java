
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileFunctions {
    static String DESTINATION = ".gvt" + File.separator;

    public static void createDirectory(long src) {
        try {
            Files.createDirectory(Paths.get(createFilePath(src)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createVersionDirectory(long src, long dst, String fileName, String operation) {
        try {
            Files.createDirectory(Paths.get(createFilePath(dst)));
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
        copy(src, dst);
        switch (operation) {
            case "add":
            case "commit":
                addFile(fileName, dst);
                break;
        }
    }

    public static void addFile(String name, long dst) {
        try {
            Files.copy(Paths.get(name), Paths.get(createFilePath(dst) + File.separator + name), REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }


    public static void addHistory(long dst) {
        try {
            Files.copy(Paths.get(DESTINATION + "history.txt"), Paths.get(createFilePath(dst) + File.separator + "history.txt"));
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void updateAddedInfo() {
        try {
            long lastV = takeLastVersion();
            FileWriter fw = new FileWriter(DESTINATION + "info.txt");
            fw.write(("curr: " + (lastV + 1) + "\n" +
                    "last: " + (lastV + 1)));
            fw.close();
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void copy(long src, long dst) {
        try {
            Files.walk(Paths.get(FileFunctions.createFilePath(src))).forEach(f -> {
                try {
                    if (!(new File(f.toString())).isDirectory() && !(f.getFileName().toString().equals("history.txt")) && !(f.getFileName().toString().equals("version.txt"))) {
                        Files.copy(f, Paths.get(FileFunctions.createFilePath(dst) + File.separator + f.getFileName()));
                    }
                } catch (IOException e) {
                    System.out.print("Underlying system problem. See ERR for details.\n");
                    e.printStackTrace();
                    System.exit(-3);
                }
            });
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void toPreviousVersion(long src) {
        try {
            Files.walk(Paths.get(FileFunctions.createFilePath(src))).forEach(f -> {
                try {
                    if (!(new File(f.toString())).isDirectory() && !(f.getFileName().toString().equals("history.txt"))) {
                        Files.copy(f, Paths.get(f.getFileName().toString()), REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    System.out.print("Underlying system problem. See ERR for details.\n");
                    e.printStackTrace();
                    System.exit(-3);
                }
            });
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }


    public static void addMessageToTxt(String path, String txt, String message) throws IOException {
        var bw = new BufferedWriter(new FileWriter(path + File.separator + txt, true));
        PrintWriter out = new PrintWriter(bw);
        out.print(message);
        out.close();
        bw.close();
    }

    public static void addMessageToVersion(String path, String txt, String message) throws IOException {
        FileWriter fw = new FileWriter(path + File.separator + txt, false);
        fw.write(message);
        fw.close();
    }


    public static long takeCurrVersion() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(DESTINATION + "info.txt")));
            return Long.parseLong(br.readLine().split(": ")[1]);
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
        return -1;
    }

    public static long takeLastVersion() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(DESTINATION + "info.txt")));
            br.readLine();
            return Long.parseLong(br.readLine().split(": ")[1]);
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
        return -1;
    }


    public static String createMessage(String[] args) {
        String comment = "";
        if (args.length >= 3) {
            comment = "\n" + args[3];
        }
        switch (args[0]) {
            case "add":
                return "Added file: " + args[1] + comment;
            case "commit":
                return "Committed file: " + args[1] + comment;
            case "detach":
                return "Detached file: " + args[1] + comment;
        }
        return comment;
    }

    public static String createMessageWithoutComment(String[] args) {
        switch (args[0]) {
            case "add":
                return "Added file: " + args[1] + "\n";
            case "commit":
                return "Committed file: " + args[1] + "\n";
            case "detach":
                return "Detached file: " + args[1] + "\n";
        }
        return "";
    }


    public static void deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

    public static void displayHistory() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(DESTINATION + "history.txt")));
            String line;
            System.out.print(br.readLine() + "\n");
            while ((line = br.readLine()) != null) {
                System.out.print(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void displayLastHistory(long number) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(DESTINATION + "history.txt")));
            for (int i = 0; i < takeLastVersion() + 1 - number; i++) {
                br.readLine();
            }
            String line;
            while ((line = br.readLine()) != null) {
                System.out.print(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static void displayVersion(long number) {
        try {
            List<String> files = Files.readAllLines(Paths.get(createFilePath(number) + File.separator + "version.txt"));
            for (String s : files) {
                System.out.print(s);
            }
        } catch (IOException e) {
            System.out.print("Underlying system problem. See ERR for details.\n");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public static String createFilePath(long numberOfVersion) {
        return DESTINATION + "version" + numberOfVersion;
    }

}
