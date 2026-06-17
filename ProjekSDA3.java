import java.util.*;

public class ProjekSDA3 {

    static String[] edgeFrom   = new String[256];
    static String[] edgeTo     = new String[256];
    static int[]    edgeWeight = new int[256];
    static int[]    edgeOneWay = new int[256];
    static int      edgeCount  = 0;

    static class MinHeap {
        String[] name = new String[1024];
        int[]    dist = new int[1024];
        int size = 0;

        void insert(String building, int distance) {
            name[size] = building;
            dist[size] = distance;
            size++;
            int i = size - 1; // 1
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (dist[i] < dist[parent]) {
                    swap(i, parent);
                    i = parent;
                } else break;
            }
        }

        int peekDist() { return dist[0]; }

        String extractMin() {
            String result = name[0];
            size--;
            name[0] = name[size];
            dist[0] = dist[size];
            int i = 0;
            while (true) {
                int left = 2*i+1, right = 2*i+2, smallest = i;
                if (left  < size && dist[left]  < dist[smallest]) smallest = left;
                if (right < size && dist[right] < dist[smallest]) smallest = right;
                if (smallest == i) break;
                swap(i, smallest);
                i = smallest;
            }
            return result;
        }

        boolean isEmpty() { return size == 0; }

        private void swap(int a, int b) {
            String tmpName = name[a]; name[a] = name[b]; name[b] = tmpName;
            int    tmpDist = dist[a]; dist[a] = dist[b]; dist[b] = tmpDist;
        }
    }

    static class UnionFind {
        String[] keys    = new String[512];
        String[] parents = new String[512];
        int count = 0;

        void register(String b) {
            for (int i = 0; i < count; i++) if (keys[i].equals(b)) return;
            keys[count] = b; parents[count] = b; count++;
        }

        String find(String b) {
            register(b);
            for (int i = 0; i < count; i++) {
                if (keys[i].equals(b)) {
                    if (!parents[i].equals(b))
                        parents[i] = find(parents[i]);
                    return parents[i];
                }
            }
            return b;
        }

        void union(String a, String b) {
            String rootA = find(a), rootB = find(b);
            if (!rootA.equals(rootB)) {
                for (int i = 0; i < count; i++)
                    if (keys[i].equals(rootA)) { parents[i] = rootB; return; }
            }
        }

        boolean connected(String a, String b) { return find(a).equals(find(b)); }
    }

    static void addEdge(String from, String to, int weight, int oneWay, boolean silent) {
        int w = 0;
        for (int i = 0; i < edgeCount; i++) {
            boolean sameForward = edgeFrom[i].equals(from) && edgeTo[i].equals(to);
            boolean sameReverse = edgeFrom[i].equals(to)   && edgeTo[i].equals(from)
                                  && edgeOneWay[i] == 0    && oneWay == 0;
            if (!sameForward && !sameReverse) {
                edgeFrom[w]   = edgeFrom[i];
                edgeTo[w]     = edgeTo[i];
                edgeWeight[w] = edgeWeight[i];
                edgeOneWay[w] = edgeOneWay[i];
                w++;
            }
        }
        edgeCount = w;

        edgeFrom[edgeCount]   = from;
        edgeTo[edgeCount]     = to;
        edgeWeight[edgeCount] = weight;
        edgeOneWay[edgeCount] = oneWay;
        edgeCount++;

        if (!silent) {
            String arrow = (oneWay == 1) ? " -> " : " <-> ";
            System.out.println("Edge added: " + from + arrow + to + " (" + weight + ")");
        }
    }

    static void printAllEdges() {
        System.out.println("\n=========================================");
        System.out.println("        Program Navigasi Kampus");
        System.out.println("=========================================");
        if (edgeCount == 0) { System.out.println("(No edges yet)"); return; }
        HashSet<String> Building = new HashSet<>();
        for (int i = 0; i < edgeCount; i++) {
            Building.add(edgeFrom[i]);
        }

        int counter = 0;
        for (String x : Building) {
            System.out.print(x + ", ");
            counter++;

            if (counter == 5) {
                System.out.print("\n");
                counter = 0; 
            }
        }
        
        
        System.out.println("\n--- Semua Gedung yang ada ---");
        

    }

    static void printGraph() {
        System.out.println("\n=========================================");
        System.out.println("            Graph Gedung Kampus");
        System.out.println("=========================================");
        if (edgeCount == 0) { System.out.println("(No edges yet)"); return; }
        for (int i = 0; i < edgeCount; i++) {
            String arrow = (edgeOneWay[i] == 1) ? " =====> " : " <=====> ";
            System.out.println("  " + edgeFrom[i] + arrow + edgeTo[i] + " [Weight: " + edgeWeight[i] + "]");
        }
    }
    static void findMST() {
        if (edgeCount == 0) { System.out.println("No edges in the graph."); return; }

        // Copy edges into temp arrays so the original graph is untouched
        String[] tmpFrom   = new String[edgeCount];
        String[] tmpTo     = new String[edgeCount];
        int[]    tmpWeight = new int[edgeCount];
        int[]    tmpOneWay = new int[edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            tmpFrom[i]   = edgeFrom[i];
            tmpTo[i]     = edgeTo[i];
            tmpWeight[i] = edgeWeight[i];
            tmpOneWay[i] = edgeOneWay[i];
        }

        for (int i = 1; i < edgeCount; i++) {
            String sf = tmpFrom[i], st = tmpTo[i], sow = String.valueOf(tmpOneWay[i]);
            int sw = tmpWeight[i];
            int j = i - 1;
            while (j >= 0 && tmpWeight[j] > sw) {
                tmpFrom[j+1]   = tmpFrom[j];
                tmpTo[j+1]     = tmpTo[j];
                tmpWeight[j+1] = tmpWeight[j];
                tmpOneWay[j+1] = tmpOneWay[j];
                j--;
            }
            tmpFrom[j+1]   = sf;
            tmpTo[j+1]     = st;
            tmpWeight[j+1] = sw;
            tmpOneWay[j+1] = Integer.parseInt(sow);
        }

        UnionFind groups = new UnionFind();
        int total = 0;
        System.out.println("\n=== MST Result - Minimum Connections (Kruskal) ===");
        for (int i = 0; i < edgeCount; i++) {
            if (!groups.connected(tmpFrom[i], tmpTo[i])) {
                groups.union(tmpFrom[i], tmpTo[i]);
                System.out.println("  " + tmpFrom[i] + " -- " + tmpTo[i] + " == " + tmpWeight[i]);
                total += tmpWeight[i];
            }
        }
        System.out.println("Total MST Weight: " + total + "\n");
    }

    static void findShortestPath(String start, String dest) {
        boolean found = false;
        for (int i = 0; i < edgeCount; i++)
            if (edgeFrom[i].equals(start) || edgeTo[i].equals(start)) { found = true; break; }
        if (!found) { System.out.println("Start building not found."); return; }

        String[] distKey = new String[256];
        int[]    distVal = new int[256];
        int      distCount = 0;

        String[] prevKey = new String[256];
        String[] prevVal = new String[256];
        int      prevCount = 0;

        distKey[0] = start; distVal[0] = 0; distCount = 1;

        MinHeap queue = new MinHeap();
        queue.insert(start, 0);

        while (!queue.isEmpty()) {
            int    curDist     = queue.peekDist();
            String curBuilding = queue.extractMin();

            if (curBuilding.equals(dest)) break;

            int knownDist = Integer.MAX_VALUE;
            for (int i = 0; i < distCount; i++)
                if (distKey[i].equals(curBuilding)) { knownDist = distVal[i]; break; }

            if (curDist > knownDist) continue;

            for (int i = 0; i < edgeCount; i++) {
                String neighbour = null;
                if      (edgeFrom[i].equals(curBuilding))                         neighbour = edgeTo[i];
                else if (edgeTo[i].equals(curBuilding) && edgeOneWay[i] == 0)     neighbour = edgeFrom[i];
                if (neighbour == null) continue;

                int neighbourDist = Integer.MAX_VALUE;
                for (int j = 0; j < distCount; j++)
                    if (distKey[j].equals(neighbour)) { neighbourDist = distVal[j]; break; }

                int newDist = knownDist + edgeWeight[i];
                if (newDist < neighbourDist) {
                    boolean updated = false;
                    for (int j = 0; j < distCount; j++)
                        if (distKey[j].equals(neighbour)) { distVal[j] = newDist; updated = true; break; }
                    if (!updated) { distKey[distCount] = neighbour; distVal[distCount] = newDist; distCount++; }

                    boolean prevUpdated = false;
                    for (int j = 0; j < prevCount; j++)
                        if (prevKey[j].equals(neighbour)) { prevVal[j] = curBuilding; prevUpdated = true; break; }
                    if (!prevUpdated) { prevKey[prevCount] = neighbour; prevVal[prevCount] = curBuilding; prevCount++; }

                    queue.insert(neighbour, newDist);
                }
            }
        }

        System.out.println("\n=== Shortest Path (Dijkstra) ===");

        boolean reached = false;
        int totalDist = 0;
        for (int i = 0; i < distCount; i++)
            if (distKey[i].equals(dest)) { reached = true; totalDist = distVal[i]; break; }

        if (!reached) { System.out.println("No path from " + start + " to " + dest); return; }

        String[] pathStack = new String[256];
        int top = -1;
        String pos = dest;
        while (pos != null) {
            pathStack[++top] = pos;
            String next = null;
            for (int i = 0; i < prevCount; i++)
                if (prevKey[i].equals(pos)) { next = prevVal[i]; break; }
            pos = next;
        }

        String path = "";
        while (top >= 0) {
            if (!path.isEmpty()) path += " -> ";
            path += pathStack[top--];
        }

        System.out.println("Total Distance : " + totalDist);
        System.out.println("Path           : " + path + "\n");
    }

    static void loadDefaultData() {
        addEdge("Tower","Gerdep",5,0,true);              
        addEdge("Gerdep","UNS Inn",5,1,true);
        addEdge("UNS Inn","LPPMP",7,1,true);             
        addEdge("LPPMP","Rektorat",8,1,true);
        addEdge("Rektorat","SPMB",8,1,true);             
        addEdge("SPMB","Akademik",4,0,true);
        addEdge("Akademik","PPLH",2,0,true);             
        addEdge("SPMB","Menwa",3,1,true);
        addEdge("Menwa","Gerdep",5,1,true);              
        addEdge("Rektorat","FT",10,0,true);
        addEdge("FT","UPT Bahasa",20,0,true);            
        addEdge("UPT Bahasa","FSRD",10,0,true);
        addEdge("FSRD","FEB",10,0,true);
        addEdge("FEB","FH",30,0,true);                   
        addEdge("Rektorat","FIB",20,0,true);
        addEdge("FIB","FH",40,0,true);                   
        addEdge("FIB","Auditorium",10,0,true);
        addEdge("Auditorium","Perpustakaan",10,0,true);  
        addEdge("Perpustakaan","FH",15,0,true);
        addEdge("FH","FISIP",30,0,true);                 
        addEdge("FISIP","Gersam",5,0,true);
        addEdge("FH","FKIP",10,0,true);                  
        addEdge("FKIP","Pascasarjana",10,0,true);
        addEdge("Pascasarjana","Stadiun",7,0,true);      
        addEdge("Pascasarjana","NH",20,1,true);
        addEdge("NH","Gerbel",10,1,true);                
        addEdge("Gerbel","Biro Kemahasiswaan",10,1,true);
        addEdge("Biro Kemahasiswaan","Student Center",5,0,true);
        addEdge("Student Center","Medical Center",1,0,true);
        addEdge("Biro Kemahasiswaan","Pascasarjana",15,1,true);
        addEdge("Biro Kemahasiswaan","Graha UKM",6,0,true);
        addEdge("Graha UKM","GOR",15,0,true);            
        addEdge("GOR","Javanologi",2,0,true);
        addEdge("Javanologi","FK",10,0,true);            
        addEdge("FK","FAPSI",10,0,true);
        addEdge("FAPSI","UPT TIK",30,0,true);           
        addEdge("Pascasarjana","UPT TIK",15,0,true);
        addEdge("UPT TIK","FMIPA",25,0,true);           
        addEdge("FMIPA","FATISDA",5,0,true);
        addEdge("FMIPA","FAPET",10,0,true);              
        addEdge("FMIPA","FP",15,0,true);
        addEdge("FP","Danau",15,0,true);                 
        addEdge("Danau","Rektorat",7,0,true);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        loadDefaultData();

        while (true) {
            printAllEdges();
            System.out.println("");
            System.out.println("1. Tambahkan Jalur \n2. Lihat MST  \n3. Cari Rute Terpendek  \n4. Lihat Graph \n5. Keluar");
            System.out.print("Pilih: ");
            String choice = input.nextLine().trim();

            if (choice.equals("5")) { System.out.println("Goodbye!"); break; }

            try {
                if (choice.equals("1")) {
                    System.out.print("From building    : "); String from   = input.nextLine().trim();
                    System.out.print("To building      : "); String to     = input.nextLine().trim();
                    System.out.print("Weight           : "); int    w      = Integer.parseInt(input.nextLine().trim());
                    System.out.print("One-way? (yes/no): ");
                    int oneWay = input.nextLine().trim().equalsIgnoreCase("yes") ? 1 : 0;
                    if (w >= 0) addEdge(from, to, w, oneWay, false);

                } else if (choice.equals("2")) {
                    findMST();

                } else if (choice.equals("3")) {
                    System.out.print("Start building : "); String s = input.nextLine().trim();
                    System.out.print("Destination    : "); String d = input.nextLine().trim();
                    findShortestPath(s, d);

                } else if (choice.equals("4")) {
                    printGraph();;

                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, please try again.");
            }

            System.out.print("Press ENTER to continue...");
            input.nextLine();
        }

        input.close();
    }
}
