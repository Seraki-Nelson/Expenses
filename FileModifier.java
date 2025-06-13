package techTitans.mt.rh.sn.kv.bk;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileModifier {

    private final File StudentData = new File("Student_data.txt");
    private ArrayList<String> data = new ArrayList<>();

    public FileModifier() {
        if (!StudentData.exists()) {
            try {
                StudentData.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileModifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized boolean fileUpdater(StudentProfile obj) {
        boolean success = true;

        int index = tempFileRead(obj);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StudentData))) {
            // Overwrite entire file
            String newEntry = obj.getDate() + "#" + obj.getID() + "#" + obj.getName() + "#" + obj.getSurname() + "#" + obj.getMonthlyAmount();
            if (index != -1) {
                data.set(index, newEntry + "\n");
            } else {
                data.add(newEntry + "\n");
            }

            for (String entry : data) {
                bw.write(entry);
            }

        } catch (IOException ex) {
            success = false;
            Logger.getLogger(FileModifier.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    public String getAmount(int mode, StudentProfile obj) {
        int ind = tempFileRead(obj);
        StringBuilder result = new StringBuilder();

        if (mode == 3 && ind != -1) {
            result.append(data.get(ind));
        } else if (mode == 0) {
            for (String entry : data) {
                result.append(entry);
            }
        }

        return result.toString();
    }

    private int tempFileRead(StudentProfile obj) {
        int index = -1;
        data.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(StudentData))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line + "\n");
                String[] parts = line.split("#");
                if (parts.length >= 2) {
                    if (parts[0].startsWith(obj.getDate().toString().substring(0, 7)) && parts[1].equals(obj.getID())) {
                        index = data.size() - 1;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileModifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return index;
    }
}
