package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;
import edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree;
import org.apache.log4j.Logger;

import java.util.*;

import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.getTreeNodesAtDepth;

/**
 * Created by madhavanrp on 6/7/17.
 */
public class IMTreeSeedSelector {
    final static Logger logger = Logger.getLogger(IMTreeSeedSelector.class);
    public static List<IMTreeSeedSet> findSeedSets(DirectedGraph graph, IMTree tree, int budget, int threshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations) {

        SeedSetFromIMTree seedSetFromIMTree = new SeedSetFromIMTree();
        Queue<IMTreeNode> leafNodes = seedSetFromIMTree.getTreeNodesAtDepth(tree.getRoot(), budget);
        Set<Integer> estimatedSeedSet = seedSetFromIMTree.findSeedSetFromPath(tree, budget);
        IMTreeNode maxLeaf = seedSetFromIMTree.getMaxLeaf(tree, budget);
        logger.info("Estimated maximum seed set is " + estimatedSeedSet);
        logger.info(String.format("Estimated maximum targets: %d", (int)SeedSetFromIMTree.countActiveTargetsInPath(maxLeaf)));
        logger.info(String.format("Estimated maximum Non Targets: %d", IMWithTargetLabels.countNonTargetsActivatedInPath(maxLeaf)));

        List<IMTreeSeedSet> seedSets = new ArrayList<>();
        for(IMTreeNode leaf:leafNodes) {
            IMTreeSeedSet imSeedSet = new IMTreeSeedSet();
            Set<Integer> seedSet = seedSetFromIMTree.findSeedSetInPath(leaf);
            Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, 20000, new HashSet<>());
            int targetsActivated = countTargets(activatedSet, graph, targetLabels);
            int nonTargetsActivated = countTargets(activatedSet, graph, nonTargetLabels);
            imSeedSet.setTargetsActivated(targetsActivated);
            imSeedSet.setNonTargetsActivated(nonTargetsActivated);
            imSeedSet.setSeeds(seedSet);
            seedSets.add(imSeedSet);
        }
        return seedSets;
    }

    public static int countTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> targetLabels) {
        int targetsCount = 0;
        for (Integer v : activatedSet) {
            if (graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            }
        }
        return targetsCount;
    }
}
