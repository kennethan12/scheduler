/** 
 * (c) Blake Bullwinkel, Kenneth An
 * Scheduler class that allows the user to schedule final exams
 * for the registrar so that no student has two exams at the same
 * time. Given a .txt file containing student names and student
 * courses, we provide the options to 1) print out a general exam
 * schedule that lists each course under a time slot, 2) print out
 * all courses with corresponding time slots in order of course name
 * and number (extension 1), and 3) print out individual exam schedules 
 * for each student (extension 2). 
 */ 

import structure5.*;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class Scheduler {

    Graph<String, Integer> conflictGraph;
    Vector<Vector<String>> schedule;
    Vector<Vector<String>> studentSchedule;

    /**
     * Constructor.
     */
    public Scheduler() {
	conflictGraph = new GraphListUndirected<String, Integer>();
	schedule = new Vector<Vector<String>>();
	studentSchedule = new Vector<Vector<String>>();
    }

    /**
     * Given the filename of a .txt file containing student
     * names and their courses, constructs an undirected graph
     * in which each course is represented by a vertex, and an
     * edge between two vertices indicates that at least one
     * student is taking both of those courses.
     * pre: filename references an appropriate .txt file
     * post: conflictGraph is constructed and returned
     */
    public Graph<String, Integer> buildGraph(String filename) {
	try {
	    File text = new File(filename);
	    Scanner sc = new Scanner(text);
	    while (sc.hasNextLine()) {
		String studentName = sc.nextLine();
		String firstCourse = sc.nextLine();
		String secondCourse = sc.nextLine();
		String thirdCourse = sc.nextLine();
		String fourthCourse = sc.nextLine();
		conflictGraph.add(firstCourse);
		conflictGraph.add(secondCourse);
		conflictGraph.add(thirdCourse);
		conflictGraph.add(fourthCourse);
		conflictGraph.addEdge(firstCourse, secondCourse, 0);
		conflictGraph.addEdge(secondCourse, thirdCourse, 0);
		conflictGraph.addEdge(thirdCourse, fourthCourse, 0);
		conflictGraph.addEdge(fourthCourse, firstCourse, 0);
		conflictGraph.addEdge(firstCourse, thirdCourse, 0);
		conflictGraph.addEdge(secondCourse, fourthCourse, 0);
		Vector<String> student = new Vector<String>();
		student.add(studentName);
		student.add(firstCourse);
		student.add(secondCourse);
		student.add(thirdCourse);
		student.add(fourthCourse);
		studentSchedule.add(student);
	    }
	}
	catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return null;
	}
	return conflictGraph;
    }

    /**
     * Returns a schedule vector in which each element 
     * contains a vector of all courses under the same
     * time slot. This course schedule is the result of
     * a greedy algorithm.
     * pre: none
     * post: the schedule vector corresponding to 
     * conflictGraph
     */
    public Vector<Vector<String>> buildSchedule() {
	Iterator<String> iter = conflictGraph.iterator();
	int visitedCount = 0;
	while (visitedCount < conflictGraph.size()) {
	    Vector<String> slot = new Vector<String>();
	    iter = conflictGraph.iterator();
	    while (iter.hasNext()) {
		String vertex = iter.next();
		if (!conflictGraph.isVisited(vertex)) {
		    boolean hasEdge = false;
		    for (int i = 0; i < slot.size(); i++) {
			if (conflictGraph.containsEdge(vertex, slot.get(i))) {
			    hasEdge = true;
			    break;
			}
		    }
		    if (!hasEdge) {
			slot.add(vertex);			    
			conflictGraph.visit(vertex);
			visitedCount++;
		    }    
		}
	    }
	    schedule.add(slot);
	}
	return schedule;
    }

    /**
     * Prints out a general exam schedule that lists each course
     * under a certain time slot by iterating through the
     * schedule vector returned by buildSchedule().
     * pre: schedule is a valid Vector of courses
     * post: a general exam schedule is printed out
     */
    public void printSchedule(Vector<Vector<String>> schedule) {
	for (int i = 0; i < schedule.size(); i++) {
	    System.out.print("Slot " + String.valueOf(i + 1) + ": ");
	    Iterator<String> iter = schedule.get(i).iterator();
	    while (iter.hasNext()) {
		System.out.print(iter.next() + " ");
	    }
	    System.out.println();
	}
    }

    /**
     * Prints out all courses and corresponding slot times in order
     * course name and number (extension 1).
     * pre: schedule is a valid Vector of courses
     * post: a final exam schedule ordered by course name and number
     * is printed out
     */
    public void printOrderedSchedule(Vector<Vector<String>> schedule) {
	Vector<Association<String, Vector<String> >> associationVector
	    = new Vector<Association<String, Vector<String>>>();
	Vector<String> slotVector = new Vector<String>();
	Association<String, Vector<String>> slotAssociation;
	for (int i = 0; i < schedule.size(); i++) {
	    slotVector = schedule.get(i);
	    slotAssociation = new Association("Slot "+String.valueOf(i+1), slotVector);
	    associationVector.add(slotAssociation);
	}
	OrderedVector<String> orderedSchedule = new OrderedVector<String>();
	for (int i = 0; i < associationVector.size(); i++) {
	    for (int j = 0; j < associationVector.get(i).getValue().size(); j++) {
		String s = "";
		s += associationVector.get(i).getValue().get(j) + ": ";
		s += associationVector.get(i).getKey();
		orderedSchedule.add(s);
	    }
	}
	Iterator<String> iter = orderedSchedule.iterator();
	while (iter.hasNext()) {
	    System.out.println(iter.next());
	}
    }

    /**
     * Prints out individual exam schedules for all students,
     * listing students in alphabetical order (extension 2).
     * pre: schedule is a valid Vector of courses
     * post: individual student exam schedules are printed out
     */ 
    public void printStudentSchedules(Vector<Vector<String>> schedule) {
	Vector<Association<String, String>> associationVector
            = new Vector<Association<String, String>>();
	Vector<String> slotVector = new Vector<String>();
        Association<String, String> slotAssociation;
        for (int i = 0; i < schedule.size(); i++) {
            slotVector = schedule.get(i);
	    for (int j = 0; j < slotVector.size(); j++) {
		slotAssociation = new Association(slotVector.get(j), "Slot "+String.valueOf(i+1));
		associationVector.add(slotAssociation);
	    }
        }
	Vector<String> studentSchedules = new Vector<String>();
	for (int i = 0; i < studentSchedule.size(); i++) {
	    String s = "";
	    Vector<String> student = studentSchedule.get(i);
	    s += student.get(0) + "\n";
	    for (int j = 1; j < student.size(); j++) {
		for (int k = 0; k < associationVector.size(); k++) {
		    if (associationVector.get(k).getKey().equals(student.get(j))) {
			s += associationVector.get(k).getKey() + ": "
			    + associationVector.get(k).getValue() + "\n";
			break;
		    }
		}
	    }
	    studentSchedules.add(s);
	}
	OrderedVector<String> orderedStudentSchedules = new OrderedVector<String>();
	Iterator<String> iter = studentSchedules.iterator();
	while (iter.hasNext()) orderedStudentSchedules.add(iter.next());
	Iterator<String> iter2 = orderedStudentSchedules.iterator();
	while (iter2.hasNext()) System.out.println(iter2.next());
    }
	
    public static void main(String args[]) {
	// We provide demos for the three main features outlined above using
	// the medium.txt file
	Scheduler demoScheduler = new Scheduler();
	demoScheduler.buildGraph("medium.txt");
	Vector<Vector<String>> demoSchedule = demoScheduler.buildSchedule();
	// DEMO 1 - printSchedule
	demoScheduler.printSchedule(demoSchedule);
	System.out.println();
	// DEMO 2 - printOrderedSchedule (extension 1)
	demoScheduler.printOrderedSchedule(demoSchedule);
	System.out.println();
	// DEMO 3 - printStudentSchedules (extension 2)
	demoScheduler.printStudentSchedules(demoSchedule);
    }
}
