package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.influence.maximization.models.IMTreeNode;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.*;

/**
 * Created by Naresh on 1/18/2017.
 */
public abstract class IMWithTargetLabelsWithPruning extends IMWithTargetLabels {

    /*
    Goes through each item in the second queue and :
        finds a node with maximum marginal gain, for all NT values within threshold,  and adds it to a hashmap: <NT hit>:<node>.
    After this, the hashmap will have one node associated with every NT value within threshold. Adds all this to the first queue.
     */
    @Override
    void processTreeLevel(Object graph, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, Queue<IMTreeNode> firstQueue, Queue<IMTreeNode> secondQueue, Map<Integer, Set<Integer>> nonTargetsEstimateMap, int noOfSimulations) {
        IMTreeNode current;
        int countDiffusions = 0;
//        logger.info("Number of nodes at level : " + secondQueue.size());
        Map<Integer, IMTreeNode> maxTreeChildNodeByNotTargetCount = new HashMap<>();
        while (!secondQueue.isEmpty()) {
            current = secondQueue.remove();
//            logger.debug("Processing TreeNode " + current.getNode());

            //For the current node, get the seed set and Targets, Non Targets
            Set<Integer> seedSetInPath = findSeedSetInPath(current);
            int currentNonThresholdCount = countNonTargetsActivatedInPath(current);
            double currentTargetsActivated = countTargetsActivatedInPath(current);


            // For each NT activated from 0 to NTthreshold-currentNTActivation, find the node with maximum marginal gain.
            for (int i = 0; i <= nonTargetThreshold - currentNonThresholdCount; i++) {
                if (nonTargetsEstimateMap.containsKey(i)) {
                    countDiffusions += nonTargetsEstimateMap.get(i).size();
                    //logger.info("Finding best child node for non target count " + i);

                    for (NodeWithInfluence maxInfluentialNode : findMaxInfluentialNode(graph, nonTargetsEstimateMap.get(i), seedSetInPath, targetLabels, noOfSimulations)) {
                        if (maxInfluentialNode.getNode() != Integer.MIN_VALUE) {
                            IMTreeNode childNode = new IMTreeNode(maxInfluentialNode, i, current);
                            childNode.setPathID(currentNonThresholdCount + i);
                            if (maxTreeChildNodeByNotTargetCount.containsKey(currentNonThresholdCount + i)) {
                                // For a NT value, if the child node is better than the existing node, replace it.
                                double currentMax = maxTreeChildNodeByNotTargetCount.get(currentNonThresholdCount + i).getActiveTargets() +
                                        countTargetsActivatedInPath(maxTreeChildNodeByNotTargetCount.get(currentNonThresholdCount + i).getParent());
                                if (currentTargetsActivated + childNode.getActiveTargets() > currentMax) {
                                    maxTreeChildNodeByNotTargetCount.put(currentNonThresholdCount + i, childNode);
                                }
                            } else {
                                maxTreeChildNodeByNotTargetCount.put(currentNonThresholdCount + i, childNode);
                            }
                        }
                    }
                }
            }
        }
//        logger.info("Number of diffusions in this level: " + countDiffusions);
        for (IMTreeNode childNode : maxTreeChildNodeByNotTargetCount.values()) {
            IMTreeNode parent = childNode.getParent();
//            logger.info("Adding child node " + childNode.getNode() + " with Target influence Spread " + childNode.getActiveTargets() + " non Targets : " + childNode.getActiveNonTargets());
            parent.addChild(childNode);
            firstQueue.add(childNode);
        }
       

    }

}
