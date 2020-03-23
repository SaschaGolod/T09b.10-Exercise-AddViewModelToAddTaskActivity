package com.example.android.todolist.database;

import com.example.android.todolist.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task")
public class TaskEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    //name
    private String name;
    private int priority;
    private String tags;
    private String status; //x = do, f = flash, t = top, p = projekt
    private String score;
    private String stoneColor;
    private String startWall;
    private String endWall;
    private String kategorieWall;
    private String setter;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;


    @Ignore
    public TaskEntry(String name, int priority, String tags, String status, String score, String stoneColor, String startWall, String endWall, String setter, Date updatedAt) {
        this.name = name;
        this.priority = priority;
        this.tags = tags;
        this.status = status;
        this.score = score;
        this.stoneColor = stoneColor;
        this.startWall = startWall;
        this.endWall = endWall;
        this.kategorieWall = setKategorieWall();
        this.setter = setter;
        this.updatedAt = updatedAt;
    }

    public TaskEntry(int id, String name, int priority, String tags, String status, String score, String stoneColor, String startWall, String endWall, String setter, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.tags = tags;
        this.status = status;
        this.score = score;
        this.stoneColor = stoneColor;
        this.startWall = startWall;
        this.endWall = endWall;
        this.kategorieWall = setKategorieWall();
        this.setter = setter;
        this.updatedAt = updatedAt;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public String getSetter() {
        return setter;
    }

    public String getKategorieWall() {
        return kategorieWall;
    }

    public String getEndWall() {
        return endWall;
    }

    public void setEndWall(String endWall) {
        this.endWall = endWall;
    }

    public void setStartWall(String startWall) {
        this.startWall = startWall;
    }

    public String getStartWall() {
        return startWall;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setKategorieWall(String kategorieWall) {
        this.kategorieWall = kategorieWall;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setStoneColor(String stoneColor) {
        this.stoneColor = stoneColor;
    }

    public String getTags() {
        return tags;

    }

    public String getStatus() {
        return status;
    }

    public String getScore() {
        return score;
    }

    public String getStoneColor() {
        return stoneColor;
    }

    public String setKategorieWall() {
        //Todo keine priorität HardCode entfernen
        List<String> kategorie = new ArrayList<>();
        kategorie.add("Amulett");
        kategorie.add("Beule");
        kategorie.add("Drehblock");
        kategorie.add("Komet");
        kategorie.add("Pyramide");
        kategorie.add("Turm");
        kategorie.add("Kurve");
        kategorie.add("Würfel");
        for (int position = 0; position < kategorie.size(); position++) {
            if (startWall.contains(kategorie.get(position))) {
                return kategorie.get(position);
            }
        }
        return "Fehler";

    }

    /*
Die Namen von den Strings werden im Strings.xml unter mStoneColors gespeichert. Die Farben werden in der colors.xml unter der
StoneColor gespeichert.
 */
    public int getImageColor() {
        if (stoneColor.equals("weiß")) {
            return R.color.Stone_weiß;
        }
        if (stoneColor.equals("gelb")) {
            return R.color.Stone_gelb;
        }
        if (stoneColor.equals("hellgruen")) {
            return R.color.Stone_hellgruen;
        }
        if (stoneColor.equals("hellblue")) {
            return R.color.Stone_hellblue;
        }
        if (stoneColor.equals("blue")) {
            return R.color.Stone_blue;
        }
        if (stoneColor.equals("rot")) {
            return R.color.Stone_rot;
        }
        if (stoneColor.equals("schwarzer")) {
            return R.color.Stone_schwarzer;
        }
        if (stoneColor.equals("lila")) {
            return R.color.Stone_lila;
        }
        if (stoneColor.equals("orange")) {
            return R.color.Stone_orange;
        }
        if (stoneColor.equals("pink")) {
            return R.color.Stone_pink;
        }

        return R.color.materialBlue;
    }
}
