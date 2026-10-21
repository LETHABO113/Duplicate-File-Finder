package duplicatefilefinder;

import javax.swing.SwingUtilities;

public class MainApp {
    
    
    public enum Mode {CLI, GUI}
    
    public static void main(String[] args){
        Logger logger = Logger.getInstance();
        
        try {
            Mode mode = parseArguments(args);
            logger.info("Starting duplicate file finde in " + mode + "mode");
            
            AppSettings settings = AppController.loadSettings();
            AppController controller = new AppController(settings);
            
            
            switch (mode){
                case GUI -> {
                    SwingUtilities.invokeLater(() -> new Main().setVisible(true));
                }
                case CLI -> {
                    new CLIMenu(controller).launch();
                }
            }
            
            logger.info("Application exited normally");
        }catch (Exception e){
            logger.error("Error during startup", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    static Mode parseArguments(String[] args){
        if (args == null ||  args.length == 0){
            return Mode.CLI;
        }
        for (String arg : args){
            if ("--gui".equalsIgnoreCase(arg)){
                return Mode.GUI;
            }
            if("--cli".equalsIgnoreCase(arg)){
                return Mode.CLI;
            }
        }
        return Mode.CLI;
    }
}
