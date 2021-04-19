package org.tulipInd;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;




public class PrimaryController {

    public TextField skypeId;
    private Desktop Desktop;


    @FXML
    private void switchToSecondary() throws IOException {
        //App.setRoot("secondary");
        try {
            System.out.println ( "ouverture du microscope" );
            Runtime runTime = Runtime.getRuntime ();
            Process process = runTime.exec ( "C:\\Program Files\\S-Viewer\\S-Viewer\\x64\\S-Viewer.EXE" );
            /*try {
                Thread.sleep (50000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }*/
            //System.out.println ( "fermeture du microscope" );
            //process.destroy ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    // FBI Surveillance
                                    // Dossier a surveiller
                                    File dir = new File("\\\\TULIP-LOCAL\\Folder_Redirection\\assooba\\Documents\\S-Viewer\\Images");
                                    watchDirectoryPath(dir.toPath());
                                }finally{
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();



        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //
                    }
                });
            }
        });

        taskThread.start();

    }

    //FBI Methode pour listening le repository
    public static void watchDirectoryPath(Path path) {
        // Verifier si le chemin est un repertoire
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path
                        + " n'est pas un repertoire");
            }
        } catch (IOException ioe) {
            // Dossier n'exist pas
            ioe.printStackTrace();
        }

        System.out.println("Surveillance path: " + path);

        // Fichier systeme // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();

                // Dequeueing events
                WatchEvent.Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; // loop
                    } else if (ENTRY_CREATE == kind) {
                        // A new Path was created
                        Path newPath = ((WatchEvent<Path>) watchEvent)
                                .context();
                        // Output

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Nouvelle photo");
                        alert.setHeaderText("Enregistrement d'images dans le dossier patient");
                        alert.setContentText("Voulez vous enregistrer la photo");
                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                System.out.println("Pressed OKkkkk.");
                                System.out.println("New path created: " + newPath);
                            }
                        });

                        System.out.println("New path created: " + newPath);
                    } else if (ENTRY_MODIFY == kind) {
                        // modified
                        Path newPath = ((WatchEvent<Path>) watchEvent)
                                .context();
                        // Output
                        System.out.println("New path modified: " + newPath);
                    } else if (ENTRY_DELETE == kind) {
                        // Delect
                        Path newPath = ((WatchEvent<Path>) watchEvent)
                                .context();
                        // Output
                        System.out.println("New path deleted: " + newPath);
                    }
                }

                if (!key.reset()) {
                    break; // loop
                }
            }

        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
        }

    }



    //Skype boutton call
    public void Skype(ActionEvent actionEvent) throws IOException {

        try {
            Desktop.getDesktop().browse(new URI("skype:"+skypeId.getText()+"?call"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }







}
