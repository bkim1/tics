package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import node.Node;
import node.NodeController;
import object.SaveState;


public class SaveRunnable implements Runnable {
    private Node node;
    private NodeController nc;

    public SaveRunnable(Node node, NodeController nc) {
        this.node = node;
        this.nc = nc;
    }

    public void run() {
        String saveLoc = this.nc.getSaveLoc();
        File saveFile = new File(saveLoc);
        
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SaveState state = new SaveState(this.node);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            ObjectOutputStream objOutputStream = new ObjectOutputStream(fileOutputStream);
            objOutputStream.writeObject(state);
            objOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved the current state!");
    }
}