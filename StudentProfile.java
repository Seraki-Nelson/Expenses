package techTitans.mt.rh.sn.kv.bk;

import java.time.LocalDate;
import java.util.ArrayList;

public class StudentProfile {

    private String ID, Name, Surname;
    private ArrayList<String> monthlyAmount;
    private LocalDate date;

    public StudentProfile(String ID, String Name, String Surname, LocalDate date) {
        this.ID = ID;
        this.Name = Name;
        this.Surname = Surname;
        this.date = date;
        this.monthlyAmount = new ArrayList<>();
    }

    // Getters & Setters
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getName() { return Name; }
    public void setName(String Name) { this.Name = Name; }

    public String getSurname() { return Surname; }
    public void setSurname(String Surname) { this.Surname = Surname; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public void upDateAmount(double[] amounts, LocalDate date) {
        StringBuilder entry = new StringBuilder(date.toString());
        for (double amount : amounts) {
            entry.append("^").append(amount);
        }

        boolean found = false;
        String monthYear = date.getYear() + "-" + String.format("%02d", date.getMonthValue());

        for (int i = 0; i < monthlyAmount.size(); i++) {
            String existing = monthlyAmount.get(i);
            if (existing.startsWith(monthYear)) {
                monthlyAmount.set(i, entry.toString());
                found = true;
                break;
            }
        }

        if (!found) {
            monthlyAmount.add(entry.toString());
        }
    }

    public String getMonthlyAmount() {
        StringBuilder money = new StringBuilder();
        for (String entry : monthlyAmount) {
            money.append(entry).append("@\n");
        }
        return money.toString();
    }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", Surname='" + Surname + '\'' +
                ", monthlyAmount=" + monthlyAmount +
                ", date=" + date +
                '}';
    }
}
