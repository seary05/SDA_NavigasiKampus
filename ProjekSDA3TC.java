import java.util.*;

public class ProjekSDA3TC {

    static String[] edgeFrom   = new String[256]; // O(1)
    static String[] edgeTo     = new String[256]; // O(1)
    static int[]    edgeWeight = new int[256]; // O(1)
    static int[]    edgeOneWay = new int[256]; // O(1)
    static int      edgeCount  = 0; // O(1)

    static class MinHeap {
        String[] name = new String[1024]; // O(1)
        int[]    dist = new int[1024]; // O(1)
        int size = 0; // O(1)

        void insert(String building, int distance) {
            name[size] = building; // O(1)
            dist[size] = distance; // O(1)
            size++; // O(1)
            int i = size - 1; // O(1)
            while (i > 0) { // O(log N) - where N is the current size of the heap
                int parent = (i - 1) / 2; // O(1)
                if (dist[i] < dist[parent]) { // O(1)
                    swap(i, parent); // O(1)
                    i = parent; // O(1)
                } else break; // O(1)
            }
        }

        int peekDist() { return dist[0]; } // O(1)

        String extractMin() {
            String result = name[0]; // O(1)
            size--; // O(1)
            name[0] = name[size]; // O(1)
            dist[0] = dist[size]; // O(1)
            int i = 0; // O(1)
            while (true) { // O(log N) - where N is the current size of the heap
                int left = 2*i+1, right = 2*i+2, smallest = i; // O(1)
                if (left  < size && dist[left]  < dist[smallest]) smallest = left; // O(1)
                if (right < size && dist[right] < dist[smallest]) smallest = right; // O(1)
                if (smallest == i) break; // O(1)
                swap(i, smallest); // O(1)
                i = smallest; // O(1)
            }
            return result; // O(1)
        }

        boolean isEmpty() { return size == 0; } // O(1)

        private void swap(int a, int b) {
            String tmpName = name[a]; name[a] = name[b]; name[b] = tmpName; // O(1)
            int    tmpDist = dist[a]; dist[a] = dist[b]; dist[b] = tmpDist; // O(1)
        }
    }

    static class UnionFind {
        String[] keys    = new String[512]; // O(1)
        String[] parents = new String[512]; // O(1)
        int count = 0; // O(1)

        void register(String b) {
            for (int i = 0; i < count; i++) if (keys[i].equals(b)) return; // O(V) - sequential scan of registered vertices
            keys[count] = b; parents[count] = b; count++; // O(1)
        }

        String find(String b) {
            register(b); // O(V)
            for (int i = 0; i < count; i++) { // O(V)
                if (keys[i].equals(b)) { // O(1)
                    if (!parents[i].equals(b)) // O(1)
                        parents[i] = find(parents[i]); // O(V) - Amortized recursion due to path compression
                    return parents[i]; // O(1)
                }
            }
            return b; // O(1)
        }

        void union(String a, String b) {
            String rootA = find(a), rootB = find(b); // O(V)
            if (!rootA.equals(rootB)) { // O(1)
                for (int i = 0; i < count; i++) // O(V)
                    if (keys[i].equals(rootA)) { parents[i] = rootB; return; } // O(1)
            }
        }

        boolean connected(String a, String b) { return find(a).equals(find(b)); } // O(V)
    }

    static void addEdge(String from, String to, int weight, int oneWay, boolean silent) {
        int w = 0; // O(1)
        for (int i = 0; i < edgeCount; i++) { // O(E) - iterates through all currently existing edges
            boolean sameForward = edgeFrom[i].equals(from) && edgeTo[i].equals(to); // O(1)
            boolean sameReverse = edgeFrom[i].equals(to)   && edgeTo[i].equals(from) && edgeOneWay[i] == 0    && oneWay == 0; // O(1)
            if (!sameForward && !sameReverse) { // O(1)
                edgeFrom[w]   = edgeFrom[i]; // O(1)
                edgeTo[w]     = edgeTo[i]; // O(1)
                edgeWeight[w] = edgeWeight[i]; // O(1)
                edgeOneWay[w] = edgeOneWay[i]; // O(1)
                w++; // O(1)
            }
        }

        edgeCount = w; // O(1)

        edgeFrom[edgeCount]   = from; // O(1)
        edgeTo[edgeCount]     = to; // O(1)
        edgeWeight[edgeCount] = weight; // O(1)
        edgeOneWay[edgeCount] = oneWay; // O(1)
        edgeCount++; // O(1)

        if (!silent) { // O(1)
            String arrow = (oneWay == 1) ? " -> " : " <-> "; // O(1)
            System.out.println("Edge added: " + from + arrow + to + " (" + weight + ")"); // O(1)
        }
    }

    static void printAllEdges() {
        System.out.println("\n========================================="); // O(1)
        System.out.println("        Program Navigasi Kampus"); // O(1)
        System.out.println("========================================="); // O(1)
        if (edgeCount == 0) { System.out.println("(No edges yet)"); return; } // O(1)
        HashSet<String> Building = new HashSet<>(); // O(1)
        for (int i = 0; i < edgeCount; i++) { // O(E)
            Building.add(edgeFrom[i]); // O(1) amortized
        }

        int counter = 0; // O(1)
        for (String x : Building) { // O(V) - loops through unique vertices
            System.out.print(x + ", "); // O(1)
            counter++; // O(1)

            if (counter == 5) { // O(1)
                System.out.print("\n"); // O(1)
                counter = 0; // O(1)
            }
        }
        
        System.out.println("\n--- Semua Gedung yang ada ---"); // O(1)
    }

    static void printGraph() {
        System.out.println("\n========================================="); // O(1)
        System.out.println("            Graph Gedung Kampus"); // O(1)
        System.out.println("========================================="); // O(1)
        if (edgeCount == 0) { System.out.println("(No edges yet)"); return; } // O(1)
        for (int i = 0; i < edgeCount; i++) { // O(E)
            String arrow = (edgeOneWay[i] == 1) ? " =====> " : " <=====> "; // O(1)
            System.out.println("  " + edgeFrom[i] + arrow + edgeTo[i] + " [Weight: " + edgeWeight[i] + "]"); // O(1)
        }
    }

    static void findMST() {
        if (edgeCount == 0) { System.out.println("No edges in the graph."); return; } // O(1)

        String[] tmpFrom   = new String[edgeCount]; // O(1)
        String[] tmpTo     = new String[edgeCount]; // O(1)
        int[]    tmpWeight = new int[edgeCount]; // O(1)
        int[]    tmpOneWay = new int[edgeCount]; // O(1)
        for (int i = 0; i < edgeCount; i++) { // O(E)
            tmpFrom[i]   = edgeFrom[i]; // O(1)
            tmpTo[i]     = edgeTo[i]; // O(1)
            tmpWeight[i] = edgeWeight[i]; // O(1)
            tmpOneWay[i] = edgeOneWay[i]; // O(1)
        }

        for (int i = 1; i < edgeCount; i++) { // O(E^2) - Insertion Sort worst case
            String sf = tmpFrom[i], st = tmpTo[i], sow = String.valueOf(tmpOneWay[i]); // O(1)
            int sw = tmpWeight[i]; // O(1)
            int j = i - 1; // O(1)
            while (j >= 0 && tmpWeight[j] > sw) { // O(E) execution inside outer loop
                tmpFrom[j+1]   = tmpFrom[j]; // O(1)
                tmpTo[j+1]     = tmpTo[j]; // O(1)
                tmpWeight[j+1] = tmpWeight[j]; // O(1)
                tmpOneWay[j+1] = tmpOneWay[j]; // O(1)
                j--; // O(1)
            }
            tmpFrom[j+1]   = sf; // O(1)
            tmpTo[j+1]     = st; // O(1)
            tmpWeight[j+1] = sw; // O(1)
            tmpOneWay[j+1] = Integer.parseInt(sow); // O(1)
        }

        UnionFind groups = new UnionFind(); // O(1)
        int total = 0; // O(1)
        System.out.println("\n=== MST Result - Minimum Connections (Kruskal) ==="); // O(1)
        for (int i = 0; i < edgeCount; i++) { // O(E * V) - loops E times doing UnionFind checks costing O(V)
            if (!groups.connected(tmpFrom[i], tmpTo[i])) { // O(V)
                groups.union(tmpFrom[i], tmpTo[i]); // O(V)
                System.out.println("  " + tmpFrom[i] + " -- " + tmpTo[i] + " == " + tmpWeight[i]); // O(1)
                total += tmpWeight[i]; // O(1)
            }
        }
        System.out.println("Total MST Weight: " + total + "\n"); // O(1)
    }

    static void findShortestPath(String start, String dest) {
        boolean found = false; // O(1)
        for (int i = 0; i < edgeCount; i++) // O(E)
            if (edgeFrom[i].equals(start) || edgeTo[i].equals(start)) { found = true; break; } // O(1)
        if (!found) { System.out.println("Start building not found."); return; } // O(1)

        String[] distKey = new String[256]; // O(1)
        int[]    distVal = new int[256]; // O(1)
        int      distCount = 0; // O(1)

        String[] prevKey = new String[256]; // O(1)
        String[] prevVal = new String[256]; // O(1)
        int      prevCount = 0; // O(1)

        distKey[0] = start; distVal[0] = 0; distCount = 1; // O(1)

        MinHeap queue = new MinHeap(); // O(1)
        queue.insert(start, 0); // O(log V)

        while (!queue.isEmpty()) { // Runs O(V) times over vertex extract mins
            int    curDist     = queue.peekDist(); // O(1)
            String curBuilding = queue.extractMin(); // O(log V)

            if (curBuilding.equals(dest)) break; // O(1)

            int knownDist = Integer.MAX_VALUE; // O(1)
            for (int i = 0; i < distCount; i++) // O(V) - dynamic array linear scan
                if (distKey[i].equals(curBuilding)) { knownDist = distVal[i]; break; } // O(1)

            if (curDist > knownDist) continue; // O(1)

            for (int i = 0; i < edgeCount; i++) { // O(E) - iterates all edges to check neighbors
                String neighbour = null; // O(1)
                if      (edgeFrom[i].equals(curBuilding))                         neighbour = edgeTo[i]; // O(1)
                else if (edgeTo[i].equals(curBuilding) && edgeOneWay[i] == 0)     neighbour = edgeFrom[i]; // O(1)
                if (neighbour == null) continue; // O(1)

                int neighbourDist = Integer.MAX_VALUE; // O(1)
                for (int j = 0; j < distCount; j++) // O(V) - array scan
                    if (distKey[j].equals(neighbour)) { neighbourDist = distVal[j]; break; } // O(1)

                int newDist = knownDist + edgeWeight[i]; // O(1)
                if (newDist < neighbourDist) { // O(1)
                    boolean updated = false; // O(1)
                    for (int j = 0; j < distCount; j++) // O(V) - array scan
                        if (distKey[j].equals(neighbour)) { distVal[j] = newDist; updated = true; break; } // O(1)
                    if (!updated) { distKey[distCount] = neighbour; distVal[distCount] = newDist; distCount++; } // O(1)

                    boolean prevUpdated = false; // O(1)
                    for (int j = 0; j < prevCount; j++) // O(V) - array scan
                        if (prevKey[j].equals(neighbour)) { prevVal[j] = curBuilding; prevUpdated = true; break; } // O(1)
                    if (!prevUpdated) { prevKey[prevCount] = neighbour; prevVal[prevCount] = curBuilding; prevCount++; } // O(1)

                    queue.insert(neighbour, newDist); // O(log V)
                }
            }
        }

        System.out.println("\n=== Shortest Path (Dijkstra) ==="); // O(1)

        boolean reached = false; // O(1)
        int totalDist = 0; // O(1)
        for (int i = 0; i < distCount; i++) // O(V)
            if (distKey[i].equals(dest)) { reached = true; totalDist = distVal[i]; break; } // O(1)

        if (!reached) { System.out.println("No path from " + start + " to " + dest); return; } // O(1)

        String[] pathStack = new String[256]; // O(1)
        int top = -1; // O(1)
        String pos = dest; // O(1)
        while (pos != null) { // O(V)
            pathStack[++top] = pos; // O(1)
            String next = null; // O(1)
            for (int i = 0; i < prevCount; i++) // O(V)
                if (prevKey[i].equals(pos)) { next = prevVal[i]; break; } // O(1)
            pos = next; // O(1)
        }

        String path = ""; // O(1)
        while (top >= 0) { // O(V)
            if (!path.isEmpty()) path += " -> "; // O(1)
            path += pathStack[top--]; // O(1)
        }

        System.out.println("Total Distance : " + totalDist); // O(1)
        System.out.println("Path           : " + path + "\n"); // O(1)
    }

    static void loadDefaultData() {
        addEdge("Tower","Gerdep",5,0,true); // O(E) base runtime relative to accumulative edgeCount
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
        Scanner input = new Scanner(System.in); // O(1)
        loadDefaultData(); // O(E^2) cumulative total across setup data

        while (true) { // O(Infinity) loop
            printAllEdges(); // O(E + V)
            System.out.println(""); // O(1)
            System.out.println("1. Tambahkan Jalur \n2. Lihat MST  \n3. Cari Rute Terpendek  \n4. Lihat Graph \n5. Keluar"); // O(1)
            System.out.print("Pilih: "); // O(1)
            String choice = input.nextLine().trim(); // O(1)

            if (choice.equals("5")) { System.out.println("Goodbye!"); break; } // O(1)

            try {
                if (choice.equals("1")) {
                    System.out.print("Enter Admin Password: "); // O(1)
                    String password = input.nextLine().trim(); // O(1)
                    
                    if (!password.equals("Admin123")) { // O(1)
                        System.out.println("Akses Ditolak: Password salah."); // O(1)
                    } else {
                        System.out.print("From building    : "); String from   = input.nextLine().trim(); // O(1)
                        System.out.print("To building      : "); String to     = input.nextLine().trim(); // O(1)
                        System.out.print("Weight           : "); int w        = Integer.parseInt(input.nextLine().trim()); // O(1)
                        System.out.print("One-way? (yes/no): "); // O(1)
                        int oneWay = input.nextLine().trim().equalsIgnoreCase("yes") ? 1 : 0; // O(1)
                        
                        if (w >= 0) { // O(1)
                            addEdge(from, to, w, oneWay, false); // O(E)
                            System.out.println("Edge berhasil ditambahkan!"); // O(1)
                        } else {
                            System.out.println("Error: Bobot tidak bisa negatif."); // O(1)
                        }
                    }

                } else if (choice.equals("2")) {
                    findMST(); // O(E^2 + E * V)

                } else if (choice.equals("3")) {
                    System.out.print("Start building : "); String s = input.nextLine().trim(); // O(1)
                    System.out.print("Destination    : "); String d = input.nextLine().trim(); // O(1)
                    findShortestPath(s, d); // O(E * V^2)

                } else if (choice.equals("4")) {
                    printGraph(); // O(E)

                } else {
                    System.out.println("Invalid choice."); // O(1)
                }
            } catch (Exception e) {
                System.out.println("Invalid input, please try again."); // O(1)
            }

            System.out.print("Press ENTER to continue..."); // O(1)
            input.nextLine(); // O(1)
        }

        input.close(); // O(1)
    }
}