package org.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import com.opencsv.*;

import static java.lang.Math.sqrt;

public class PrimaryController {

    @FXML
    private void initialize() {
        loadCSVFiles();
        run();
    }

    private void loadCSVFiles() {
        AdjacencyMatrix am = new AdjacencyMatrix(310); //Room for 8 nodes (could use larger!)
        try {
//            Map<String, String> stations = new CSVReaderHeaderAware(new FileReader("/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/stations.csv")).readMap();
//            Map<String, String> lines = new CSVReaderHeaderAware(new FileReader("lines.csv")).readMap();
//            Map<String, String> routes = new CSVReaderHeaderAware(new FileReader("routes.csv")).readMap();

            //https://stackoverflow.com/a/12169103
            String strFile = "/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/stations.csv";
            CSVReader reader = new CSVReader(new FileReader(strFile));
            String [] nextLine;
            reader.readNext(); // Skips the header file
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                short stationId = Short.parseShort(nextLine[0]);
                double latitude = Double.parseDouble(nextLine[1]);
                double longitude = Double.parseDouble(nextLine[2]);
                String name = nextLine[3];
                GraphNode<String> node = new GraphNode<>(stationId, latitude, longitude, name, am);
                System.out.println(node);
                System.out.println();
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        try {
            //https://stackoverflow.com/a/12169103
            String strFile = "/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/lines.csv";
            CSVReader reader = new CSVReader(new FileReader(strFile));
            String [] nextLine;
            reader.readNext(); // Skips the header file
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                short station1 = Short.parseShort(nextLine[0]);
                short station2 = Short.parseShort(nextLine[1]);
                am.getNodeFromStationId(station1).connectToNodeUndirected(am.getNodeFromStationId(station2));
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\nAgenda based depth first traversal starting at station 1");
        System.out.println("-------------------------------------------------");
        List<GraphNode<?>> agenda=new ArrayList<>();
        short station2 = 11;
        agenda.add(am.getNodeFromStationId(station2));
        traverseGraphDepthFirst(agenda,null);
    }

    private void run() {
        //Create undirected graph
//        AdjacencyMatrix am = new AdjacencyMatrix(8); //Room for 8 nodes (could use larger!)
//        GraphNode<String> a = new GraphNode<>("Cherry", am);
//        GraphNode<String> b = new GraphNode<>("Apple", am);
//        GraphNode<String> c = new GraphNode<>("Plum", am);
//        GraphNode<String> d = new GraphNode<>("Mango", am);
//        GraphNode<String> e = new GraphNode<>("Kiwi", am);
//        GraphNode<String> f = new GraphNode<>("Coconut", am);
//        GraphNode<String> g = new GraphNode<>("Pear", am);
//        GraphNode<String> h = new GraphNode<>("Orange", am);
//        a.connectToNodeUndirected(b); //Pairs either way to connect undirected
//        a.connectToNodeUndirected(c);
//        b.connectToNodeUndirected(c);
//        b.connectToNodeUndirected(g);
//        c.connectToNodeUndirected(d);
//        g.connectToNodeUndirected(e);
//        d.connectToNodeUndirected(e);
//        f.connectToNodeUndirected(e);
//        e.connectToNodeUndirected(h);
//
//        System.out.println("\nAgenda based depth first traversal starting at Coconut");
//        System.out.println("-------------------------------------------------");
//        List<GraphNode<?>> agenda=new ArrayList<>();
//        agenda.add(f);
//        traverseGraphDepthFirst(agenda,null);
//        System.out.println("\nAgenda based breadth first traversal starting at Coconut");
//        System.out.println("-------------------------------------------------");
//        agenda.add(f);
//        traverseGraphBreadthFirst(agenda,null);
//
//        System.out.println("\nSolution path from Coconut (object f) to node containing Plum");
//        System.out.println("--------------------------------------------------------------");
//        List<GraphNode<?>> path=findPathDepthFirst(f,null,"Plum");
//        for(GraphNode<?> n : path) System.out.println(n.data);
//
//        System.out.println("\nAll solution paths from Coconut (object f) to node containing Plum");
//        System.out.println("------------------------------------------");
//        List<List<GraphNode<?>>> allPaths=findAllPathsDepthFirst(f,null,"Plum");
//        int pCount=1;
//        for(List<GraphNode<?>> p : allPaths) {
//            System.out.println("\nPath "+(pCount++)+"\n--------");
//            for(GraphNode<?> n : p)
//                System.out.println(n.data);
//        }
//
//        System.out.println("The shortest solution path from Coconut (object f)");
//        System.out.println("to node containing Plum");
//        System.out.println("------------------------------------------");
//        List<List<GraphNode<?>>> allPaths2=findAllPathsDepthFirst(f,null,"Plum");
//        for(GraphNode<?> n : Collections.min(allPaths2,(p1, p2)->p1.size()-p2.size()) ) System.out.println(n.data);
//
    }

    //Agenda list based breadth-first graph traversal (tail recursive)
    public static void traverseGraphBreadthFirst(List<GraphNode<?>> agenda, List<GraphNode<?>> encountered ){
        if(agenda.isEmpty()) return;
        GraphNode<?> next=agenda.remove(0);
        System.out.println(next.getStationId());
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(next);
        for(int i=0;i<next.mat.nodeCount;i++)
            if(next.mat.amat[next.nodeId][i] && !encountered.contains(next.mat.nodes[i]) &&
                    !agenda.contains(next.mat.nodes[i])) agenda.add(next.mat.nodes[i]); //Add children to end of agenda
        traverseGraphBreadthFirst(agenda, encountered ); //Tail call
    }

    //Agenda list based depth-first graph traversal (tail recursive)
    public static void traverseGraphDepthFirst(List<GraphNode<?>> agenda, List<GraphNode<?>> encountered ){
        if(agenda.isEmpty()) return;
        GraphNode<?> next=agenda.remove(0);
        System.out.println(next.getStationId());
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(next);
        for(int i=0;i<next.mat.nodeCount;i++)
            if(next.mat.amat[next.nodeId][i] && !encountered.contains(next.mat.nodes[i]) &&
                    !agenda.contains(next.mat.nodes[i])) agenda.add( 0 ,next.mat.nodes[i]); //Add children to front of agenda (order unimportant here)
        traverseGraphDepthFirst(agenda, encountered ); //Tail call
    }

    // Recursive depth-first search of graph (first single path identified returned)
    public static <T> List<GraphNode<?>> findPathDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor) {
        List<GraphNode<?>> result;
        if (from.data.equals(lookingfor)) { // Found it
            result=new ArrayList<>(); // Create new list to store the path info
            result.add(from); // Add the current node as the only/last entry in the path list
            return result; // Return the path list
        }
        if (encountered==null) encountered=new ArrayList<>(); // First node so create new (empty) encountered list
        encountered.add(from);

        for (GraphNode<?> adjNode : from.mat.nodes) {
            if (!encountered.contains(adjNode)) {
                result=findPathDepthFirst(adjNode,encountered,lookingfor);
                if(result!=null) { // Result of the last recursive call contains a path to the solution node
                    result.add(0,from); // Add the current node to the front of the path list
                    return result; // Return the path list
                }
            }
        }
        return null;
    }

    //Recursive depth-first search of graph (all paths identified returned)
    public static <T> List<List<GraphNode<?>>> findAllPathsDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor){
        List<List<GraphNode<?>>> result=null, temp2;
        if(from.data.equals(lookingfor)) { //Found it
            List<GraphNode<?>> temp=new ArrayList<>(); //Create new single solution path list
            temp.add(from); //Add current node to the new single path list
            result=new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from); //Add current node to encountered list
        for(GraphNode<?> adjNode : from.mat.nodes){
            if(!encountered.contains(adjNode)) {
                temp2=findAllPathsDepthFirst(adjNode,new ArrayList<>(encountered),lookingfor); //Use clone of encountered list
//for recursive call!
                if(temp2!=null) { //Result of the recursive call contains one or more paths to the solution node
                    for(List<GraphNode<?>> x : temp2) //For each partial path list returned
                        x.add(0,from); //Add the current node to the front of each path list
                    if(result==null) result=temp2; //If this is the first set of solution paths found use it as the result
                    else result.addAll(temp2); //Otherwise append them to the previously found paths
                }
            }
        }
        return result;
    }

    // First child and next sibling references only
    public static void traverseDepthFirst(TreeNode<?> root) {
        TreeNode<?> temp=root.firstChild;
        while(temp!=null) {
            System.out.println(temp.data+" is a child of "+root.data);
            traverseDepthFirst(temp);
            temp=temp.nextSibling;
        }
    }

    public static <T> TreeNode<T> searchDepthFirst(TreeNode<T> root, T lookingfor) {
        if (root.data.equals(lookingfor)) return root;
        TreeNode<T> temp=root.firstChild;
        while(temp!=null) {
            TreeNode<T> result=searchDepthFirst(temp,lookingfor);
            if (result!=null) return result;
            temp=temp.nextSibling;
        }
        return null;
    }

    // Iterative breadth-first traversal method
    public static <T> void traverseBreadthFirst(List<TreeNode<T>> agenda) {
        while(!agenda.isEmpty()) {
            TreeNode<T> nextNode=agenda.remove(0), temp=nextNode.firstChild;
            System.out.println(nextNode.data);
            while(temp!=null) {
                agenda.add(temp);
                temp=temp.nextSibling;
            }
        }
    }

    // First child and next sibling references only (iterative)
    public static <T> TreeNode<T> searchBreadthFirst(List<TreeNode<T>> agenda, T lookingfor) {
        while(!agenda.isEmpty()) {
            TreeNode<T> nextNode=agenda.remove(0), temp=nextNode.firstChild;
            if(nextNode.data.equals(lookingfor)) return nextNode;
            while(temp!=null) {
                agenda.add(temp);
                temp=temp.nextSibling;
            }
        }
        return null;
    }

    private double getEuclideanDistance(int x1,int y1,int x2,int y2) {
        return sqrt(((x2-x1)^2) + ((y2-y1)^2));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
