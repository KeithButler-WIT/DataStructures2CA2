package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.opencsv.exceptions.CsvException;
import javafx.fxml.FXML;
import com.opencsv.*;

import static java.lang.Math.sqrt;

public class PrimaryController {

    @FXML
    private void initialize() {
        AdjacencyMatrix am = new AdjacencyMatrix(310); //Room for 8 nodes (could use larger!)
        loadStationsCSVFile(am);
        loadLinesCSVFile(am);
        run(am);
    }

    private void loadStationsCSVFile(AdjacencyMatrix am) {
        try {
//            Map<String, String> stations = new CSVReaderHeaderAware(new FileReader("/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/stations.csv")).readMap();
//            Map<String, String> lines = new CSVReaderHeaderAware(new FileReader("lines.csv")).readMap();
//            Map<String, String> routes = new CSVReaderHeaderAware(new FileReader("routes.csv")).readMap();

            //https://stackoverflow.com/a/12169103
            String strFile = "/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/stations.csv";
            CSVReader reader = new CSVReader(new FileReader(strFile));
            String[] nextLine;
            reader.readNext(); // Skips the header file
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                short stationId = Short.parseShort(nextLine[0]);
                double latitude = Double.parseDouble(nextLine[1]);
                double longitude = Double.parseDouble(nextLine[2]);
                String name = nextLine[3];
                GraphNode<String> node = new GraphNode<>(stationId, latitude, longitude, name, am);
//                System.out.println(node);
//                System.out.println();
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadLinesCSVFile(AdjacencyMatrix am) {
        // Connecting all the correct nodes together according to the lines.csv file
        try {
            //https://stackoverflow.com/a/12169103
            String strFile = "/home/keith/workspace/college/Semester 4 Repeat/Assignment02/src/main/java/org/example/lines.csv";
            CSVReader reader = new CSVReader(new FileReader(strFile));
            String [] nextLine;
            reader.readNext(); // Skips the header line
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                GraphNode<?> station1 = am.getNodeFromStationId(Short.parseShort(nextLine[0]));
                GraphNode<?> station2 = am.getNodeFromStationId(Short.parseShort(nextLine[1]));
                station1.connectToNodeUndirected(station2);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private void run(AdjacencyMatrix am) {
        short stationId = 294;

        // Ask the user for start and destination stations
        // Select the option the user wants
        // 1. Multiple route permutations between starting and destination stations.
        //    (Depth First Search)
//        System.out.println("All solution paths from West Ruislip to node containing South Ruislip");
//        System.out.println("------------------------------------------");
//        List<List<GraphNode<?>>> allPaths=findAllPathsDepthFirst(am.getNodeFromStationId(stationId),null,"West Acton");
//        int pCount=1;
//        for(List<GraphNode<?>> p : allPaths) {
//            System.out.println("\nPath "+(pCount++)+"\n--------");
//            int level = 0;
//            for(GraphNode<?> n : p) {
//                for (int i=0; i<level;i++)
//                    System.out.print("\t");
//                System.out.println(n.getStationName());
//                level++;
//            }
//        }

        // 2. Route with the fewest stations/stops between the starting and destination stations.
        //    (Breadth First Search)
        System.out.println("\nThe shortest solution path from West Ruislip to node containing South Ruislip");
        System.out.println("--------------------------------------------------------------");
        List<GraphNode<?>> bfsPath=findPathBreadthFirst(am.getNodeFromStationId(stationId),"West Acton");
        for(GraphNode<?> n : bfsPath) System.out.println(n.getStationName());

        // 3. Shortest route (in terms of distance travelled) between the starting and destination stations.
        //    (Dijkstra's algorithm)
        // 4. Shortest route (in terms of distance travelled) between the given starting and destination
        //    stations, but allowing a user-specified cost penalty to be applied for every time a traveller has
        //    to change lines/trains at a station.
        //    (Dijkstra's algorithm but cost on every line/train change)

//        System.out.println("\nAgenda based depth first traversal starting at Ruislip station.");
//        System.out.println("-------------------------------------------------");
//        List<GraphNode<?>> agenda=new ArrayList<>();
//        agenda.add(am.getNodeFromStationId(stationId));
//        traverseGraphDepthFirst(agenda,null);

        // Single valid route between two stations
        System.out.println("\nSolution path from West Ruislip to node containing South Ruislip");
        System.out.println("--------------------------------------------------------------");
        List<GraphNode<?>> path=findPathDepthFirst(am.getNodeFromStationId(stationId),null,"South Ruislip");
        for(GraphNode<?> n : path) System.out.println(n.getStationName());

        // Multiple valid route between two stations


//        System.out.println("The shortest solution path from Coconut (object f)");
//        System.out.println("to node containing Plum");
//        System.out.println("------------------------------------------");
//        List<List<GraphNode<?>>> allPaths2=findAllPathsDepthFirst(f,null,"Plum");
//        for(GraphNode<?> n : Collections.min(allPaths2,(p1, p2)->p1.size()-p2.size()) ) System.out.println(n.data);
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
        System.out.println(next.getStationName());
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(next);
        for(int i=0;i<next.mat.nodeCount;i++)
            if(next.mat.amat[next.nodeId][i] && !encountered.contains(next.mat.nodes[i]) &&
                    !agenda.contains(next.mat.nodes[i])) agenda.add( 0 ,next.mat.nodes[i]); //Add children to front of agenda (order unimportant here)
        traverseGraphDepthFirst(agenda, encountered ); //Tail call
    }

    // Recursive depth-first search of graph (first single path identified returned)
    public static <T> List<GraphNode<?>> findPathDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, String lookingfor) {
        List<GraphNode<?>> result;
        if (from.getStationName().equals(lookingfor)) { // Found it
            result=new ArrayList<>(); // Create new list to store the path info
            result.add(from); // Add the current node as the only/last entry in the path list
            return result; // Return the path list
        }
        if (encountered==null) encountered=new ArrayList<>(); // First node so create new (empty) encountered list
        encountered.add(from);

        for(int i=0;i<from.mat.nodeCount;i++) {
            if (from.mat.amat[from.nodeId][i] && !encountered.contains(from.mat.nodes[i])) {
                result=findPathDepthFirst(from.mat.nodes[i],encountered,lookingfor);
                if(result!=null) { // Result of the last recursive call contains a path to the solution node
                    result.add(0,from); // Add the current node to the front of the path list
                    return result; // Return the path list
                }
            }
        }
        return null;
    }

    //Recursive depth-first search of graph (all paths identified returned)
    public static <T> List<List<GraphNode<?>>> findAllPathsDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, String lookingfor){
        // TODO: ADD DEPTH LIMIT
        List<List<GraphNode<?>>> result=null, temp2;
        if(from.getStationName().equals(lookingfor)) { //Found it
            List<GraphNode<?>> temp=new ArrayList<>(); //Create new single solution path list
            temp.add(from); //Add current node to the new single path list
            result=new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from); //Add current node to encountered list
        for(int i=0;i<from.mat.nodeCount;i++) {
            if (from.mat.amat[from.nodeId][i] && !encountered.contains(from.mat.nodes[i])) {
                temp2=findAllPathsDepthFirst(from.mat.nodes[i],new ArrayList<>(encountered),lookingfor); //Use clone of encountered list for recursive call!
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

    //Interface method to allow just the starting node and the goal node data to match to be specified
    public static <T> List<GraphNode<?>> findPathBreadthFirst(GraphNode<?> startNode, String lookingfor){
        List<List<GraphNode<?>>> agenda=new ArrayList<>(); //Agenda comprised of path lists here!
        List<GraphNode<?>> firstAgendaPath=new ArrayList<>(),resultPath;
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        resultPath=findPathBreadthFirst(agenda,null,lookingfor); //Get single BFS path (will be shortest)
        Collections.reverse(resultPath); //Reverse path (currently has the goal node as the first item)
        return resultPath;
    }
    // Agenda list based breadth-first graph search returning a single reversed path (tail recursive)
    public static <T> List<GraphNode<?>> findPathBreadthFirst(List<List<GraphNode<?>>> agenda, List<GraphNode<?>> encountered, String lookingfor){
        if(agenda.isEmpty()) return null; //Search failed
        List<GraphNode<?>> nextPath=agenda.remove(0); //Get first item (next path to consider) off agenda
        GraphNode<?> currentNode=nextPath.get(0); //The first item in the next path is the current node
        if(currentNode.getStationName().equals(lookingfor)) return nextPath; //If that's the goal, we've found our path (so return it)
        if(encountered==null) encountered=new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered, so it isn't revisited again
        for(int i=0;i<currentNode.mat.nodeCount;i++) { // For each adjacent node
            if (currentNode.mat.amat[currentNode.nodeId][i] && !encountered.contains(currentNode.mat.nodes[i])) { // If it hasn't already been encountered
                List<GraphNode<?>> newPath = new ArrayList<>(nextPath); //Create a new path list as a copy of the current/next path
                newPath.add(0, currentNode.mat.nodes[i]); //And add the adjacent node to the front of the new copy
                agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        }
        return findPathBreadthFirst(agenda,encountered,lookingfor); //Tail call
    }

    private double getEuclideanDistance(int x1,int y1,int x2,int y2) {
        return sqrt(((x2-x1)^2) + ((y2-y1)^2));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
