package duplicatefilefinder;
public class MainApp {
    
    //operating modes
    public enum Mode {CLI, GUI}
    
    public static void main(String[] args){
        Logger logger = new Logger.getInstance();
        
        try {
            Mode mode = parseArgurments(args);
            logger.info("Starting duplicate file finde in " + mode + "mode");
            
            AppSettings settings = AppController.loadSettings();
            AppController controller = new AppController(settings);
            
            
            switch (mode){
                case GUI -> {
                    MainWindow window = new MainWindow(controller);
                    window.launch();
                }
                case CLI -> {
                    CLIMenu = menu = new CLIMenu(controller);
                    menu.launch();
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
        
    }
}
