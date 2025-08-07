import java.util.*;

public class DependencyGraphManager {

    private final Map<String, Set<String>> graph = new HashMap<>();
    private final Set<String> visited = new HashSet<>();
    private final List<String> sortedModules = new ArrayList<>();

    public void addModule(String module) {
        if (!graph.containsKey(module)) {
            graph.put(module, new HashSet<>());
        }
    }

    public void addDependency(String from, String to) {
        graph.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    /**
     * Checks if the dependency graph contains any circular dependencies.
     *
     * This method iterates through all nodes in the graph and uses a depth-first search
     * to detect cycles. If a cycle is found, it returns {@code true}; otherwise, it returns {@code false}.
     *
     * @return {@code true} if a circular dependency exists in the graph; {@code false} otherwise.
     */
    public boolean hasCircularDependency() {
        Set<String> visiting = new HashSet<>();
        for (String node : graph.keySet()) {
            if (dfsCycle(node, visiting)) return true;
        }
        return false;
    }

    /**
     * Performs a depth-first search (DFS) to detect cycles in a directed graph starting from the given node.
     *
     * @param node     The current node being visited in the DFS traversal.
     * @param visiting A set of nodes that are currently in the recursion stack (being visited).
     * @return {@code true} if a cycle is detected starting from the given node; {@code false} otherwise.
     */
    private boolean dfsCycle(String node, Set<String> visiting) {
        if (visiting.contains(node)) return true;
        if (visited.contains(node)) return false;
        visiting.add(node);
        for (String neighbor : graph.getOrDefault(node, new HashSet<>())) {
            if (dfsCycle(neighbor, visiting)) return true;
        }
        visiting.remove(node);
        visited.add(node);
        return false;
    }

    /**
     * Returns a list of module names representing the order in which modules should be loaded,
     * based on their dependencies. This method performs a topological sort on the dependency graph,
     * ensuring that each module appears after all of its dependencies in the returned list.
     * 
     * @return a {@code List<String>} containing the names of modules in load order
     */
    public List<String> getLoadOrder() {
        sortedModules.clear();
        visited.clear();
        for (String node : graph.keySet()) {
            dfsSort(node);
        }
        Collections.reverse(sortedModules);
        return sortedModules;
    }

    private void dfsSort(String node) {
        if (visited.contains(node)) return;
        visited.add(node);
        for (String dep : graph.getOrDefault(node, new HashSet<>())) {
            dfsSort(dep);
        }
        sortedModules.add(node);
    }
}
