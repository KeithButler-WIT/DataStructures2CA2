package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.exceptions.CsvException;
import javafx.fxml.FXML;
import com.opencsv.*;

import static java.lang.Math.sqrt;

public class PrimaryController {

    @FXML
    private void initialize() {
        AdjacencyMatrix am = new AdjacencyMatrix(310); //Room for 310 nodes (could use larger!)
        loadStationsCSVFile(am);
        loadLinesCSVFile(am);
        mainMenu(am);
    }

    private void loadStationsCSVFile(AdjacencyMatrix am) {
        try {
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
//                short zone = Short.parseShort(nextLine[5]);
                GraphNode<?> node = new GraphNode<>(stationId, latitude, longitude, name, am);
                node.setNodeValue(Integer.MAX_VALUE);
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
                station1.connectToNodeUndirected(station2, (int) (getEuclideanDistance(station1.getLongitude(), station1.getLatitude(), station2.getLongitude(), station2.getLatitude())*1000));
//                System.out.println(getEuclideanDistance(station1.getLongitude(), station1.getLatitude(), station2.getLongitude(), station2.getLatitude())*10000);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private void printMainMenu() {
        System.out.println("------------------------");
        System.out.println("Main Menu");
        System.out.println("------------------------");
        System.out.println("1: Multiple Paths");
        System.out.println("2: Fewest Stations");
        System.out.println("3: Shortest Route");
        System.out.println("4: Shortest Route with cost for line change");
        System.out.println("------------------------");
    }

    private void mainMenu(AdjacencyMatrix am) {
        while (true) {
            //TODO: uncomment scanner.nextLines()
            Scanner scanner = new Scanner(new InputStreamReader(System.in));
            System.out.println("\nSelect a start station:");
//            String stationId = scanner.nextShort();   // make sure it's a short
            String startName = scanner.nextLine();
//            String startName = "West Ruislip";
            System.out.println("Select a destination station:");
            String destName = scanner.nextLine();
//            String destName = "South Ruislip";
            printMainMenu();
            System.out.println("Select an option:");
            int choice = scanner.nextInt();   // make sure it's an int
            switch (choice) {
                case 0: System.exit(0);
                    break;
                case 1: multiplePaths(am, startName, destName);
                    break;
                case 2: fewestStations(am, startName, destName);
                    break;
                case 3: shortestRoute(am, startName, destName);
                    break;
                case 4: shortestRouteUserChoice(am, startName, destName);
                    break;
                default: System.out.println("Type a number between 0-4");
                    break;
            }
        }
    }

    private void multiplePaths(AdjacencyMatrix am, String startName, String destName) {
        // 1. Multiple route permutations between starting and destination stations.
        //    (Depth First Search)
        System.out.println("All solution paths from " + startName + " to node containing " + destName);
        System.out.println("------------------------------------------");
        List<List<GraphNode<?>>> allPaths=findAllPathsDepthFirst(am.getNodeFromStationName(startName),null,destName,0);
        int pCount=1;
        for(List<GraphNode<?>> p : allPaths) {
            System.out.println("\nPath "+(pCount++)+"\n--------");
            int level = 0;
            for(GraphNode<?> n : p) {
                for (int i=0; i<level;i++) // Adds a tab to the output depending on level of depth
                    System.out.print("\t");
                System.out.println(n.getStationName());
                level++;
            }
        }
    }
    private void fewestStations(AdjacencyMatrix am, String startName, String destName) {
        ArrayList<String> waypointList = getWaypoints(startName,destName);
        ArrayList<String> avoidingList = getStationsToAvoid();

        List<GraphNode<?>> bfsPathTotal = new ArrayList<>(Collections.emptyList());
//        for (String waypoint : waypointList) {
        for (int i = 0; i<waypointList.size()-1; i++) {
            List<GraphNode<?>> bfsPath;
            bfsPath=findPathBreadthFirst(am.getNodeFromStationName(waypointList.get(i)), waypointList.get(i+1), avoidingList);
            bfsPathTotal = Stream.concat(bfsPathTotal.stream(), bfsPath.stream()).collect(Collectors.toList()); // add the currently found path to the overall list
        }


        // 2. Route with the fewest stations/stops between the starting and destination stations. (Breadth First Search)
        System.out.println("\nThe shortest solution path from " + startName + " to node containing " + destName);
        System.out.println("-----------------------------------------------------------------------------");
        for(GraphNode<?> n : bfsPathTotal) System.out.println(n.getStationName());
    }
    private void shortestRoute(AdjacencyMatrix am, String startName, String destName) {
        // 3. Shortest route (in terms of distance travelled) between the starting and destination stations.
        //    (Dijkstra's algorithm)
        System.out.println("The cheapest path from "+startName+" to "+destName+" using Dijkstra's algorithm");
        System.out.println("-----------------------------------------------------------------------------");
        CostedPath cpa=findCheapestPathDijkstra(am.getNodeFromStationName(startName),destName);
        for(GraphNode<?> n : cpa.pathList)
            System.out.println(n.getStationName());
        System.out.println("\nThe total path cost is: "+cpa.pathCost);
    }
    private void shortestRouteUserChoice(AdjacencyMatrix am, String startName, String destName) {
        // 4. Shortest route (in terms of distance travelled) between the given starting and destination
        //    stations, but allowing a user-specified cost penalty to be applied for every time a traveller has
        //    to change lines/trains at a station.
        //    (Dijkstra's algorithm but cost on every line/train change)
    }

    private ArrayList<String> getWaypoints(String startName, String destName) {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Input a list of waypoints");
        System.out.println("Leave blank to stop putting in waypoints");
        ArrayList<String> waypointList = new ArrayList<>();
        waypointList.add(startName); // makes the start point the first waypoint
        String waypointName = "";
        do {
            System.out.println("Next waypoint:");
            // ask the user to input waypoints if none ask for destination only
            waypointName = scanner.nextLine();
            waypointList.add(waypointName);
        } while(!Objects.equals(waypointName, ""));

        waypointList.remove(waypointList.size()-1); // Removes the empty/last object
        waypointList.add(destName); // makes the destination the last waypoint

        return waypointList;
    }
    private ArrayList<String> getStationsToAvoid() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Input a list of stations to avoid");
        System.out.println("Leave blank to stop putting in stations");
        ArrayList<String> avoidingList= new ArrayList<>();
        String stationName = "";
        do {
            System.out.println("Next station:");
            // ask the user to input stations
            stationName = scanner.nextLine();
            avoidingList.add(stationName);
        } while(!Objects.equals(stationName, ""));

        avoidingList.remove(avoidingList.size()-1); // Removes the empty/last object

        return avoidingList;
    }

    //Agenda list based breadth-first graph traversal (tail recursive)
    public static void traverseGraphBreadthFirst(List<GraphNode<?>> agenda, List<GraphNode<?>> encountered ){
        if(agenda.isEmpty()) return;
        GraphNode<?> next=agenda.remove(0);
        System.out.println(next.getStationName());
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(next);
        for(int i=0;i<next.mat.nodeCount;i++)
            if(next.mat.amat[next.nodeId][i] > 0 && !encountered.contains(next.mat.nodes[i]) &&
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
            if(next.mat.amat[next.nodeId][i] > 0 && !encountered.contains(next.mat.nodes[i]) &&
                    !agenda.contains(next.mat.nodes[i])) agenda.add( 0 ,next.mat.nodes[i]); //Add children to front of agenda (order unimportant here)
        traverseGraphDepthFirst(agenda, encountered ); //Tail call
    }

    // Recursive depth-first search of graph (first single path identified returned)
    public static <T> List<GraphNode<?>> findPathDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, String lookingFor) {
        List<GraphNode<?>> result;
        if (from.getStationName().equals(lookingFor)) { // Found it
            result=new ArrayList<>(); // Create new list to store the path info
            result.add(from); // Add the current node as the only/last entry in the path list
            return result; // Return the path list
        }
        if (encountered==null) encountered=new ArrayList<>(); // First node so create new (empty) encountered list
        encountered.add(from);

        for(int i=0;i<from.mat.nodeCount;i++) {
            if (from.mat.amat[from.nodeId][i] > 0 && !encountered.contains(from.mat.nodes[i])) {
                result=findPathDepthFirst(from.mat.nodes[i],encountered,lookingFor);
                if(result!=null) { // Result of the last recursive call contains a path to the solution node
                    result.add(0,from); // Add the current node to the front of the path list
                    return result; // Return the path list
                }
            }
        }
        return null;
    }

    //Recursive depth-first search of graph (all paths identified returned)
    public static <T> List<List<GraphNode<?>>> findAllPathsDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, String lookingFor, int depth){
        List<List<GraphNode<?>>> result=null, temp2;

        if(from.getStationName().equals(lookingFor)) { //Found it
            List<GraphNode<?>> temp=new ArrayList<>(); //Create new single solution path list
            temp.add(from); //Add current node to the new single path list
            result=new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }

        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from); //Add current node to encountered list

        for(int i=0;i<from.mat.nodeCount;i++) {
            if (depth>=50) continue; // stops the current path if it goes on for too long
            if (from.mat.amat[from.nodeId][i] > 0 && !encountered.contains(from.mat.nodes[i])) {
//                System.out.println("Valid node found");
//                for (int j=0; j<depth;j++) // Adds a tab to the output depending on level of depth
//                    System.out.print("\t");
//                System.out.println(from.mat.nodes[i].getStationName());
                temp2=findAllPathsDepthFirst(from.mat.nodes[i],new ArrayList<>(encountered),lookingFor, ++depth); //Use clone of encountered list for recursive call!
                if(temp2!=null) { //Result of the recursive call contains one or more paths to the solution node
//                    if (temp2.size() >= 100) continue;
                    for(List<GraphNode<?>> x : temp2) { //For each partial path list returned
                        x.add(0, from); //Add the current node to the front of each path list
                    }
                    if(result==null) result=temp2; //If this is the first set of solution paths found use it as the result
                    else result.addAll(temp2); //Otherwise append them to the previously found paths
                }
            }
        }
        return result;
    }

    //Interface method to allow just the starting node and the goal node data to match to be specified
    public static <T> List<GraphNode<?>> findPathBreadthFirst(GraphNode<?> startNode, String lookingFor, ArrayList<String> toAvoidList){
        List<List<GraphNode<?>>> agenda=new ArrayList<>(); //Agenda comprised of path lists here!
        List<GraphNode<?>> firstAgendaPath=new ArrayList<>(),resultPath;
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        resultPath=findPathBreadthFirst(agenda,null,lookingFor, toAvoidList); //Get single BFS path (will be shortest)
        if (resultPath != null) {
            Collections.reverse(resultPath); //Reverse path (currently has the goal node as the first item)
        }
        return resultPath;
    }
    // Agenda list based breadth-first graph search returning a single reversed path (tail recursive)
    public static <T> List<GraphNode<?>> findPathBreadthFirst(List<List<GraphNode<?>>> agenda, List<GraphNode<?>> encountered, String lookingFor, ArrayList<String> toAvoidList){
        if(agenda.isEmpty()) return null; //Search failed
        List<GraphNode<?>> nextPath=agenda.remove(0); //Get first item (next path to consider) off agenda
        GraphNode<?> currentNode=nextPath.get(0); //The first item in the next path is the current node
        if(currentNode.getStationName().equals(lookingFor)) return nextPath; //If that's the goal, we've found our path (so return it)
        if(encountered==null) encountered=new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered, so it isn't revisited again
        for(int i=0;i<currentNode.mat.nodeCount;i++) { // For each adjacent node
            if (toAvoidList.contains(currentNode.mat.nodes[i].getStationName())) continue;
            if (currentNode.mat.amat[currentNode.nodeId][i] > 0 && !encountered.contains(currentNode.mat.nodes[i])) { // If it hasn't already been encountered
                List<GraphNode<?>> newPath = new ArrayList<>(nextPath); //Create a new path list as a copy of the current/next path
                newPath.add(0, currentNode.mat.nodes[i]); //And add the adjacent node to the front of the new copy
                agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        }
        return findPathBreadthFirst(agenda,encountered,lookingFor); //Tail call
    }
    // TODO: remove duplicate methods
    //Interface method to allow just the starting node and the goal node data to match to be specified
    public static <T> List<GraphNode<?>> findPathBreadthFirst(GraphNode<?> startNode, String lookingFor){
        List<List<GraphNode<?>>> agenda=new ArrayList<>(); //Agenda comprised of path lists here!
        List<GraphNode<?>> firstAgendaPath=new ArrayList<>(),resultPath;
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        resultPath=findPathBreadthFirst(agenda,null,lookingFor); //Get single BFS path (will be shortest)
        if (resultPath != null) {
            Collections.reverse(resultPath); //Reverse path (currently has the goal node as the first item)
        }
        return resultPath;
    }
    // Agenda list based breadth-first graph search returning a single reversed path (tail recursive)
    public static <T> List<GraphNode<?>> findPathBreadthFirst(List<List<GraphNode<?>>> agenda, List<GraphNode<?>> encountered, String lookingFor){
        if(agenda.isEmpty()) return null; //Search failed
        List<GraphNode<?>> nextPath=agenda.remove(0); //Get first item (next path to consider) off agenda
        GraphNode<?> currentNode=nextPath.get(0); //The first item in the next path is the current node
        if(currentNode.getStationName().equals(lookingFor)) return nextPath; //If that's the goal, we've found our path (so return it)
        if(encountered==null) encountered=new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered, so it isn't revisited again
        for(int i=0;i<currentNode.mat.nodeCount;i++) { // For each adjacent node
            if (currentNode.mat.amat[currentNode.nodeId][i] > 0 && !encountered.contains(currentNode.mat.nodes[i])) { // If it hasn't already been encountered
                List<GraphNode<?>> newPath = new ArrayList<>(nextPath); //Create a new path list as a copy of the current/next path
                newPath.add(0, currentNode.mat.nodes[i]); //And add the adjacent node to the front of the new copy
                agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        }
        return findPathBreadthFirst(agenda,encountered,lookingFor); //Tail call
    }

    //New class to hold a CostedPath object i.e. a list of GraphNode objects and a total cost attribute
    public static class CostedPath{
        public int pathCost=0;
        public List<GraphNode<?>> pathList=new ArrayList<>();
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNode<?> startNode, String lookingFor){
        CostedPath cp=new CostedPath(); //Create result object for cheapest path
        List<GraphNode<?>> encountered=new ArrayList<>(), unencountered=new ArrayList<>(); //Create encountered/unencountered lists
        startNode.setNodeValue(0); //Set the starting node value to zero
        unencountered.add(startNode); //Add the start node as the only value in the unencountered list to start
        GraphNode<?> currentNode;
        do{ //Loop until unencountered list is empty
            currentNode=unencountered.remove(0); //Get the first unencountered node (sorted list, so will have the lowest value)
            encountered.add(currentNode); //Record current node in encountered list
            if(currentNode.getStationName().equals(lookingFor)){ //Found goal - assemble path list back to start and return it
//                System.out.println("found goal");
                cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
                cp.pathCost=currentNode.getNodeValue(); //The total cheapest path cost is the node value of the current/goal node
                while(currentNode!=startNode) { //While we're not back to the start node...
//                    System.out.println("While Looping");
                    boolean foundPrevPathNode=false; //Use a flag to identify when the previous path node is identified
                    for(GraphNode<?> n : encountered) { //For each node in the encountered list...
//                        System.out.println("For each node in encountered list");

                        for(int i=0;i<n.mat.nodeCount;i++) { //For each edge from that node...
//                            System.out.println("For each edge from that node");
//                            if (encountered.contains(n.mat.nodes[i])) continue; // Checks if destination node already visited
//                            if (n.mat.nodes[i] != currentNode) continue; // dest node and current node are not the same
                            // The difference in node values is the cost of the edge
                            if (currentNode.getNodeValue()-n.mat.amat[n.nodeId][i] != n.getNodeValue()) continue; // nodeValue-edgeCost != destNodeValue
                            if (n.mat.amat[n.nodeId][currentNode.nodeId] <= 0) continue; // If that edge links to the current node

                            // -> found path node!
                            System.out.println("found path node");
                            System.out.println("Cost: " + n.mat.amat[n.nodeId][i]);
                            cp.pathList.add(0, n); //Add the identified path node to the front of the result list
                            currentNode = n; //Move the currentNode reference back to the identified path node
                            foundPrevPathNode = true; //Set the flag to break the outer loop
                            break; //We've found the correct previous path node and moved the currentNode reference
                            //back to it so break the inner loop
                        }
                        if(foundPrevPathNode) break; //We've identified the previous path node, so break the inner loop to continue
                    }
                }
                //Reset the node values for all nodes to (effectively) infinity, so we can search again (leave no footprint!)
                for(GraphNode<?> n : encountered) n.setNodeValue(Integer.MAX_VALUE);
                for(GraphNode<?> n : unencountered) n.setNodeValue(Integer.MAX_VALUE);
                return cp; //The costed (cheapest) path has been assembled, so return it!
            }
            //We're not at the goal node yet, so...
            for(int i=0;i<currentNode.mat.nodeCount;i++) //For each edge/link from the current node...
                if(!encountered.contains(currentNode.mat.nodes[i])) { //If the node it leads to has not yet been encountered (i.e. processed)
                    currentNode.mat.nodes[i].nodeValue=Integer.min(currentNode.mat.nodes[i].getNodeValue(), currentNode.nodeValue+currentNode.mat.amat[currentNode.nodeId][i]); //Update the node value at the end
                    //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
                    if(!unencountered.contains(currentNode.mat.nodes[i])) unencountered.add(currentNode.mat.nodes[i]);
                }
            Collections.sort(unencountered, Comparator.comparingInt(n -> n.getNodeValue())); //Sort in ascending node value order
        }while(!unencountered.isEmpty());
        return null; //No path found, so return null
    }

    private double getEuclideanDistance(double x1,double y1,double x2,double y2) {
        return sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
