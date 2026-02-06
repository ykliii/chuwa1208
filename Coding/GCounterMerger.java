import java.util.*;

public class GCounterMerger {
    /**
     * Merge two G-Counter states by taking the element-wise maximum.
     *
     *
     @param counterA  first counter state (nodeId -> count)
     *
     *
     @param counterB  second counter state (nodeId -> count)
     @return a new map representing the merged counter
     */
    public static Map<String, Integer> mergeCounters(
            Map<String, Integer> counterA,
            Map<String, Integer> counterB) {
        // TODO: implement this method
        Map<String, Integer> counter = new HashMap<>();
        Set<String> allNodes = new HashSet<>(counterA.keySet());
        allNodes.addAll(counterB.keySet());

        for(String node_id : allNodes) {
            int candidate1 = counterA.getOrDefault(node_id, Integer.MIN_VALUE);
            int candidate2 = counterB.getOrDefault(node_id, Integer.MIN_VALUE);
            counter.put(node_id, Math.max(candidate1, candidate2));
        }
        return counter;
    }

    // ==================== TEST CODE (DO NOT MODIFY) ====================
    public static void main(String[] args) {

        // Test 1: basic merge with overlapping and non-overlapping keys
        Map<String, Integer> a = new HashMap<>(Map.of("node1", 5, "node2", 3));
        Map<String, Integer> b = new HashMap<>(Map.of("node2", 7, "node3", 2));
        Map<String, Integer> merged = mergeCounters(a, b);
        if (merged.get("node1") != 5) {
            System.out.println("FAIL Test 1a: node1 should be 5, got " +
                    merged.get("node1"));
            return;
        }
        if (merged.get("node2") != 7) {
            System.out.println("FAIL Test 1b: node2 should be max(3,7)=7, got " +
                    merged.get("node2"));
            return;
        }
        if (merged.get("node3") != 2) {
            System.out.println("FAIL Test 1c: node3 should be 2, got " +
                    merged.get("node3"));
            return;
        }
        int total = merged.values().stream().mapToInt(i -> i).sum();
        if (total != 14) {
            System.out.println("FAIL Test 1d: total count should be 14, got " +
                    total);
            return;
        }
        System.out.println("PASS Test 1: basic merge correct (node1=5, node2=7, node3=2, total=14)");

        // Test 2: merge with empty counter
        Map<String, Integer> empty = new HashMap<>();
        Map<String, Integer> merged2 = mergeCounters(a, empty);
        if (!merged2.equals(a)) {
            System.out.println("FAIL Test 2: merging with empty map should return the other map's values");
            return;
        }
        System.out.println("PASS Test 2: merge with empty counter correct");

        // Test 3: merge is commutative (order should not matter)
        Map<String, Integer> mergedAB = mergeCounters(a, b);
        Map<String, Integer> mergedBA = mergeCounters(b, a);
        if (!mergedAB.equals(mergedBA)) {
            System.out.println("FAIL Test 3: merge should be commutative — mergeCounters(a,b) must equal mergeCounters(b,a)");
            return;
        }
        System.out.println("PASS Test 3: merge is commutative");
        System.out.println("\nAll tests passed!");
    }
}
