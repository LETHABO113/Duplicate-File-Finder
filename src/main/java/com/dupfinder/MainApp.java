package com.dupfinder;

import com.dupfinder.core.AppController;

/**
 * Program entry point.
 *
 * Owner: Member 1 (Ntokozo) - Project Lead / Integrator
 *
 * Usage:
 *   java -jar duplicate-file-finder.jar          -> launches CLI menu
 *   java -jar duplicate-file-finder.jar --gui     -> launches Swing GUI
 */
public class MainApp {

    public static void main(String[] args) {
        boolean useGui = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--gui")) {
                useGui = true;
            }
        }

        AppController controller = new AppController();

        if (useGui) {
            controller.startGui();
        } else {
            controller.startCli();
        }
    }
}
