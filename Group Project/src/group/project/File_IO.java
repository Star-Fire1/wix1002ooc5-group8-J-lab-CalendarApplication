/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.project;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

public class File_IO {
    //Define the file path
    private static String eventPath="event.csv";
    private static String recurrentPath="recurrent.csv";
    private static String reminderPath="reminder.csv";
    private static String backupPath="backup.csv";
    
    //Cache the current maximum ID
    private static int maxId=0;
    private static boolean idLoaded=false;
    
    //Add "synchronized" to prevent conflicts in multi-threading.
    public synchronized void saveEvent(Event event){
        //Use object event to call its get method to obtain the attribute value
        String eventLine=event.getEventId()+"|"+event.getTitle()+"|"+event.getDescription()+"|"+event.getStartDateTime()+"|"+event.getEndDateTime()+"|"+event.getLocation()+"|"+event.getCategory();
        
        //Write "line" into the file
        try(FileWriter fw=new FileWriter(eventPath,true)){// true represents enabling append mode
            //Wrap it in a PrintWriter.
            PrintWriter pw=new PrintWriter(fw);
            pw.println(eventLine);
            
            //After the saving is successful, update the ID in the memory to ensure that the next acquisition is a new one.
            if(event.getEventId()>maxId){
                maxId=event.getEventId();
                idLoaded=true;
            }
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public int getId(){
        //// If the ID has already been loaded, simply return the next one stored in the memory.
        if(idLoaded){
            return maxId+1;
        }
        
        // Only when it is the first run or the file has not been loaded before, will the scanning of the file be carried out.
        int count=0;
        try{
            File f=new File(eventPath);
            if(!f.exists()){
                // The default maximum ID is 0. If the file does not exist, the next ID will be 1.
                return 1;
            }
            
            //Use try-with-resources to automatically close the Scanner
            try(Scanner s=new Scanner(f)){
                while(s.hasNextLine()){
                    String line=s.nextLine();
                    String[] eventParts=line.split("\\|");
                    if(Integer.parseInt(eventParts[0])>count){
                        count=Integer.parseInt(eventParts[0]);
                    }
                }
            }
        }catch(FileNotFoundException e){
            return 1;
        }
        
        // Update cache
        maxId=count;
        idLoaded=true;
        
        //Return to the next available ID
        return count+1;
    }
    
    public ArrayList<Event> readEvents(){
        ArrayList<Event> al=new ArrayList<>();//Create an empty box
        File f=new File(eventPath);
        
        // If the file does not exist, directly return an empty box to prevent errors.
        if(!f.exists()){
            return al;
        }
        
        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine();
                if(line.trim().isEmpty()){
                    continue;
                }else{
                    String[] eventParts=line.split("\\|");
                    if(eventParts.length>=7){
                        Event e=new Event();
                        e.setEventId(Integer.parseInt(eventParts[0]));
                        e.setTitle(eventParts[1]);
                        e.setDescription(eventParts[2]);
                        e.setStartDateTime(eventParts[3]);
                        e.setEndDateTime(eventParts[4]);
                        e.setLocation(eventParts[5]);
                        e.setCategory(eventParts[6]);
                        al.add(e);
                        
                        // Update ID cache
                        if(e.getEventId()>maxId){
                            maxId=e.getEventId();
                            idLoaded=true;
                        }
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
        
        // Return the fully-filled box
        return al;
    }
    
    public synchronized void saveRecurrent(int id, String interval, int times, String endTime){
        String eventLine=id+"|"+interval+"|"+times+"|"+endTime;
        
        //Write "line" into the file
        try(FileWriter fw=new FileWriter(recurrentPath,true)){// true represents enabling append mode
            //Wrap it in a PrintWriter.
            PrintWriter pw=new PrintWriter(fw);
            pw.println(eventLine);
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public void backupData(){
        try(FileWriter fw=new FileWriter(backupPath,false);PrintWriter pw=new PrintWriter(fw);){
            File e=new File(eventPath);
            if(e.exists()){
                Scanner ev=new Scanner(e);
                pw.println("Event");
                while(ev.hasNextLine()){
                    pw.println(ev.nextLine());
                }
                ev.close();
            }
            File r=new File(recurrentPath);
            if(r.exists()){
                Scanner re=new Scanner(r);
                pw.println();
                pw.println("Recurrent");
                while(re.hasNextLine()){
                    pw.println(re.nextLine());
                }
                re.close();
            }
        }catch(IOException i){
            System.out.println("Problem with file output.");
        }
    }
    
    public void rewriteEvents(ArrayList<Event> allEvents){
        try(FileWriter fw=new FileWriter(eventPath,false);PrintWriter pw=new PrintWriter(fw)){
            for(Event e: allEvents){
                String eventLine=e.getEventId()+"|"+e.getTitle()+"|"+e.getDescription()+"|"+e.getStartDateTime()+"|"+e.getEndDateTime()+"|"+e.getLocation()+"|"+e.getCategory();
                pw.println(eventLine);
            }
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public synchronized void saveReminder(Reminder reminder){
        String eventLine=reminder.getEventId()+"|"+reminder.getTime()+"|"+reminder.getMessage();
        try(FileWriter fw=new FileWriter(reminderPath,true);PrintWriter pw=new PrintWriter(fw)){
            pw.println(eventLine);
        }catch(IOException e){
            System.out.println("Problem with file output.");
        }
    }
    
    public ArrayList<Reminder> readReminders(){
        ArrayList<Reminder> al=new ArrayList<>();
        File f=new File(reminderPath);
        if(!f.exists()){
            return al;
        }
        try(Scanner s=new Scanner(f)){
            while(s.hasNextLine()){
                String line=s.nextLine();
                if(line.trim().isEmpty()){
                    continue;
                }else{
                    String[] reminderParts=line.split("\\|");
                    if(reminderParts.length>=3){
                        Reminder r=new Reminder();
                        r.setEventId(Integer.parseInt(reminderParts[0]));
                        r.setTime(reminderParts[1]);
                        r.setMessage(reminderParts[2]);
                        al.add(r);
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
        return al;
    }
    
    public void recoverData(){
        File f=new File(backupPath);
        if(!f.exists()){
            return;
        }
        try(Scanner s=new Scanner(f)){
            try(FileWriter e=new FileWriter(eventPath,false);PrintWriter ev=new PrintWriter(e)){
                try(FileWriter r=new FileWriter(recurrentPath,false);PrintWriter re=new PrintWriter(r)){
                    boolean check=false;
                    while(s.hasNextLine()){
                        String line=s.nextLine();
                        if(line.trim().isEmpty()){
                            continue;
                        }else if(line.equals("Event")){
                            check=false;
                        }else if(line.equals("Recurrent")){
                            check=true;
                        }else{
                            if(check){
                                re.println(line);
                            }else{
                                ev.println(line);
                            }
                        }
                    }
                }catch(IOException a){
                    System.out.println("Problem with file output.");
                }
            }catch(IOException b){
                System.out.println("Problem with file output.");
            }
        }catch(FileNotFoundException e){
            System.out.println("File was not found.");
        }
    }
}